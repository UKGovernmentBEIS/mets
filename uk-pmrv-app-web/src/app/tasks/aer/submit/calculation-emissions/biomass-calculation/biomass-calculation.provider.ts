import { AbstractControl, UntypedFormBuilder, UntypedFormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { AerApplicationSubmitRequestTaskPayload, CalculationOfCO2Emissions } from 'pmrv-api';

import { AER_CALCULATION_EMISSIONS_FORM } from '../calculation-emissions';

export const biomassCalculationFormProvider = {
  provide: AER_CALCULATION_EMISSIONS_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, ActivatedRoute],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore, route: ActivatedRoute) => {
    const state = store.getValue();
    const disabled = !state.isEditable;

    const payload: AerApplicationSubmitRequestTaskPayload = state.requestTaskItem.requestTask.payload;

    const sourceStreamEmission = route.snapshot.paramMap.get('index')
      ? (payload.aer.monitoringApproachEmissions.CALCULATION_CO2 as CalculationOfCO2Emissions)?.sourceStreamEmissions[
          Number(route.snapshot.paramMap.get('index'))
        ]
      : null;

    return fb.group(
      {
        ...getBiomassFormControls(sourceStreamEmission, disabled),
      },
      { ...getBiomassFormValidators() },
    );
  },
};

export const fiveDecimalPlacesValidator = GovukValidators.pattern(
  '-?[0-9]*\\.?[0-9]{0,5}',
  'Enter a number between 0 and 100, up to 5 decimal places',
);

export function percentageValidator(): ValidatorFn {
  return (control: AbstractControl): { [key: string]: string } | null => {
    let error = null;
    if (control.value) {
      if (control.value < 0 || control.value > 100) {
        error = { invalidLength: `Enter a number between 0 and 100, up to 5 decimal places` };
      }
    }
    return error;
  };
}

function maxSumValidator(): ValidatorFn {
  return (group: UntypedFormGroup): ValidationErrors => {
    const biomassPercentage = group.get('biomassPercentage').value;
    const nonSustainableBiomassPercentage = group.get('nonSustainableBiomassPercentage').value;

    if (Number(biomassPercentage) + Number(nonSustainableBiomassPercentage) > 100) {
      return { invalidPercentage: `The total biomass values entered must be up to or equal to 100%` };
    }
    return null;
  };
}

export function getBiomassFormControls(sourceStreamEmission, disabled) {
  const controls = {
    biomassPercentage: [
      {
        value: sourceStreamEmission?.biomassPercentages?.biomassPercentage ?? null,
        disabled: disabled,
      },
      {
        validators: [
          GovukValidators.required('Enter the biomass fraction'),
          percentageValidator(),
          fiveDecimalPlacesValidator,
        ],
      },
    ],
    nonSustainableBiomassPercentage: [
      {
        value: sourceStreamEmission?.biomassPercentages?.nonSustainableBiomassPercentage ?? null,
        disabled: disabled,
      },
      {
        validators: [
          GovukValidators.required('Enter the non-sustainable biomass fraction'),
          percentageValidator(),
          fiveDecimalPlacesValidator,
        ],
      },
    ],
  };

  return controls;
}

export function getBiomassFormValidators() {
  return {
    validators: [maxSumValidator()],
  };
}
