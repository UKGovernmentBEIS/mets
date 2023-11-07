import { FormBuilder } from '@angular/forms';

import { RequestTaskFileService } from '@shared/services/request-task-file-service/request-task-file.service';
import { PERMIT_TRANSFER_A_FORM } from '@tasks/permit-transfer-a/core/permit-transfer-a-task-form.token';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { PermitTransferAApplicationRequestTaskPayload } from 'pmrv-api';

export const reasonFormProvider = {
  provide: PERMIT_TRANSFER_A_FORM,
  deps: [FormBuilder, CommonTasksStore, RequestTaskFileService],
  useFactory: (fb: FormBuilder, store: CommonTasksStore, requestTaskFileService: RequestTaskFileService) => {
    const state = store.getState();
    const payload = state.requestTaskItem.requestTask.payload as PermitTransferAApplicationRequestTaskPayload;

    const group = fb.group({
      reason: [
        payload.reason ?? null,
        [GovukValidators.required('Enter a reason'), GovukValidators.maxLength(10000, 'Enter up to 10000 characters')],
      ],
      reasonAttachments: requestTaskFileService.buildFormControl(
        store.requestTaskId,
        payload.reasonAttachments ?? [],
        payload.transferAttachments,
        'PERMIT_TRANSFER_A_UPLOAD_SECTION_ATTACHMENT',
        false,
        !state.isEditable,
      ),
    });

    if (!state.isEditable) {
      group.disable();
    }
    return group;
  },
};
