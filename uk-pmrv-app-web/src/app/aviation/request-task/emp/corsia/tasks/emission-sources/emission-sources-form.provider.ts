import { Provider } from '@angular/core';
import { AbstractControl, FormArray, FormBuilder, FormGroup, ValidatorFn } from '@angular/forms';

import { transformAircraftTypeDescription } from '@aviation/shared/pipes/aircraft-type-description.pipe';

import { GovukValidators } from 'govuk-components';

import { TASK_FORM_PROVIDER } from '../../../../task-form.provider';
import { AircraftTypeDetailsFormModelCorsia } from './aircraft-type/form/types';
import { EmissionSourcesFormModelCorsia } from './emission-sources-form.model';

export const EmissionSourcesCorsiaFormProvider: Provider = {
  provide: TASK_FORM_PROVIDER,
  deps: [FormBuilder],
  useFactory: (fb: FormBuilder) => {
    return fb.group(
      {
        aircraftTypes: fb.array([], AircraftTypeValidator),
      },
      { updateOn: 'change' },
    );
  },
};

const AircraftTypeValidator: ValidatorFn = (array: FormArray<AircraftTypeDetailsFormModelCorsia>) => {
  const hasOneUsedAircraft = !!array.getRawValue().length;
  if (hasOneUsedAircraft) return null;
  return {
    requiredInUseAircraft: 'You must add at least one item to the aircraft currently in use table',
  };
};

export const FUMMValidator = (array: FormArray<AircraftTypeDetailsFormModelCorsia>) => {
  // aircraft types
  const ats = array.getRawValue();
  const atsWithNoMethod = ats.filter((at) => !at.fuelConsumptionMeasuringMethod);
  if (!atsWithNoMethod.length) return null;
  const errors = {};
  atsWithNoMethod.forEach((at) => {
    const atTitle = transformAircraftTypeDescription(at.aircraftTypeInfo);
    const description = atTitle + ' is missing a method';
    errors[at.aircraftTypeInfo.model] = description;
  });
  if (Object.keys(errors).length) array.markAsTouched();
  return errors;
};

export function hasMoreThanOneMonitoringMethod(form: FormGroup<EmissionSourcesFormModelCorsia>): boolean {
  const methods = new Set();
  form.getRawValue().aircraftTypes.forEach((at) => {
    if (at.fuelConsumptionMeasuringMethod) {
      methods.add(at.fuelConsumptionMeasuringMethod);
    }
  });
  return methods.size > 1;
}

export function addMultipleMethodsControl(
  emissionSourcesForm: FormGroup<EmissionSourcesFormModelCorsia>,
  fb: FormBuilder,
) {
  if (!emissionSourcesForm.controls.multipleFuelConsumptionMethodsExplanation) {
    emissionSourcesForm.addControl(
      'multipleFuelConsumptionMethodsExplanation',
      fb.control(null, [
        GovukValidators.maxLength(2000, 'Enter up to 2000 characters.'),
        MultipleMethodsExplanationValidator,
      ]),
    );
  }
}
export function removeMultipleMethodsControl(form: FormGroup<EmissionSourcesFormModelCorsia>) {
  form.removeControl('multipleFuelConsumptionMethodsExplanation');
}
const MultipleMethodsExplanationValidator: ValidatorFn = (control: AbstractControl) => {
  const form = control.parent as FormGroup<EmissionSourcesFormModelCorsia>;
  if (!form) return null;
  const hasMultipleMethods = hasMoreThanOneMonitoringMethod(form);
  const multipleMethods = form.controls.multipleFuelConsumptionMethodsExplanation.value;
  if (!hasMultipleMethods) return null;
  if (!(multipleMethods || multipleMethods?.length))
    return {
      multipleMethodsExplanationRequired: 'Say why you are using more than one method to measure fuel consumption.',
    };
};
