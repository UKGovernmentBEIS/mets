import { UntypedFormBuilder } from '@angular/forms';

import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { NER_TASK_FORM } from '@tasks/ner/core';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { NerApplicationSubmitRequestTaskPayload } from 'pmrv-api';

export const nerUploadNerFormProvider = {
  provide: NER_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, RequestTaskFileService],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore, requestTaskFileService: RequestTaskFileService) => {
    const state = store.getValue();
    const disabled = !state.isEditable;
    const statePayload = state.requestTaskItem.requestTask.payload as NerApplicationSubmitRequestTaskPayload;
    const { nerFiles: { file, supportingFiles } = {}, notes } = statePayload.ner || {};
    const nerAttachments = statePayload?.nerAttachments;

    return fb.group({
      file: requestTaskFileService.buildFormControl(
        store.requestTaskId,
        file ?? '',
        nerAttachments,
        'NER_UPLOAD_ATTACHMENT',
        true,
        disabled,
      ),
      supportingFiles: requestTaskFileService.buildFormControl(
        store.requestTaskId,
        supportingFiles ?? [],
        nerAttachments,
        'NER_UPLOAD_ATTACHMENT',
        false,
        disabled,
      ),
      notes: [{ value: notes ?? null, disabled }],
    });
  },
};
