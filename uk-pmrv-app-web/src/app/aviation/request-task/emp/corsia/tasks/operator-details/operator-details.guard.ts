import { inject } from '@angular/core';
import { CanActivateFn, CanDeactivateFn } from '@angular/router';

import { map, take, tap } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { EmpCorsiaStoreDelegate } from '@aviation/request-task/store/delegates/emp-corsia';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';

import { empQuery } from '../../../shared/emp.selectors';
import { OperatorDetailsCorsiaFormProvider } from './operator-details-form.provider';

export const canActivateOperatorDetails: CanActivateFn = () => {
  const store = inject(RequestTaskStore);
  const formProvider = inject<OperatorDetailsCorsiaFormProvider>(TASK_FORM_PROVIDER);
  const payload = store.getState().requestTaskItem.requestTask.payload;

  return store.pipe(
    empQuery.selectEmissionsMonitoringPlanCorsia,
    take(1),
    tap((emp) => {
      if (!emp) {
        store.setPayload({
          ...payload,
          emissionsMonitoringPlan: {
            operatorDetails: EmpCorsiaStoreDelegate.INITIAL_STATE.operatorDetails,
          },
        } as any);
      }

      if (!emp?.operatorDetails) {
        store.empCorsiaDelegate.setOperatorDetails(EmpCorsiaStoreDelegate.INITIAL_STATE.operatorDetails);
      }

      formProvider.setFormValue(emp?.operatorDetails);
    }),
    map(() => true),
  );
};

export const canDeactivateOperatorDetails: CanDeactivateFn<any> = () => {
  inject<OperatorDetailsCorsiaFormProvider>(TASK_FORM_PROVIDER).destroyForm();
  return true;
};
