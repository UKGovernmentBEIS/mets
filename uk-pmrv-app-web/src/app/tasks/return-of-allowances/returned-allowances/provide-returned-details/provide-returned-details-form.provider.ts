import { UntypedFormBuilder, UntypedFormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { ReturnOfAllowancesReturnedApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { CommonTasksStore } from '../../../store/common-tasks.store';
import { RETURN_OF_ALLOWANCES_TASK_FORM } from '../../core/return-of-allowances-task-form.token';

export const provideReturnedDetailsFormProvider = {
  provide: RETURN_OF_ALLOWANCES_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore) => {
    const state = store.getValue();
    const disabled = !state.isEditable;

    const payload = (
      state.requestTaskItem.requestTask.payload as ReturnOfAllowancesReturnedApplicationSubmitRequestTaskPayload
    )?.returnOfAllowancesReturned;

    return fb.group({
      isAllowancesReturned: [
        {
          value: payload?.isAllowancesReturned ?? null,
          disabled,
        },
        {
          validators: [GovukValidators.required('Select an option')],
        },
      ],

      returnedAllowancesDate: [
        {
          value: payload?.returnedAllowancesDate ? new Date(payload.returnedAllowancesDate) : null,
          disabled,
        },
        {
          validators: [dateToBeReturnedMaxValidator()],
        },
      ],

      regulatorComments: [
        { value: payload?.regulatorComments ?? null, disabled },
        {
          validators: [GovukValidators.maxLength(10000, `Enter up to 10000 characters`)],
        },
      ],
    });
  },
};

// Validators
const dateToBeReturnedMaxValidator = (): ValidatorFn => {
  return (group: UntypedFormGroup): ValidationErrors => {
    return group.value && group.value > new Date() ? { invalidDate: `The date must be today or in the past` } : null;
  };
};
