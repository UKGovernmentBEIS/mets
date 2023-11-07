import { UntypedFormBuilder } from '@angular/forms';

import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { AerApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

export const materialityLevelFormProvider = {
  provide: AER_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore) => {
    const state = store.getValue();

    const materialityLevelInfo = (
      state.requestTaskItem.requestTask.payload as AerApplicationVerificationSubmitRequestTaskPayload
    ).verificationReport.materialityLevel;

    return fb.group({
      materialityDetails: [
        { value: materialityLevelInfo?.materialityDetails ?? null, disabled: !state.isEditable },
        {
          validators: [
            GovukValidators.required(`Please give details about the materiality level`),
            GovukValidators.maxLength(
              10000,
              `The details about the materiality level should not be more than 10000 characters`,
            ),
          ],
        },
      ],
    });
  },
};
