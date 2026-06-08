import { UntypedFormBuilder } from '@angular/forms';

import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { NER_TASK_FORM } from '@tasks/ner/core';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { NERApplicationRegulatorReviewSubmitRequestTaskPayload } from 'pmrv-api';

export const nerReviewUploadNerFormProvider = {
  provide: NER_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, RequestTaskFileService],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore, requestTaskFileService: RequestTaskFileService) => {
    const state = store.getValue();
    const disabled = !state.isEditable;
    const statePayload = state.requestTaskItem.requestTask
      .payload as NERApplicationRegulatorReviewSubmitRequestTaskPayload;
    const { nerFile, supportingFiles } = statePayload.regulatorReviewOutcome || {};
    const reviewAttachments = statePayload?.regulatorReviewAttachments;

    return fb.group({
      nerFile: requestTaskFileService.buildFormControl(
        store.requestTaskId,
        nerFile ?? '',
        reviewAttachments,
        'NER_UPLOAD_REVIEW_GROUP_DECISION_ATTACHMENT',
        false,
        disabled,
      ),
      supportingFiles: requestTaskFileService.buildFormControl(
        store.requestTaskId,
        supportingFiles ?? [],
        reviewAttachments,
        'NER_UPLOAD_REVIEW_GROUP_DECISION_ATTACHMENT',
        false,
        disabled,
      ),
    });
  },
};
