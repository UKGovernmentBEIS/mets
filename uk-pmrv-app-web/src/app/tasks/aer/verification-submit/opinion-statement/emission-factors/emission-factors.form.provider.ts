import { UntypedFormBuilder } from '@angular/forms';

import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import {
  maxDescription,
  noEmissionFactorDescription,
} from '@tasks/aer/verification-submit/opinion-statement/errors/opinion-statement-validation.errors';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { AerApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

export const emissionFactorsFormProvider = {
  provide: AER_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore) => {
    const state = store.getValue();
    const opinionStatement = (
      state.requestTaskItem.requestTask.payload as AerApplicationVerificationSubmitRequestTaskPayload
    ).verificationReport.opinionStatement;

    return fb.group({
      emissionFactorsDescription: [
        { value: opinionStatement?.emissionFactorsDescription ?? null, disabled: !state.isEditable },
        {
          validators: [
            GovukValidators.required(noEmissionFactorDescription),
            GovukValidators.maxLength(10000, maxDescription),
          ],
        },
      ],
    });
  },
};
