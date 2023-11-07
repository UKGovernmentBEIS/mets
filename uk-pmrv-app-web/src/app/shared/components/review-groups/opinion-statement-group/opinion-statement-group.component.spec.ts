import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { OpinionStatementGroupComponent } from '@shared/components/review-groups/opinion-statement-group/opinion-statement-group.component';
import { SharedModule } from '@shared/shared.module';
import {
  mockState,
  mockVerificationApplyPayload,
} from '@tasks/aer/verification-submit/testing/mock-verification-apply-action';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

describe('OpinionStatementGroupComponent', () => {
  let page: Page;
  let store: CommonTasksStore;
  let component: OpinionStatementGroupComponent;
  let fixture: ComponentFixture<OpinionStatementGroupComponent>;

  class Page extends BasePage<OpinionStatementGroupComponent> {
    get installationDetailsSummary() {
      return this.query('app-installation-details-summary');
    }

    get regulatedActivitiesGroup() {
      return this.query('app-aer-regulated-activities-group');
    }

    get combustionSourcesGroup() {
      return this.query('app-aer-combustion-sources-group');
    }

    get processSourcesGroup() {
      return this.query('app-aer-process-sources-group');
    }

    get emissionDetailsGroup() {
      return this.query('app-aer-emission-details-group');
    }

    get emissionsSummaryGroup() {
      return this.query('app-emissions-summary-group');
    }

    get verifiersEmissionsAssessmentGroup() {
      return this.query('app-aer-verifiers-emissions-assessment-group');
    }

    get monitoringPlanVersions() {
      return this.query('app-monitoring-plan-versions');
    }

    get monitoringPlanSummaryTemplate() {
      return this.query('app-monitoring-plan-summary-template');
    }

    get siteVisitGroup() {
      return this.query('app-aer-site-visit-group');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [OpinionStatementGroupComponent],
      imports: [SharedModule, RouterTestingModule],
      providers: [KeycloakService],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonTasksStore);
    store.setState(mockState);
    fixture = TestBed.createComponent(OpinionStatementGroupComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    component.payload = mockVerificationApplyPayload;
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display all summary elements', () => {
    expect(page.installationDetailsSummary).toBeTruthy();
    expect(page.regulatedActivitiesGroup).toBeTruthy();
    expect(page.combustionSourcesGroup).toBeTruthy();
    expect(page.processSourcesGroup).toBeTruthy();
    expect(page.emissionDetailsGroup).toBeTruthy();
    expect(page.emissionsSummaryGroup).toBeTruthy();
    expect(page.verifiersEmissionsAssessmentGroup).toBeTruthy();
    expect(page.monitoringPlanVersions).toBeTruthy();
    expect(page.monitoringPlanSummaryTemplate).toBeTruthy();
    expect(page.siteVisitGroup).toBeTruthy();
  });
});
