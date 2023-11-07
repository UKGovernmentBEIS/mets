import { UntypedFormBuilder } from '@angular/forms';

import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { AerApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

export const misstatementsFormProvider = {
  provide: AER_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore) => {
    const state = store.getValue();

    const uncorrectedMisstatementsInfo = (
      state.requestTaskItem.requestTask.payload as AerApplicationVerificationSubmitRequestTaskPayload
    ).verificationReport.uncorrectedMisstatements;

    return fb.group({
      areThereUncorrectedMisstatements: [
        { value: uncorrectedMisstatementsInfo?.areThereUncorrectedMisstatements ?? null, disabled: !state.isEditable },
        { validators: [GovukValidators.required('Please select yes or no')] },
      ],
    });
  },
};
