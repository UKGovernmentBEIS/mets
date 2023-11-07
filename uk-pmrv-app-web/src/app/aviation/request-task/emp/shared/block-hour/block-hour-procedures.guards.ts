import { inject } from '@angular/core';
import { CanActivateFn, CanDeactivateFn } from '@angular/router';

import { map, take, tap } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { EmpUkEtsStoreDelegate } from '@aviation/request-task/store/delegates';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';

import { empQuery } from '../emp.selectors';
import { BlockHourProceduresFormProvider } from './block-hour-procedures-form.provider';

export const canActivateBlockHourProcedures: CanActivateFn = () => {
  const store = inject(RequestTaskStore);
  const formProvider = inject<BlockHourProceduresFormProvider>(TASK_FORM_PROVIDER);
  const payload = store.getState().requestTaskItem.requestTask.payload;

  return store.pipe(
    empQuery.selectEmissionsMonitoringPlan,
    take(1),
    tap((emp) => {
      if (!emp) {
        store.setPayload({
          ...payload,
          emissionsMonitoringPlan: {
            blockHourMethodProcedures: EmpUkEtsStoreDelegate.INITIAL_STATE.blockHourMethodProcedures, //TODO consider corsia as well
          },
        } as any);
      }

      if (!emp?.blockHourMethodProcedures) {
        store.empDelegate.setBlockHourMethodProcedures(EmpUkEtsStoreDelegate.INITIAL_STATE.blockHourMethodProcedures);
      }

      formProvider.setFormValue(emp?.blockHourMethodProcedures);
    }),
    map(() => true),
  );
};

export const canDeactivateBlockHourProcedures: CanDeactivateFn<any> = () => {
  inject<BlockHourProceduresFormProvider>(TASK_FORM_PROVIDER).destroyForm();
  return true;
};
