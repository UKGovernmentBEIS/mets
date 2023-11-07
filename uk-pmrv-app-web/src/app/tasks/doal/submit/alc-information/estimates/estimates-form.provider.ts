import { UntypedFormBuilder } from '@angular/forms';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { DoalApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { DOAL_TASK_FORM } from '../../../core/doal-task-form.token';

export const estimatesFormProvider = {
  provide: DOAL_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore) => {
    const state = store.getValue();
    const disabled = !state.isEditable;

    const alc = (state.requestTaskItem.requestTask.payload as DoalApplicationSubmitRequestTaskPayload)?.doal
      ?.activityLevelChangeInformation;

    return fb.group({
      areConservativeEstimates: [
        { value: alc?.areConservativeEstimates ?? null, disabled },
        { validators: GovukValidators.required('Select yes or no') },
      ],
      explainEstimates: [
        { value: alc?.explainEstimates ?? null, disabled },
        {
          validators: [
            GovukValidators.required('Enter a comment'),
            GovukValidators.maxLength(10000, `Enter up to 10000 characters`),
          ],
        },
      ],
    });
  },
};
