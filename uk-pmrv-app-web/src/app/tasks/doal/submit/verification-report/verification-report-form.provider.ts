import { UntypedFormBuilder } from '@angular/forms';

import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { DoalApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { DOAL_TASK_FORM } from '../../core/doal-task-form.token';

export const verificationReportFormProvider = {
  provide: DOAL_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, RequestTaskFileService],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore, requestTaskFileService: RequestTaskFileService) => {
    const state = store.getValue();
    const disabled = !state.isEditable;

    const payload = state.requestTaskItem.requestTask.payload as DoalApplicationSubmitRequestTaskPayload;
    const verificationReportOfTheActivityLevelReport = (
      state.requestTaskItem.requestTask.payload as DoalApplicationSubmitRequestTaskPayload
    )?.doal?.verificationReportOfTheActivityLevelReport;

    return fb.group({
      document: requestTaskFileService.buildFormControl(
        store.requestTaskId,
        verificationReportOfTheActivityLevelReport?.document ?? null,
        payload?.doalAttachments,
        'DOAL_UPLOAD_ATTACHMENT',
        true,
        disabled,
      ),
      comment: [
        { value: verificationReportOfTheActivityLevelReport?.comment ?? null, disabled },
        {
          validators: [
            GovukValidators.required('Enter a comment'),
            GovukValidators.maxLength(10000, `Enter up to 10000 characters`),
          ],
        },
      ],
    });
  },
};
