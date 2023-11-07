import { FormControl } from '@angular/forms';

import { AviationAerEtsComplianceRules } from 'pmrv-api';

export interface AviationAerEtsComplianceRulesFormModel {
  monitoringPlanRequirementsMet: FormControl<AviationAerEtsComplianceRules['monitoringPlanRequirementsMet'] | null>;
  monitoringPlanRequirementsNotMetReason?: FormControl<
    AviationAerEtsComplianceRules['monitoringPlanRequirementsNotMetReason'] | null
  >;

  ukEtsOrderRequirementsMet: FormControl<AviationAerEtsComplianceRules['ukEtsOrderRequirementsMet'] | null>;
  ukEtsOrderRequirementsNotMetReason?: FormControl<
    AviationAerEtsComplianceRules['ukEtsOrderRequirementsNotMetReason'] | null
  >;

  detailSourceDataVerified: FormControl<AviationAerEtsComplianceRules['detailSourceDataVerified'] | null>;
  detailSourceDataNotVerifiedReason?: FormControl<
    AviationAerEtsComplianceRules['detailSourceDataNotVerifiedReason'] | null
  >;

  partOfSiteVerification?: FormControl<AviationAerEtsComplianceRules['partOfSiteVerification'] | null>;

  controlActivitiesDocumented: FormControl<AviationAerEtsComplianceRules['controlActivitiesDocumented'] | null>;
  controlActivitiesNotDocumentedReason?: FormControl<
    AviationAerEtsComplianceRules['controlActivitiesNotDocumentedReason'] | null
  >;

  proceduresMonitoringPlanDocumented: FormControl<
    AviationAerEtsComplianceRules['proceduresMonitoringPlanDocumented'] | null
  >;
  proceduresMonitoringPlanNotDocumentedReason?: FormControl<
    AviationAerEtsComplianceRules['proceduresMonitoringPlanNotDocumentedReason'] | null
  >;

  dataVerificationCompleted: FormControl<AviationAerEtsComplianceRules['dataVerificationCompleted'] | null>;
  dataVerificationNotCompletedReason?: FormControl<
    AviationAerEtsComplianceRules['dataVerificationNotCompletedReason'] | null
  >;

  monitoringApproachAppliedCorrectly: FormControl<
    AviationAerEtsComplianceRules['monitoringApproachAppliedCorrectly'] | null
  >;
  monitoringApproachNotAppliedCorrectlyReason?: FormControl<
    AviationAerEtsComplianceRules['monitoringApproachNotAppliedCorrectlyReason'] | null
  >;

  flightsCompletenessComparedWithAirTrafficData: FormControl<
    AviationAerEtsComplianceRules['monitoringApproachNotAppliedCorrectlyReason'] | null
  >;
  flightsCompletenessNotComparedWithAirTrafficDataReason?: FormControl<
    AviationAerEtsComplianceRules['flightsCompletenessNotComparedWithAirTrafficDataReason'] | null
  >;

  reportedDataConsistencyChecksPerformed: FormControl<
    AviationAerEtsComplianceRules['reportedDataConsistencyChecksPerformed'] | null
  >;
  reportedDataConsistencyChecksNotPerformedReason?: FormControl<
    AviationAerEtsComplianceRules['reportedDataConsistencyChecksNotPerformedReason'] | null
  >;

  fuelConsistencyChecksPerformed: FormControl<AviationAerEtsComplianceRules['fuelConsistencyChecksPerformed'] | null>;
  fuelConsistencyChecksNotPerformedReason?: FormControl<
    AviationAerEtsComplianceRules['fuelConsistencyChecksNotPerformedReason'] | null
  >;

  missingDataMethodsApplied: FormControl<AviationAerEtsComplianceRules['missingDataMethodsApplied'] | null>;
  missingDataMethodsNotAppliedReason?: FormControl<
    AviationAerEtsComplianceRules['missingDataMethodsNotAppliedReason'] | null
  >;

  regulatorGuidanceMet: FormControl<AviationAerEtsComplianceRules['regulatorGuidanceMet'] | null>;
  regulatorGuidanceNotMetReason?: FormControl<AviationAerEtsComplianceRules['regulatorGuidanceNotMetReason'] | null>;

  nonConformities: FormControl<AviationAerEtsComplianceRules['nonConformities'] | null>;
}
