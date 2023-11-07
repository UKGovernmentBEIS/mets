import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { WithholdingOfAllowancesApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { WITHHOLDING_ALLOWANCES_TASK_FORM } from '../../core/withholding-allowances';

export const recommendationResponseFormProvider = {
  provide: WITHHOLDING_ALLOWANCES_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, ActivatedRoute],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore) => {
    const state = store.value;
    const disabled = !state.isEditable;

    const withholdingOfAllowances = (
      state.requestTaskItem.requestTask.payload as WithholdingOfAllowancesApplicationSubmitRequestTaskPayload
    )?.withholdingOfAllowances;

    return fb.group({
      year: [
        { value: withholdingOfAllowances?.year ?? null, disabled },
        { validators: [GovukValidators.required('Select a year')] },
      ],
      reasonType: [
        { value: withholdingOfAllowances?.reasonType ?? null, disabled },
        { validators: [GovukValidators.required('Select a reason')] },
      ],
      otherReason: [
        { value: withholdingOfAllowances?.otherReason ?? null, disabled },
        {
          validators: [
            GovukValidators.required('Enter a reason'),
            GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
          ],
        },
      ],
      regulatorComments: [
        { value: withholdingOfAllowances?.regulatorComments ?? null, disabled },
        GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
      ],
    });
  },
};
