import { UntypedFormBuilder } from '@angular/forms';

import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { DOAL_TASK_FORM } from '@tasks/doal/core/doal-task-form.token';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { DoalAuthorityResponseRequestTaskPayload, DoalGrantAuthorityResponse } from 'pmrv-api';

export const approvedAllocationsComponentFormProvider = {
  provide: DOAL_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, RequestTaskFileService],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore, requestTaskFileService: RequestTaskFileService) => {
    const state = store.getValue();
    const disabled = !state.isEditable;

    const payload = state.requestTaskItem.requestTask.payload as DoalAuthorityResponseRequestTaskPayload;
    const documents = (payload?.doalAuthority?.authorityResponse as DoalGrantAuthorityResponse)?.documents;

    return fb.group({
      documents: requestTaskFileService.buildFormControl(
        store.requestTaskId,
        documents ?? [],
        payload?.doalAttachments,
        'DOAL_AUTHORITY_RESPONSE_UPLOAD_ATTACHMENT',
        false,
        disabled,
      ),
    });
  },
};
