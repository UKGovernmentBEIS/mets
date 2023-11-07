import { UntypedFormGroup, ValidatorFn } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

/**
 * Validates that at least one of the monitoring approaches 'certDetails' | 'fuelUseMonitoringDetails' exist
 */
export const atLeastOneMonitoringDetailsValidator = (): ValidatorFn => {
  return GovukValidators.builder('At least one monitoring approach method required', (group: UntypedFormGroup) => {
    const certDetails = group.controls.certDetails;
    const fuelUseMonitoringDetails = group.controls.fuelUseMonitoringDetails;

    return certDetails?.valid || fuelUseMonitoringDetails?.valid
      ? null
      : { atLeastOneMonitoringApproachRequired: true };
  });
};
