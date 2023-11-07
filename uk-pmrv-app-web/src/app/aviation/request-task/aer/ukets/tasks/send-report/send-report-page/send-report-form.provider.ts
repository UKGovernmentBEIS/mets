import { UntypedFormBuilder } from '@angular/forms';

import { AER_TASK_FORM } from '@aviation/request-task/aer/ukets/tasks/send-report/aer-submit-token';
import { RequestTaskStore } from '@aviation/request-task/store';

import { GovukValidators } from 'govuk-components';

export const sendReportFormProvider = {
  provide: AER_TASK_FORM,
  deps: [UntypedFormBuilder, RequestTaskStore],
  useFactory: (fb: UntypedFormBuilder, store: RequestTaskStore) => {
    const state = store.getValue();

    return fb.group({
      option: [
        { value: null, disabled: !state.isEditable },
        { validators: GovukValidators.required('Select yes or no'), updateOn: 'change' },
      ],
    });
  },
};
