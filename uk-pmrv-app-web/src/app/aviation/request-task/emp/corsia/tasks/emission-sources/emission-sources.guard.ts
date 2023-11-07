import { inject } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { CanActivateFn, CanDeactivateFn } from '@angular/router';

import { map, take, tap } from 'rxjs';

import { empQuery } from '@aviation/request-task/emp/shared/emp.selectors';
import {
  EmpRequestTaskPayloadCorsia,
  EmpRequestTaskPayloadUkEts,
  RequestTaskStore,
} from '@aviation/request-task/store';
import { EmpCorsiaStoreDelegate } from '@aviation/request-task/store/delegates/emp-corsia';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { isFUMM } from '@aviation/shared/components/emp/emission-sources/isFUMM';

import { AircraftTypeDetailsFormModelCorsia } from './aircraft-type/form/types';
import { EmissionSourcesFormModelCorsia } from './emission-sources-form.model';
import {
  addMultipleMethodsControl,
  FUMMValidator,
  hasMoreThanOneMonitoringMethod,
  removeMultipleMethodsControl,
} from './emission-sources-form.provider';

export const canActivateEmissionSources: CanActivateFn = () => {
  const store = inject(RequestTaskStore);
  const payload = store.getState().requestTaskItem.requestTask.payload as EmpRequestTaskPayloadUkEts;
  const form = inject<FormGroup<EmissionSourcesFormModelCorsia>>(TASK_FORM_PROVIDER);
  const fb = inject(FormBuilder);
  return store.pipe(
    empQuery.selectEmissionsMonitoringPlan,
    take(1),
    tap((emp) => {
      if (!emp) {
        store.setPayload({
          ...payload,
          emissionsMonitoringPlan: {
            emissionSources: EmpCorsiaStoreDelegate.INITIAL_STATE.emissionSources,
          },
        } as any);
      }
      if (!emp?.emissionSources) {
        store.empCorsiaDelegate.setEmissionSources(EmpCorsiaStoreDelegate.INITIAL_STATE.emissionSources);
      }
    }),
    tap(() => {
      const formValue = form.getRawValue();
      const hasValues = formValue.aircraftTypes.length;
      const payload = store.getState().requestTaskItem.requestTask.payload as EmpRequestTaskPayloadCorsia;

      if (payload && !hasValues) {
        const emissionSources = payload.emissionsMonitoringPlan.emissionSources;
        const multipleMethodsExplanationValue = emissionSources.multipleFuelConsumptionMethodsExplanation;
        emissionSources.aircraftTypes?.forEach((at) =>
          form.controls.aircraftTypes.push(
            fb.group({
              aircraftTypeInfo: [at.aircraftTypeInfo],
              fuelTypes: [at.fuelTypes],
              numberOfAircrafts: [at.numberOfAircrafts],
              subtype: [at.subtype],
              fuelConsumptionMeasuringMethod: [at.fuelConsumptionMeasuringMethod],
            }) as AircraftTypeDetailsFormModelCorsia,
          ),
        );
        if (hasMoreThanOneMonitoringMethod(form)) {
          addMultipleMethodsControl(form, fb);
          form.controls.multipleFuelConsumptionMethodsExplanation.updateValueAndValidity();
          if (multipleMethodsExplanationValue) {
            form.patchValue({
              multipleFuelConsumptionMethodsExplanation: multipleMethodsExplanationValue,
            });
          }
        }
        if (isFUMM(store.getValue().requestTaskItem?.requestTask?.payload as EmpRequestTaskPayloadCorsia)) {
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
  const form = inject<FormGroup<EmissionSourcesFormModelCorsia>>(TASK_FORM_PROVIDER);
  while (form.controls.aircraftTypes.length !== 0) {
    form.controls.aircraftTypes.removeAt(0);
  }
  removeMultipleMethodsControl(form);
  form.clearValidators();
  form.reset();
  form.updateValueAndValidity();
  return true;
};
