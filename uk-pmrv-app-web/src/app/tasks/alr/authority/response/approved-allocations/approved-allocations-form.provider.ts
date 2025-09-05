import { UntypedFormBuilder } from '@angular/forms';

import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { ALR_TASK_FORM } from '@tasks/alr/core';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { ALRAuthorityResponseSubmitRequestTaskPayload, ALRGrantAuthorityResponse } from 'pmrv-api';

export const alrApprovedAllocationsFormProvider = {
  provide: ALR_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, RequestTaskFileService],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore, requestTaskFileService: RequestTaskFileService) => {
    const state = store.getValue();
    const disabled = !state.isEditable;

    const payload = state.requestTaskItem.requestTask.payload as ALRAuthorityResponseSubmitRequestTaskPayload;
    const documents = (payload?.authorityReviewOutcome?.authorityResponse as ALRGrantAuthorityResponse)?.documents;

    return fb.group({
      documents: requestTaskFileService.buildFormControl(
        store.requestTaskId,
        documents ?? [],
        payload?.alrAttachments,
        'ALR_AUTHORITY_RESPONSE_UPLOAD_ATTACHMENT',
        false,
        disabled,
      ),
    });
  },
};
