import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { ALR_TASK_FORM } from '@tasks/alr/core';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { ALRApplicationSubmitRequestTaskPayload } from 'pmrv-api';

export const alrUploadReportFormProvider = {
  provide: ALR_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, RequestTaskFileService, ActivatedRoute],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore, requestTaskFileService: RequestTaskFileService) => {
    const state = store.getValue();
    const disabled = !state.isEditable;
    const statePayload = state.requestTaskItem.requestTask.payload as ALRApplicationSubmitRequestTaskPayload;
    const uploadReportFiles = statePayload.alr?.files;
    const alrAttachments = statePayload?.alrAttachments;
    const uploadAlrDocumentReport = statePayload.alr?.alrFile;

    return fb.group({
      alrFile: requestTaskFileService.buildFormControl(
        store.requestTaskId,
        uploadAlrDocumentReport ?? '',
        alrAttachments,
        'ALR_UPLOAD_ATTACHMENT',
        true,
        disabled,
      ),
      files: requestTaskFileService.buildFormControl(
        store.requestTaskId,
        uploadReportFiles ?? [],
        alrAttachments,
        'ALR_UPLOAD_ATTACHMENT',
        false,
        disabled,
      ),
    });
  },
};
