import { FormBuilder } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { PermitTransferDetailsConfirmation } from 'pmrv-api';

import { PERMIT_TRANSFER_B_FORM } from '../core/permit-transfer-task-form.token';
import { PermitTransferStore } from '../store/permit-transfer.store';

export const transferDetailsFormProvider = {
  provide: PERMIT_TRANSFER_B_FORM,
  deps: [FormBuilder, PermitTransferStore],
  useFactory: (fb: FormBuilder, store: PermitTransferStore) => {
    const state = store.getState();
    const {
      detailsAccepted,
      detailsRejectedReason,
      regulatedActivitiesInOperation,
      regulatedActivitiesNotInOperationReason,
      transferAccepted,
      transferRejectedReason,
    } = (state.permitTransferDetailsConfirmation || {}) as PermitTransferDetailsConfirmation;

    const radioValidator = GovukValidators.required('Select yes or no');
    const reasonValidator = GovukValidators.required('Enter a reason');

    const group = fb.group({
      detailsAccepted: [detailsAccepted ?? null, radioValidator],
      detailsRejectedReason: [detailsRejectedReason ?? null, reasonValidator],
      regulatedActivitiesInOperation: [regulatedActivitiesInOperation ?? null, radioValidator],
      regulatedActivitiesNotInOperationReason: [regulatedActivitiesNotInOperationReason ?? null, reasonValidator],
      transferAccepted: [transferAccepted ?? null, radioValidator],
      transferRejectedReason: [transferRejectedReason ?? null, reasonValidator],
    });

    if (!state.isEditable) {
      group.disable();
    }
    return group;
  },
};
