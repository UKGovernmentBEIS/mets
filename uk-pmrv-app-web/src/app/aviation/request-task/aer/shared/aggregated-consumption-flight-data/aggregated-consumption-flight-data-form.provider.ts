import { inject, Injectable } from '@angular/core';
import { AsyncValidatorFn, FormBuilder, FormControl, FormGroup, ValidationErrors, Validators } from '@angular/forms';

import { combineLatest, forkJoin, map, Observable, of, Subject, take } from 'rxjs';

import { aerQuery } from '@aviation/request-task/aer/shared/aer.selectors';
import { RequestTaskStore } from '@aviation/request-task/store';
import { getFuelTypeValues } from '@aviation/shared/components/emp/emission-sources/aircraft-type/fuel-types';

import {
  AviationAerCorsiaAggregatedEmissionDataDetails,
  AviationAerCorsiaAggregatedEmissionsData,
  AviationAerUkEtsAggregatedEmissionDataDetails,
  AviationAerUkEtsAggregatedEmissionsData,
  AviationReportedAirportsService,
  AviationRptAirportsDTO,
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
    aviationAerAggregatedEmissionsData:
      | AviationAerUkEtsAggregatedEmissionsData
      | AviationAerCorsiaAggregatedEmissionsData
      | null,
  ): void {
    this.store.pipe(aerQuery.selectIsCorsia, take(1)).subscribe((isCorsia) => {
      if (isCorsia && aviationAerAggregatedEmissionsData) {
        this.form.patchValue({
          aggregatedEmissionDataDetails: (
            aviationAerAggregatedEmissionsData as AviationAerCorsiaAggregatedEmissionsData
          ).aggregatedEmissionDataDetails,
        });
      } else if (!isCorsia && aviationAerAggregatedEmissionsData) {
        this.form.patchValue({
          aggregatedEmissionDataDetails: (aviationAerAggregatedEmissionsData as AviationAerUkEtsAggregatedEmissionsData)
            .aggregatedEmissionDataDetails,
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
              aggregatedEmissionDataDetails: [null, [Validators.required], [this.businessValidatorsCorsia(aerYear)]],
            },
            { updateOn: 'change' },
          );
          return (this._form = formGroup);
        } else {
          const formGroup = this.fb.group(
            {
              aggregatedEmissionDataDetails: [null, [Validators.required], [this.businessValidatorsUkEts(aerYear)]],
            },
            { updateOn: 'change' },
          );
          return (this._form = formGroup);
        }
      });
  }

  uniqueFuelTypePerAerodromePairValidator(control: FormControl): ValidationErrors | null {
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
          message: 'You cannot enter the same aerodrome pair and fuel type',
        },
      };
    }

    return null;
  }

  validateFuelType(control: FormControl, fuelTypes: string[]): ValidationErrors | null {
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
          message: 'Fuel type must be a standard fuel type',
        },
      };
    }

    return null;
  }

  validateFromAndToAerodromesAreDifferent(control: FormControl): ValidationErrors | null {
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
          message: 'Departure and arrival aerodromes must be different',
        },
      };
    }

    return null;
  }

  validateNumericValues(control: FormControl): ValidationErrors | null {
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
          message: 'Enter a number',
        },
      };
    }

    return null;
  }

  businessValidatorsUkEts(aerYear: number): AsyncValidatorFn {
    return (control: FormControl): Observable<ValidationErrors | null> => {
      const uniqueFuelTypePerAerodromePairValidatorResult = this.uniqueFuelTypePerAerodromePairValidator(control);
      const fuleTypeValidatorResult = this.validateFuelType(control, getFuelTypeValues());
      const fromAndToAerodromesAreDifferentValidatorResult = this.validateFromAndToAerodromesAreDifferent(control);
      const numericValuesValidatorResult = this.validateNumericValues(control);

      const data = control.value;

      if (!Array.isArray(data)) {
        return of(null);
      }

      const icaosFrom = data.map((entry) => entry.airportFrom.icao);
      const icaosTo = data.map((entry) => entry.airportTo.icao);

      const airportsFrom = this.aviationReportedAirportsService.getReportedAirports({
        icaos: icaosFrom,
        year: aerYear,
      });
      const airportsTo = this.aviationReportedAirportsService.getReportedAirports({ icaos: icaosTo, year: aerYear });

      return forkJoin([
        of(uniqueFuelTypePerAerodromePairValidatorResult),
        of(fuleTypeValidatorResult),
        of(fromAndToAerodromesAreDifferentValidatorResult),
        of(numericValuesValidatorResult),
        airportsFrom,
        airportsTo,
      ]).pipe(
        map(
          ([
            uniqueFuelTypePerAerodromePairValidatorResult,
            fuleTypeValidatorResult,
            fromAndToAerodromesAreDifferentValidatorResult,
            numericValuesValidatorResult,
            airportsFrom,
            airportsTo,
          ]) => {
            let errors = {};

            if (uniqueFuelTypePerAerodromePairValidatorResult) {
              errors = { ...errors, ...uniqueFuelTypePerAerodromePairValidatorResult };
            }

            if (fuleTypeValidatorResult) {
              errors = { ...errors, ...fuleTypeValidatorResult };
            }

            if (fromAndToAerodromesAreDifferentValidatorResult) {
              errors = { ...errors, ...fromAndToAerodromesAreDifferentValidatorResult };
            }

            if (numericValuesValidatorResult) {
              errors = { ...errors, ...numericValuesValidatorResult };
            }

            const validIcaoCodesFrom = airportsFrom.map((airport) => airport.icao);
            const validIcaoCodesTo = airportsTo.map((airport) => airport.icao);

            const validUkEtsFromToCountryTypes: {
              from: AviationRptAirportsDTO['countryType'];
              to: AviationRptAirportsDTO['countryType'];
            }[] = [
              { from: 'UKETS_FLIGHTS_TO_EEA_REPORTED', to: 'UKETS_FLIGHTS_TO_EEA_REPORTED' },
              { from: 'UKETS_FLIGHTS_TO_EEA_REPORTED', to: 'UKETS_FLIGHTS_TO_EEA_NOT_REPORTED' },
              { from: 'UKETS_FLIGHTS_TO_EEA_REPORTED', to: 'EEA_COUNTRY' },
              { from: 'UKETS_FLIGHTS_TO_EEA_REPORTED', to: 'EFTA_COUNTRY' },
              { from: 'UKETS_FLIGHTS_TO_EEA_NOT_REPORTED', to: 'UKETS_FLIGHTS_TO_EEA_REPORTED' },
            ];

            const invalidDepartureAerodromeCodeRows = [];
            const notUkEtsDepartureAerodromeCodeRows = [];
            const invalidArrivalAerodromeCodeRows = [];
            const notUkEtsArrivalAerodromeCodeRows = [];
            const notUkEtsDepartureArrivalAerodromeCodesPairRows = [];

            data.forEach((entry, index) => {
              if (!validIcaoCodesFrom.includes(entry.airportFrom.icao)) {
                invalidDepartureAerodromeCodeRows.push({ rowIndex: index + 1 });
              }

              const airportFrom = airportsFrom.find((airport) => airport.icao === entry.airportFrom.icao);
              const notUkEtsDepartureAerodromeCode =
                airportFrom &&
                !validUkEtsFromToCountryTypes.some((combination) => combination.from === airportFrom.countryType);
              if (notUkEtsDepartureAerodromeCode) {
                notUkEtsDepartureAerodromeCodeRows.push({ rowIndex: index + 1 });
              }

              if (!validIcaoCodesTo.includes(entry.airportTo.icao)) {
                invalidArrivalAerodromeCodeRows.push({ rowIndex: index + 1 });
              }

              const airportTo = airportsTo.find((airport) => airport.icao === entry.airportTo.icao);
              const notUkEtsArrivalAerodromeCode =
                airportTo &&
                !validUkEtsFromToCountryTypes.some((combination) => combination.to === airportTo.countryType);
              if (notUkEtsArrivalAerodromeCode) {
                notUkEtsArrivalAerodromeCodeRows.push({ rowIndex: index + 1 });
              }

              if (
                airportTo &&
                airportFrom &&
                !notUkEtsDepartureAerodromeCode &&
                !notUkEtsArrivalAerodromeCode &&
                !validUkEtsFromToCountryTypes.some(
                  (combination) =>
                    combination.from === airportFrom.countryType && combination.to === airportTo.countryType,
                )
              ) {
                notUkEtsDepartureArrivalAerodromeCodesPairRows.push({ rowIndex: index + 1 });
              }
            });

            if (invalidDepartureAerodromeCodeRows.length > 0) {
              errors['invalidDepartureAerodromeCode'] = {
                rows: invalidDepartureAerodromeCodeRows,
                columns: this.mapFieldsToColumnNames(['airportFrom']),
                message: 'One or more aerodrome of departure ICAO codes are invalid',
              };
            }

            if (notUkEtsDepartureAerodromeCodeRows.length > 0) {
              errors['notUkEtsDepartureAerodromeCode'] = {
                rows: notUkEtsDepartureAerodromeCodeRows,
                columns: this.mapFieldsToColumnNames(['airportFrom']),
                message: 'Aerodrome of departure codes must be within the scope of UK ETS',
              };
            }

            if (invalidArrivalAerodromeCodeRows.length > 0) {
              errors['invalidArrivalAerodromeCode'] = {
                rows: invalidArrivalAerodromeCodeRows,
                columns: this.mapFieldsToColumnNames(['airportTo']),
                message: 'One or more aerodrome of arrival ICAO codes are invalid',
              };
            }

            if (notUkEtsArrivalAerodromeCodeRows.length > 0) {
              errors['notUkEtsArrivalAerodromeCode'] = {
                rows: notUkEtsArrivalAerodromeCodeRows,
                columns: this.mapFieldsToColumnNames(['airportTo']),
                message: 'Aerodrome of arrival codes must be within the scope of UK ETS',
              };
            }

            if (notUkEtsDepartureArrivalAerodromeCodesPairRows.length > 0) {
              errors['notUkEtsDepartureArrivalAerodromeCodesPair'] = {
                rows: notUkEtsDepartureArrivalAerodromeCodesPairRows,
                columns: this.mapFieldsToColumnNames(['airportTo']),
                message: 'Aerodrome of arrival codes must be within the scope of UK ETS',
              };
            }

            return Object.keys(errors).length ? errors : null;
          },
        ),
      );
    };
  }

  businessValidatorsCorsia(aerYear: number): AsyncValidatorFn {
    return (control: FormControl): Observable<ValidationErrors | null> => {
      const uniqueFuelTypePerAerodromePairValidatorResult = this.uniqueFuelTypePerAerodromePairValidator(control);
      const fuelTypeValidatorResult = this.validateFuelType(control, getFuelTypeValues(true));
      const fromAndToAerodromesAreDifferentValidatorResult = this.validateFromAndToAerodromesAreDifferent(control);
      const numericValuesValidatorResult = this.validateNumericValues(control);

      const data = control.value;

      if (!Array.isArray(data)) {
        return of(null);
      }

      const icaosFrom = data.map((entry) => entry.airportFrom.icao);
      const icaosTo = data.map((entry) => entry.airportTo.icao);

      const airportsFrom = this.aviationReportedAirportsService.getReportedAirports({
        icaos: icaosFrom,
        year: aerYear,
      });
      const airportsTo = this.aviationReportedAirportsService.getReportedAirports({ icaos: icaosTo, year: aerYear });

      return forkJoin([
        of(uniqueFuelTypePerAerodromePairValidatorResult),
        of(fuelTypeValidatorResult),
        of(fromAndToAerodromesAreDifferentValidatorResult),
        of(numericValuesValidatorResult),
        airportsFrom,
        airportsTo,
      ]).pipe(
        map(
          ([
            uniqueFuelTypePerAerodromePairValidatorResult,
            fuelTypeValidatorResult,
            fromAndToAerodromesAreDifferentValidatorResult,
            numericValuesValidatorResult,
            airportsFrom,
            airportsTo,
          ]) => {
            let errors = {};

            if (uniqueFuelTypePerAerodromePairValidatorResult) {
              errors = { ...errors, ...uniqueFuelTypePerAerodromePairValidatorResult };
            }

            if (fuelTypeValidatorResult) {
              errors = { ...errors, ...fuelTypeValidatorResult };
            }

            if (fromAndToAerodromesAreDifferentValidatorResult) {
              errors = { ...errors, ...fromAndToAerodromesAreDifferentValidatorResult };
            }

            if (numericValuesValidatorResult) {
              errors = { ...errors, ...numericValuesValidatorResult };
            }

            const validIcaoCodesFrom = airportsFrom.map((airport) => airport.icao);
            const validIcaoCodesTo = airportsTo.map((airport) => airport.icao);
            const invalidCodeDepartureRows = [];
            const invalidCodeArrivalRows = [];
            const invalidCountryMatchRows = [];

            data.forEach((entry, index) => {
              const departureAirport = airportsFrom.find((airport) => airport.icao === entry.airportFrom.icao);
              const arrivalAirport = airportsTo.find((airport) => airport.icao === entry.airportTo.icao);

              if (!validIcaoCodesFrom.includes(entry.airportFrom.icao)) {
                invalidCodeDepartureRows.push({ rowIndex: index + 1 });
              }

              if (!validIcaoCodesTo.includes(entry.airportTo.icao)) {
                invalidCodeArrivalRows.push({ rowIndex: index + 1 });
              }

              if (
                fromAndToAerodromesAreDifferentValidatorResult == null &&
                departureAirport?.state === arrivalAirport?.state
              ) {
                invalidCountryMatchRows.push({ rowIndex: index + 1 });
              }
            });

            if (invalidCodeDepartureRows.length > 0) {
              errors['invalidDepartureAerodromeCode'] = {
                rows: invalidCodeDepartureRows,
                columns: this.mapFieldsToColumnNames(['airportFrom']),
                message: 'One or more aerodrome of departure ICAO codes are invalid',
              };
            }

            if (invalidCodeArrivalRows.length > 0) {
              errors['invalidArrivalAerodromeCode'] = {
                rows: invalidCodeArrivalRows,
                columns: this.mapFieldsToColumnNames(['airportTo']),
                message: 'One or more aerodrome of arrival ICAO codes are invalid',
              };
            }

            if (invalidCountryMatchRows.length > 0) {
              errors['invalidCountryMatch'] = {
                rows: invalidCountryMatchRows,
                columns: this.mapFieldsToColumnNames(['airportFrom', 'airportTo']),
                message: 'Departure and arrival aerodromes must be in different ICAO states',
              };
            }

            return Object.keys(errors).length ? errors : null;
          },
        ),
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
