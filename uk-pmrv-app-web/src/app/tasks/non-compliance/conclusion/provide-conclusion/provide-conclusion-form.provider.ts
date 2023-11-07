import { UntypedFormBuilder } from '@angular/forms';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { NonComplianceFinalDeterminationRequestTaskPayload } from 'pmrv-api';

import { NON_COMPLIANCE_TASK_FORM } from '../../core/non-compliance-form.token';

export const provideConclusionFormProvider = {
  provide: NON_COMPLIANCE_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore) => {
    const state = store.getValue();
    const disabled = !state.isEditable;
    const statePayload = state.requestTaskItem.requestTask.payload as NonComplianceFinalDeterminationRequestTaskPayload;

    return fb.group({
      complianceRestored: [
        { value: statePayload?.complianceRestored ?? null, disabled },
        { validators: GovukValidators.required('Select yes or no'), updateOn: 'change' },
      ],

      complianceRestoredDate: [
        {
          value: statePayload?.complianceRestoredDate ? new Date(statePayload.complianceRestoredDate) : null,
          disabled,
        },
      ],

      comments: [
        { value: statePayload?.comments ?? null, disabled },
        {
          validators: [
            GovukValidators.required('Please provide comments'),
            GovukValidators.maxLength(10000, `Enter up to 10000 characters`),
          ],
        },
      ],

      reissuePenalty: [
        { value: statePayload?.reissuePenalty ?? null, disabled },
        { validators: GovukValidators.required('Select yes or no'), updateOn: 'change' },
      ],

      operatorPaid: [{ value: statePayload?.operatorPaid ?? null, disabled }],

      operatorPaidDate: [
        {
          value: statePayload?.operatorPaidDate ? new Date(statePayload.operatorPaidDate) : null,
          disabled,
        },
      ],
    });
  },
};
