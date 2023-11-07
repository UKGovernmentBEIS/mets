import { UntypedFormBuilder } from '@angular/forms';

import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { NonComplianceDailyPenaltyNoticeRequestTaskPayload } from 'pmrv-api';

import { NON_COMPLIANCE_TASK_FORM } from '../../core/non-compliance-form.token';

export const uploadInitialNoticeFormProvider = {
  provide: NON_COMPLIANCE_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, RequestTaskFileService],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore, requestTaskFileService: RequestTaskFileService) => {
    const state = store.getValue();
    const disabled = !state.isEditable;
    const statePayload = state.requestTaskItem.requestTask.payload as NonComplianceDailyPenaltyNoticeRequestTaskPayload;

    return fb.group({
      dailyPenaltyNotice: requestTaskFileService.buildFormControl(
        store.requestTaskId,
        statePayload?.dailyPenaltyNotice ?? null,
        statePayload?.nonComplianceAttachments,
        'NON_COMPLIANCE_UPLOAD_ATTACHMENT',
        true,
        disabled,
      ),

      comments: [
        { value: statePayload?.comments ?? null, disabled },
        {
          validators: [GovukValidators.maxLength(10000, `Enter up to 10000 characters`)],
        },
      ],
    });
  },
};
