import { UntypedFormGroup, ValidatorFn } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

export function requiredFieldsValidator(): ValidatorFn {
  return GovukValidators.builder('You must fill all required values', (group: UntypedFormGroup) =>
    Object.keys(group.controls).find((key) => group.controls[key].hasError('required'))
      ? { emptyRequiredFields: true }
      : null,
  );
}

export function atLeastOneRequiredValidator(message: string): ValidatorFn {
  return GovukValidators.builder(message, (group: UntypedFormGroup) =>
    Object.keys(group.controls).find((key) => !!group.controls[key].value) ? null : { atLeastOneRequired: true },
  );
}

export function atLeastOneRequiredNestedValidator(message: string): ValidatorFn {
  return GovukValidators.builder(message, (group: UntypedFormGroup) => {
    // Check if at least one nested form group has a control with a non-empty value
    const hasValue = Object.values(group?.controls ?? {}).some((nestedGroup: UntypedFormGroup) =>
      Object.values(nestedGroup?.controls ?? {}).some((control) => !!control.value),
    );

    return hasValue ? null : { atLeastOneRequired: true };
  });
}
