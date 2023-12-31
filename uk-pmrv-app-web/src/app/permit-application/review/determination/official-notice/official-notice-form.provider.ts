import { UntypedFormBuilder } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { PermitIssuanceRejectDetermination } from 'pmrv-api';

import { PERMIT_TASK_FORM } from '../../../shared/permit-task-form.token';
import { PermitApplicationState } from '../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../store/permit-application.store';

export const officialNoticeFormProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [UntypedFormBuilder, PermitApplicationStore],
  useFactory: (fb: UntypedFormBuilder, store: PermitApplicationStore<PermitApplicationState>) => {
    const state = store.getValue();
    const value = state.determination as PermitIssuanceRejectDetermination;

    return fb.group({
      officialNotice: [
        {
          value: value?.officialNotice ?? null,
          disabled: !state.isEditable,
        },
        [
          GovukValidators.required('Enter text to be included in the official refusal letter.'),
          GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
        ],
      ],
    });
  },
};
