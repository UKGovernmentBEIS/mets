import { FormBuilder } from '@angular/forms';

import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

export const energyCrfCodeFormProvider = {
  provide: AER_TASK_FORM,
  deps: [FormBuilder, CommonTasksStore],
  useFactory: (fb: FormBuilder, store: CommonTasksStore) => {
    const state = store.getState();
    const group = fb.group(
      {
        energyCrfCategory: [null, GovukValidators.required('You must select at least one energy type')],
        energyCrf: [null, GovukValidators.required('You must select at least one energy type')],
      },
      {
        updateOn: 'change',
      },
    );
    if (!state.isEditable) {
      group.disable();
    }
    return group;
  },
};
