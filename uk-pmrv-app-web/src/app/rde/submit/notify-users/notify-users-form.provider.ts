import { FactoryProvider, InjectionToken } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { RdeStore } from '../../store/rde.store';

export const RDE_FORM = new InjectionToken<UntypedFormGroup>('Rde form');

export const notifyUsersFormFactory: FactoryProvider = {
  provide: RDE_FORM,
  deps: [UntypedFormBuilder, RdeStore],
  useFactory: (fb: UntypedFormBuilder, store: RdeStore) => {
    const state = store.getState();

    return fb.group({
      users: [state?.rdePayload?.operators ?? null, { updateOn: 'change' }],
      assignees: [
        state?.rdePayload?.signatory ?? null,
        GovukValidators.required('Select a name to appear on the official notice document.'),
      ],
    });
  },
};
