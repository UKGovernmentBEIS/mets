import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { BDR_TASK_FORM } from '@tasks/bdr/core/bdr-task-form.token';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { BDRApplicationRegulatorReviewSubmitRequestTaskPayload } from 'pmrv-api';

export const QuestionFormProvider = {
  provide: BDR_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, ActivatedRoute],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore) => {
    const state = store.getValue();
    const disabled = !state.isEditable;
    const statePayload = state.requestTaskItem.requestTask
      .payload as BDRApplicationRegulatorReviewSubmitRequestTaskPayload;
    const outcome = statePayload?.regulatorReviewOutcome;

    return fb.group({
      hasOperatorMetDataSubmissionRequirements: [
        {
          value: outcome?.hasOperatorMetDataSubmissionRequirements
            ? [outcome?.hasOperatorMetDataSubmissionRequirements]
            : null,
          disabled,
        },
        {
          updateOn: 'change',
          validators: [
            GovukValidators.required(
              'Select if the operator has met the data submission requirements and this information has been sent to the UK ETS authority',
            ),
          ],
        },
      ],
    });
  },
};
