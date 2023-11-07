import { RequestTaskDTO } from 'pmrv-api';

export interface EmpUKEtsVariationModification {
  type:
    | 'EMISSION_FACTOR_VALUES'
    | 'METHOD_USED_TO_CALCULATE_EMISSIONS_FACTOR'
    | 'DIFFERENT_FUMMS'
    | 'FUMM_TO_ESTIMATION_METHOD'
    | 'NEW_FUEL_TYPE'
    | 'SMALL_EMITTER_STATUS_OR_EMISSIONS_THRESHOLD_STATUS'
    | 'LEGAL_ENTITY_NAME'
    | 'REGISTERED_OFFICE_ADDRESS'
    | 'OTHER';
}

export const significantChanges: { [key in EmpUKEtsVariationModification['type']]?: string } = {
  EMISSION_FACTOR_VALUES: 'changing the emission factor values',
  METHOD_USED_TO_CALCULATE_EMISSIONS_FACTOR: 'changing the methodology you use to calculate an emissions factor',
  DIFFERENT_FUMMS: 'changing between different fuel use monitoring methodologies (FUMMs)',
  FUMM_TO_ESTIMATION_METHOD: 'changing from using FUMM to an estimation methodology, or vice versa',
  NEW_FUEL_TYPE: 'introducing a new fuel type',
  SMALL_EMITTER_STATUS_OR_EMISSIONS_THRESHOLD_STATUS:
    'changing either your small emitter status, or the status of one of the annual emissions thresholds that allows you to apply simplified reporting',
};

export const nonSignificantChanges: { [key in EmpUKEtsVariationModification['type']]?: string } = {
  LEGAL_ENTITY_NAME: 'changing the name of your legal entity',
  REGISTERED_OFFICE_ADDRESS: 'changing your registered office address',
  OTHER: 'other changes',
};

export function isReasonWizardRequired(taskType: RequestTaskDTO['type']): boolean {
  return taskType === 'EMP_VARIATION_UKETS_REGULATOR_LED_APPLICATION_SUBMIT';
}
