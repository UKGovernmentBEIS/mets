import { UntypedFormBuilder } from '@angular/forms';

import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { noSelection } from '@tasks/aer/submit/activity-level-report/errors/activity-level-report.errors';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { AerApplicationSubmitRequestTaskPayload } from 'pmrv-api';

export const activityLevelReportFormProvider = {
  provide: AER_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, RequestTaskFileService],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore, requestTaskFileService: RequestTaskFileService) => {
    const state = store.getValue();
    const disabled = !state.isEditable;
    const statePayload = state.requestTaskItem.requestTask.payload as AerApplicationSubmitRequestTaskPayload;
    const activityLevelReport = statePayload.aer?.activityLevelReport;
    const aerAttachments = statePayload?.aerAttachments;

    return fb.group({
      freeAllocationOfAllowances: [
        { value: activityLevelReport?.freeAllocationOfAllowances ?? null, disabled },
        { validators: GovukValidators.required(noSelection), updateOn: 'change' },
      ],
      file: requestTaskFileService.buildFormControl(
        store.requestTaskId,
        activityLevelReport?.file ?? null,
        aerAttachments,
        'AER_UPLOAD_SECTION_ATTACHMENT',
        true,
        disabled,
      ),
    });
  },
};
