import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { VIR_TASK_FORM } from '@tasks/vir/core/vir-task-form.token';
import { noSelection } from '@tasks/vir/errors/validation-errors';

import { GovukValidators } from 'govuk-components';

import { VirApplicationSubmitRequestTaskPayload } from 'pmrv-api';

export const uploadEvidenceQuestionFormProvider = {
  provide: VIR_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, ActivatedRoute],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore, route: ActivatedRoute) => {
    const state = store.value;
    const disabled = !state.isEditable;
    const id = route.snapshot.paramMap.get('id');
    const operatorImprovementResponse = (
      state.requestTaskItem.requestTask.payload as VirApplicationSubmitRequestTaskPayload
    )?.operatorImprovementResponses?.[id];

    return fb.group({
      uploadEvidence: [
        { value: operatorImprovementResponse?.uploadEvidence ?? null, disabled },
        { validators: [GovukValidators.required(noSelection)] },
      ],
    });
  },
};
