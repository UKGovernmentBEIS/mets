import { FormBuilder } from '@angular/forms';

import { PERMIT_TRANSFER_A_FORM } from '@tasks/permit-transfer-a/core/permit-transfer-a-task-form.token';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { PermitTransferAApplicationRequestTaskPayload } from 'pmrv-api';

export const activityLevelReportFormProvider = {
  provide: PERMIT_TRANSFER_A_FORM,
  deps: [FormBuilder, CommonTasksStore],
  useFactory: (fb: FormBuilder, store: CommonTasksStore) => {
    const state = store.getState();
    const payload = state.requestTaskItem.requestTask.payload as PermitTransferAApplicationRequestTaskPayload;

    const group = fb.group({
      alrLiable: [
        payload.alrLiable ?? null,
        [GovukValidators.required('Select the operator who will complete the activity level report')],
      ],
    });

    if (!state.isEditable) {
      group.disable();
    }
    return group;
  },
};
