import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, MockType } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { RequestActionsService } from 'pmrv-api';

import { PermitTransferAModule } from '../permit-transfer-a.module';
import { mockState } from '../testing/mock-state';
import { TransferWaitComponent } from './wait.component';

describe('WaitComponent', () => {
  let component: TransferWaitComponent;
  let fixture: ComponentFixture<TransferWaitComponent>;
  let store: CommonTasksStore;
  let page: Page;
  let requestActionsService: MockType<RequestActionsService>;

  class Page extends BasePage<TransferWaitComponent> {
    get content(): string {
      return this.query<HTMLElement>('.govuk-warning-text').textContent.trim();
    }

    get heading(): string {
      return this.query<HTMLHeadingElement>('app-base-task-container-component h1').textContent.trim();
    }

    get bannerHeading(): string {
      return this.query<HTMLHeadingElement>('govuk-notification-banner h1').textContent.trim();
    }
  }

  beforeEach(async () => {
    requestActionsService = {
      getRequestActionsByRequestId: jest.fn().mockReturnValue(
        of([
          {
            id: 781,
            type: 'PERMIT_TRANSFER_A_APPLICATION_SUBMITTED',
            submitter: 'John Doe',
            creationDate: new Date().toISOString(),
          },
        ]),
      ),
    };

    await TestBed.configureTestingModule({
      declarations: [TransferWaitComponent],
      providers: [KeycloakService, { provide: RequestActionsService, useValue: requestActionsService }],
      imports: [PermitTransferAModule, RouterTestingModule],
    }).compileComponents();

    store = TestBed.inject(CommonTasksStore);
    store.setState({
      ...mockState,
      requestTaskItem: {
        ...mockState.requestTaskItem,
        requestTask: { ...mockState.requestTaskItem.requestTask, type: 'PERMIT_TRANSFER_A_WAIT_FOR_TRANSFER' },
      },
    });

    fixture = TestBed.createComponent(TransferWaitComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the transfer wait content', () => {
    expect(page.bannerHeading).toEqual('Permit transfer started by John Doe');
    expect(page.heading).toEqual('Full transfer of permit');
    expect(page.content).toEqual('!Warning Application is being reviewed by the new operator');
  });
});
