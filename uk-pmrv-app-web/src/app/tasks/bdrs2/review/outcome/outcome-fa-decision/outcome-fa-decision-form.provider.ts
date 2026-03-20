import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { BDRS2_TASK_FORM } from '@tasks/bdrs2/core';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload } from 'pmrv-api';

export const bdrs2FreeAllocationFormProvider = {
  provide: BDRS2_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, ActivatedRoute],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore) => {
    const state = store.getValue();
    const disabled = !state.isEditable;
    const statePayload = state.requestTaskItem.requestTask
      .payload as BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload;
    const outcome = statePayload?.regulatorReviewOutcome;

    return fb.group({
      freeAllocationOpinion: [
        {
          value: outcome?.freeAllocationOpinion ?? null,
          disabled,
        },
        {
          updateOn: 'change',
          validators: [GovukValidators.required('Select your opinion on the free allocation application')],
        },
      ],

      operatorNotes: [
        {
          value: outcome?.freeAllocationReviewNotes?.operatorNotes ?? null,
          disabled,
        },
        {
          updateOn: 'change',
        },
      ],
      internalNotes: [
        {
          value: outcome?.freeAllocationReviewNotes?.internalNotes ?? null,
          disabled,
        },
        {
          updateOn: 'change',
        },
      ],
    });
  },
};
