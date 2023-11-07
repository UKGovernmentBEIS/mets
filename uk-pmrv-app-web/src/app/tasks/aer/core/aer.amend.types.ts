import { AerMonitoringApproachEmissions } from 'pmrv-api';

export const AER_AMEND_STATUS_PREFIX = 'AMEND_';

export type AerAmendTotalEmissionsGroup =
  | Exclude<AerMonitoringApproachEmissions['type'], 'TRANSFERRED_CO2_N2O'>
  | 'EMISSIONS_SUMMARY';

export type AerAmendRestGroup =
  | 'INSTALLATION_DETAILS'
  | 'FUELS_AND_EQUIPMENT'
  | 'ADDITIONAL_INFORMATION'
  | 'ACTIVITY_LEVEL_REPORT';

export type AerAmendGroup = AerAmendRestGroup | 'TOTAL_EMISSIONS';
export type AerAmendGroupStatusKey = `${typeof AER_AMEND_STATUS_PREFIX}${AerAmendGroup}`;

export function getTotalEmissionsKeys(): Array<AerAmendTotalEmissionsGroup> {
  return [
    'CALCULATION_CO2',
    'CALCULATION_PFC',
    'MEASUREMENT_CO2',
    'MEASUREMENT_N2O',
    'INHERENT_CO2',
    'FALLBACK',
    'EMISSIONS_SUMMARY',
  ];
}

export const amendTasksPerReviewGroup = {
  INSTALLATION_DETAILS: ['INSTALLATION_DETAILS'],
  FUELS_AND_EQUIPMENT: ['FUELS_AND_EQUIPMENT'],
  TOTAL_EMISSIONS: getTotalEmissionsKeys(),
  ADDITIONAL_INFORMATION: ['ADDITIONAL_INFORMATION'],
  ACTIVITY_LEVEL_REPORT: ['ACTIVITY_LEVEL_REPORT'],
};

export const amendTasksPerReviewSection = {
  INSTALLATION_DETAILS: [
    'aerMonitoringPlanDeviation',
    'pollutantRegisterActivities',
    'naceCodes',
    'regulatedActivities',
    'monitoringApproachEmissions',
  ],
  FUELS_AND_EQUIPMENT: ['sourceStreams', 'emissionSources', 'emissionPoints'],
  CALCULATION_CO2: ['CALCULATION_CO2'],
  CALCULATION_PFC: ['CALCULATION_PFC'],
  MEASUREMENT_CO2: ['MEASUREMENT_CO2'],
  MEASUREMENT_N2O: ['MEASUREMENT_N2O'],
  INHERENT_CO2: ['INHERENT_CO2'],
  FALLBACK: ['FALLBACK'],
  EMISSIONS_SUMMARY: ['EMISSIONS_SUMMARY'],
  ADDITIONAL_INFORMATION: ['abbreviations', 'additionalDocuments', 'confidentialityStatement'],
  ACTIVITY_LEVEL_REPORT: ['activityLevelReport'],
};
