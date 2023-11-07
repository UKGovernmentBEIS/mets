import { UntypedFormBuilder } from '@angular/forms';

import { DRE_TASK_FORM } from '@tasks/dre/core/dre-task-form.token';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { DreApplicationSubmitRequestTaskPayload } from 'pmrv-api';

export const officialNoticeReasonFormProvider = {
  provide: DRE_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore) => {
    const state = store.getValue();
    const disabled = !state.isEditable;

    const officialNoticeReason = (state.requestTaskItem.requestTask.payload as DreApplicationSubmitRequestTaskPayload)
      ?.dre?.officialNoticeReason;

    return fb.group({
      officialNoticeReason: [
        { value: officialNoticeReason ?? null, disabled },
        [
          GovukValidators.required('Provide a reason that will be included in the official notice.'),
          GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
        ],
      ],
    });
  },
};
