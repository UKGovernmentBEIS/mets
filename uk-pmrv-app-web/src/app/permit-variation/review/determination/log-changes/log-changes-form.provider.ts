import { FormBuilder } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { PermitVariationGrantDetermination, PermitVariationRegulatorLedGrantDetermination } from 'pmrv-api';

import { PERMIT_TASK_FORM } from '../../../../permit-application/shared/permit-task-form.token';
import { PermitVariationStore } from '../../../store/permit-variation.store';

export const logChangesFormProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [FormBuilder, PermitVariationStore],
  useFactory: (fb: FormBuilder, store: PermitVariationStore) => {
    const state = store.getValue();
    const value = state.determination as
      | PermitVariationGrantDetermination
      | PermitVariationRegulatorLedGrantDetermination;

    return fb.group({
      logChanges: [
        {
          value: value?.logChanges ?? null,
          disabled: !state.isEditable,
        },
        [
          GovukValidators.required('Enter a summary of the changes for the permit variation log'),
          GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
        ],
      ],
    });
  },
};
