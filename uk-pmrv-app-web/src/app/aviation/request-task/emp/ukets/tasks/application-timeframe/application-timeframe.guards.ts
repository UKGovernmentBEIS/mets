import { inject } from '@angular/core';
import { CanActivateFn, CanDeactivateFn } from '@angular/router';

import { map, take, tap } from 'rxjs';

import { RequestTaskStore } from '../../../../store';
import { EmpUkEtsStoreDelegate } from '../../../../store/delegates';
import { TASK_FORM_PROVIDER } from '../../../../task-form.provider';
import { empQuery } from '../../../shared/emp.selectors';
import { ApplicationTimeframeFormProvider } from './application-timeframe-form.provider';

export const canActivateApplicationTimeframe: CanActivateFn = () => {
  const store = inject(RequestTaskStore);
  const formProvider = inject<ApplicationTimeframeFormProvider>(TASK_FORM_PROVIDER);
  const payload = store.getState().requestTaskItem.requestTask.payload;

  return store.pipe(
    empQuery.selectEmissionsMonitoringPlan,
    take(1),
    tap((emp) => {
      if (!emp) {
        store.setPayload({
          ...payload,
          emissionsMonitoringPlan: {
            applicationTimeframeInfo: EmpUkEtsStoreDelegate.INITIAL_STATE.applicationTimeframeInfo,
          },
        } as any);
      }

      if (!emp?.applicationTimeframeInfo) {
        store.empUkEtsDelegate.setApplicationTimeframeInfo(
          EmpUkEtsStoreDelegate.INITIAL_STATE.applicationTimeframeInfo,
        );
      }

      formProvider.setFormValue(emp.applicationTimeframeInfo);
    }),
    map(() => true),
  );
};

export const canDeactivateApplicationTimeframe: CanDeactivateFn<any> = () => {
  inject<ApplicationTimeframeFormProvider>(TASK_FORM_PROVIDER).destroyForm();
  return true;
};
