import { InjectionToken } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

export const AER_SKIP_REVIEW_FORM = new InjectionToken<UntypedFormGroup>('Aer skip review form');

export const aerSkipReviewFormProvider = {
  provide: AER_SKIP_REVIEW_FORM,
  deps: [UntypedFormBuilder],
  useFactory: (fb: UntypedFormBuilder) => {
    return fb.group({
      type: [{ value: null, disabled: false }, GovukValidators.required('Select an option')],
      reason: [
        { value: null, disabled: false },
        [GovukValidators.required('Enter a reason'), GovukValidators.maxLength(10000, 'Enter up to 10000 characters')],
      ],
    });
  },
};
