import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { AerApplicationVerificationSubmitRequestTaskPayload, VerifiedWithCommentsOverallAssessment } from 'pmrv-api';

export const reasonItemFormProvider = {
  provide: AER_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, ActivatedRoute],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore, route: ActivatedRoute) => {
    const state = store.getValue();
    const index = Number(route.snapshot.paramMap.get('index'));

    const overallAssessmentInfo = (
      state.requestTaskItem.requestTask.payload as AerApplicationVerificationSubmitRequestTaskPayload
    ).verificationReport.overallAssessment as VerifiedWithCommentsOverallAssessment;

    const item = index < overallAssessmentInfo.reasons?.length ? overallAssessmentInfo.reasons[index] : null;

    return fb.group({
      reason: [
        { value: item, disabled: !state.isEditable },
        {
          validators: [
            GovukValidators.required('Enter a reason'),
            GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
          ],
        },
      ],
    });
  },
};
