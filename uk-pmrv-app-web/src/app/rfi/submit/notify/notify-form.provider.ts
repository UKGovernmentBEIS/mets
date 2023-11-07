import { FactoryProvider, InjectionToken } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { RfiStore } from '../../store/rfi.store';

export const NOTIFY_FORM = new InjectionToken<UntypedFormGroup>('Notify form');

export const notifyFormFactory: FactoryProvider = {
  provide: NOTIFY_FORM,
  deps: [UntypedFormBuilder, RfiStore],
  useFactory: (fb: UntypedFormBuilder, rfiStore: RfiStore) => {
    const operators = rfiStore.getValue().rfiSubmitPayload?.operators;
    const signatory = rfiStore.getValue().rfiSubmitPayload?.signatory;

    return fb.group({
      users: [operators, { updateOn: 'change' }],
      assignee: [signatory, GovukValidators.required('Select a name to appear on the official notice document.')],
    });
  },
};
