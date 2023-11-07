import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';

import { render } from '@testing-library/angular';

import { AviationAerEtsComplianceRules } from 'pmrv-api';

import { EtsComplianceRulesFormComponent } from './ets-compliance-rules-form.component';

describe('EtsComplianceRulesFormComponent', () => {
  let element: HTMLElement;

  const renderComponent = async () => {
    const { fixture, detectChanges } = await render(
      `<form [formGroup]="form">
            <app-ets-compliance-rules-form></app-ets-compliance-rules-form>
       </form>`,
      {
        imports: [ReactiveFormsModule, EtsComplianceRulesFormComponent],
        componentProperties: {
          form: new FormGroup({
            monitoringPlanRequirementsMet: new FormControl<
              AviationAerEtsComplianceRules['monitoringPlanRequirementsMet'] | null
            >(null),
            monitoringPlanRequirementsNotMetReason: new FormControl<
              AviationAerEtsComplianceRules['monitoringPlanRequirementsNotMetReason'] | null
            >(null),

            ukEtsOrderRequirementsMet: new FormControl<
              AviationAerEtsComplianceRules['ukEtsOrderRequirementsMet'] | null
            >(null),
            ukEtsOrderRequirementsNotMetReason: new FormControl<
              AviationAerEtsComplianceRules['ukEtsOrderRequirementsNotMetReason'] | null
            >(null),

            detailSourceDataVerified: new FormControl<AviationAerEtsComplianceRules['detailSourceDataVerified'] | null>(
              null,
            ),
            detailSourceDataNotVerifiedReason: new FormControl<
              AviationAerEtsComplianceRules['detailSourceDataNotVerifiedReason'] | null
            >(null),

            partOfSiteVerification: new FormControl<AviationAerEtsComplianceRules['partOfSiteVerification'] | null>(
              null,
            ),

            controlActivitiesDocumented: new FormControl<
              AviationAerEtsComplianceRules['controlActivitiesDocumented'] | null
            >(null),
            controlActivitiesNotDocumentedReason: new FormControl<
              AviationAerEtsComplianceRules['controlActivitiesNotDocumentedReason'] | null
            >(null),

            proceduresMonitoringPlanDocumented: new FormControl<
              AviationAerEtsComplianceRules['proceduresMonitoringPlanDocumented'] | null
            >(null),
            proceduresMonitoringPlanNotDocumentedReason: new FormControl<
              AviationAerEtsComplianceRules['proceduresMonitoringPlanNotDocumentedReason'] | null
            >(null),

            dataVerificationCompleted: new FormControl<
              AviationAerEtsComplianceRules['dataVerificationCompleted'] | null
            >(null),
            dataVerificationNotCompletedReason: new FormControl<
              AviationAerEtsComplianceRules['dataVerificationNotCompletedReason'] | null
            >(null),

            monitoringApproachAppliedCorrectly: new FormControl<
              AviationAerEtsComplianceRules['monitoringApproachAppliedCorrectly'] | null
            >(null),
            monitoringApproachNotAppliedCorrectlyReason: new FormControl<
              AviationAerEtsComplianceRules['monitoringApproachNotAppliedCorrectlyReason'] | null
            >(null),

            flightsCompletenessComparedWithAirTrafficData: new FormControl<
              AviationAerEtsComplianceRules['flightsCompletenessComparedWithAirTrafficData'] | null
            >(null),
            flightsCompletenessNotComparedWithAirTrafficDataReason: new FormControl<
              AviationAerEtsComplianceRules['flightsCompletenessNotComparedWithAirTrafficDataReason'] | null
            >(null),

            reportedDataConsistencyChecksPerformed: new FormControl<
              AviationAerEtsComplianceRules['reportedDataConsistencyChecksPerformed'] | null
            >(null),
            reportedDataConsistencyChecksNotPerformedReason: new FormControl<
              AviationAerEtsComplianceRules['reportedDataConsistencyChecksNotPerformedReason'] | null
            >(null),

            fuelConsistencyChecksPerformed: new FormControl<
              AviationAerEtsComplianceRules['fuelConsistencyChecksPerformed'] | null
            >(null),
            fuelConsistencyChecksNotPerformedReason: new FormControl<
              AviationAerEtsComplianceRules['fuelConsistencyChecksNotPerformedReason'] | null
            >(null),

            missingDataMethodsApplied: new FormControl<
              AviationAerEtsComplianceRules['missingDataMethodsApplied'] | null
            >(null),
            missingDataMethodsNotAppliedReason: new FormControl<
              AviationAerEtsComplianceRules['missingDataMethodsNotAppliedReason'] | null
            >(null),

            regulatorGuidanceMet: new FormControl<AviationAerEtsComplianceRules['regulatorGuidanceMet'] | null>(null),
            regulatorGuidanceNotMetReason: new FormControl<
              AviationAerEtsComplianceRules['regulatorGuidanceNotMetReason'] | null
            >(null),

            nonConformities: new FormControl<AviationAerEtsComplianceRules['nonConformities'] | null>(null),
          }),
        },
      },
    );

    element = fixture.nativeElement;
    return { component: fixture.componentInstance, detectChanges };
  };

  it('should create', async () => {
    const { component } = await renderComponent();

    expect(component).toBeTruthy();
  });

  it('should render the review groups', () => {
    expect(Array.from(element.querySelectorAll<HTMLHeadElement>('h2')).map((el) => el.textContent.trim())).toEqual([
      'Have the emissions monitoring plan requirements and conditions been met?',
      'Have the requirements of the UK ETS Order, which amends the MRR, been met?',
      'Can you verify the detail and source of data?',
      'Were control activities documented, implemented, maintained and effective to reduce any risks?',
      'Were procedures in the emissions monitoring plan documented, implemented, maintained and effective to reduce risks?',
      'Has data verification been completed as required?',
      'Has the monitoring approach been applied correctly?',
      'Has the completeness of flights or data been compared with air traffic data?',
      "Have consistency checks been made between reported data and 'mass & balance' documentation?",
      'Have consistency checks been made between aggregate fuel consumption and fuel purchase or supply data?',
      'Were methods used for applying missing data appropriate?',
      'Has the regulator guidance on monitoring and reporting been met?',
      'Have any non-conformities from last year been corrected?',
    ]);
  });
});
