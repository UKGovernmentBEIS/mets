import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

export const activityFormProvider = {
  provide: AER_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, ActivatedRoute],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore) => {
    const state = store.getValue();

    const sectorError = 'Enter the relevant sector';

    const formGroup = fb.group({
      activity: [
        { value: null, disabled: !state.isEditable },
        { validators: [GovukValidators.required(sectorError)], updateOn: 'change' },
      ],
    });

    return formGroup;
  },
};
