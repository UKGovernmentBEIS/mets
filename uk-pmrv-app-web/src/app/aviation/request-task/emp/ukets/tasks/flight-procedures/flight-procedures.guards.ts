import { inject } from '@angular/core';
import { CanActivateFn, CanDeactivateFn } from '@angular/router';

import { map, take, tap } from 'rxjs';

import { empQuery } from '@aviation/request-task/emp/shared/emp.selectors';
import { RequestTaskStore } from '@aviation/request-task/store';
import { EmpUkEtsStoreDelegate } from '@aviation/request-task/store/delegates';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';

import { FlightProceduresFormProvider } from './flight-procedures-form.provider';

export const canActivateFlightProcedures: CanActivateFn = () => {
  const store = inject(RequestTaskStore);
  const formProvider = inject<FlightProceduresFormProvider>(TASK_FORM_PROVIDER);
  const payload = store.getState().requestTaskItem.requestTask.payload;

  return store.pipe(
    empQuery.selectEmissionsMonitoringPlan,
    take(1),
    tap((emp) => {
      if (!emp) {
        store.setPayload({
          ...payload,
          emissionsMonitoringPlan: {
            flightAndAircraftProcedures: EmpUkEtsStoreDelegate.INITIAL_STATE.flightAndAircraftProcedures,
          },
        } as any);
      }

      if (!emp?.flightAndAircraftProcedures) {
        store.empUkEtsDelegate.setFlightProcedures(EmpUkEtsStoreDelegate.INITIAL_STATE.flightAndAircraftProcedures);
      }

      formProvider.setFormValue(emp?.flightAndAircraftProcedures);
    }),
    map(() => true),
  );
};

export const canDeactivateFlightProcedures: CanDeactivateFn<any> = () => {
  inject<FlightProceduresFormProvider>(TASK_FORM_PROVIDER).destroyForm();
  return true;
};
