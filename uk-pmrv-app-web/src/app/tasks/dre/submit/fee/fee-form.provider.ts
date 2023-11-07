import { AbstractControl, UntypedFormBuilder, ValidatorFn } from '@angular/forms';

import { DRE_TASK_FORM } from '@tasks/dre/core/dre-task-form.token';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { DreApplicationSubmitRequestTaskPayload } from 'pmrv-api';

export const feeFormProvider = {
  provide: DRE_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore) => {
    const state = store.getValue();
    const disabled = !state.isEditable;

    const feeDetails = (state.requestTaskItem.requestTask.payload as DreApplicationSubmitRequestTaskPayload)?.dre?.fee
      ?.feeDetails;

    return fb.group({
      totalBillableHours: [
        { value: feeDetails?.totalBillableHours ?? null, disabled },
        {
          validators: [
            GovukValidators.required('Enter the total billable hours'),
            GovukValidators.maxDecimalsValidator(5),
            GovukValidators.positiveNumber(),
          ],
          updateOn: 'change',
        },
      ],
      hourlyRate: [
        { value: feeDetails?.hourlyRate ?? null, disabled },
        {
          validators: [
            GovukValidators.required('Enter the hourly rate '),
            GovukValidators.maxDecimalsValidator(5),
            GovukValidators.positiveNumber(),
          ],
          updateOn: 'change',
        },
      ],
      dueDate: [
        { value: feeDetails?.dueDate ? new Date(feeDetails?.dueDate) : null, disabled },
        [futureDateValidator()],
      ],
    });
  },
};

function futureDateValidator(): ValidatorFn {
  return (control: AbstractControl): { [key: string]: string } | null => {
    const dateAndTime = new Date();
    const date = new Date(dateAndTime.toDateString());
    return control.value && control.value < date ? { invalidDate: `The date must be today or in the future` } : null;
  };
}
