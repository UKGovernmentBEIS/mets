import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { BDRS2_TASK_FORM } from '@tasks/bdrs2/core';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload } from 'pmrv-api';

export const uploadBdrs2FilesFormProvider = {
  provide: BDRS2_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, RequestTaskFileService, ActivatedRoute],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore, requestTaskFileService: RequestTaskFileService) => {
    const state = store.getValue();
    const disabled = !state.isEditable;
    const statePayload = state.requestTaskItem.requestTask
      .payload as BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload;
    const outcome = statePayload?.regulatorReviewOutcome;
    const uploadOutcomeSupportingFiles = outcome?.supportingFiles;
    const uploadBdrDocumentReport = outcome?.file;
    const bdrAttachments = statePayload?.regulatorReviewAttachments;

    return fb.group({
      file: requestTaskFileService.buildFormControl(
        store.requestTaskId,
        uploadBdrDocumentReport ?? '',
        bdrAttachments,
        'BDRS2_UPLOAD_REGULATOR_REVIEW_GROUP_DECISION_ATTACHMENT',
        false,
        disabled,
      ),
      supportingFiles: requestTaskFileService.buildFormControl(
        store.requestTaskId,
        uploadOutcomeSupportingFiles ?? [],
        bdrAttachments,
        'BDRS2_UPLOAD_REGULATOR_REVIEW_GROUP_DECISION_ATTACHMENT',
        false,
        disabled,
      ),
    });
  },
};
