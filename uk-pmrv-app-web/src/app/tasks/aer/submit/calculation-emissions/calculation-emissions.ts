import { InjectionToken } from '@angular/core';
import { AbstractControl, UntypedFormGroup, ValidatorFn } from '@angular/forms';

import {
  AerApplicationSubmitRequestTaskPayload,
  CalculationOfCO2Emissions,
  CalculationSourceStreamEmission,
} from 'pmrv-api';

export const AER_CALCULATION_EMISSIONS_FORM = new InjectionToken<UntypedFormGroup>('Aer calculation emissions form');

export function buildParameterCalculationMethodData(
  sourceStreamEmission: CalculationSourceStreamEmission,
  parameterCalculationMethodData?: any,
  emissionCalculationParamValuesData?: any,
) {
  const emissionCalculationParamValues =
    (sourceStreamEmission?.parameterCalculationMethod as any)?.emissionCalculationParamValues ||
    emissionCalculationParamValuesData
      ? {
          emissionCalculationParamValues: {
            ...(sourceStreamEmission?.parameterCalculationMethod as any)?.emissionCalculationParamValues,
            totalReportableEmissions: null,
            totalSustainableBiomassEmissions: null,
            calculationCorrect: null,
            providedEmissions: null,
            ...emissionCalculationParamValuesData,
          },
        }
      : {};

  const data =
    sourceStreamEmission?.parameterCalculationMethod || parameterCalculationMethodData
      ? {
          parameterCalculationMethod: {
            ...sourceStreamEmission.parameterCalculationMethod,
            ...parameterCalculationMethodData,
            ...emissionCalculationParamValues,
          },
        }
      : {};

  return data;
}

export function buildTaskData(payload: AerApplicationSubmitRequestTaskPayload, sourceStreamEmissions) {
  const monitoringApproachEmissions = payload.aer.monitoringApproachEmissions;
  const calculation = monitoringApproachEmissions.CALCULATION_CO2 as CalculationOfCO2Emissions;

  const data = {
    monitoringApproachEmissions: {
      ...monitoringApproachEmissions,
      CALCULATION_CO2: {
        ...calculation,
        type: 'CALCULATION_CO2',
        sourceStreamEmissions: sourceStreamEmissions,
      },
    },
  };

  return data;
}

// Validators

// TODO should be replaced with rangeIntegerPartValidator once input-type="number" is used in respective html fields
export function maxIntegerPartValidator(): ValidatorFn {
  return (control: AbstractControl): { [key: string]: string } | null => {
    if (control.value) {
      const minAllowedValue = -999999999999;
      const maxAllowedValue = Math.abs(minAllowedValue);

      const intergerPart = Math.floor(control.value);
      return isNaN(intergerPart) || intergerPart > maxAllowedValue || intergerPart < minAllowedValue
        ? { invalidLength: `Enter a number up to 12 figures` }
        : null;
    }
    return null;
  };
}

// export function rangeIntegerPartValidator(): ValidatorFn {
//   return (control: AbstractControl): { [key: string]: string } | null => {
//     if (control.value) {
//       if (isNaN(control.value)) {
//         return null;
//       }

//       const minAllowedValue = -999999999999;
//       const maxAllowedValue = Math.abs(minAllowedValue);

//       const integerPart = Math.floor(control.value);

//       return integerPart > maxAllowedValue || integerPart < minAllowedValue
//         ? { invalidLength: `Enter a number up to 12 figures` }
//         : null;
//     }
//     return null;
//   };
// }
