import { inject } from '@angular/core';
import { CanActivateFn, CanDeactivateFn } from '@angular/router';

import { map, take, tap } from 'rxjs';

import { empQuery } from '@aviation/request-task/emp/shared/emp.selectors';
import { EmpRequestTaskPayloadCorsia, RequestTaskStore } from '@aviation/request-task/store';
import { EmpCorsiaStoreDelegate } from '@aviation/request-task/store/delegates/emp-corsia';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';

import { FlightProceduresFormProvider } from './flight-procedures-form.provider';

export const canActivateFlightProcedures: CanActivateFn = () => {
  const store = inject(RequestTaskStore);
  const formProvider = inject<FlightProceduresFormProvider>(TASK_FORM_PROVIDER);
  const payload = store.getState().requestTaskItem.requestTask.payload;

  return store.pipe(
    empQuery.selectEmissionsMonitoringPlanCorsia,
    take(1),
    tap((empCorsia) => {
      if (!empCorsia) {
        store.setPayload({
          ...payload,
          emissionsMonitoringPlan: {
            flightAndAircraftProcedures: EmpCorsiaStoreDelegate.INITIAL_STATE.flightAndAircraftProcedures,
          },
        } as EmpRequestTaskPayloadCorsia);
      }

      if (!empCorsia?.flightAndAircraftProcedures) {
        store.empCorsiaDelegate.setFlightProcedures(EmpCorsiaStoreDelegate.INITIAL_STATE.flightAndAircraftProcedures);
      }

      formProvider.setFormValue(empCorsia?.flightAndAircraftProcedures);
    }),
    map(() => true),
  );
};

export const canDeactivateFlightProcedures: CanDeactivateFn<any> = () => {
  inject<FlightProceduresFormProvider>(TASK_FORM_PROVIDER).destroyForm();
  return true;
};
