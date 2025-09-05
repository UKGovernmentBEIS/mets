import { UntypedFormBuilder } from '@angular/forms';

import { ALR_TASK_FORM } from '@tasks/alr/core';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { ALRApplicationRegulatorReviewSubmitRequestTaskPayload } from 'pmrv-api';

export const alrCommentsFormProvider = {
  provide: ALR_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore) => {
    const state = store.getValue();
    const disabled = !state.isEditable;

    const alc = (state.requestTaskItem.requestTask.payload as ALRApplicationRegulatorReviewSubmitRequestTaskPayload)
      ?.regulatorReviewOutcome;

    return fb.group({
      ukEtsAuthorityComments: [
        { value: alc?.ukEtsAuthorityComments ?? null, disabled },
        {
          validators: [GovukValidators.maxLength(10000, `Enter up to 10000 characters`)],
        },
      ],
    });
  },
};
