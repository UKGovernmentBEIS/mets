import { AbstractControl, ValidatorFn } from '@angular/forms';

import { todayOrPastDate } from '@tasks/aer/verification-submit/opinion-statement/errors/opinion-statement-validation.errors';

export function todayOrPastDateValidator(): ValidatorFn {
  return (control: AbstractControl): { [key: string]: string } | null => {
    return control.value && control.value >= new Date() ? { invalidDate: todayOrPastDate } : null;
  };
}
