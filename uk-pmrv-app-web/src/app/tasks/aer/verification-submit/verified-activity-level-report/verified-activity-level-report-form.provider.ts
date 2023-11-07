import { UntypedFormBuilder } from '@angular/forms';

import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { noSelection } from '@tasks/aer/verification-submit/verified-activity-level-report/errors/verified-activity-level-report.errors';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { AerApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

export const verifiedActivityLevelReportFormProvider = {
  provide: AER_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, RequestTaskFileService],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore, requestTaskFileService: RequestTaskFileService) => {
    const state = store.getValue();
    const disabled = !state.isEditable;
    const statePayload = state.requestTaskItem.requestTask
      .payload as AerApplicationVerificationSubmitRequestTaskPayload;
    const activityLevelReport = statePayload.verificationReport?.activityLevelReport;
    const verificationAttachments = statePayload?.verificationAttachments;

    return fb.group({
      freeAllocationOfAllowances: [
        { value: activityLevelReport?.freeAllocationOfAllowances ?? null, disabled },
        { validators: GovukValidators.required(noSelection), updateOn: 'change' },
      ],
      file: requestTaskFileService.buildFormControl(
        store.requestTaskId,
        activityLevelReport?.file ?? null,
        verificationAttachments,
        'AER_VERIFICATION_UPLOAD_ATTACHMENT',
        true,
        disabled,
      ),
    });
  },
};
