import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { BDR_TASK_FORM } from '@tasks/bdr/core/bdr-task-form.token';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { BDRApplicationRegulatorReviewSubmitRequestTaskPayload } from 'pmrv-api';

export const freeAllocationFormProvider = {
  provide: BDR_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, ActivatedRoute],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore) => {
    const state = store.getValue();
    const disabled = !state.isEditable;
    const statePayload = state.requestTaskItem.requestTask
      .payload as BDRApplicationRegulatorReviewSubmitRequestTaskPayload;
    const outcome = statePayload?.regulatorReviewOutcome;

    return fb.group({
      hasRegulatorSentFreeAllocation: [
        {
          value: outcome?.hasRegulatorSentFreeAllocation ?? null,
          disabled,
        },
        {
          updateOn: 'change',
          validators: [GovukValidators.required('Select your opinion on the free allocation application')],
        },
      ],
      freeAllocationNotesOperator: [
        {
          value: outcome?.freeAllocationNotesOperator ?? null,
          disabled,
        },
        {
          updateOn: 'change',
        },
      ],
      freeAllocationNotes: [
        {
          value: outcome?.freeAllocationNotes ?? null,
          disabled,
        },
        {
          updateOn: 'change',
        },
      ],
    });
  },
};
