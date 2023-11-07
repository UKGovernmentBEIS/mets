import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { AerApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

export const misstatementsItemFormProvider = {
  provide: AER_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, ActivatedRoute],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore, route: ActivatedRoute) => {
    const state = store.getValue();
    const index = Number(route.snapshot.paramMap.get('index'));

    const uncorrectedMisstatementsInfo = (
      state.requestTaskItem.requestTask.payload as AerApplicationVerificationSubmitRequestTaskPayload
    ).verificationReport.uncorrectedMisstatements;
    const item =
      index < uncorrectedMisstatementsInfo.uncorrectedMisstatements?.length
        ? uncorrectedMisstatementsInfo.uncorrectedMisstatements[index]
        : null;

    return fb.group({
      explanation: [
        { value: item?.explanation ?? null, disabled: !state.isEditable },
        {
          validators: [
            GovukValidators.required('Please give details of the misstatement'),
            GovukValidators.maxLength(10000, `Enter up to 10000 characters`),
          ],
        },
      ],
      materialEffect: [
        { value: item?.materialEffect ?? null, disabled: !state.isEditable },
        { validators: [GovukValidators.required('Please select yes or no')] },
      ],
    });
  },
};
