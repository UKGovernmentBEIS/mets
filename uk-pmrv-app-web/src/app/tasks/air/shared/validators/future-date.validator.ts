import { AbstractControl, ValidatorFn } from '@angular/forms';

import { noPastDate } from '@tasks/air/shared/errors/validation-errors';

export function futureDateValidator(): ValidatorFn {
  return (control: AbstractControl): { [key: string]: string } | null => {
    return control.value && control.value < new Date() ? { invalidDate: noPastDate } : null;
  };
}
