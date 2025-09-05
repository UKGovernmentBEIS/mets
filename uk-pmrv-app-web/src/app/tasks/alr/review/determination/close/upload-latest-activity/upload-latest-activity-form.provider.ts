import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { ALR_TASK_FORM } from '@tasks/alr/core';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { ALRApplicationRegulatorReviewSubmitRequestTaskPayload, ALRProceedToAuthorityDetermination } from 'pmrv-api';

export const alrUploadLatestActivityFormProvider = {
  provide: ALR_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, RequestTaskFileService, ActivatedRoute],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore, requestTaskFileService: RequestTaskFileService) => {
    const state = store.getValue();
    const disabled = !state.isEditable;
    const statePayload = state.requestTaskItem.requestTask
      .payload as ALRApplicationRegulatorReviewSubmitRequestTaskPayload;
    const determination = statePayload.regulatorReviewOutcome?.determination as ALRProceedToAuthorityDetermination;
    const uploadDocumentReport = determination?.alrFile || statePayload.alr?.alrFile;
    const uploadReportFiles = determination?.files || statePayload.alr?.files;
    const attachments = { ...statePayload?.alrAttachments, ...statePayload.regulatorReviewAttachments };

    return fb.group({
      alrFile: requestTaskFileService.buildFormControl(
        store.requestTaskId,
        uploadDocumentReport ?? '',
        attachments,
        'ALR_UPLOAD_REGULATOR_REVIEW_GROUP_DECISION_ATTACHMENT',
        true,
        disabled,
      ),
      files: requestTaskFileService.buildFormControl(
        store.requestTaskId,
        uploadReportFiles ?? [],
        attachments,
        'ALR_UPLOAD_REGULATOR_REVIEW_GROUP_DECISION_ATTACHMENT',
        false,
        disabled,
      ),
    });
  },
};
