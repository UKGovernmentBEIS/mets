import { inject } from '@angular/core';
import { CanActivateFn, CanDeactivateFn } from '@angular/router';

import { map, take, tap } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { EmpUkEtsStoreDelegate } from '@aviation/request-task/store/delegates/emp-ukets';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';

import { empQuery } from '../../../shared/emp.selectors';
import { OperatorDetailsFormProvider } from './operator-details-form.provider';

export const canActivateOperatorDetails: CanActivateFn = () => {
  const store = inject(RequestTaskStore);
  const formProvider = inject<OperatorDetailsFormProvider>(TASK_FORM_PROVIDER);
  const payload = store.getState().requestTaskItem.requestTask.payload;

  return store.pipe(
    empQuery.selectEmissionsMonitoringPlan,
    take(1),
    tap((emp) => {
      if (!emp) {
        store.setPayload({
          ...payload,
          emissionsMonitoringPlan: {
            operatorDetails: EmpUkEtsStoreDelegate.INITIAL_STATE.operatorDetails,
          },
        } as any);
      }

      if (!emp?.operatorDetails) {
        store.empUkEtsDelegate.setOperatorDetails(EmpUkEtsStoreDelegate.INITIAL_STATE.operatorDetails);
      }

      formProvider.setFormValue(emp?.operatorDetails);
    }),
    map(() => true),
  );
};

export const canDeactivateOperatorDetails: CanDeactivateFn<any> = () => {
  inject<OperatorDetailsFormProvider>(TASK_FORM_PROVIDER).destroyForm();
  return true;
};
