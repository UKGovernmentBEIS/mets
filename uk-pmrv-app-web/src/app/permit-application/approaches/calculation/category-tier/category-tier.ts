import { CalculationOfCO2MonitoringApproach, CalculationSourceStreamCategoryAppliedTier } from 'pmrv-api';

import { StatusKey } from '../../../shared/types/permit-task.type';
import { PermitApplicationState } from '../../../store/permit-application.state';

export type categoryAppliedTier = Omit<
  CalculationSourceStreamCategoryAppliedTier,
  'sourceStreamCategory' | 'activityData'
>;

export const statusKeyToSubtaskNameMapper: Partial<
  Record<StatusKey, keyof CalculationSourceStreamCategoryAppliedTier>
> = {
  CALCULATION_CO2_Calorific: 'netCalorificValue',
  CALCULATION_CO2_Emission_Factor: 'emissionFactor',
  CALCULATION_CO2_Oxidation_Factor: 'oxidationFactor',
  CALCULATION_CO2_Carbon_Content: 'carbonContent',
  CALCULATION_CO2_Conversion_Factor: 'conversionFactor',
  CALCULATION_CO2_Biomass_Fraction: 'biomassFraction',
};
export const statusKeyTosubtaskUrlParamMapper: Partial<Record<StatusKey, string>> = {
  CALCULATION_CO2_Calorific: 'calorific-value',
  CALCULATION_CO2_Emission_Factor: 'emission-factor',
  CALCULATION_CO2_Oxidation_Factor: 'oxidation-factor',
  CALCULATION_CO2_Carbon_Content: 'carbon-content',
  CALCULATION_CO2_Conversion_Factor: 'conversion-factor',
  CALCULATION_CO2_Biomass_Fraction: 'biomass-fraction',
};

export function getSubtaskData(state: PermitApplicationState, index: number, statusKey: StatusKey) {
  const sourceStreamCategory = (state.permit.monitoringApproaches.CALCULATION_CO2 as CalculationOfCO2MonitoringApproach)
    ?.sourceStreamCategoryAppliedTiers?.[index] as categoryAppliedTier;

  const subtask = statusKeyToSubtaskNameMapper[statusKey];
  return sourceStreamCategory?.[subtask];
}
