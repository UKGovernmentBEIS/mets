import { AbstractControl, UntypedFormBuilder, UntypedFormGroup, ValidationErrors, ValidatorFn } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { GovukValidators } from 'govuk-components';

import { AerApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { AER_MEASUREMENT_FORM } from '../measurement-status';

export const biomassCalculationFormProvider = {
  provide: AER_MEASUREMENT_FORM,
  deps: [UntypedFormBuilder, CommonTasksStore, ActivatedRoute],
  useFactory: (fb: UntypedFormBuilder, store: CommonTasksStore, route: ActivatedRoute) => {
    const taskKey = route.snapshot.data.taskKey;
    const state = store.getValue();
    const disabled = !state.isEditable;

    const payload: AerApplicationSubmitRequestTaskPayload = state.requestTaskItem.requestTask.payload;

    const emissionPointEmission = route.snapshot.paramMap.get('index')
      ? (payload.aer?.monitoringApproachEmissions[taskKey] as any)?.emissionPointEmissions?.[
          Number(route.snapshot.paramMap.get('index'))
        ]
      : null;

    return fb.group(
      {
        biomassPercentage: [
          {
            value: emissionPointEmission?.biomassPercentages?.biomassPercentage ?? null,
            disabled: disabled,
          },
          {
            validators: [
              GovukValidators.required('Enter the biomass fraction'),
              numberValidator(),
              GovukValidators.maxDecimalsValidator(5),
            ],
          },
        ],
        nonSustainableBiomassPercentage: [
          {
            value: emissionPointEmission?.biomassPercentages?.nonSustainableBiomassPercentage ?? null,
            disabled: disabled,
          },
          {
            validators: [
              GovukValidators.required('Enter the non-sustainable biomass fraction'),
              numberValidator(),
              GovukValidators.maxDecimalsValidator(5),
            ],
          },
        ],
      },
      {
        validators: [maxSumValidator()],
      },
    );
  },
};

function numberValidator(): ValidatorFn {
  return (control: AbstractControl): { [key: string]: string } | null => {
    let error = null;
    if (control.value) {
      if (control.value < 0 || control.value > 100) {
        error = { invalidLength: `Enter a number between 0 and 100` };
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
