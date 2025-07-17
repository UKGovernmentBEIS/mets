import {
  MeasurementOfCO2EmissionPointCategoryAppliedTier,
  MeasurementOfCO2MonitoringApproach,
  MeasurementOfN2OEmissionPointCategoryAppliedTier,
} from 'pmrv-api';

import { StatusKey } from '../../../shared/types/permit-task.type';
import { PermitApplicationState } from '../../../store/permit-application.state';

export type categoryAppliedTier = Omit<
  MeasurementOfN2OEmissionPointCategoryAppliedTier,
  'emissionPointCategory' | 'activityData'
>;

export const statusKeyToSubtaskNameMapper: Partial<
  Record<StatusKey, keyof MeasurementOfCO2EmissionPointCategoryAppliedTier>
> = {
  MEASUREMENT_CO2_Category: 'emissionPointCategory',
  MEASUREMENT_CO2_Measured_Emissions: 'measuredEmissions',
  MEASUREMENT_CO2_Applied_Standard: 'appliedStandard',
  MEASUREMENT_CO2_Biomass_Fraction: 'biomassFraction',
};
export const statusKeyTosubtaskUrlParamMapper: Partial<Record<StatusKey, string>> = {
  MEASUREMENT_CO2_Category: 'category',
  MEASUREMENT_CO2_Measured_Emissions: 'emissions',
  MEASUREMENT_CO2_Applied_Standard: 'applied-standard',
  MEASUREMENT_CO2_Biomass_Fraction: 'biomass-fraction',
};

export function getSubtaskData(state: PermitApplicationState, index: number, statusKey: StatusKey) {
  const emissionPointCategory = (
    state.permit.monitoringApproaches.MEASUREMENT_CO2 as MeasurementOfCO2MonitoringApproach
  )?.emissionPointCategoryAppliedTiers?.[index] as categoryAppliedTier;

  const subtask = statusKeyToSubtaskNameMapper[statusKey];
  return emissionPointCategory?.[subtask];
}
