import { UntypedFormBuilder } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { NonComplianceApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { CommonTasksStore } from '../../../store/common-tasks.store';
import { NON_COMPLIANCE_TASK_FORM } from '../../core/non-compliance-form.token';

export const civilPenaltyFormProvider = {
  provide: NON_COMPLIANCE_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore) => {
    const state = store.getValue();
    const disabled = !state.isEditable;
    const payload = state.requestTaskItem.requestTask.payload as NonComplianceApplicationSubmitRequestTaskPayload;

    return fb.group({
      civilPenalty: [
        { value: payload?.civilPenalty ?? null, disabled },
        { validators: GovukValidators.required('Select yes or no') },
      ],
      noCivilPenaltyJustification: [
        { value: payload?.noCivilPenaltyJustification ?? null, disabled },
        {
          validators: [
            GovukValidators.required('You must enter a reason'),
            GovukValidators.maxLength(10000, `The regulator comments should not be more than 10000 characters`),
          ],
        },
      ],
    });
  },
};
