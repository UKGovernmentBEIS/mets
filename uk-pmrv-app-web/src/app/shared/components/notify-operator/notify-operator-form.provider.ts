import { FactoryProvider, InjectionToken } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

export const NOTIFY_OPERATOR_FORM = new InjectionToken<UntypedFormGroup>('Notify operator form');

export const notifyOperatorFormFactory: FactoryProvider = {
  provide: NOTIFY_OPERATOR_FORM,
  useFactory: (fb: UntypedFormBuilder) =>
    fb.group({
      users: [[], { updateOn: 'change' }],
      contacts: [[], { updateOn: 'change' }],
      assignees: [
        null,
        {
          validators: GovukValidators.required('Select a name to appear on the official notice document.'),
          updateOn: 'change',
        },
      ],
    }),
  deps: [UntypedFormBuilder],
};
