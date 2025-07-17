import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { RequestActionTaskComponent } from '@aviation/request-action/shared/components/request-action-task/request-action-task.component';
import { AerUkEtsRequestActionPayload, RequestActionStore } from '@aviation/request-action/store';
import { TYPE_AWARE_STORE } from '@aviation/type-aware.store';
import { SharedModule } from '@shared/shared.module';
import { ActivatedRouteStub, BasePage } from '@testing';

import EtsComplianceRulesComponent from './ets-compliance-rules.component';

class Page extends BasePage<EtsComplianceRulesComponent> {
  get header(): string {
    return this.query('app-page-heading').textContent.trim();
  }
  get summaryValues() {
    return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
      .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
      .map((pair) => pair.map((element) => element.textContent.trim()));
  }
}

describe('EtsComplianceRulesComponent', () => {
  let fixture: ComponentFixture<EtsComplianceRulesComponent>;
  let store: RequestActionStore;
  let page: Page;

  const route = new ActivatedRouteStub();

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RequestActionTaskComponent, EtsComplianceRulesComponent],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TYPE_AWARE_STORE, useExisting: RequestActionStore },
      ],
    }).compileComponents();

    store = TestBed.inject(RequestActionStore);
    store.setState({
      requestActionItem: {
        type: 'AVIATION_AER_UKETS_APPLICATION_VERIFICATION_SUBMITTED',
        creationDate: '2023-09-20T12:18:46.714Z',
        payload: {
          verificationReport: {
            etsComplianceRules: {
              monitoringPlanRequirementsMet: false,
              monitoringPlanRequirementsNotMetReason: 'reason',
              ukEtsOrderRequirementsMet: false,
              ukEtsOrderRequirementsNotMetReason: 'reason',
              detailSourceDataVerified: false,
              detailSourceDataNotVerifiedReason: 'reason',
              partOfSiteVerification: 'reason',
              controlActivitiesDocumented: false,
              controlActivitiesNotDocumentedReason: 'reason',
              proceduresMonitoringPlanDocumented: false,
              proceduresMonitoringPlanNotDocumentedReason: 'reason',
              dataVerificationCompleted: false,
              dataVerificationNotCompletedReason: 'reason',
              monitoringApproachAppliedCorrectly: false,
              monitoringApproachNotAppliedCorrectlyReason: 'reason',
              flightsCompletenessComparedWithAirTrafficData: false,
              flightsCompletenessNotComparedWithAirTrafficDataReason: 'reason',
              reportedDataConsistencyChecksPerformed: false,
              reportedDataConsistencyChecksNotPerformedReason: 'reason',
              fuelConsistencyChecksPerformed: false,
              fuelConsistencyChecksNotPerformedReason: 'reason',
              missingDataMethodsApplied: false,
              missingDataMethodsNotAppliedReason: 'reason',
              regulatorGuidanceMet: false,
              regulatorGuidanceNotMetReason: 'reason',
              nonConformities: 'NOT_APPLICABLE',
            },
          },
        } as AerUkEtsRequestActionPayload,
      },
      regulatorViewer: false,
    });

    fixture = TestBed.createComponent(EtsComplianceRulesComponent);
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(fixture.componentInstance).toBeTruthy();
  });

  it('should show task details', () => {
    expect(page.header).toEqual('Compliance with ETS rules');

    expect(page.summaryValues).toEqual([
      ['Emissions monitoring plan requirements and conditions met?', 'No  reason'],
      ['ETS Order requirements met?', 'No  reason'],
      ['Data detail and source verified?', 'No  reason'],
      ['Control activities documented, implemented, maintained and effective to reduce risks?', 'No  reason'],
      ['Procedures in EMP documented, implemented, maintained and effective to reduce risks?', 'No  reason'],
      ['Data verification completed as required?', 'No  reason'],
      ['Correct application of monitoring approach?', 'No  reason'],
      ['Comparison of completeness of flights or data with air traffic data?', 'No  reason'],
      ['Consistency checks made between reported data and ‘mass and balance’ documentation?', 'No  reason'],
      ['Consistency checks made between aggregate fuel consumption and fuel purchase or supply data?', 'No  reason'],
      ['Appropriate methods used for applying missing data?', 'No  reason'],
      ['Regulator guidance on monitoring and reporting met?', 'No  reason'],
      ['Corrected non-conformities from last year?', 'Not applicable'],
    ]);
  });
});
