import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import {
  maxLength,
  noRecommendedImprovementsExplanation,
} from '@tasks/aer/verification-submit/recommended-improvements/errors/recommended-improvements-validation.errors';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { AerApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

export const recommendedImprovementsItemFormProvider = {
  provide: AER_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, ActivatedRoute],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore, route: ActivatedRoute) => {
    const state = store.getValue();
    const index = Number(route.snapshot.paramMap.get('index'));
    const recommendedImprovements = (
      state.requestTaskItem.requestTask.payload as AerApplicationVerificationSubmitRequestTaskPayload
    ).verificationReport?.recommendedImprovements;
    const item =
      index < recommendedImprovements?.recommendedImprovements?.length
        ? recommendedImprovements?.recommendedImprovements[index]
        : null;

    return fb.group({
      explanation: [
        { value: item?.explanation ?? null, disabled: !state.isEditable },
        {
          validators: [
            GovukValidators.required(noRecommendedImprovementsExplanation),
            GovukValidators.maxLength(10000, maxLength),
          ],
        },
      ],
    });
  },
};
