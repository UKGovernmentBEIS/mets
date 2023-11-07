import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { VIR_TASK_FORM } from '@tasks/vir/core/vir-task-form.token';
import { maxLength, noRecommendation, noSelection } from '@tasks/vir/errors/validation-errors';

import { GovukValidators } from 'govuk-components';

import { VirApplicationSubmitRequestTaskPayload } from 'pmrv-api';

export const recommendationResponseFormProvider = {
  provide: VIR_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, ActivatedRoute],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore, route: ActivatedRoute) => {
    const state = store.value;
    const disabled = !state.isEditable;
    const id = route.snapshot.paramMap.get('id');
    const operatorImprovementResponse = (
      state.requestTaskItem.requestTask.payload as VirApplicationSubmitRequestTaskPayload
    )?.operatorImprovementResponses?.[id];

    const positiveDescription =
      operatorImprovementResponse?.isAddressed === true && operatorImprovementResponse?.addressedDescription
        ? operatorImprovementResponse?.addressedDescription
        : null;

    const negativeDescription =
      operatorImprovementResponse?.isAddressed === false && operatorImprovementResponse?.addressedDescription
        ? operatorImprovementResponse?.addressedDescription
        : null;

    return fb.group({
      isAddressed: [
        { value: operatorImprovementResponse?.isAddressed ?? null, disabled },
        { validators: [GovukValidators.required(noSelection)] },
      ],
      addressedDescription: [
        { value: positiveDescription, disabled },
        { validators: [GovukValidators.required(noRecommendation), GovukValidators.maxLength(10000, maxLength)] },
      ],
      addressedDescription2: [
        { value: negativeDescription, disabled },
        { validators: [GovukValidators.required(noRecommendation), GovukValidators.maxLength(10000, maxLength)] },
      ],
      addressedDate: [
        {
          value: operatorImprovementResponse?.addressedDate
            ? new Date(operatorImprovementResponse.addressedDate)
            : null,
          disabled,
        },
      ],
    });
  },
};
