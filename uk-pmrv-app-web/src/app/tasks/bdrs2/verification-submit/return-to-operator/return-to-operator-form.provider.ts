import { UntypedFormBuilder } from '@angular/forms';

import { BDRS2_TASK_FORM } from '@tasks/bdrs2/core/bdrs2-task-form.token';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

export const returnToOperatorFormProvider = {
  provide: BDRS2_TASK_FORM,
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
