import { FormControl, ValidatorFn } from '@angular/forms';

import { mapFieldsToColumnNames } from '@aviation/request-task/aer/corsia/tasks/monitoring-approach/validators/fields-column-names.map';

import { AviationAerCorsiaAircraftTypeDetails } from 'pmrv-api';

/**
 * Validates a CSV field for its length
 * Returns the column and row the error was found at
 */
export function csvFieldMaxLengthValidator(
  field: keyof AviationAerCorsiaAircraftTypeDetails,
  length: number,
  message: string,
): ValidatorFn {
  return (control: FormControl): { [key: string]: any } | null => {
    const data = control.value;

    if (!Array.isArray(data)) {
      return null;
    }

    const rows = [];

    for (let i = 0; i < data.length; i++) {
      const entry = data[i];

      if (entry) {
        const myField = entry[field];
        if (myField && myField?.length > length) {
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
