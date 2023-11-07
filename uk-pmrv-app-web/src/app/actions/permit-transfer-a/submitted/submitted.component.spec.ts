import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { GovukDatePipe } from '@shared/pipes/govuk-date.pipe';
import { SharedModule } from '@shared/shared.module';
import { mockTransferPayload } from '@tasks/permit-transfer-a/testing/mock-state';
import { BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { ActionSharedModule } from '../../shared/action-shared-module';
import { CommonActionsStore } from '../../store/common-actions.store';
import { SubmittedComponent } from './submitted.component';

describe('SubmittedComponent', () => {
  let component: SubmittedComponent;
  let fixture: ComponentFixture<SubmittedComponent>;
  let activatedRouteSpy;
  let page: Page;
  let store: CommonActionsStore;

  class Page extends BasePage<SubmittedComponent> {
    get summaryListValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelectorAll('dd')[0]])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }

    get heading(): string {
      return this.query<HTMLHeadingElement>('h1').textContent.trim();
    }
  }

  beforeEach(async () => {
    activatedRouteSpy = {
      data: of({ pageTitle: 'Full transfer of permit' }),
    };

    await TestBed.configureTestingModule({
      declarations: [SubmittedComponent],
      imports: [ActionSharedModule, RouterTestingModule, SharedModule],
      providers: [KeycloakService, { provide: ActivatedRoute, useValue: activatedRouteSpy }],
    }).compileComponents();

    store = TestBed.inject(CommonActionsStore);

    store.setState({
      ...store.getState(),
      storeInitialized: true,
      action: {
        type: 'PERMIT_TRANSFER_A_APPLICATION_SUBMITTED',
        submitter: '123',
        payload: {
          ...mockTransferPayload,
          payloadType: 'PERMIT_TRANSFER_A_APPLICATION_SUBMITTED_PAYLOAD',
        } as unknown,
      },
    });

    fixture = TestBed.createComponent(SubmittedComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render heading content', () => {
    expect(page.heading).toEqual('Full transfer of permit');
  });

  it('should display the permit transfer "a" summary', () => {
    const govukDatePipe = new GovukDatePipe();

    expect(page.summaryListValues).toEqual([
      ['Reason for the transfer', 'Reason of transfer'],
      ['Files added', 'None'],
      ['Actual or estimated date of transfer', govukDatePipe.transform(mockTransferPayload.transferDate)],
      ['Who is paying for the transfer?', 'Receiver'],
      ['Who is completing the annual emission report?', 'Transferer'],
      ['Transfer code', '123456789'],
    ]);
  });
});
