import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { VIR_TASK_FORM } from '@tasks/vir/core/vir-task-form.token';
import { maxLength, noRecommendation, noSelection } from '@tasks/vir/errors/validation-errors';

import { GovukValidators } from 'govuk-components';

import { VirApplicationRespondToRegulatorCommentsRequestTaskPayload } from 'pmrv-api';

export const operatorFollowupFormProvider = {
  provide: VIR_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, ActivatedRoute],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore, route: ActivatedRoute) => {
    const state = store.value;
    const disabled = !state.isEditable;
    const id = route.snapshot.paramMap.get('id');
    const operatorImprovementFollowUpResponse = (
      state.requestTaskItem.requestTask.payload as VirApplicationRespondToRegulatorCommentsRequestTaskPayload
    )?.operatorImprovementFollowUpResponses?.[id];

    return fb.group({
      improvementCompleted: [
        { value: operatorImprovementFollowUpResponse?.improvementCompleted ?? null, disabled },
        { validators: [GovukValidators.required(noSelection)] },
      ],
      dateCompleted: [
        {
          value: operatorImprovementFollowUpResponse?.dateCompleted
            ? new Date(operatorImprovementFollowUpResponse.dateCompleted)
            : null,
          disabled,
        },
      ],
      reason: [
        { value: operatorImprovementFollowUpResponse?.reason ?? null, disabled },
        { validators: [GovukValidators.required(noRecommendation), GovukValidators.maxLength(10000, maxLength)] },
      ],
    });
  },
};
