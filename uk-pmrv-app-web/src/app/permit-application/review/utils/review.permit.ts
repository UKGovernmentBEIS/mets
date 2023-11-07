import { PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload } from 'pmrv-api';

export const mandatoryReviewGroups: Array<PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload['group']> = [
  'PERMIT_TYPE',
  'CONFIDENTIALITY_STATEMENT',
  'FUELS_AND_EQUIPMENT',
  'INSTALLATION_DETAILS',
  'MANAGEMENT_PROCEDURES',
  'MONITORING_METHODOLOGY_PLAN',
  'ADDITIONAL_INFORMATION',
  'DEFINE_MONITORING_APPROACHES',
  'UNCERTAINTY_ANALYSIS',
];

export const reviewGroupsTasks: {
  [key in PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload['group']]: string[];
} = {
  PERMIT_TYPE: ['permitType'],
  INSTALLATION_DETAILS: [
    'environmentalPermitsAndLicences',
    'installationDescription',
    'regulatedActivities',
    'estimatedAnnualEmissions',
  ],
  FUELS_AND_EQUIPMENT: [
    'sourceStreams',
    'emissionSources',
    'emissionPoints',
    'emissionSummaries',
    'measurementDevicesOrMethods',
    'siteDiagrams',
  ],
  DEFINE_MONITORING_APPROACHES: ['monitoringApproachesPrepare', 'monitoringApproaches'],
  CALCULATION_CO2: ['CALCULATION_CO2'],
  MEASUREMENT_CO2: ['MEASUREMENT_CO2'],
  FALLBACK: ['FALLBACK'],
  MEASUREMENT_N2O: ['MEASUREMENT_N2O'],
  CALCULATION_PFC: ['CALCULATION_PFC'],
  INHERENT_CO2: ['INHERENT_CO2'],
  TRANSFERRED_CO2_N2O: ['TRANSFERRED_CO2_N2O'],
  MANAGEMENT_PROCEDURES: [
    'monitoringReporting',
    'assignmentOfResponsibilities',
    'monitoringPlanAppropriateness',
    'dataFlowActivities',
    'qaDataFlowActivities',
    'reviewAndValidationOfData',
    'assessAndControlRisk',
    'qaMeteringAndMeasuringEquipment',
    'correctionsAndCorrectiveActions',
    'controlOfOutsourcedActivities',
    'recordKeepingAndDocumentation',
    'environmentalManagementSystem',
  ],
  MONITORING_METHODOLOGY_PLAN: ['monitoringMethodologyPlans'],
  ADDITIONAL_INFORMATION: ['abbreviations', 'additionalDocuments'],
  CONFIDENTIALITY_STATEMENT: ['confidentialityStatement'],
  UNCERTAINTY_ANALYSIS: ['uncertaintyAnalysis'],
};

export const mandatoryReviewGroupsTasks = (({
  PERMIT_TYPE,
  CONFIDENTIALITY_STATEMENT,
  FUELS_AND_EQUIPMENT,
  INSTALLATION_DETAILS,
  MANAGEMENT_PROCEDURES,
  MONITORING_METHODOLOGY_PLAN,
  ADDITIONAL_INFORMATION,
  DEFINE_MONITORING_APPROACHES,
  UNCERTAINTY_ANALYSIS,
}) => ({
  PERMIT_TYPE,
  CONFIDENTIALITY_STATEMENT,
  FUELS_AND_EQUIPMENT,
  INSTALLATION_DETAILS,
  MANAGEMENT_PROCEDURES,
  MONITORING_METHODOLOGY_PLAN,
  ADDITIONAL_INFORMATION,
  DEFINE_MONITORING_APPROACHES,
  UNCERTAINTY_ANALYSIS,
}))(reviewGroupsTasks);

export const reviewGroupHeading: Record<
  PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload['group'],
  string
> = {
  PERMIT_TYPE: 'Permit type',
  INSTALLATION_DETAILS: 'Installation details',
  FUELS_AND_EQUIPMENT: 'Fuels and equipment',
  DEFINE_MONITORING_APPROACHES: 'Define monitoring approaches',
  CALCULATION_CO2: 'Calculation of CO2',
  MEASUREMENT_CO2: 'Measurement of CO2',
  FALLBACK: 'Fallback approach',
  MEASUREMENT_N2O: 'Measurement of nitrous oxide (N2O)',
  CALCULATION_PFC: 'Calculation of perfluorocarbons (PFC)',
  INHERENT_CO2: 'Inherent CO2 emissions',
  TRANSFERRED_CO2_N2O: 'Procedures for transferred CO2 or N2O',
  UNCERTAINTY_ANALYSIS: 'Uncertainty analysis',
  MANAGEMENT_PROCEDURES: 'Management procedures',
  MONITORING_METHODOLOGY_PLAN: 'Monitoring methodology',
  ADDITIONAL_INFORMATION: 'Additional information',
  CONFIDENTIALITY_STATEMENT: 'Confidentiality',
};
