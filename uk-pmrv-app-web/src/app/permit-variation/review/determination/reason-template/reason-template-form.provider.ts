import { UntypedFormBuilder } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { PermitVariationRegulatorLedGrantDetermination } from 'pmrv-api';

import { PERMIT_TASK_FORM } from '../../../../permit-application/shared/permit-task-form.token';
import { PermitVariationStore } from '../../../store/permit-variation.store';

export const reasonTemplateFormProvider = {
  provide: PERMIT_TASK_FORM,
  deps: [UntypedFormBuilder, PermitVariationStore],
  useFactory: (fb: UntypedFormBuilder, store: PermitVariationStore) => {
    const state = store.getValue();
    const determination = state.determination as PermitVariationRegulatorLedGrantDetermination;

    return fb.group({
      reasonTemplate: [
        { value: determination.reasonTemplate ?? null, disabled: !state.isEditable },
        GovukValidators.required('Select a reason template'),
      ],
      reasonTemplateOtherSummary: [
        {
          value: determination?.reasonTemplateOtherSummary ?? null,
          disabled: !state.isEditable,
        },
        [
          GovukValidators.required('Enter the reason for the decision'),
          GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
        ],
      ],
    });
  },
};
