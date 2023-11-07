import { FormControl, ValidatorFn } from '@angular/forms';

import { mapFieldsToColumnNames } from '@aviation/request-task/aer/corsia/tasks/monitoring-approach/validators/fields-column-names.map';

import { AviationAerCorsiaAircraftTypeDetails } from 'pmrv-api';

/**
 * Validates a CSV field as required
 * Returns the column and row the error was found at
 */
export function csvFieldRequiredValidator(
  field: keyof AviationAerCorsiaAircraftTypeDetails,
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
        if ((myField && myField.length === 0) || !myField) {
          rows.push({
            rowIndex: i + 1,
          });
        }
      }
    }

    if (rows.length > 0) {
      return {
        ['csvFieldRequired' + field]: {
          rows,
          columns: mapFieldsToColumnNames([field]),
          message,
        },
      };
    }

    return null;
  };
}
