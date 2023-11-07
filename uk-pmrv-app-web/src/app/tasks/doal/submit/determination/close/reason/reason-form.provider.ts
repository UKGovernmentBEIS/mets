import { UntypedFormBuilder } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { DoalApplicationSubmitRequestTaskPayload, DoalClosedDetermination } from 'pmrv-api';

import { CommonTasksStore } from '../../../../../store/common-tasks.store';
import { DOAL_TASK_FORM } from '../../../../core/doal-task-form.token';

export const reasonFormProvider = {
  provide: DOAL_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore) => {
    const state = store.getValue();
    const disabled = !state.isEditable;

    const closeDetermination = (state.requestTaskItem.requestTask.payload as DoalApplicationSubmitRequestTaskPayload)
      ?.doal?.determination as DoalClosedDetermination;

    return fb.group({
      reason: [
        {
          value: closeDetermination?.reason ?? null,
          disabled,
        },
        {
          validators: [
            GovukValidators.required('Enter a reason to support your decision'),
            GovukValidators.maxLength(10000, `Enter up to 10000 characters`),
          ],
        },
      ],
    });
  },
};
