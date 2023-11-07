import { inject } from '@angular/core';
import { CanActivateFn, CanDeactivateFn } from '@angular/router';

import { map, take, tap } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { EmpUkEtsStoreDelegate } from '@aviation/request-task/store/delegates';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';

import { empQuery } from '../emp.selectors';
import { MethodBProceduresFormProvider } from './method-b-procedures-form.provider';

export const canActivateMethodBProcedures: CanActivateFn = () => {
  const store = inject(RequestTaskStore);
  const formProvider = inject<MethodBProceduresFormProvider>(TASK_FORM_PROVIDER);
  const payload = store.getState().requestTaskItem.requestTask.payload;

  return store.pipe(
    empQuery.selectEmissionsMonitoringPlan,
    take(1),
    tap((emp) => {
      if (!emp) {
        store.setPayload({
          ...payload,
          emissionsMonitoringPlan: {
            methodBProcedures: EmpUkEtsStoreDelegate.INITIAL_STATE.methodBProcedures, //TODO consider corsia as well
          },
        } as any);
      }

      if (!emp?.methodBProcedures) {
        store.empDelegate.setMethodBProcedures(EmpUkEtsStoreDelegate.INITIAL_STATE.methodBProcedures);
      }

      formProvider.setFormValue(emp?.methodBProcedures);
    }),
    map(() => true),
  );
};

export const canDeactivateMethodBProcedures: CanDeactivateFn<any> = () => {
  inject<MethodBProceduresFormProvider>(TASK_FORM_PROVIDER).destroyForm();
  return true;
};
