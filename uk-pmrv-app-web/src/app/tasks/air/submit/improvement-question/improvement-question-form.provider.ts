import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { AIR_TASK_FORM } from '@tasks/air/shared/air-task-form.token';
import { noSelection } from '@tasks/air/shared/errors/validation-errors';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { AirApplicationSubmitRequestTaskPayload } from 'pmrv-api';

export const improvementQuestionFormProvider = {
  provide: AIR_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, ActivatedRoute],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore, route: ActivatedRoute) => {
    const state = store.value;
    const disabled = !state.isEditable;
    const id = route.snapshot.paramMap.get('id');
    const operatorImprovementResponse = (
      state.requestTaskItem.requestTask.payload as AirApplicationSubmitRequestTaskPayload
    )?.operatorImprovementResponses?.[id];

    return fb.group({
      type: [
        { value: operatorImprovementResponse?.type ?? null, disabled },
        { validators: [GovukValidators.required(noSelection)] },
      ],
    });
  },
};
