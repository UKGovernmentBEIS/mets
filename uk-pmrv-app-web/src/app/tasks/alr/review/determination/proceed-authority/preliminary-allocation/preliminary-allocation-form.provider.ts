import { UntypedFormBuilder } from '@angular/forms';

import { ALR_TASK_FORM } from '@tasks/alr/core';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { ALRApplicationRegulatorReviewSubmitRequestTaskPayload, DoalProceedToAuthorityDetermination } from 'pmrv-api';

export const alrPreliminaryAllocationFormProvider = {
  provide: ALR_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore) => {
    const state = store.getValue();
    const disabled = !state.isEditable;
    const determination = (
      state.requestTaskItem.requestTask.payload as ALRApplicationRegulatorReviewSubmitRequestTaskPayload
    )?.regulatorReviewOutcome?.determination as DoalProceedToAuthorityDetermination;

    return fb.group(
      {
        needsOfficialNotice: [
          {
            value: determination?.needsOfficialNotice ?? null,
            disabled,
          },
          {
            validators: [GovukValidators.required('Select yes or no')],
          },
        ],
      },
      {
        updateOn: 'change',
      },
    );
  },
};
