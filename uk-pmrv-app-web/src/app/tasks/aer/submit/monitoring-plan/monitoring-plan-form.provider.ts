import { UntypedFormBuilder } from '@angular/forms';

import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { AerApplicationSubmitRequestTaskPayload } from 'pmrv-api';

export const monitoringPlanFormFactory = {
  provide: AER_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore) => {
    const state = store.getValue();

    const item = (state.requestTaskItem.requestTask.payload as AerApplicationSubmitRequestTaskPayload).aer
      ?.aerMonitoringPlanDeviation;

    return fb.group({
      existChangesNotCoveredInApprovedVariations: [
        { value: item?.existChangesNotCoveredInApprovedVariations ?? null, disabled: !store.getValue().isEditable },
        { validators: GovukValidators.required('Select yes or no'), updateOn: 'change' },
      ],
      details: [
        { value: item?.details ?? null, disabled: !store.getValue().isEditable },
        { validators: GovukValidators.required('Enter details'), updateOn: 'change' },
      ],
    });
  },
};
