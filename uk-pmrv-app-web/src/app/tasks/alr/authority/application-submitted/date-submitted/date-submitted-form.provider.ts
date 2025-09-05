import { UntypedFormBuilder } from '@angular/forms';

import { ALR_TASK_FORM } from '@tasks/alr/core';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { ALRAuthorityResponseSubmitRequestTaskPayload } from 'pmrv-api';

export const alrAuthorityDateSubmittedFormProvider = {
  provide: ALR_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore) => {
    const state = store.getValue();
    const disabled = !state.isEditable;
    const submissionDate = (state.requestTaskItem.requestTask.payload as ALRAuthorityResponseSubmitRequestTaskPayload)
      ?.authorityReviewOutcome?.submissionDate;

    return fb.group({
      submissionDate: [
        {
          value: submissionDate ? new Date(submissionDate) : null,
          disabled,
        },
        {
          updateOn: 'change',
        },
      ],
    });
  },
};
