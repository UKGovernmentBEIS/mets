import { inject } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { CanActivateFn, CanDeactivateFn } from '@angular/router';

import { map, take, tap } from 'rxjs';

import { isFUMM } from '@aviation/shared/components/emp/emission-sources/isFUMM';

import { EmpRequestTaskPayloadUkEts, RequestTaskStore } from '../../../../store';
import { EmpUkEtsStoreDelegate } from '../../../../store/delegates';
import { TASK_FORM_PROVIDER } from '../../../../task-form.provider';
import { empQuery } from '../../../shared/emp.selectors';
import { AircraftTypeDetailsFormModel } from './aircraft-type/form/types';
import { EmissionSourcesFormModel } from './emission-sources-form.model';
import {
  addAdditionalAircraftMonitoringApproachForm,
  addMultipleMethodsControl,
  addOtherFuelTypeExplanationControl,
  FUMMValidator,
  hasMoreThanOneMonitoringMethod,
  hasOtherFuelType,
  removeAdditionalAircraftMonitoringApproachForm,
  removeMultipleMethodsControl,
  removeOtherFuelTypeExplanationControl,
} from './emission-sources-form.provider';

export const canActivateEmissionSources: CanActivateFn = () => {
  const store = inject(RequestTaskStore);
  const payload = store.getState().requestTaskItem.requestTask.payload as EmpRequestTaskPayloadUkEts;
  const form = inject<FormGroup<EmissionSourcesFormModel>>(TASK_FORM_PROVIDER);
  const fb = inject(FormBuilder);
  return store.pipe(
    empQuery.selectEmissionsMonitoringPlan,
    take(1),
    tap((emp) => {
      if (!emp) {
        store.setPayload({
          ...payload,
          emissionsMonitoringPlan: {
            emissionSources: EmpUkEtsStoreDelegate.INITIAL_STATE.emissionSources,
          },
        } as any);
      }
      if (!emp?.emissionSources) {
        store.empUkEtsDelegate.setEmissionSources(EmpUkEtsStoreDelegate.INITIAL_STATE.emissionSources);
      }
    }),
    tap(() => {
      const formValue = form.getRawValue();
      const hasValues = formValue.aircraftTypes.length || formValue.otherFuelExplanation;
      const payload = store.getState().requestTaskItem.requestTask.payload as EmpRequestTaskPayloadUkEts;

      if (payload && !hasValues) {
        const emissionSources = payload.emissionsMonitoringPlan.emissionSources;
        const otherFuelExplanationValue = emissionSources.otherFuelExplanation;
        const multipleMethodsExplanationValue = emissionSources.multipleFuelConsumptionMethodsExplanation;
        const additionalAircraftsMonitoringApproachValue = emissionSources.additionalAircraftMonitoringApproach;
        emissionSources.aircraftTypes?.forEach((at) =>
          form.controls.aircraftTypes.push(
            fb.group({
              aircraftTypeInfo: [at.aircraftTypeInfo],
              fuelTypes: [at.fuelTypes],
              isCurrentlyUsed: [at.isCurrentlyUsed],
              numberOfAircrafts: [at.numberOfAircrafts],
              subtype: [at.subtype],
              fuelConsumptionMeasuringMethod: [at.fuelConsumptionMeasuringMethod],
            }) as AircraftTypeDetailsFormModel,
          ),
        );
        if (hasOtherFuelType(form)) {
          addOtherFuelTypeExplanationControl(form, fb);
          if (otherFuelExplanationValue) {
            form.patchValue({ otherFuelExplanation: otherFuelExplanationValue });
          }
        }
        if (hasMoreThanOneMonitoringMethod(form)) {
          addMultipleMethodsControl(form, fb);
          form.controls.multipleFuelConsumptionMethodsExplanation.updateValueAndValidity();
          if (multipleMethodsExplanationValue) {
            form.patchValue({
              multipleFuelConsumptionMethodsExplanation: multipleMethodsExplanationValue,
            });
          }
        }
        if (isFUMM(store.getValue().requestTaskItem?.requestTask?.payload as EmpRequestTaskPayloadUkEts)) {
          addAdditionalAircraftMonitoringApproachForm(form);
          if (additionalAircraftsMonitoringApproachValue) {
            form.patchValue({
              additionalAircraftMonitoringApproach: additionalAircraftsMonitoringApproachValue,
            });
          }
          form.controls.aircraftTypes.addValidators(FUMMValidator);
          form.controls.aircraftTypes.updateValueAndValidity();
        } else {
          form.controls.aircraftTypes.removeValidators(FUMMValidator);
          form.controls.aircraftTypes.updateValueAndValidity();
        }
      }
      form.updateValueAndValidity();
    }),
    map(() => true),
  );
};

export const canDeactivateEmissionSources: CanDeactivateFn<unknown> = () => {
  const form = inject<FormGroup<EmissionSourcesFormModel>>(TASK_FORM_PROVIDER);
  while (form.controls.aircraftTypes.length !== 0) {
    form.controls.aircraftTypes.removeAt(0);
  }
  removeOtherFuelTypeExplanationControl(form);
  removeMultipleMethodsControl(form);
  removeAdditionalAircraftMonitoringApproachForm(form);
  form.clearValidators();
  form.reset();
  form.updateValueAndValidity();
  return true;
};
