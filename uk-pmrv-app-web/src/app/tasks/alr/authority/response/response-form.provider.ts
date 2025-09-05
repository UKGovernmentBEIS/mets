import { UntypedFormBuilder } from '@angular/forms';

import { ALR_TASK_FORM } from '@tasks/alr/core';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import {
  ALRAuthorityResponseSubmitRequestTaskPayload,
  ALRGrantAuthorityWithCorrectionsResponse,
  ALRRejectAuthorityResponse,
} from 'pmrv-api';

export const alrResponseFormProvider = {
  provide: ALR_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore) => {
    const state = store.getValue();

    const authorityResponse = (
      state.requestTaskItem.requestTask.payload as ALRAuthorityResponseSubmitRequestTaskPayload
    )?.authorityReviewOutcome?.authorityResponse;

    return fb.group({
      authorityRespondDate: [
        {
          value: authorityResponse?.authorityRespondDate ? new Date(authorityResponse.authorityRespondDate) : null,
          disabled: !state.isEditable,
        },
      ],
      type: [
        { value: authorityResponse?.type ?? null, disabled: !state.isEditable },
        { validators: GovukValidators.required('Select an option') },
      ],
      acceptedDecisionNotice: [
        {
          value: (authorityResponse as ALRGrantAuthorityWithCorrectionsResponse)?.decisionNotice ?? null,
          disabled: !state.isEditable,
        },
        {
          validators: [
            GovukValidators.required('Enter a comment'),
            GovukValidators.maxLength(10000, `Enter up to 10000 characters`),
          ],
        },
      ],
      rejectedDecisionNotice: [
        {
          value: (authorityResponse as ALRRejectAuthorityResponse)?.decisionNotice ?? null,
          disabled: !state.isEditable,
        },
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
