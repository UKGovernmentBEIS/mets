import { UntypedFormBuilder } from '@angular/forms';

import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { AerApplicationVerificationSubmitRequestTaskPayload, NotVerifiedOverallAssessment } from 'pmrv-api';

export const notVerifiedFormProvider = {
  provide: AER_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore) => {
    const state = store.getValue();

    const overallAssessmentInfo = (
      state.requestTaskItem.requestTask.payload as AerApplicationVerificationSubmitRequestTaskPayload
    ).verificationReport.overallAssessment as NotVerifiedOverallAssessment;
    const reasons = overallAssessmentInfo.notVerifiedReasons?.map((reason) => reason.type) ?? [];
    const otherReason = overallAssessmentInfo.notVerifiedReasons?.find(
      (reason) => reason?.type === 'ANOTHER_REASON',
    ) ?? { otherReason: null };

    return fb.group({
      type: [
        { value: reasons, disabled: !state.isEditable },
        { validators: [GovukValidators.required('Select a reason')] },
      ],
      otherReason: [
        { value: otherReason.otherReason, disabled: !state.isEditable },
        [GovukValidators.required('Enter a reason'), GovukValidators.maxLength(10000, 'Enter up to 10000 characters')],
      ],
    });
  },
};
