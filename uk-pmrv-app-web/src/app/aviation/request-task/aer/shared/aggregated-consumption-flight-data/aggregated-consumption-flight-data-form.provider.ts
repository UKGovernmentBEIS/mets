import { inject, Injectable } from '@angular/core';
import { AsyncValidatorFn, FormBuilder, FormControl, FormGroup, ValidationErrors, Validators } from '@angular/forms';

import { combineLatest, forkJoin, map, Observable, of, Subject, take } from 'rxjs';

import { aerQuery } from '@aviation/request-task/aer/shared/aer.selectors';
import { RequestTaskStore } from '@aviation/request-task/store';
import { AirportTypes } from '@aviation/shared/components/aer/util/airport-types';
import { getFuelTypeValues } from '@aviation/shared/components/emp/emission-sources/aircraft-type/fuel-types';

import {
  AviationAerCorsiaAggregatedEmissionDataDetails,
  AviationAerCorsiaAggregatedEmissionsData,
  AviationAerUkEtsAggregatedEmissionDataDetails,
  AviationAerUkEtsAggregatedEmissionsData,
  AviationReportedAirportsService,
} from 'pmrv-api';

import { TaskFormProvider } from '../../../task-form.provider';

export interface AviationAerAggregatedEmissionDataFormModel {
  aggregatedEmissionDataDetails: FormControl<
    AviationAerUkEtsAggregatedEmissionDataDetails[] | AviationAerCorsiaAggregatedEmissionDataDetails[]
  >;
}

