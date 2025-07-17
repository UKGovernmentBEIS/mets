import { inject } from '@angular/core';
import { CanActivateFn, CanDeactivateFn } from '@angular/router';

import { map, take, tap } from 'rxjs';

import { empCorsiaQuery } from '@aviation/request-task/emp/shared/emp-corsia.selectors';
import { EmpCorsiaStoreDelegate } from '@aviation/request-task/store/delegates/emp-corsia';

import { RequestTaskStore } from '../../../../store';
import { TASK_FORM_PROVIDER } from '../../../../task-form.provider';
import { MonitoringApproachCorsiaFormProvider } from './monitoring-approach-form.provider';
import { monitoringApproachCorsiaQuery } from './store/monitoring-approach.selectors';

export const canActivateMonitoringApproachCorsia: CanActivateFn = () => {
  const store = inject(RequestTaskStore);
  const formProvider = inject<MonitoringApproachCorsiaFormProvider>(TASK_FORM_PROVIDER);
  const payload = store.getState().requestTaskItem.requestTask.payload;

  return store.pipe(
    empCorsiaQuery.selectEmissionsMonitoringPlanCorsia,
    take(1),
    tap((emp) => {
      if (!emp) {
        store.setPayload({
          ...payload,
          emissionsMonitoringPlan: {
            emissionsMonitoringApproach: EmpCorsiaStoreDelegate.INITIAL_STATE.emissionsMonitoringApproach,
          },
        } as any);
      }

      if (!emp?.emissionsMonitoringApproach) {
        store.empCorsiaDelegate.setEmissionsMonitoringApproach(
          EmpCorsiaStoreDelegate.INITIAL_STATE.emissionsMonitoringApproach,
        );
      }

      formProvider.setFormValue(emp?.emissionsMonitoringApproach);
    }),
    map(() => true),
  );
};

export const canDeactivateMonitoringApproachCorsia: CanDeactivateFn<any> = () => {
  inject<MonitoringApproachCorsiaFormProvider>(TASK_FORM_PROVIDER).destroyForm();

  return true;
};

export const canActivateSimplifiedApproach: CanActivateFn = () => {
  const store = inject(RequestTaskStore);
  return store.pipe(
    monitoringApproachCorsiaQuery.selectMonitoringApproachCorsia,
    take(1),
    map((emissionsMonitoringApproach) => {
      return emissionsMonitoringApproach.monitoringApproachType === 'CERT_MONITORING';
    }),
  );
};
