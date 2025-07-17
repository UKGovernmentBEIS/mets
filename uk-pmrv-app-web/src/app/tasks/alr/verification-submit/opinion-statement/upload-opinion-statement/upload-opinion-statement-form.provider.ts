import { UntypedFormBuilder } from '@angular/forms';

import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { ALR_TASK_FORM } from '@tasks/alr/core';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { ALRApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

export const alrOpinionStatementFormProvider = {
  provide: ALR_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, RequestTaskFileService],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore, requestTaskFileService: RequestTaskFileService) => {
    const state = store.getValue();
    const disabled = !state.isEditable;
    const {
      verificationAttachments,
      verificationReport: { opinionStatement: { opinionStatementFiles, supportingFiles, notes } = {} } = {},
    } = state.requestTaskItem.requestTask.payload as ALRApplicationVerificationSubmitRequestTaskPayload;

    return fb.group({
      opinionStatementFiles: requestTaskFileService.buildFormControl(
        store.requestTaskId,
        opinionStatementFiles ?? [],
        verificationAttachments,
        'ALR_VERIFICATION_UPLOAD_ATTACHMENT',
        true,
        disabled,
      ),
      supportingFiles: requestTaskFileService.buildFormControl(
        store.requestTaskId,
        supportingFiles ?? [],
        verificationAttachments,
        'ALR_VERIFICATION_UPLOAD_ATTACHMENT',
        false,
        disabled,
      ),
      notes: [{ value: notes ?? null, disabled }],
    });
  },
};