@Injectable()
export class AggregatedConsumptionFlightDataFormProvider
  implements
    TaskFormProvider<
      AviationAerUkEtsAggregatedEmissionsData | AviationAerCorsiaAggregatedEmissionsData,
      AviationAerAggregatedEmissionDataFormModel
    >
{
  constructor(
    private aviationReportedAirportsService: AviationReportedAirportsService,
    private store: RequestTaskStore,
  ) {}

  private fb = inject(FormBuilder);
  private _form: FormGroup;

  private destroy$ = new Subject<void>();

  get form(): FormGroup {
    if (!this._form) {
      this.buildForm();
    }

    return this._form;
  }

  destroyForm() {
    this.destroy$.next();
    this.destroy$.complete();
    this._form = null;
  }

  setFormValue(
    aviationAerUkEtsCorsiaAggregatedEmissionsData:
      | AviationAerUkEtsAggregatedEmissionsData
      | AviationAerCorsiaAggregatedEmissionsData
      | null,
  ): void {
    this.store.pipe(aerQuery.selectIsCorsia, take(1)).subscribe((isCorsia) => {
      if (isCorsia && aviationAerUkEtsCorsiaAggregatedEmissionsData) {
        this.form.patchValue({
          aggregatedEmissionDataDetails: (
            aviationAerUkEtsCorsiaAggregatedEmissionsData as AviationAerCorsiaAggregatedEmissionsData
          ).aggregatedEmissionDataDetails,
        });
      } else if (!isCorsia && aviationAerUkEtsCorsiaAggregatedEmissionsData) {
        this.form.patchValue({
          aggregatedEmissionDataDetails: (
            aviationAerUkEtsCorsiaAggregatedEmissionsData as AviationAerUkEtsAggregatedEmissionsData
          ).aggregatedEmissionDataDetails,
        });
      }
    });
  }

  getFormValue(): AviationAerUkEtsAggregatedEmissionsData | AviationAerCorsiaAggregatedEmissionsData {
    const formValue = this.form.value;
    return {
      aggregatedEmissionDataDetails: formValue.aggregatedEmissionDataDetails,
    };
  }

  private buildForm() {
    combineLatest([this.store.pipe(aerQuery.selectIsCorsia), this.store.pipe(aerQuery.selectAerYear)])
      .pipe(take(1))
      .subscribe(([isCorsia, aerYear]) => {
        if (isCorsia) {
          const formGroup = this.fb.group(
            {
              aggregatedEmissionDataDetails: [
                null,
                [
                  Validators.required,
                  this.uniqueFuelTypePerAerodromePairValidator(
                    'You cannot enter the same aerodrome pair and fuel type',
                  ),
                  this.validateFuelType(getFuelTypeValues(true), 'Fuel type must be a standard fuel type'),
                  this.validateAerodromes('Departure and arrival aerodromes must be different'),
                  this.validateNumericValues('Enter a number'),
                ],
                [
                  this.combinedAerodromeValidatorCorsia(
                    aerYear,
                    'One or more aerodrome of departure ICAO codes are invalid',
                    'One or more aerodrome of arrival ICAO codes are invalid',
                    'Departure and arrival aerodromes must be in different ICAO states',
                  ),
                ],
              ],
            },
            { updateOn: 'change' },
          );
          return (this._form = formGroup);
        } else {
          const formGroup = this.fb.group(
            {
              aggregatedEmissionDataDetails: [
                null,
                [
                  Validators.required,
                  this.uniqueFuelTypePerAerodromePairValidator(
                    'You cannot enter the same aerodrome pair and fuel type',
                  ),
                  this.validateFuelType(getFuelTypeValues(), 'Fuel type must be a standard fuel type'),
                  this.validateAerodromes('Departure and arrival aerodromes must be different'),
                  this.validateNumericValues('Enter a number'),
                ],
                [
                  this.combinedAerodromeValidatorUkEts(
                    aerYear,
                    'One or more aerodrome of departure ICAO codes are invalid',
                    'Aerodrome of departure codes must be within the scope of UK ETS',
                    'One or more aerodrome of arrival ICAO codes are invalid',
                    'Aerodrome of arrival codes must be within the scope of UK ETS',
                  ),
                ],
              ],
            },
            { updateOn: 'change' },
          );
          return (this._form = formGroup);
        }
      });
  }

  uniqueFuelTypePerAerodromePairValidator(message: string) {
    return (control: FormControl): ValidationErrors | null => {
      const data = control.value;

      if (!Array.isArray(data)) {
        return null;
      }

      const combinations = new Map<string, number[]>();
      const invalidColumns = [];
      const rows = [];

      data.forEach((entry, index) => {
        const { airportFrom, airportTo, fuelType } = entry;
        const key = `${airportFrom.icao}-${airportTo.icao}-${fuelType}`;

        const existingRows = combinations.get(key);
        if (existingRows) {
          existingRows.push(index + 1);
        } else {
          combinations.set(key, [index + 1]);
        }
      });

      combinations.forEach((indices) => {
        if (indices.length > 1) {
          invalidColumns.push('fuelType');
          indices.forEach((rowIndex) => {
            rows.push({
              rowIndex: rowIndex,
            });
          });
        }
      });

      if (rows.length > 0) {
        return {
          multipleFuelTypes: {
            rows,
            columns: this.mapFieldsToColumnNames(invalidColumns),
            message,
          },
        };
      }

      return null;
    };
  }

  validateFuelType(fuelTypes: string[], message: string) {
    return (control: FormControl): { [key: string]: any } | null => {
      const data = control.value;

      if (!Array.isArray(data)) {
        return null;
      }

      const rows = [];

      for (let i = 0; i < data.length; i++) {
        const entry = data[i];
        if (entry && !fuelTypes.includes(entry.fuelType)) {
          rows.push({
            rowIndex: i + 1,
          });
        }
      }

      if (rows.length > 0) {
        return {
          invalidFuelType: {
            rows,
            columns: this.mapFieldsToColumnNames(['fuelType']),
            message,
          },
        };
      }

      return null;
    };
  }

  validateAerodromes(message: string) {
    return (control: FormControl): { [key: string]: any } | null => {
      const data = control.value;

      if (!Array.isArray(data)) {
        return null;
      }

      const rows = [];
      const columnsSet = new Set<string>();

      for (let i = 0; i < data.length; i++) {
        const entry = data[i];

        if (entry && entry.airportFrom.icao === entry.airportTo.icao) {
          columnsSet.add('airportFrom');
          columnsSet.add('airportTo');
          rows.push({
            rowIndex: i + 1,
          });
        }
      }

      const invalidColumns = Array.from(columnsSet);

      if (rows.length > 0) {
        return {
          sameAerodromes: {
            rows,
            columns: this.mapFieldsToColumnNames(invalidColumns),
            message,
          },
        };
      }

      return null;
    };
  }

  validateNumericValues(message: string) {
    return (control: FormControl): { [key: string]: any } | null => {
      const data = control.value;

      if (!Array.isArray(data)) {
        return null;
      }

      const rows = [];
      const invalidColumnsSet = new Set<string>();

      for (let i = 0; i < data.length; i++) {
        const entry = data[i];

        if (entry) {
          const fuelConsumption = parseFloat(entry.fuelConsumption);
          const decimalPlaces = (fuelConsumption.toString().split('.')[1] || '').length;

          if (isNaN(fuelConsumption) || fuelConsumption <= 0 || decimalPlaces > 3) {
            invalidColumnsSet.add('fuelConsumption');
            rows.push({
              rowIndex: i + 1,
            });
          }

          const flightsNumber = entry.flightsNumber;

          if (!Number.isInteger(flightsNumber) || flightsNumber <= 0) {
            invalidColumnsSet.add('flightsNumber');
            rows.push({
              rowIndex: i + 1,
            });
          }
        }
      }

      if (rows.length > 0) {
        return {
          invalidNumericValue: {
            rows,
            columns: this.mapFieldsToColumnNames([...invalidColumnsSet]),
            message,
          },
        };
      }

      return null;
    };
  }

  combinedAerodromeValidatorUkEts(
    aerYear: number,
    messageForCodeDeparture: string,
    messageForTypeDeparture: string,
    messageForCodeArrival: string,
    messageForTypeArrival: string,
  ): AsyncValidatorFn {
    return (control: FormControl): Observable<ValidationErrors | null> => {
      const data = control.value;

      if (!Array.isArray(data)) {
        return of(null);
      }

      const icaosFrom = data.map((entry) => entry.airportFrom.icao);
      const icaosTo = data.map((entry) => entry.airportTo.icao);

      return forkJoin({
        airportsFrom: this.aviationReportedAirportsService.getReportedAirports({ icaos: icaosFrom, year: aerYear }),
        airportsTo: this.aviationReportedAirportsService.getReportedAirports({ icaos: icaosTo, year: aerYear }),
      }).pipe(
        map(({ airportsFrom, airportsTo }) => {
          const validIcaoCodesFrom = airportsFrom.map((airport) => airport.icao);
          const validIcaoCodesTo = airportsTo.map((airport) => airport.icao);

          const validCombinations = [
            { from: AirportTypes.UKETS_FLIGHTS_TO_EEA_REPORTED, to: AirportTypes.UKETS_FLIGHTS_TO_EEA_REPORTED },
            { from: AirportTypes.UKETS_FLIGHTS_TO_EEA_REPORTED, to: AirportTypes.UKETS_FLIGHTS_TO_EEA_NOT_REPORTED },
            { from: AirportTypes.UKETS_FLIGHTS_TO_EEA_REPORTED, to: AirportTypes.EEA_COUNTRY },
            { from: AirportTypes.UKETS_FLIGHTS_TO_EEA_REPORTED, to: AirportTypes.EFTA_COUNTRY },
            { from: AirportTypes.UKETS_FLIGHTS_TO_EEA_NOT_REPORTED, to: AirportTypes.UKETS_FLIGHTS_TO_EEA_REPORTED },
          ];

          const invalidCodeDepartureRows = [];
          const invalidTypeDepartureRows = [];
          const invalidCodeArrivalRows = [];
          const invalidTypeArrivalRows = [];

          data.forEach((entry, index) => {
            const airportFrom = airportsFrom.find((airport) => airport.icao === entry.airportFrom.icao);
            const airportTo = airportsTo.find((airport) => airport.icao === entry.airportTo.icao);

            if (!validIcaoCodesFrom.includes(entry.airportFrom.icao)) {
              invalidCodeDepartureRows.push({ rowIndex: index + 1 });
            }

            if (
              airportFrom &&
              !validCombinations.some((combination) => combination.from === airportFrom?.countryType)
            ) {
              invalidTypeDepartureRows.push({ rowIndex: index + 1 });
            }

            if (!validIcaoCodesTo.includes(entry.airportTo.icao)) {
              invalidCodeArrivalRows.push({ rowIndex: index + 1 });
            }

            if (
              airportTo &&
              airportFrom &&
              !validCombinations.some(
                (combination) =>
                  combination.from === airportFrom?.countryType && combination.to === airportTo?.countryType,
              )
            ) {
              invalidTypeArrivalRows.push({ rowIndex: index + 1 });
            }
          });

          const errors = {};

          if (invalidCodeDepartureRows.length > 0) {
            errors['invalidDepartureAerodromeCode'] = {
              rows: invalidCodeDepartureRows,
              columns: this.mapFieldsToColumnNames(['airportFrom']),
              message: messageForCodeDeparture,
            };
          }

          if (invalidTypeDepartureRows.length > 0) {
            errors['invalidDepartureAerodrome'] = {
              rows: invalidTypeDepartureRows,
              columns: this.mapFieldsToColumnNames(['airportFrom']),
              message: messageForTypeDeparture,
            };
          }

          if (invalidCodeArrivalRows.length > 0) {
            errors['invalidArrivalAerodromeCode'] = {
              rows: invalidCodeArrivalRows,
              columns: this.mapFieldsToColumnNames(['airportTo']),
              message: messageForCodeArrival,
            };
          }

          if (invalidTypeArrivalRows.length > 0) {
            errors['invalidArrivalAerodrome'] = {
              rows: invalidTypeArrivalRows,
              columns: this.mapFieldsToColumnNames(['airportTo']),
              message: messageForTypeArrival,
            };
          }

          return Object.keys(errors).length ? errors : null;
        }),
      );
    };
  }

  combinedAerodromeValidatorCorsia(
    aerYear: number,
    messageForCodeDeparture: string,
    messageForCodeArrival: string,
    messageForInvalidIcao: string,
  ): AsyncValidatorFn {
    return (control: FormControl): Observable<ValidationErrors | null> => {
      const data = control.value;

      if (!Array.isArray(data)) {
        return of(null);
      }

      const icaosFrom = data.map((entry) => entry.airportFrom.icao);
      const icaosTo = data.map((entry) => entry.airportTo.icao);

      return forkJoin({
        airportsFrom: this.aviationReportedAirportsService.getReportedAirports({ icaos: icaosFrom, year: aerYear }),
        airportsTo: this.aviationReportedAirportsService.getReportedAirports({ icaos: icaosTo, year: aerYear }),
      }).pipe(
        map(({ airportsFrom, airportsTo }) => {
          const validIcaoCodesFrom = airportsFrom.map((airport) => airport.icao);
          const validIcaoCodesTo = airportsTo.map((airport) => airport.icao);

          const invalidCodeDepartureRows = [];
          const invalidCodeArrivalRows = [];
          const invalidCountryMatchRows = [];

          data.forEach((entry, index) => {
            const departureAirport = airportsFrom.find((airport) => airport.icao === entry.airportFrom.icao);
            const arrivalAirport = airportsTo.find((airport) => airport.icao === entry.airportTo.icao);

            if (departureAirport?.state === arrivalAirport?.state) {
              invalidCountryMatchRows.push({ rowIndex: index + 1 });
            }

            if (!validIcaoCodesFrom.includes(entry.airportFrom.icao)) {
              invalidCodeDepartureRows.push({ rowIndex: index + 1 });
            }

            if (!validIcaoCodesTo.includes(entry.airportTo.icao)) {
              invalidCodeArrivalRows.push({ rowIndex: index + 1 });
            }
          });

          const errors = {};

          if (invalidCodeDepartureRows.length > 0) {
            errors['invalidDepartureAerodromeCode'] = {
              rows: invalidCodeDepartureRows,
              columns: this.mapFieldsToColumnNames(['airportFrom']),
              message: messageForCodeDeparture,
            };
          }

          if (invalidCodeArrivalRows.length > 0) {
            errors['invalidArrivalAerodromeCode'] = {
              rows: invalidCodeArrivalRows,
              columns: this.mapFieldsToColumnNames(['airportTo']),
              message: messageForCodeArrival,
            };
          }

          if (invalidCountryMatchRows.length > 0) {
            errors['invalidCountryMatch'] = {
              rows: invalidCountryMatchRows,
              columns: this.mapFieldsToColumnNames(['airportFrom', 'airportTo']),
              message: messageForInvalidIcao,
            };
          }

          return Object.keys(errors).length ? errors : null;
        }),
      );
    };
  }

  private mapFieldsToColumnNames(fields) {
    const fieldColumnMap = {
      airportFrom: 'A',
      airportTo: 'B',
      fuelType: 'C',
      fuelConsumption: 'D',
      flightsNumber: 'E',
    };
    return fields.map((field) => fieldColumnMap[field]);
  }
}
