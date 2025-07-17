import { InjectionToken } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

export const AER_NOT_REQUIRE_FORM = new InjectionToken<UntypedFormGroup>('Aer not required form');

export const aerReasonProvider = {
  provide: AER_NOT_REQUIRE_FORM,
  deps: [UntypedFormBuilder],
  useFactory: (fb: UntypedFormBuilder) => {
    return fb.group({
      reason: [
        { value: '', disabled: false },
        {
          validators: [
            GovukValidators.required('Enter reason'),
            GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
          ],
          updateOn: 'change',
        },
      ],
    });
  },
};
