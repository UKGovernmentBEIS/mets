import { UntypedFormBuilder } from '@angular/forms';

import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import {
  maxDescription,
  noMonitoringApproachDescription,
} from '@tasks/aer/verification-submit/opinion-statement/errors/opinion-statement-validation.errors';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { AerApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

export const monitoringApproachesFormProvider = {
  provide: AER_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore) => {
    const state = store.getValue();
    const opinionStatement = (
      state.requestTaskItem.requestTask.payload as AerApplicationVerificationSubmitRequestTaskPayload
    ).verificationReport.opinionStatement;

    return fb.group({
      monitoringApproachDescription: [
        { value: opinionStatement?.monitoringApproachDescription ?? null, disabled: !state.isEditable },
        {
          validators: [
            GovukValidators.required(noMonitoringApproachDescription),
            GovukValidators.maxLength(10000, maxDescription),
          ],
        },
      ],
    });
  },
};
