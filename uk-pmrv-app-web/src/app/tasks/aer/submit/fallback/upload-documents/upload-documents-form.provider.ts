import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { AerApplicationSubmitRequestTaskPayload, FallbackEmissions } from 'pmrv-api';

export const uploadDocumentsFormProvider = {
  provide: AER_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, RequestTaskFileService, ActivatedRoute],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore, requestTaskFileService: RequestTaskFileService) => {
    const state = store.getValue();
    const disabled = !state.isEditable;
    const statePayload = state.requestTaskItem.requestTask.payload as AerApplicationSubmitRequestTaskPayload;
    const fallbackEmissions = statePayload.aer?.monitoringApproachEmissions?.FALLBACK as FallbackEmissions;
    const aerAttachments = statePayload?.aerAttachments;

    return fb.group({
      files: requestTaskFileService.buildFormControl(
        store.requestTaskId,
        fallbackEmissions?.files ?? [],
        aerAttachments,
        'AER_UPLOAD_SECTION_ATTACHMENT',
        true,
        disabled,
      ),
    });
  },
};
