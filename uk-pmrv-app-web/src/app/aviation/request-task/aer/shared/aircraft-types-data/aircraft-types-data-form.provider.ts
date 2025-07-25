import { inject, Injectable } from '@angular/core';
import { AsyncValidatorFn, FormBuilder, FormControl, FormGroup, ValidationErrors, Validators } from '@angular/forms';

import { catchError, map, Observable, of, Subject, take } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TaskFormProvider } from '@aviation/request-task/task-form.provider';
import { differenceInMilliseconds, getYear, isValid, parse } from 'date-fns';

import {
  AircraftTypeSearchResults,
  AircraftTypesService,
  AviationAerAircraftData,
  AviationAerAircraftDataDetails,
} from 'pmrv-api';

import { aerQuery } from '../aer.selectors';

export interface AviationAerAircraftTypesDataFormModel {
  aviationAerAircraftDataDetails: FormControl<AviationAerAircraftDataDetails[]>;
}

@Injectable()
export class AircraftTypesDataFormProvider
  implements TaskFormProvider<AviationAerAircraftData, AviationAerAircraftTypesDataFormModel>
{
  private fb = inject(FormBuilder);
  private _form: FormGroup;
  store = inject(RequestTaskStore);
  private aircraftTypeService = inject(AircraftTypesService);
  private destroy$ = new Subject<void>();

  get form(): FormGroup {
    if (!this._form) {
      this.buildForm();
    }

    return this._form;
  }

  getAircraftDataDetailsControl(): FormControl {
    return this._form.get('aviationAerAircraftDataDetails') as FormControl;
  }

  destroyForm() {
    this.destroy$.next();
    this.destroy$.complete();
    this._form = null;
  }

  setFormValue(aviationAerAircraftTypesData: AviationAerAircraftData | null) {
    if (aviationAerAircraftTypesData && aviationAerAircraftTypesData?.aviationAerAircraftDataDetails) {
      this.form.patchValue({
        aviationAerAircraftDataDetails: aviationAerAircraftTypesData.aviationAerAircraftDataDetails,
      });
    }
  }

  getFormValue(): AviationAerAircraftData {
    const formValue = this.form.value;
    return {
      aviationAerAircraftDataDetails: formValue.aviationAerAircraftDataDetails,
    };
  }

  private buildForm() {
    const formGroup = this.fb.group(
      {
        aviationAerAircraftDataDetails: [
          null,
          [Validators.required],
          [
            this.validateMaxLengthAsync('subType', 255, 'Enter up to 255 characters'),
            this.validateMaxLengthAsync('registrationNumber', 20, 'Enter up to 20 characters'),
            this.validateMaxLengthAsync('ownerOrLessor', 255, 'Enter up to 255 characters'),
            this.validateMandatoryAsync('aircraftTypeDesignator', 'Enter an aircraft designator'),
            this.validateMandatoryAsync('registrationNumber', 'Enter an aircraft registration number'),
            this.validateDateFormatAsync(['startDate', 'endDate'], 'The date must be entered as dd/mm/yyyy'),
            this.validateStartEndDateAsync(),
            this.validateAerYear(['startDate', 'endDate'], 'The dates entered must be within the scheme year'),
            this.validateDesignators('The designator entered is not valid. Please check and try again.'),
          ],
        ],
      },
      { updateOn: 'change' },
    );
    return (this._form = formGroup);
  }

  validateMaxLengthAsync(field: string, length: number, message: string): AsyncValidatorFn {
    return (control: FormControl): Observable<ValidationErrors | null> => {
      return of(this.validateMaxLength(control, field, length, message));
    };
  }

  private validateMaxLength(
    control: FormControl,
    field: string,
    length: number,
    message: string,
  ): ValidationErrors | null {
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
        ['invalidMaxLength' + field]: {
          rows,
          columns: this.mapFieldsToColumnNames([field]),
          message,
        },
      };
    }

    return null;
  }

  validateMandatoryAsync(field: string, message: string): AsyncValidatorFn {
    return (control: FormControl): Observable<ValidationErrors | null> => {
      return of(this.validateMandatory(control, field, message));
    };
  }

  private validateMandatory(control: FormControl, field: string, message: string) {
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
        ['invalidMandatory' + field]: {
          rows,
          columns: this.mapFieldsToColumnNames([field]),
          message,
        },
      };
    }

    return null;
  }

  validateDateFormatAsync(fields: string[], message: string): AsyncValidatorFn {
    return (control: FormControl): Observable<ValidationErrors | null> => {
      return of(this.validateDateFormat(control, fields, message));
    };
  }

  private validateDateFormat(control: FormControl, fields: string[], message: string) {
    const data = control.value;
    if (!Array.isArray(data)) {
      return null;
    }

    const rows = [];
    const returnedFields = [];
    for (let i = 0; i < data.length; i++) {
      const entry = data[i];

      const validFields = [];

      for (const field of fields) {
        if (entry) {
          const myField = entry[field];
          if ((myField && myField.length > 0) || !myField) {
            const isValidDate = isValid(parse(myField, 'yyyy-MM-dd', new Date()));
            validFields.push(isValidDate);
            if (!returnedFields.includes(field)) returnedFields.push(field);
          }
        }
      }
      if (validFields.includes(false)) {
        rows.push({
          rowIndex: i + 1,
        });
      }
    }

    if (rows.length > 0) {
      return {
        invalidDate: {
          rows,
          columns: this.mapFieldsToColumnNames(returnedFields),
          message,
        },
      };
    }

    return null;
  }

  validateStartEndDateAsync(): AsyncValidatorFn {
    return (control: FormControl): Observable<ValidationErrors | null> => {
      return of(this.validateStartEndDate(control));
    };
  }

  private validateStartEndDate(control: FormControl): ValidationErrors | null {
    const data = control.value;

    if (!Array.isArray(data)) {
      return null;
    }

    const rows = [];

    for (let i = 0; i < data.length; i++) {
      const entry = data[i];

      if (entry) {
        if (
          entry['startDate'] &&
          entry['endDate'] &&
          entry['startDate'].length > 0 &&
          entry['endDate'].length > 0 &&
          isValid(parse(entry['startDate'], 'yyyy-MM-dd', new Date())) &&
          isValid(parse(entry['endDate'], 'yyyy-MM-dd', new Date()))
        ) {
          const startDate = parse(entry['startDate'], 'yyyy-MM-dd', new Date());
          const endDate = parse(entry['endDate'], 'yyyy-MM-dd', new Date());

          if (differenceInMilliseconds(endDate, startDate) < 0) {
            rows.push({
              rowIndex: i + 1,
            });
          }
        }
      }
    }

    if (rows.length > 0) {
      return {
        invalidStartEndDate: {
          rows,
          columns: this.mapFieldsToColumnNames(['startDate', 'endDate']),
          message: 'The start date must be the same as or before the end date',
        },
      };
    }

    return null;
  }

  validateAerYear(fields: string[], message: string): AsyncValidatorFn {
    return (control: FormControl): Observable<ValidationErrors | null> => {
      const data = control.value;
      if (!Array.isArray(data)) {
        return of(null);
      }

      return this.store.pipe(
        aerQuery.selectAerYear,
        take(1),
        map((aerYear) => {
          const rows = [];
          const returnedFields = [];
          for (let i = 0; i < data.length; i++) {
            const entry = data[i];
            const notValidYears = [];
            for (const field of fields) {
              if (entry && entry[field] && aerYear) {
                const year = getYear(parse(entry[field], 'yyyy-MM-dd', new Date())).toString();

                if (year !== aerYear + '') {
                  notValidYears.push(year);
                  if (!returnedFields.includes(field)) returnedFields.push(field);
                }
              }
            }

            if (notValidYears.length > 0) {
              rows.push({
                rowIndex: i + 1,
              });
            }
          }

          if (rows.length > 0) {
            return {
              invalidAerYear: {
                rows,
                columns: this.mapFieldsToColumnNames(returnedFields),
                message,
              },
            };
          }

          return null;
        }),
      );
    };
  }

  validateDesignators(message: string): AsyncValidatorFn {
    return (control: FormControl): Observable<ValidationErrors | null> => {
      const data = control.value;
      if (!Array.isArray(data)) {
        return of(null);
      }
      const searchCriteria = {
        pageSize: 100000,
        pageNumber: 0,
      };

      return this.aircraftTypeService.getAircraftTypes(searchCriteria).pipe(
        take(1),
        catchError((error) => of({ error })),
        map((result: AircraftTypeSearchResults) => {
          const designatorCodes = result.aircraftTypes.map((type) => type.designatorType);

          const rows = [];

          for (let i = 0; i < data.length; i++) {
            const entry = data[i];
            if (entry && designatorCodes) {
              if (!designatorCodes.includes(entry.aircraftTypeDesignator)) {
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
                columns: ['aircraftTypeDesignator'],
                message,
              },
            });

            return {
              invalidDesignatorCodes: {
                rows,
                columns: this.mapFieldsToColumnNames(['aircraftTypeDesignator']),
                message,
              },
            };
          }

          return null;
        }),
      );
    };
  }

  private mapFieldsToColumnNames(fields) {
    const fieldColumnMap = {
      aircraftTypeDesignator: 'A',
      subType: 'B',
      registrationNumber: 'C',
      ownerOrLessor: 'D',
      startDate: 'E',
      endDate: 'F',
    };
    return fields.map((field) => fieldColumnMap[field]);
  }
}
