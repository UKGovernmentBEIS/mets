import { AbstractControl, AsyncValidatorFn, UntypedFormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';

import { Observable } from 'rxjs';

import { GovukValidators } from 'govuk-components';

export function atLeastOneRequiredValidator(message: string): ValidatorFn {
  return GovukValidators.builder(message, (group: UntypedFormGroup) =>
    Object.values(group.controls).some((control) => !!control.value) ? null : { atLeastOneRequired: true },
  );
}

export const csvRowValidator = (message: string, validRows: number): AsyncValidatorFn => {
  return (control: AbstractControl): Observable<ValidationErrors | null> => {
    return new Observable((observer) => {
      const file = control.value;

      if (file instanceof File) {
        const reader = new FileReader();

        reader.onload = (e: any) => {
          const fileContent = e.target.result;
          const rows = fileContent.split('\n');

          if (rows[rows.length - 1].trim() === '') {
            rows.pop();
          }

          for (const row of rows) {
            if (row.trim() === '') {
              //empty line
              continue;
            }

            const values = row.split(',');

            if (values.length !== validRows) {
              observer.next({ invalidRowFormat: { message } });
              observer.complete();
              return;
            }
          }

          observer.next(null);
          observer.complete();
        };

        reader.onerror = () => {
          observer.next({ readError: { message: 'Error reading the file.' } });
          observer.complete();
        };

        reader.readAsText(file);
      } else {
        observer.next(null);
        observer.complete();
      }
    });
  };
};
