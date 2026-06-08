import { UntypedFormBuilder } from '@angular/forms';

import { TASKS_RETURN_TO_OPERATOR_FORM } from '@tasks/shared/core';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

export const tasksReturnToOperatorFormProvider = {
  provide: TASKS_RETURN_TO_OPERATOR_FORM,
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
