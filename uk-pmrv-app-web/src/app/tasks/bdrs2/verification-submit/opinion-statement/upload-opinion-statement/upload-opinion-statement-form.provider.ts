import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { BDRS2_TASK_FORM } from '@tasks/bdrs2/core/bdrs2-task-form.token';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { BDRS2ApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

export const opinionStatementFormProvider = {
  provide: BDRS2_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, RequestTaskFileService, ActivatedRoute],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore, requestTaskFileService: RequestTaskFileService) => {
    const state = store.getValue();
    const disabled = !state.isEditable;
    const statePayload = state.requestTaskItem.requestTask
      .payload as BDRS2ApplicationVerificationSubmitRequestTaskPayload;
    const provideOpinionStatementFiles = statePayload.verificationReport?.opinionStatement?.opinionStatementFiles;
    const verificationAttachments = statePayload?.verificationAttachments;

    return fb.group({
      opinionStatementFiles: requestTaskFileService.buildFormControl(
        store.requestTaskId,
        provideOpinionStatementFiles ?? [],
        verificationAttachments,
        'BDRS2_VERIFICATION_UPLOAD_ATTACHMENT',
        true,
        disabled,
      ),
      notes: [
        {
          value: statePayload?.verificationReport?.opinionStatement?.notes ?? null,
          disabled,
        },
      ],
    });
  },
};
