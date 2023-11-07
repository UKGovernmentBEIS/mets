import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import {
  maxLength,
  noNonConformityExplanation,
  noSelection,
} from '@tasks/aer/verification-submit/non-conformities/errors/non-conformities-validation.errors';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { AerApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

export const perPlanItemFormProvider = {
  provide: AER_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, ActivatedRoute],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore, route: ActivatedRoute) => {
    const state = store.getValue();
    const index = Number(route.snapshot.paramMap.get('index'));
    const nonConformities = (
      state.requestTaskItem.requestTask.payload as AerApplicationVerificationSubmitRequestTaskPayload
    ).verificationReport.uncorrectedNonConformities;
    const item =
      index < nonConformities.uncorrectedNonConformities?.length
        ? nonConformities.uncorrectedNonConformities[index]
        : null;

    return fb.group({
      explanation: [
        { value: item?.explanation ?? null, disabled: !state.isEditable },
        {
          validators: [
            GovukValidators.required(noNonConformityExplanation),
            GovukValidators.maxLength(10000, maxLength),
          ],
        },
      ],
      materialEffect: [
        { value: item?.materialEffect ?? null, disabled: !state.isEditable },
        { validators: [GovukValidators.required(noSelection)] },
      ],
    });
  },
};
