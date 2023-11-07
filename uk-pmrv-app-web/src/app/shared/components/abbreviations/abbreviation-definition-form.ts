import { UntypedFormControl, UntypedFormGroup } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { AbbreviationDefinition } from 'pmrv-api';

export function createAbbreviationDefinition(value?: AbbreviationDefinition): UntypedFormGroup {
  return new UntypedFormGroup({
    abbreviation: new UntypedFormControl(value?.abbreviation ?? null, [
      GovukValidators.required('Enter an abbreviation, acronym or term'),
      GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
    ]),
    definition: new UntypedFormControl(value?.definition ?? null, [
      GovukValidators.required('Enter a definition'),
      GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
    ]),
  });
}
