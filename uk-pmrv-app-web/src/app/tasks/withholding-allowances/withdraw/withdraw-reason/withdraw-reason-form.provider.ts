import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { WITHHOLDING_ALLOWANCES_TASK_FORM } from '@tasks/withholding-allowances/core/withholding-allowances';

import { GovukValidators } from 'govuk-components';

import { WithholdingOfAllowancesWithdrawalApplicationSubmitRequestTaskPayload } from 'pmrv-api';

export const reasonFormProvider = {
  provide: WITHHOLDING_ALLOWANCES_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, ActivatedRoute],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore) => {
    const state = store.value;
    const disabled = !state.isEditable;

    const withholdingWithdrawal = (
      state.requestTaskItem.requestTask.payload as WithholdingOfAllowancesWithdrawalApplicationSubmitRequestTaskPayload
    )?.withholdingWithdrawal;

    return fb.group({
      reason: [
        { value: withholdingWithdrawal?.reason ?? null, disabled },
        [
          GovukValidators.required('Explain why you are withdrawing the withholding of allowances notice'),
          GovukValidators.maxLength(10000, 'Enter up to 10000 characters'),
        ],
      ],
    });
  },
};
