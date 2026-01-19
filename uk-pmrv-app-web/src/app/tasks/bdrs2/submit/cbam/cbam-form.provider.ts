import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { BDRS2_TASK_FORM } from '@tasks/bdrs2/core/bdrs2-task-form.token';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { BDRS2ApplicationSubmitRequestTaskPayload } from 'pmrv-api';

export const cbamFormProvider = {
  provide: BDRS2_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, ActivatedRoute],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore) => {
    const state = store.getValue();
    const disabled = !state.isEditable;
    const statePayload = state.requestTaskItem.requestTask.payload as BDRS2ApplicationSubmitRequestTaskPayload;
    const bdrs2 = statePayload?.bdrs2;

    return fb.group({
      requiresAdditionalSubInstallationSplitsForCbam: [
        { value: bdrs2?.bdrs2guardQuestions?.requiresAdditionalSubInstallationSplitsForCbam ?? null, disabled },
        {
          updateOn: 'change',
          validators: [GovukValidators.required('Select yes if additional sub-installation splits are required')],
        },
      ],
    });
  },
};
