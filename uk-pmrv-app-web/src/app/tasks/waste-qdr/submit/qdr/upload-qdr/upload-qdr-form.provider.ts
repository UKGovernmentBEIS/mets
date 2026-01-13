import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { WASTE_QDR_TASK_FORM } from '@tasks/waste-qdr/core';

import { WasteQDRApplicationSubmitRequestTaskPayload } from 'pmrv-api';

export const wasteQdrUploadReportFormProvider = {
  provide: WASTE_QDR_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, RequestTaskFileService, ActivatedRoute],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore, requestTaskFileService: RequestTaskFileService) => {
    const state = store.getValue();
    const disabled = !state.isEditable;
    const statePayload = state.requestTaskItem.requestTask.payload as WasteQDRApplicationSubmitRequestTaskPayload;
    const uploadReport = statePayload.qdr?.report;
    const uploadSupportingFiles = statePayload.qdr?.supportingFiles;
    const wasteQDRAttachments = statePayload?.wasteQDRAttachments;

    return fb.group({
      report: requestTaskFileService.buildFormControl(
        store.requestTaskId,
        uploadReport ?? '',
        wasteQDRAttachments,
        'WASTE_QDR_UPLOAD_ATTACHMENT',
        true,
        disabled,
      ),
      supportingFiles: requestTaskFileService.buildFormControl(
        store.requestTaskId,
        uploadSupportingFiles ?? [],
        wasteQDRAttachments,
        'WASTE_QDR_UPLOAD_ATTACHMENT',
        false,
        disabled,
      ),
      notes: [{ value: statePayload.qdr?.notes ?? null, disabled }],
    });
  },
};
