import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { BDR_TASK_FORM } from '@tasks/bdr/core/bdr-task-form.token';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { BDRApplicationSubmitRequestTaskPayload } from 'pmrv-api';

export const uploadReportFormProvider = {
  provide: BDR_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, RequestTaskFileService, ActivatedRoute],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore, requestTaskFileService: RequestTaskFileService) => {
    const state = store.getValue();
    const disabled = !state.isEditable;
    const statePayload = state.requestTaskItem.requestTask.payload as BDRApplicationSubmitRequestTaskPayload;
    const uploadReportFiles = statePayload.bdr?.files;
    const bdrAttachments = statePayload?.bdrAttachments;
    const uploadBdrDocumentReport = statePayload.bdr?.bdrFile;

    return fb.group({
      bdrFile: requestTaskFileService.buildFormControl(
        store.requestTaskId,
        uploadBdrDocumentReport ?? '',
        bdrAttachments,
        'BDR_UPLOAD_ATTACHMENT',
        true,
        disabled,
      ),
      files: requestTaskFileService.buildFormControl(
        store.requestTaskId,
        uploadReportFiles ?? [],
        bdrAttachments,
        'BDR_UPLOAD_ATTACHMENT',
        false,
        disabled,
      ),
    });
  },
};
