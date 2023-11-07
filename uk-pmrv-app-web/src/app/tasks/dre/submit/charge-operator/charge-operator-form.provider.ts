import { UntypedFormBuilder } from '@angular/forms';

import { DRE_TASK_FORM } from '@tasks/dre/core/dre-task-form.token';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { DreApplicationSubmitRequestTaskPayload } from 'pmrv-api';

export const chargeOperatorFormProvider = {
  provide: DRE_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore) => {
    const state = store.getValue();
    const disabled = !state.isEditable;

    const chargeOperator = (state.requestTaskItem.requestTask.payload as DreApplicationSubmitRequestTaskPayload)?.dre
      ?.fee?.chargeOperator;

    return fb.group({
      chargeOperator: [
        { value: chargeOperator ?? false, disabled },
        [GovukValidators.required('Please select yes or no')],
      ],
    });
  },
};
