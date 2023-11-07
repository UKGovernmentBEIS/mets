import { Provider } from '@angular/core';
import { AbstractControl, FormArray, FormBuilder, FormGroup, ValidatorFn } from '@angular/forms';

import { transformAircraftTypeDescription } from '@aviation/shared/pipes/aircraft-type-description.pipe';

import { GovukValidators } from 'govuk-components';

import { TASK_FORM_PROVIDER } from '../../../../task-form.provider';
import { ProcedureFormBuilder } from '../../../shared/procedure-form-step';
import { AircraftTypeDetailsFormModel } from './aircraft-type/form/types';
import { AdditionalAircraftMonitoringApproachModel, EmissionSourcesFormModel } from './emission-sources-form.model';

export const EmissionSourcesFormProvider: Provider = {
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

const AircraftTypeValidator: ValidatorFn = (array: FormArray<AircraftTypeDetailsFormModel>) => {
  const hasOneUsedAircraft = !!array.getRawValue().filter((at) => at.isCurrentlyUsed).length;
  if (hasOneUsedAircraft) return null;
  return {
    requiredInUseAircraft: 'You must add at least one item to the aircraft currently in use table',
  };
};

export const FUMMValidator = (array: FormArray<AircraftTypeDetailsFormModel>) => {
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

export function hasOtherFuelType(form: FormGroup<EmissionSourcesFormModel>): boolean {
  return form.controls.aircraftTypes?.controls.some((at) => at.getRawValue().fuelTypes.includes('OTHER'));
}

export function hasMoreThanOneMonitoringMethod(form: FormGroup<EmissionSourcesFormModel>): boolean {
  const methods = new Set();
  form.getRawValue().aircraftTypes.forEach((at) => {
    if (at.fuelConsumptionMeasuringMethod) {
      methods.add(at.fuelConsumptionMeasuringMethod);
    }
  });
  return methods.size > 1;
}

export function addAdditionalAircraftMonitoringApproachForm(emissionSourcesForm: FormGroup<EmissionSourcesFormModel>) {
  if (!emissionSourcesForm.controls.additionalAircraftMonitoringApproach) {
    const form: FormGroup<AdditionalAircraftMonitoringApproachModel> = ProcedureFormBuilder.createProcedureForm();
    emissionSourcesForm.addControl('additionalAircraftMonitoringApproach', form);
  }
}
export function removeAdditionalAircraftMonitoringApproachForm(form: FormGroup<EmissionSourcesFormModel>) {
  form.removeControl('additionalAircraftMonitoringApproach');
}
export function addMultipleMethodsControl(emissionSourcesForm: FormGroup<EmissionSourcesFormModel>, fb: FormBuilder) {
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
export function removeMultipleMethodsControl(form: FormGroup<EmissionSourcesFormModel>) {
  form.removeControl('multipleFuelConsumptionMethodsExplanation');
}
const MultipleMethodsExplanationValidator: ValidatorFn = (control: AbstractControl) => {
  const form = control.parent as FormGroup<EmissionSourcesFormModel>;
  if (!form) return null;
  const hasMultipleMethods = hasMoreThanOneMonitoringMethod(form);
  const multipleMethods = form.controls.multipleFuelConsumptionMethodsExplanation.value;
  if (!hasMultipleMethods) return null;
  if (!(multipleMethods || multipleMethods?.length))
    return {
      multipleMethodsExplanationRequired: 'Say why you are using more than one method to measure fuel consumption.',
    };
};
export function addOtherFuelTypeExplanationControl(
  emissionSourcesForm: FormGroup<EmissionSourcesFormModel>,
  fb: FormBuilder,
): void {
  if (!emissionSourcesForm.controls.otherFuelExplanation) {
    emissionSourcesForm.addControl(
      'otherFuelExplanation',
      fb.control(null, [
        GovukValidators.maxLength(10000, 'Enter up to 10000 characters.'),
        OtherFuelsExplanationValidator,
      ]),
    );
  }
}

export function removeOtherFuelTypeExplanationControl(form: FormGroup<EmissionSourcesFormModel>): void {
  form.removeControl('otherFuelExplanation');
}
const OtherFuelsExplanationValidator: ValidatorFn = (control: AbstractControl) => {
  const otherFuelExplanation = control.value;
  if (!(otherFuelExplanation || otherFuelExplanation?.length))
    return {
      otherFuelExplanationRequired:
        'Say how you will calculate the emission factors for the ‘other fuel’ added to the emission sources table',
    };
};
