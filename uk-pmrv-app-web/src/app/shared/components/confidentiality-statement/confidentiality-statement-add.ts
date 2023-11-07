import { UntypedFormControl, UntypedFormGroup } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { ConfidentialSection } from 'pmrv-api';

export function createAnotherSection(value?: ConfidentialSection): UntypedFormGroup {
  return new UntypedFormGroup({
    section: new UntypedFormControl(value?.section ?? null, [
      GovukValidators.required('Enter a section'),
      GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
    ]),
    explanation: new UntypedFormControl(value?.explanation ?? null, [
      GovukValidators.required('Enter an explanation'),
      GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
    ]),
  });
}
