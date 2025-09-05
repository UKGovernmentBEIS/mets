import { UntypedFormBuilder } from '@angular/forms';

import { ALR_TASK_FORM } from '@tasks/alr/core';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { ALRApplicationRegulatorReviewSubmitRequestTaskPayload, DoalProceedToAuthorityDetermination } from 'pmrv-api';

export const alrProceedAuthorityReasonFormProvider = {
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
        articleReasonGroupType: [
          {
            value: determination?.articleReasonGroupType ?? null,
            disabled,
          },
          {
            validators: [GovukValidators.required('Select an option')],
          },
        ],
        article6aReasons: [
          {
            value: determination?.articleReasonItems ?? null,
            disabled,
          },
          {
            validators: [GovukValidators.required('Select an option')],
          },
        ],
        article34HReasonItems: [
          {
            value: determination?.articleReasonItems ?? null,
            disabled,
          },
          {
            validators: [GovukValidators.required('Select an option')],
          },
        ],
        reason: [
          { value: determination?.reason ?? null, disabled },
          {
            validators: [
              GovukValidators.required('Enter a comment'),
              GovukValidators.maxLength(10000, `Enter up to 10000 characters`),
            ],
          },
        ],
      },
      {
        updateOn: 'change',
      },
    );
  },
};
