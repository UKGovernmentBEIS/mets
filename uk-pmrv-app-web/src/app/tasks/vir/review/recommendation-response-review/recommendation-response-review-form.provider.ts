import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { VIR_TASK_FORM } from '@tasks/vir/core/vir-task-form.token';
import { maxLength, noComment, noSelection } from '@tasks/vir/errors/validation-errors';

import { GovukValidators } from 'govuk-components';

import { VirApplicationReviewRequestTaskPayload } from 'pmrv-api';

export const recommendationResponseReviewFormProvider = {
  provide: VIR_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, ActivatedRoute],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore, route: ActivatedRoute) => {
    const state = store.value;
    const disabled = !state.isEditable;
    const id = route.snapshot.paramMap.get('id');
    const regulatorImprovementResponse = (
      state.requestTaskItem.requestTask.payload as VirApplicationReviewRequestTaskPayload
    )?.regulatorReviewResponse?.regulatorImprovementResponses?.[id];

    return fb.group({
      improvementRequired: [
        { value: regulatorImprovementResponse?.improvementRequired ?? null, disabled },
        { validators: [GovukValidators.required(noSelection)] },
      ],
      improvementDeadline: [
        {
          value: regulatorImprovementResponse?.improvementDeadline
            ? new Date(regulatorImprovementResponse.improvementDeadline)
            : null,
          disabled,
        },
      ],
      improvementComments: [
        { value: regulatorImprovementResponse?.improvementComments ?? null, disabled },
        { validators: [GovukValidators.maxLength(10000, maxLength)] },
      ],
      operatorActions: [
        { value: regulatorImprovementResponse?.operatorActions ?? null, disabled },
        { validators: [GovukValidators.required(noComment), GovukValidators.maxLength(10000, maxLength)] },
      ],
    });
  },
};
