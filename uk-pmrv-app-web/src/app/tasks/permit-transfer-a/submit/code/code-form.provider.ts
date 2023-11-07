import { FormBuilder, UntypedFormGroup, ValidationErrors } from '@angular/forms';

import { PERMIT_TRANSFER_A_FORM } from '@tasks/permit-transfer-a/core/permit-transfer-a-task-form.token';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { PermitTransferAApplicationRequestTaskPayload } from 'pmrv-api';

export const codeFormProvider = {
  provide: PERMIT_TRANSFER_A_FORM,
  deps: [FormBuilder, CommonTasksStore],
  useFactory: (fb: FormBuilder, store: CommonTasksStore) => {
    const state = store.getState();
    const payload = state.requestTaskItem.requestTask.payload as PermitTransferAApplicationRequestTaskPayload;

    const group = fb.group({
      transferCode: [payload.transferCode ?? null, [nineDigitLengthNullNumberValidator()]],
    });

    if (!state.isEditable) {
      group.disable();
    }
    return group;
  },
};

const nineDigitLengthNullNumberValidator = () => {
  return (group: UntypedFormGroup): ValidationErrors => {
    return (group.value || '')?.length !== 9 || isNaN(group.value)
      ? { invalidDigitLength: 'Enter a valid transfer code' }
      : null;
  };
};
