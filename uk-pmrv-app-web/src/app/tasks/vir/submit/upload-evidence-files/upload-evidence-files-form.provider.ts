import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { VIR_TASK_FORM } from '@tasks/vir/core/vir-task-form.token';

import { VirApplicationSubmitRequestTaskPayload } from 'pmrv-api';

export const uploadEvidenceFilesFormProvider = {
  provide: VIR_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, RequestTaskFileService, ActivatedRoute],
  useFactory: (
    fb: UntypedFormBuilder,
    store: CommonTasksStore,
    requestTaskFileService: RequestTaskFileService,
    route: ActivatedRoute,
  ) => {
    const state = store.value;
    const disabled = !state.isEditable;
    const id = route.snapshot.paramMap.get('id');
    const statePayload = state.requestTaskItem.requestTask.payload as VirApplicationSubmitRequestTaskPayload;
    const operatorImprovementResponse = statePayload?.operatorImprovementResponses?.[id];
    const virAttachments = statePayload?.virAttachments;

    return fb.group({
      files: requestTaskFileService.buildFormControl(
        store.requestTaskId,
        operatorImprovementResponse?.files ?? [],
        virAttachments,
        'VIR_UPLOAD_ATTACHMENT',
        true,
        disabled,
      ),
    });
  },
};
