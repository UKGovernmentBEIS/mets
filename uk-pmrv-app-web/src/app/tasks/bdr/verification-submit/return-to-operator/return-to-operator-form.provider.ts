import { UntypedFormBuilder } from '@angular/forms';

import { BDR_TASK_FORM } from '@tasks/bdr/core/bdr-task-form.token';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

export const returnToOperatorFormProvider = {
  provide: BDR_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore) => {
    const state = store.getValue();
    const disabled = !state.isEditable;

    return fb.group({
      changesRequired: [
        { value: null, disabled },
        {
          validators: [GovukValidators.required('Enter a comment')],
        },
      ],
    });
  },
};
