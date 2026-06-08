import { UntypedFormBuilder } from '@angular/forms';

import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { NER_TASK_FORM } from '@tasks/ner/core';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { NERApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

export const nerOpinionStatementFormProvider = {
  provide: NER_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, RequestTaskFileService],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore, requestTaskFileService: RequestTaskFileService) => {
    const state = store.getValue();
    const disabled = !state.isEditable;
    const {
      verificationAttachments,
      verificationReport: { opinionStatement: { opinionStatementFile, supportingFiles, notes } = {} } = {},
    } = state.requestTaskItem.requestTask.payload as NERApplicationVerificationSubmitRequestTaskPayload;

    return fb.group({
      opinionStatementFile: requestTaskFileService.buildFormControl(
        store.requestTaskId,
        opinionStatementFile ?? '',
        verificationAttachments,
        'NER_VERIFICATION_UPLOAD_ATTACHMENT',
        true,
        disabled,
      ),
      supportingFiles: requestTaskFileService.buildFormControl(
        store.requestTaskId,
        supportingFiles ?? [],
        verificationAttachments,
        'NER_VERIFICATION_UPLOAD_ATTACHMENT',
        false,
        disabled,
      ),
      notes: [{ value: notes ?? null, disabled }],
    });
  },
};
