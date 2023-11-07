import { UntypedFormBuilder } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { AerApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

export const notIncludedItemFormProvider = {
  provide: AER_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, ActivatedRoute],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore, route: ActivatedRoute) => {
    const state = store.getValue();
    const index = Number(route.snapshot.paramMap.get('index'));

    const summaryOfConditions = (
      state.requestTaskItem.requestTask.payload as AerApplicationVerificationSubmitRequestTaskPayload
    ).verificationReport.summaryOfConditions;
    const item =
      index < summaryOfConditions.approvedChangesNotIncluded?.length
        ? summaryOfConditions.approvedChangesNotIncluded[index]
        : null;

    return fb.group({
      explanation: [
        { value: item?.explanation ?? null, disabled: !state.isEditable },
        {
          validators: [
            GovukValidators.required('Please give details of the change'),
            GovukValidators.maxLength(10000, `The details of the change should not be more than 10000 characters`),
          ],
        },
      ],
    });
  },
};
