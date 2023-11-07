import { UntypedFormBuilder } from '@angular/forms';

import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { noSelection } from '@tasks/aer/verification-submit/non-conformities/errors/non-conformities-validation.errors';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { AerApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

export const perPlanFormProvider = {
  provide: AER_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore) => {
    const state = store.getValue();

    const uncorrectedNonConformities = (
      state.requestTaskItem.requestTask.payload as AerApplicationVerificationSubmitRequestTaskPayload
    ).verificationReport.uncorrectedNonConformities;

    return fb.group({
      areThereUncorrectedNonConformities: [
        { value: uncorrectedNonConformities?.areThereUncorrectedNonConformities ?? null, disabled: !state.isEditable },
        { validators: [GovukValidators.required(noSelection)] },
      ],
    });
  },
};
