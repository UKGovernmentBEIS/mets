import { InjectionToken } from '@angular/core';
import { AbstractControl, UntypedFormBuilder, UntypedFormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';

import { GovukDatePipe } from '../../../shared/pipes/govuk-date.pipe';
import { RdeStore } from '../../store/rde.store';

export const RDE_FORM = new InjectionToken<UntypedFormGroup>('Rde form');

export const extendDeterminationFormProvider = {
  provide: RDE_FORM,
  deps: [UntypedFormBuilder, RdeStore],
  useFactory: (fb: UntypedFormBuilder, store: RdeStore) => {
    const state = store.getState();

    const { daysRemaining } = state;

    return fb.group(
      {
        extensionDate: [
          { value: state?.rdePayload?.extensionDate ?? null, disabled: !state.isEditable },
          {
            validators: [extensionDateValidator(daysRemaining), futureDateValidator()],
          },
        ],
        deadline: [
          { value: state?.rdePayload?.deadline ?? null, disabled: !state.isEditable },
          {
            validators: [futureDateValidator()],
          },
        ],
      },
      { validators: [deadlineValidator()] },
    );
  },
};

export function extensionDateValidator(daysRemaining?: number): ValidatorFn {
  return (control: AbstractControl): { [key: string]: string } | null => {
    const govukDatePipe = new GovukDatePipe();

    const now = new Date();
    const determinationDueDate = now.setDate(now.getDate() + daysRemaining);
    const determinationDueDateToString = govukDatePipe.transform(new Date(determinationDueDate));

    return control.value && control.value <= determinationDueDate
      ? { invalidDate: `The day must be after ${determinationDueDateToString}` }
      : null;
  };
}

export function deadlineValidator(): ValidatorFn {
  return (group: UntypedFormGroup): ValidationErrors => {
    const extensionDate = group.get('extensionDate').value;
    const deadline = group.get('deadline').value;

    const govukDatePipe = new GovukDatePipe();
    const extensionDateToString = govukDatePipe.transform(new Date(extensionDate));

    const errors = group.controls['deadline'].errors;
    if (extensionDate && deadline && deadline >= extensionDate) {
      group.controls['deadline'].setErrors({
        ...errors,
        invalidDeadline: `Deadline must be before ${extensionDateToString}`,
      });
      // if the extension date is null or is after the deadline then remove the error
    } else if (!extensionDate || (extensionDate && deadline && deadline < extensionDate)) {
      if (errors) {
        delete errors.invalidDeadline;
      }
      if (errors && Object.keys(errors).length === 0) {
        group.controls['deadline'].setErrors(null);
      }
    }
    return null;
  };
}

export function futureDateValidator(): ValidatorFn {
  return (control: AbstractControl): { [key: string]: string } | null => {
    return control.value && control.value < new Date() ? { invalidDate: `The date must be in the future` } : null;
  };
}
