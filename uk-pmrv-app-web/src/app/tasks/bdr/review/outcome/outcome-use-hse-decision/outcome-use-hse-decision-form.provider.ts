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
      hasRegulatorSentHSE: [
        {
          value: outcome?.hasRegulatorSentHSE ?? null,
          disabled,
        },
        {
          updateOn: 'change',
          validators: [GovukValidators.required('Select your opinion on the hospital and small emitter application')],
        },
      ],
      hasRegulatorSentUSE: [
        {
          value: outcome?.hasRegulatorSentUSE ?? null,
          disabled,
        },
        {
          updateOn: 'change',
          validators: [GovukValidators.required('Select your opinion on the ultra small emitter application')],
        },
      ],
      useHseNotesOperator: [
        {
          value: outcome?.useHseNotesOperator ?? null,
          disabled,
        },
        {
          updateOn: 'change',
        },
      ],
      useHseNotes: [
        {
          value: outcome?.useHseNotes ?? null,
          disabled,
        },
        {
          updateOn: 'change',
        },
      ],
    });
  },
};
