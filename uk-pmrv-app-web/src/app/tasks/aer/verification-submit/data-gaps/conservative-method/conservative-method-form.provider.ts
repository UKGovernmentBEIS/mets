import { UntypedFormBuilder } from '@angular/forms';

import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { AerApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

export const conservativeMethodFormProvider = {
  provide: AER_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore) => {
    const state = store.getValue();

    const dataGapApprovedDetailsInfo = (
      state.requestTaskItem.requestTask.payload as AerApplicationVerificationSubmitRequestTaskPayload
    ).verificationReport.methodologiesToCloseDataGaps?.dataGapRequiredDetails?.dataGapApprovedDetails;

    return fb.group({
      conservativeMethodUsed: [
        { value: dataGapApprovedDetailsInfo?.conservativeMethodUsed ?? null, disabled: !state.isEditable },
        { validators: [GovukValidators.required('Please select yes or no')] },
      ],
      methodDetails: [
        { value: dataGapApprovedDetailsInfo?.methodDetails ?? null, disabled: !state.isEditable },
        {
          validators: [
            GovukValidators.required('Please give more detail about why the method you used was not conservative'),
            GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
          ],
        },
      ],
    });
  },
};
