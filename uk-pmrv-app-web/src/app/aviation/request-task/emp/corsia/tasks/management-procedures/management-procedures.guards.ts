import { inject } from '@angular/core';
import { CanActivateFn, CanDeactivateFn } from '@angular/router';

import { map, take, tap } from 'rxjs';

import { empCorsiaQuery } from '@aviation/request-task/emp/shared/emp-corsia.selectors';
import { EmpRequestTaskPayloadCorsia, RequestTaskStore } from '@aviation/request-task/store';
import { EmpCorsiaStoreDelegate } from '@aviation/request-task/store/delegates/emp-corsia';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';

import { ManagementProceduresCorsiaFormProvider } from './management-procedures-form.provider';

export const canActivateManagementProceduresCorsia: CanActivateFn = () => {
  const store = inject(RequestTaskStore);
  const formProvider = inject<ManagementProceduresCorsiaFormProvider>(TASK_FORM_PROVIDER);
  const payload = store.getState().requestTaskItem.requestTask.payload;

  return store.pipe(
    empCorsiaQuery.selectEmissionsMonitoringPlanCorsia,
    take(1),
    tap((emp) => {
      if (!emp) {
        store.setPayload({
          ...payload,
          emissionsMonitoringPlan: {
            managementProcedures: EmpCorsiaStoreDelegate.INITIAL_STATE.managementProcedures,
          },
        } as EmpRequestTaskPayloadCorsia);
      }

      if (!emp?.managementProcedures) {
        store.empCorsiaDelegate.setManagementProcedures(EmpCorsiaStoreDelegate.INITIAL_STATE.managementProcedures);
      }

      formProvider.setFormValue(emp.managementProcedures);
    }),
    map(() => true),
  );
};

export const canDeactivateManagementProceduresCorsia: CanDeactivateFn<any> = () => {
  inject<ManagementProceduresCorsiaFormProvider>(TASK_FORM_PROVIDER).destroyForm();
  return true;
};
