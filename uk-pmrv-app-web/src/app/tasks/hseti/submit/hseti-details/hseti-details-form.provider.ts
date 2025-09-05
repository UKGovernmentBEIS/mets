import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { HSE_TI_TASK_FORM } from '@tasks/hseti/core';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { HSETIApplicationSubmitRequestTaskPayload } from 'pmrv-api';

export const hseTiUploadReportFormProvider = {
  provide: HSE_TI_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, RequestTaskFileService, ActivatedRoute],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore, requestTaskFileService: RequestTaskFileService) => {
    const state = store.getValue();
    const disabled = !state.isEditable;
    const statePayload = state.requestTaskItem.requestTask.payload as HSETIApplicationSubmitRequestTaskPayload;
    const uploadReportFiles = statePayload.hseti?.files;
    const hseTiAttachments = statePayload?.hsetiAttachments;
    const uploadΗseTiDocumentReport = statePayload.hseti?.hsetiFile;

    return fb.group({
      hseTiFile: requestTaskFileService.buildFormControl(
        store.requestTaskId,
        uploadΗseTiDocumentReport ?? '',
        hseTiAttachments,
        'HSE_TI_UPLOAD_ATTACHMENT',
        true,
        disabled,
      ),
      files: requestTaskFileService.buildFormControl(
        store.requestTaskId,
        uploadReportFiles ?? [],
        hseTiAttachments,
        'HSE_TI_UPLOAD_ATTACHMENT',
        false,
        disabled,
      ),
      notes: [
        {
          value: statePayload?.hseti?.notes ?? null,
          disabled,
        },
        { validators: [GovukValidators.maxLength(10000, 'Enter up to 10000 characters')] },
      ],
    });
  },
};
