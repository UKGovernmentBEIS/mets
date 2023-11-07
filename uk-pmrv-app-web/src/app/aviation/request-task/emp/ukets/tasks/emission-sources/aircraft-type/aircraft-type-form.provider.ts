import { InjectionToken, Provider } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { EmpRequestTaskPayloadUkEts, RequestTaskStore } from '@aviation/request-task/store';
import { isFUMM } from '@aviation/shared/components/emp/emission-sources/isFUMM';

import { GovukValidators } from 'govuk-components';

import { TASK_FORM_PROVIDER } from '../../../../../task-form.provider';
import { EmissionSourcesFormModel } from '../emission-sources-form.model';
import { AircraftTypeDetailsFormModel } from './form/types';

export const EMP_AIRCRAFT_TYPE_FORM = new InjectionToken<{ form: AircraftTypeDetailsFormModel; reset: () => void }>(
  'Aircraft type form',
);
export const AircraftTypeFormProvider: Provider = {
  provide: EMP_AIRCRAFT_TYPE_FORM,
  deps: [FormBuilder, ActivatedRoute, TASK_FORM_PROVIDER, RequestTaskStore],
  useFactory: (
    fb: FormBuilder,
    route: ActivatedRoute,
    emissionSourcesForm: FormGroup<EmissionSourcesFormModel>,
    store: RequestTaskStore,
  ) => {
    const isFUMMethod = isFUMM(store.getValue().requestTaskItem?.requestTask?.payload as EmpRequestTaskPayloadUkEts);

    let form: AircraftTypeDetailsFormModel | null = null;
    return {
      get form() {
        if (!form) {
          form = createForm(route, fb, emissionSourcesForm, isFUMMethod);
        }
        return form;
      },
      reset() {
        form = null;
      },
    };
  },
};
function createForm(
  route: ActivatedRoute,
  fb: FormBuilder,
  emissionSourcesForm: FormGroup<EmissionSourcesFormModel>,
  isFUMM: boolean,
) {
  const aircraftTypeId = route.snapshot.queryParamMap.get('aircraftTypeIndex');
  const isCurrentlyUsed = !!Number(route.snapshot.queryParamMap.get('isCurrentlyUsed'));
  // create the form
  const form: AircraftTypeDetailsFormModel = fb.group(
    {
      aircraftTypeInfo: fb.control(null, GovukValidators.required('You must choose an aircraft type')),
      fuelTypes: fb.control([], GovukValidators.required('Select at least one fuel')),
      isCurrentlyUsed: [isCurrentlyUsed],
      numberOfAircrafts: fb.control(null, [
        GovukValidators.required('Number of aircrafts must be greater than 0'),
        GovukValidators.naturalNumber('Enter a whole number'),
        GovukValidators.maxDigitsValidator(10),
      ]),
      subtype: fb.control(null, GovukValidators.maxLength(10000, 'Enter up to 10000 characters.')),
    },
    { updateOn: 'change' },
  );
  // add fuelConsumptionMeasuringMethod fuelConsumptionMeasuringMethodcontrol depending on emissionsMonitoringApproach
  if (isFUMM) {
    form.addControl(
      'fuelConsumptionMeasuringMethod',
      fb.control(null, GovukValidators.required('Select a fuel consumption method')),
    );
  }
  // patch the form if we are on edit mode meaning the aircraftTypeId exists
  if (aircraftTypeId) {
    const aircraftTypeFormDetails = emissionSourcesForm.controls.aircraftTypes.at(+aircraftTypeId);
    const aircraftTypeDetails = aircraftTypeFormDetails.getRawValue();
    form.patchValue({
      aircraftTypeInfo: aircraftTypeDetails.aircraftTypeInfo,
      fuelTypes: aircraftTypeDetails.fuelTypes,
      numberOfAircrafts: aircraftTypeDetails.numberOfAircrafts,
      subtype: aircraftTypeDetails.subtype,
    });
    // patch fuelConsumptionMeasuringMethod if emissionsMonitoringApproach is FUEL_USE_MONITORING
    if (isFUMM) {
      form.controls.fuelConsumptionMeasuringMethod.patchValue(aircraftTypeDetails.fuelConsumptionMeasuringMethod);
    }
  }
  return form;
}
