import { UntypedFormBuilder } from '@angular/forms';

import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { maxDescription, noDescription } from '@tasks/aer/submit/fallback/errors/fallback-validation.errors';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { AerApplicationSubmitRequestTaskPayload, FallbackEmissions } from 'pmrv-api';

export const descriptionFormProvider = {
  provide: AER_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore) => {
    const state = store.getValue();
    const fallbackEmissions = (state.requestTaskItem.requestTask.payload as AerApplicationSubmitRequestTaskPayload).aer
      ?.monitoringApproachEmissions?.FALLBACK as FallbackEmissions;

    return fb.group({
      description: [
        { value: fallbackEmissions?.description ?? null, disabled: !state.isEditable },
        { validators: [GovukValidators.required(noDescription), GovukValidators.maxLength(10000, maxDescription)] },
      ],
    });
  },
};
