import { UntypedFormBuilder } from '@angular/forms';

import { NER_TASK_FORM } from '@tasks/ner/core';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { NERApplicationRegulatorReviewSubmitRequestTaskPayload } from 'pmrv-api';

export const nerReviewOutcomeFormProvider = {
  provide: NER_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore) => {
    const state = store.getValue();
    const disabled = !state.isEditable;
    const statePayload = state.requestTaskItem.requestTask
      .payload as NERApplicationRegulatorReviewSubmitRequestTaskPayload;
    const outcome = statePayload?.regulatorReviewOutcome;

    return fb.group(
      {
        opinion: [
          {
            value: outcome?.opinion ?? null,
            disabled,
          },
          {
            validators: [GovukValidators.required('Select your opinion on the new entrant reserve application')],
          },
        ],
        notes: [
          {
            value: outcome?.notes ?? null,
            disabled,
          },
        ],
      },
      {
        updateOn: 'change',
      },
    );
  },
};
