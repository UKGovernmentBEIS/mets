import { UntypedFormBuilder, UntypedFormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';

import { GovukValidators } from 'govuk-components';

import { ReturnOfAllowancesApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { CommonTasksStore } from '../../../store/common-tasks.store';
import { RETURN_OF_ALLOWANCES_TASK_FORM } from '../../core/return-of-allowances-task-form.token';

export const provideDetailsFormProvider = {
  provide: RETURN_OF_ALLOWANCES_TASK_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore) => {
    const state = store.getValue();
    const disabled = !state.isEditable;

    const payload = (state.requestTaskItem.requestTask.payload as ReturnOfAllowancesApplicationSubmitRequestTaskPayload)
      ?.returnOfAllowances;

    return fb.group({
      numberOfAllowancesToBeReturned: [
        {
          value: payload?.numberOfAllowancesToBeReturned ?? null,
          disabled,
        },
        {
          validators: [GovukValidators.required('Enter a whole number'), GovukValidators.naturalNumber()],
        },
      ],

      years: [payload?.years ?? [], GovukValidators.required('Select a year')],

      reason: [
        { value: payload?.reason ?? null, disabled },
        {
          validators: [
            GovukValidators.required('Enter a comment'),
            GovukValidators.maxLength(10000, `Enter up to 10000 characters`),
          ],
        },
      ],

      dateToBeReturned: [
        {
          value: payload?.dateToBeReturned ? new Date(payload.dateToBeReturned) : null,
          disabled,
        },
        {
          validators: [dateToBeReturnedMinValidator()],
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
const dateToBeReturnedMinValidator = (): ValidatorFn => {
  return (group: UntypedFormGroup): ValidationErrors => {
    return group.value && group.value < new Date() ? { invalidDate: `The date must be in the future` } : null;
  };
};
