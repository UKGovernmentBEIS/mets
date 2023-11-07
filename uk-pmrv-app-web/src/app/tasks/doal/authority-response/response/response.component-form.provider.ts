import { UntypedFormBuilder } from '@angular/forms';

import { DOAL_TASK_FORM } from '@tasks/doal/core/doal-task-form.token';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import {
  DoalAuthorityResponseRequestTaskPayload,
  DoalGrantAuthorityWithCorrectionsResponse,
  DoalRejectAuthorityResponse,
} from 'pmrv-api';

export const responseComponentFormProvider = {
  provide: DOAL_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore) => {
    const state = store.getValue();

    const authorityResponse = (state.requestTaskItem.requestTask.payload as DoalAuthorityResponseRequestTaskPayload)
      ?.doalAuthority?.authorityResponse;

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
          value: (authorityResponse as DoalGrantAuthorityWithCorrectionsResponse)?.decisionNotice ?? null,
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
          value: (authorityResponse as DoalRejectAuthorityResponse)?.decisionNotice ?? null,
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
