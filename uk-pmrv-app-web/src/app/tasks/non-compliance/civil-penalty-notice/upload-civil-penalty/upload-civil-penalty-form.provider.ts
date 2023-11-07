import { UntypedFormBuilder } from '@angular/forms';

import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { NonComplianceCivilPenaltyRequestTaskPayload } from 'pmrv-api';

import { NON_COMPLIANCE_TASK_FORM } from '../../core/non-compliance-form.token';

export const uploadCivilPenaltyFormProvider = {
  provide: NON_COMPLIANCE_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, RequestTaskFileService],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore, requestTaskFileService: RequestTaskFileService) => {
    const state = store.getValue();
    const disabled = !state.isEditable;
    const statePayload = state.requestTaskItem.requestTask.payload as NonComplianceCivilPenaltyRequestTaskPayload;

    return fb.group({
      civilPenalty: requestTaskFileService.buildFormControl(
        store.requestTaskId,
        statePayload?.civilPenalty ?? null,
        statePayload?.nonComplianceAttachments,
        'NON_COMPLIANCE_UPLOAD_ATTACHMENT',
        true,
        disabled,
      ),

      penaltyAmount: [
        { value: statePayload?.penaltyAmount ?? null, disabled },
        GovukValidators.maxLength(255, 'Enter up to 255 characters'),
      ],

      dueDate: [
        {
          value: statePayload?.dueDate ? new Date(statePayload.dueDate) : null,
          disabled,
        },
      ],

      comments: [
        { value: statePayload?.comments ?? null, disabled },
        {
          validators: [GovukValidators.maxLength(10000, `Enter up to 10000 characters`)],
        },
      ],
    });
  },
};
