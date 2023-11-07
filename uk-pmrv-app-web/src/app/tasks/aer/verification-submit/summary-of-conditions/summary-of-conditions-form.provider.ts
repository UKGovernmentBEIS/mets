import { UntypedFormBuilder } from '@angular/forms';

import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { AerApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

export const summaryOfConditionsFormProvider = {
  provide: AER_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore) => {
    const state = store.getValue();

    const summaryOfConditions = (
      state.requestTaskItem.requestTask.payload as AerApplicationVerificationSubmitRequestTaskPayload
    ).verificationReport.summaryOfConditions;

    return fb.group({
      changesNotIncludedInPermit: [
        { value: summaryOfConditions?.changesNotIncludedInPermit ?? null, disabled: !state.isEditable },
        { validators: [GovukValidators.required('Please select yes or no')] },
      ],
    });
  },
};
