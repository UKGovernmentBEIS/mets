import { inject } from '@angular/core';
import { CanActivateFn, CanDeactivateFn } from '@angular/router';

import { map, take, tap } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { EmpUkEtsStoreDelegate } from '@aviation/request-task/store/delegates';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';

import { empQuery } from '../emp.selectors';
import { MethodAProceduresFormProvider } from './method-a-procedures-form.provider';

export const canActivateMethodAProcedures: CanActivateFn = () => {
  const store = inject(RequestTaskStore);
  const formProvider = inject<MethodAProceduresFormProvider>(TASK_FORM_PROVIDER);
  const payload = store.getState().requestTaskItem.requestTask.payload;

  return store.pipe(
    empQuery.selectEmissionsMonitoringPlan,
    take(1),
    tap((emp) => {
      if (!emp) {
        store.setPayload({
          ...payload,
          emissionsMonitoringPlan: {
            methodAProcedures: EmpUkEtsStoreDelegate.INITIAL_STATE.methodAProcedures, //TODO consider corsia as well
          },
        } as any);
      }

      if (!emp?.methodAProcedures) {
        store.empDelegate.setMethodAProcedures(EmpUkEtsStoreDelegate.INITIAL_STATE.methodAProcedures); //TODO consider corsia as well
      }

      formProvider.setFormValue(emp?.methodAProcedures);
    }),
    map(() => true),
  );
};

export const canDeactivateMethodAProcedures: CanDeactivateFn<any> = () => {
  inject<MethodAProceduresFormProvider>(TASK_FORM_PROVIDER).destroyForm();
  return true;
};
