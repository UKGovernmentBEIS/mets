import { inject } from '@angular/core';
import { CanActivateFn, CanDeactivateFn } from '@angular/router';

import { map, take, tap } from 'rxjs';

import { EmpUkEtsStoreDelegate } from '@aviation/request-task/store/delegates';

import { EmpRequestTaskPayloadUkEts, RequestTaskStore } from '../../../../store';
import { TASK_FORM_PROVIDER } from '../../../../task-form.provider';
import { empQuery } from '../../../shared/emp.selectors';
import { ManagementProceduresFormProvider } from './management-procedures-form.provider';

export const canActivateManagementProcedures: CanActivateFn = () => {
  const store = inject(RequestTaskStore);
  const formProvider = inject<ManagementProceduresFormProvider>(TASK_FORM_PROVIDER);
  const payload = store.getState().requestTaskItem.requestTask.payload;

  return store.pipe(
    empQuery.selectEmissionsMonitoringPlan,
    take(1),
    tap((emp) => {
      if (!emp) {
        store.setPayload({
          ...payload,
          emissionsMonitoringPlan: {
            managementProcedures: EmpUkEtsStoreDelegate.INITIAL_STATE.managementProcedures,
          },
        } as EmpRequestTaskPayloadUkEts);
      }

      if (!emp?.managementProcedures) {
        store.empUkEtsDelegate.setManagementProcedures(EmpUkEtsStoreDelegate.INITIAL_STATE.managementProcedures);
      }

      formProvider.setFormValue(emp.managementProcedures);
    }),
    map(() => true),
  );
};

export const canDeactivateManagementProcedures: CanDeactivateFn<any> = () => {
  inject<ManagementProceduresFormProvider>(TASK_FORM_PROVIDER).destroyForm();
  return true;
};
