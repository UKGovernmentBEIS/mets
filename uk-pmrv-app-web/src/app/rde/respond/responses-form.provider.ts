import { InjectionToken } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { RdeStore } from '../store/rde.store';

export const RDE_FORM = new InjectionToken<UntypedFormGroup>('Rde form');

export const responseFormProvider = {
  provide: RDE_FORM,
  deps: [UntypedFormBuilder, RdeStore],
  useFactory: (fb: UntypedFormBuilder, store: RdeStore) => {
    const state = store.getState();

    return fb.group({
      decision: [{ value: null, disabled: !state.isEditable }, GovukValidators.required('Select a decision')],
      reason: [
        { value: null, disabled: !state.isEditable },
        [GovukValidators.required('Enter a reason'), GovukValidators.maxLength(10000, 'Enter up to 10000 characters')],
      ],
    });
  },
};
