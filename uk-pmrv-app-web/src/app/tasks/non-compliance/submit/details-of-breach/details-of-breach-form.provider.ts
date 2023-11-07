import { UntypedFormBuilder } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { NonComplianceApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { CommonTasksStore } from '../../../store/common-tasks.store';
import { NON_COMPLIANCE_TASK_FORM } from '../../core/non-compliance-form.token';

export const detailsOfBreanchFormProvider = {
  provide: NON_COMPLIANCE_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore) => {
    const state = store.getValue();
    const disabled = !state.isEditable;

    const payload = state.requestTaskItem.requestTask.payload as NonComplianceApplicationSubmitRequestTaskPayload;

    return fb.group({
      reason: [
        { value: payload?.reason ?? null, disabled },
        { validators: GovukValidators.required('You must select a reason') },
      ],

      nonComplianceDate: [
        { value: payload?.nonComplianceDate ? new Date(payload.nonComplianceDate) : null, disabled },
        {
          validators: [],
        },
      ],

      complianceDate: [
        { value: payload?.complianceDate ? new Date(payload.complianceDate) : null, disabled },
        {
          validators: [],
        },
      ],
      comments: [
        { value: payload?.comments ?? null, disabled },
        {
          validators: [
            GovukValidators.maxLength(10000, `The regulator comments should not be more than 10000 characters`),
          ],
        },
      ],
    });
  },
};
