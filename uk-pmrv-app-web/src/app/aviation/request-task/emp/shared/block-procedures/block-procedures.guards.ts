import { inject } from '@angular/core';
import { CanActivateFn, CanDeactivateFn } from '@angular/router';

import { map, take, tap } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { EmpUkEtsStoreDelegate } from '@aviation/request-task/store/delegates';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';

import { empQuery } from '../emp.selectors';
import { BlockProceduresFormProvider } from './block-procedures-form.provider';

export const canActivateBlockProcedures: CanActivateFn = () => {
  const store = inject(RequestTaskStore);
  const formProvider = inject<BlockProceduresFormProvider>(TASK_FORM_PROVIDER);
  const payload = store.getState().requestTaskItem.requestTask.payload;

  return store.pipe(
    empQuery.selectEmissionsMonitoringPlan,
    take(1),
    tap((emp) => {
      if (!emp) {
        store.setPayload({
          ...payload,
          emissionsMonitoringPlan: {
            blockOnBlockOffMethodProcedures: EmpUkEtsStoreDelegate.INITIAL_STATE.blockOnBlockOffMethodProcedures, //TODO consider corsia as well
          },
        } as any);
      }

      if (!emp?.blockOnBlockOffMethodProcedures) {
        store.empDelegate.setblockOnBlockOffMethodProcedures(
          EmpUkEtsStoreDelegate.INITIAL_STATE.blockOnBlockOffMethodProcedures,
        );
      }

      formProvider.setFormValue(emp.blockOnBlockOffMethodProcedures);
    }),
    map(() => true),
  );
};

export const canDeactivateBlockProcedures: CanDeactivateFn<any> = () => {
  inject<BlockProceduresFormProvider>(TASK_FORM_PROVIDER).destroyForm();
  return true;
};
