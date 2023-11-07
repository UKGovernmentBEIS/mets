import { FormBuilder } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { PERMIT_TASK_FORM } from '../../permit-application/shared/permit-task-form.token';
import { PermitVariationStore } from '../store/permit-variation.store';

export const aboutVariationFormProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [FormBuilder, PermitVariationStore],
  useFactory: (fb: FormBuilder, store: PermitVariationStore) => {
    const state = store.getState();
    const permitVariationDetails = state?.permitVariationDetails;

    return fb.group({
      reason: [
        permitVariationDetails?.reason ?? null,
        [
          GovukValidators.required('Enter details of the change'),
          GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
        ],
      ],
    });
  },
};
