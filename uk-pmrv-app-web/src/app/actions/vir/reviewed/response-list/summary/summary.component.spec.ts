import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { ActivatedRouteStub, BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { UncorrectedItem } from 'pmrv-api';

import { mockVirApplicationReviewedRequestActionPayload } from '../../testing/mock-aer-reviewed';
import { VirActionReviewedModule } from '../../vir-action-reviewed.module';
import { SummaryComponent } from './summary.component';

describe('SummaryComponent', () => {
  let page: Page;
  let component: SummaryComponent;
  let fixture: ComponentFixture<SummaryComponent>;

  const currentItem: UncorrectedItem =
    mockVirApplicationReviewedRequestActionPayload.verificationData.uncorrectedNonConformities.B1;
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

    get regulatorResponseItem() {
      return this.query('app-regulator-response-item');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [VirActionReviewedModule, RouterTestingModule],
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
    expect(page.heading1.textContent.trim()).toEqual('B1: an uncorrected error in the monitoring plan');
    expect(page.verificationItem).toBeTruthy();
    expect(page.operatorResponseItem).toBeTruthy();
    expect(page.regulatorResponseItem).toBeTruthy();
  });
});
