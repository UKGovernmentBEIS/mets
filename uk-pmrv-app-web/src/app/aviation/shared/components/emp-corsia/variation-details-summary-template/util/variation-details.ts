import { RequestTaskDTO } from 'pmrv-api';

export interface EmpCorsiaVariationModification {
  type:
    | 'EMISSION_FACTOR_VALUES'
    | 'FUEL_USE_MONITORING_METHODOLOGY'
    | 'FUMM_TO_CERT'
    | 'NEW_FUEL_TYPE'
    | 'STATUS_FOR_THRESHOLDS'
    | 'PARENT_SUBSIDIARY_RELATIONSHIP'
    | 'AVAILABILITY_OF_DATA'
    | 'INFORMATION_FOUND_INCORRECT'
    | 'RECOMMENDATIONS_IN_VERIFICATION_IMPROVEMENT_REPORT'
    | 'AEROPLANE_OPERATOR_NAME'
    | 'REGISTERED_OR_CONTACT_ADDRESS'
    | 'OTHER';
}

export function isReasonWizardRequired(taskType: RequestTaskDTO['type']): boolean {
  return taskType === 'EMP_VARIATION_CORSIA_REGULATOR_LED_APPLICATION_SUBMIT';
}

export const materialChanges: { [key in EmpCorsiaVariationModification['type']]?: string } = {
  EMISSION_FACTOR_VALUES: 'Changing the emission factor values',
  FUEL_USE_MONITORING_METHODOLOGY: 'Changing your fuel use monitoring methodology (FUMM)',
  FUMM_TO_CERT: 'Changing from using a FUMM to the CERT, or vice versa',
  NEW_FUEL_TYPE: 'Introducing a new fuel type',
  STATUS_FOR_THRESHOLDS:
    'Changing your status for one of the thresholds specified in article 22(7) to (12) of the Air Navigation Order',
  PARENT_SUBSIDIARY_RELATIONSHIP:
    'Changing your parent-subsidiary relationship where you are to be considered a single aeroplane operator for the purposes of the Order',
};

export const otherChanges: { [key in EmpCorsiaVariationModification['type']]?: string } = {
  AVAILABILITY_OF_DATA:
    'Changing the availability of your data, due to the use of new types of measuring instrument, sampling methods or analysis methods, or other reasons, which leads to higher accuracy in the determination of emissions',
  INFORMATION_FOUND_INCORRECT:
    'Changing information that has been found to be incorrect under the data monitoring methodology previously applied',
  RECOMMENDATIONS_IN_VERIFICATION_IMPROVEMENT_REPORT:
    'Changes to account for recommendations in a verification improvement report',
};

export const nonMaterialChanges: { [key in EmpCorsiaVariationModification['type']]?: string } = {
  AEROPLANE_OPERATOR_NAME: 'Changing your aeroplane operator name',
  REGISTERED_OR_CONTACT_ADDRESS: 'Changing your registered or contact address',
  OTHER: 'Other non-material changes',
};
