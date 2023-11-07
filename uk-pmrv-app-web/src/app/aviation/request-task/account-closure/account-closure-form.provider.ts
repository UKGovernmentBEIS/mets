import { Provider } from '@angular/core';
import { FormBuilder, FormControl } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { AccountClosureTaskPayload, RequestTaskStore } from '../store';
import { TASK_FORM_PROVIDER } from '../task-form.provider';

export interface AccountClosureFormModel {
  reason: FormControl<string>;
}

export const accountClosureFormProvider: Provider = {
  provide: TASK_FORM_PROVIDER,
  deps: [FormBuilder, RequestTaskStore],
  useFactory: (fb: FormBuilder, store: RequestTaskStore) => {
    const state = store.getState();
    const payload = state.requestTaskItem?.requestTask?.payload as AccountClosureTaskPayload;
    const reason = payload?.aviationAccountClosure?.reason;

    return fb.group({
      reason: [
        reason,
        {
          validators: [
            GovukValidators.required('You must say why you are closing this account'),
            GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
          ],
          updateOn: 'change',
        },
      ],
    });
  },
};
