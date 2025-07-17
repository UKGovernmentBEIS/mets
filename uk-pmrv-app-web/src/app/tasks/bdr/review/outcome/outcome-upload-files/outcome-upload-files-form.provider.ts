import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { BDR_TASK_FORM } from '@tasks/bdr/core/bdr-task-form.token';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { BDRApplicationRegulatorReviewSubmitRequestTaskPayload } from 'pmrv-api';

export const uploadFilesFormProvider = {
  provide: BDR_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, RequestTaskFileService, ActivatedRoute],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore, requestTaskFileService: RequestTaskFileService) => {
    const state = store.getValue();
    const disabled = !state.isEditable;
    const statePayload = state.requestTaskItem.requestTask
      .payload as BDRApplicationRegulatorReviewSubmitRequestTaskPayload;
    const outcome = statePayload?.regulatorReviewOutcome;
    const uploadOutcomeReportFiles = outcome?.files;
    const uploadBdrDocumentReport = outcome?.bdrFile;
    const bdrAttachments = statePayload?.regulatorReviewAttachments;

    return fb.group({
      bdrFile: requestTaskFileService.buildFormControl(
        store.requestTaskId,
        uploadBdrDocumentReport ?? '',
        bdrAttachments,
        'BDR_UPLOAD_REGULATOR_REVIEW_GROUP_DECISION_ATTACHMENT',
        false,
        disabled,
      ),
      files: requestTaskFileService.buildFormControl(
        store.requestTaskId,
        uploadOutcomeReportFiles ?? [],
        bdrAttachments,
        'BDR_UPLOAD_REGULATOR_REVIEW_GROUP_DECISION_ATTACHMENT',
        false,
        disabled,
      ),
    });
  },
};
