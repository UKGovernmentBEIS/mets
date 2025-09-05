import { UntypedFormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';

import { PreliminaryAllocation } from 'pmrv-api';

export const duplicatePreliminaryAllocationValidator = (
  preliminaryAllocations: PreliminaryAllocation[],
  index: number,
): ValidatorFn => {
  return (group: UntypedFormGroup): ValidationErrors => {
    const subInstallationName = group.get('subInstallationName').value;
    const year = group.get('year').value;

    return preliminaryAllocations?.some(
      (allocation, idx) =>
        allocation.subInstallationName === subInstallationName &&
        allocation.year === year &&
        (index === null || idx !== index),
    )
      ? { invalidForm: 'You have already added this year and sub-installation' }
      : null;
  };
};
