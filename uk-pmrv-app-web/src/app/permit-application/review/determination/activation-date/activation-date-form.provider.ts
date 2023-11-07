import { UntypedFormBuilder } from '@angular/forms';

import { PermitIssuanceGrantDetermination } from 'pmrv-api';

import { PERMIT_TASK_FORM } from '../../../shared/permit-task-form.token';
import { PermitApplicationState } from '../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../store/permit-application.store';

export const activationDateFormProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [UntypedFormBuilder, PermitApplicationStore],
  useFactory: (fb: UntypedFormBuilder, store: PermitApplicationStore<PermitApplicationState>) => {
    const state = store.getValue();
    const value = state.determination as PermitIssuanceGrantDetermination;

    return fb.group({
      activationDate: [
        {
          value: value?.activationDate ? new Date(value.activationDate) : null,
          disabled: !state.isEditable,
        },
      ],
    });
  },
};
