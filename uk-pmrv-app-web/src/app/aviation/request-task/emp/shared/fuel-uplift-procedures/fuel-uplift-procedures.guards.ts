import { inject } from '@angular/core';
import { CanActivateFn, CanDeactivateFn } from '@angular/router';

import { map, take, tap } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { EmpUkEtsStoreDelegate } from '@aviation/request-task/store/delegates';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';

import { empQuery } from '../emp.selectors';
import { FuelUpliftProceduresFormProvider } from './fuel-uplift-procedures-form.provider';

export const canActivateFuelUpliftProcedures: CanActivateFn = () => {
  const store = inject(RequestTaskStore);
  const formProvider = inject<FuelUpliftProceduresFormProvider>(TASK_FORM_PROVIDER);
  const payload = store.getState().requestTaskItem.requestTask.payload;

  return store.pipe(
    empQuery.selectEmissionsMonitoringPlan,
    take(1),
    tap((emp) => {
      if (!emp) {
        store.setPayload({
          ...payload,
          emissionsMonitoringPlan: {
            fuelUpliftMethodProcedures: EmpUkEtsStoreDelegate.INITIAL_STATE.fuelUpliftMethodProcedures, //TODO consider corsia as well
          },
        } as any);
      }

      if (!emp?.fuelUpliftMethodProcedures) {
        store.empDelegate.setFuelUpliftMethodProcedures(EmpUkEtsStoreDelegate.INITIAL_STATE.fuelUpliftMethodProcedures);
      }

      formProvider.setFormValue(emp?.fuelUpliftMethodProcedures);
    }),
    map(() => true),
  );
};

export const canDeactivateFuelUpliftProcedures: CanDeactivateFn<any> = () => {
  inject<FuelUpliftProceduresFormProvider>(TASK_FORM_PROVIDER).destroyForm();
  return true;
};
