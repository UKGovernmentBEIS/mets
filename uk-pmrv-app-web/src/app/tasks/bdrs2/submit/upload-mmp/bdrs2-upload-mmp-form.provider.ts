import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { BDRS2_TASK_FORM } from '@tasks/bdrs2/core';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { BDRS2ApplicationSubmitRequestTaskPayload } from 'pmrv-api';

export const bdrs2UploadMmpFormProvider = {
  provide: BDRS2_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, RequestTaskFileService, ActivatedRoute],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore, requestTaskFileService: RequestTaskFileService) => {
    const state = store.getValue();
    const disabled = !state.isEditable;
    const statePayload = state.requestTaskItem.requestTask.payload as BDRS2ApplicationSubmitRequestTaskPayload;
    const uploadMmpFiles = statePayload.bdrs2?.mmpFiles?.supportingFiles;
    const bdrs2Attachments = statePayload?.bdrs2Attachments;
    const uploadMmpFile = statePayload.bdrs2?.mmpFiles?.file;

    return fb.group({
      mmpFile: requestTaskFileService.buildFormControl(
        store.requestTaskId,
        uploadMmpFile ?? '',
        bdrs2Attachments,
        'BDRS2_UPLOAD_ATTACHMENT',
        true,
        disabled,
      ),
      files: requestTaskFileService.buildFormControl(
        store.requestTaskId,
        uploadMmpFiles ?? [],
        bdrs2Attachments,
        'BDRS2_UPLOAD_ATTACHMENT',
        false,
        disabled,
      ),
    });
  },
};
