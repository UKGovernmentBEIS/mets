import { UntypedFormBuilder } from '@angular/forms';

import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import {
  maxReason,
  noReason,
  noSelection,
} from '@tasks/aer/verification-submit/compliance-ets/errors/compliance-ets-validation.errors';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { AerApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

export const dataVerificationFormProvider = {
  provide: AER_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore) => {
    const state = store.getValue();

    const etsComplianceRules = (
      state.requestTaskItem.requestTask.payload as AerApplicationVerificationSubmitRequestTaskPayload
    ).verificationReport.etsComplianceRules;

    return fb.group({
      dataVerificationCompleted: [
        { value: etsComplianceRules?.dataVerificationCompleted ?? null, disabled: !state.isEditable },
        { validators: [GovukValidators.required(noSelection)] },
      ],
      dataVerificationNotCompletedReason: [
        { value: etsComplianceRules?.dataVerificationNotCompletedReason ?? null, disabled: !state.isEditable },
        {
          validators: [GovukValidators.required(noReason), GovukValidators.maxLength(10000, maxReason)],
        },
      ],
    });
  },
};
