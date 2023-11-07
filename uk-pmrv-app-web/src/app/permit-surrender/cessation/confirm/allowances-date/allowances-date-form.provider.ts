import { UntypedFormBuilder } from '@angular/forms';

import { PERMIT_SURRENDER_TASK_FORM } from '../../../core/permit-surrender-task-form.token';
import { PermitSurrenderStore } from '../../../store/permit-surrender.store';

export const allowancesDateFormProvider = {
  provide: PERMIT_SURRENDER_TASK_FORM,
  deps: [UntypedFormBuilder, PermitSurrenderStore],
  useFactory: (fb: UntypedFormBuilder, store: PermitSurrenderStore) => {
    const state = store.getValue();
    const allowancesSurrenderDate = state.cessation.allowancesSurrenderDate;
    const disabled = !state.isEditable;

    return fb.group({
      allowancesSurrenderDate: [
        { value: allowancesSurrenderDate ? new Date(allowancesSurrenderDate) : null, disabled },
      ],
    });
  },
};
