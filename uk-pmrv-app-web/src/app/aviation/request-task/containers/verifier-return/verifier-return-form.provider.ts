import { InjectionToken } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

export const AER_VERIFIER_RETURN_FORM = new InjectionToken<UntypedFormGroup>(
  'Aer aviation return to operator for changes form',
);

export const aerVerifierReturnProvider = {
  provide: AER_VERIFIER_RETURN_FORM,
  deps: [UntypedFormBuilder],
  useFactory: (fb: UntypedFormBuilder) => {
    return fb.group({
      changesRequired: [
        { value: '', disabled: false },
        {
          validators: [GovukValidators.required('Enter the changes required')],
          updateOn: 'change',
        },
      ],
    });
  },
};
