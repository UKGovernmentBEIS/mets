import { FormControl, ValidatorFn } from '@angular/forms';

import { mapFieldsToColumnNames } from '@aviation/request-task/aer/corsia/tasks/monitoring-approach/validators/fields-column-names.map';

import { AviationAerCorsiaAircraftTypeDetails } from 'pmrv-api';

/**
 * Validates a CSV field whether it is positive and has maximum decimals digits
 * Returns the column and row the error was found at
 */
export function csvFieldPositiveMaxDecimalsValidator(
  field: keyof AviationAerCorsiaAircraftTypeDetails,
  decimalDigits: number,
  message: string,
): ValidatorFn {
  return (control: FormControl): { [key: string]: any } | null => {
    const data = control.value;
    const regex = new RegExp(`^-?[0-9]+(\\.[0-9]{1,${decimalDigits}})?$`, '');

    if (!Array.isArray(data)) {
      return null;
    }

    const rows = [];

    for (let i = 0; i < data.length; i++) {
      const entry = data[i];

      if (entry) {
        const myField = entry[field];
        if (myField <= 0 || !regex.test(myField)) {
          rows.push({
            rowIndex: i + 1,
          });
        }
      }
    }

    if (rows.length > 0) {
      return {
        ['csvFieldMaxLength' + field]: {
          rows,
          columns: mapFieldsToColumnNames([field]),
          message,
        },
      };
    }

    return null;
  };
}
