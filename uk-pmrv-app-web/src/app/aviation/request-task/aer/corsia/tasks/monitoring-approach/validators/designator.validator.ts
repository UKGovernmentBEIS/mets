import { AsyncValidatorFn, FormControl, ValidationErrors } from '@angular/forms';

import { catchError, map, Observable, of, take } from 'rxjs';

import { mapFieldsToColumnNames } from '@aviation/request-task/aer/corsia/tasks/monitoring-approach/validators/fields-column-names.map';

import { AircraftTypeSearchResults, AircraftTypesService, AviationAerCorsiaAircraftTypeDetails } from 'pmrv-api';

/**
 * Validates 'designator' FormControl within a FormArray, against 'designatorType' found in cachedAircraftDataTypes$
 * Returns the column and row the error was found at
 */
export function designatorValidator(message: string, aircraftTypesService: AircraftTypesService): AsyncValidatorFn {
  return (control: FormControl): Observable<ValidationErrors | null> => {
    const data = control.value;
    if (!Array.isArray(data)) {
      return null;
    }

    const cachedAircraftDataTypes$ = aircraftTypesService
      .getAircraftTypes({
        pageSize: 100000,
        pageNumber: 0,
      })
      .pipe(
        take(1),
        catchError((error) => of({ error })),
        map((result: AircraftTypeSearchResults) => result),
      );

    return cachedAircraftDataTypes$.pipe(
      map((result: AircraftTypeSearchResults) => {
        const designatorCodes = result.aircraftTypes.map((type) => type.designatorType);
        const rows = [];

        for (let i = 0; i < data.length; i++) {
          const entry = data[i] as AviationAerCorsiaAircraftTypeDetails;
          if (entry && designatorCodes) {
            if (!designatorCodes.includes(entry.designator)) {
              rows.push({
                rowIndex: i + 1,
              });
            }
          }
        }
        if (rows.length > 0) {
          control.setErrors({
            invalidDesignatorCodes: {
              rows,
              columns: ['designator'],
              message,
            },
          });

          return {
            invalidDesignatorCodes: {
              rows,
              columns: mapFieldsToColumnNames(['designator']),
              message,
            },
          };
        }

        return null;
      }),
    );
  };
}
