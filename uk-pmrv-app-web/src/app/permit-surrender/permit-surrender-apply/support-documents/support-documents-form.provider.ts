import { UntypedFormBuilder } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { PermitSurrender } from 'pmrv-api';

import { PERMIT_SURRENDER_TASK_FORM } from '../../core/permit-surrender-task-form.token';
import { PermitSurrenderStore } from '../../store/permit-surrender.store';

export const supportDocumentsFormProvider = {
  provide: PERMIT_SURRENDER_TASK_FORM,
  deps: [UntypedFormBuilder, PermitSurrenderStore],
  useFactory: (fb: UntypedFormBuilder, store: PermitSurrenderStore) => {
    const state = store.getValue();
    const permitSurrender = state.permitSurrender as PermitSurrender;

    return fb.group({
      documentsExist: [
        {
          value: permitSurrender?.documentsExist ?? null,
          disabled: !state.isEditable,
        },
        GovukValidators.required('Select yes or no'),
      ],
    });
  },
};
