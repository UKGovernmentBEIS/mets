import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { ActivatedRouteStub, BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { UncorrectedItem } from 'pmrv-api';

import { mockVirApplicationSubmittedRequestActionPayload } from '../testing/mock-aer-submitted';
import { VirActionSubmittedModule } from '../vir-action-submitted.module';
import { SummaryComponent } from './summary.component';

describe('SummaryComponent', () => {
  let page: Page;
  let component: SummaryComponent;
  let fixture: ComponentFixture<SummaryComponent>;

  const currentItem: UncorrectedItem =
    mockVirApplicationSubmittedRequestActionPayload.verificationData.uncorrectedMisstatements.A1;
  const activatedRoute: ActivatedRouteStub = new ActivatedRouteStub({ taskId: 1, id: currentItem.reference }, null, {
    verificationDataItem: currentItem,
  });

  class Page extends BasePage<SummaryComponent> {
    get heading1(): HTMLHeadingElement {
      return this.query<HTMLHeadingElement>('h1');
    }

    get verificationItem() {
      return this.query('app-verification-recommendation-item');
    }

    get operatorResponseItem() {
      return this.query('app-operator-response-item');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VirActionSubmittedModule, RouterTestingModule],
      providers: [KeycloakService, { provide: ActivatedRoute, useValue: activatedRoute }],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display all HTMLElements', () => {
    expect(page.heading1).toBeTruthy();
    expect(page.heading1.textContent.trim()).toEqual(
      'A1: an uncorrected error that remained before the verification report was issued',
    );
    expect(page.verificationItem).toBeTruthy();
    expect(page.operatorResponseItem).toBeTruthy();
  });
});
