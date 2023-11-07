import { FormBuilder } from '@angular/forms';

import { PERMIT_TRANSFER_A_FORM } from '@tasks/permit-transfer-a/core/permit-transfer-a-task-form.token';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { PermitTransferAApplicationRequestTaskPayload } from 'pmrv-api';

export const dateFormProvider = {
  provide: PERMIT_TRANSFER_A_FORM,
  deps: [FormBuilder, CommonTasksStore],
  useFactory: (fb: FormBuilder, store: CommonTasksStore) => {
    const state = store.getState();
    const payload = state.requestTaskItem.requestTask.payload as PermitTransferAApplicationRequestTaskPayload;

    const group = fb.group({
      transferDate: [payload.transferDate ? new Date(payload.transferDate) : null],
    });

    if (!state.isEditable) {
      group.disable();
    }
    return group;
  },
};
