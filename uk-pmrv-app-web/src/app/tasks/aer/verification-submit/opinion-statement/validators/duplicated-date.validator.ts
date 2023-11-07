import { AbstractControl, UntypedFormArray, ValidatorFn } from '@angular/forms';

import { noDuplicatedDate } from '@tasks/aer/verification-submit/opinion-statement/errors/opinion-statement-validation.errors';

export function duplicatedDateValidator(visitDates: string): ValidatorFn {
  return (control: AbstractControl): { [key: string]: string } | null => {
    return duplicatedDatesExist(control.get(visitDates) as UntypedFormArray)
      ? { duplicatedDate: noDuplicatedDate }
      : null;
  };
}

function duplicatedDatesExist(visitDates: UntypedFormArray): boolean {
  const visitDatesValues = visitDates.controls.map((visitDate) => new Date(visitDate.value).toISOString());
  return visitDatesValues.length !== new Set(visitDatesValues).size;
}
