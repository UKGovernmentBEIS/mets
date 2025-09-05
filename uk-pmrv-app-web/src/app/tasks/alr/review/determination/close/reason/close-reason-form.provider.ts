import { UntypedFormBuilder } from '@angular/forms';

import { ALR_TASK_FORM } from '@tasks/alr/core';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { ALRApplicationRegulatorReviewSubmitRequestTaskPayload, ALRClosedDetermination } from 'pmrv-api';

export const alrReasonFormProvider = {
  provide: ALR_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore) => {
    const state = store.getValue();
    const disabled = !state.isEditable;

    const closeDetermination = (
      state.requestTaskItem.requestTask.payload as ALRApplicationRegulatorReviewSubmitRequestTaskPayload
    )?.regulatorReviewOutcome?.determination as ALRClosedDetermination;

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
