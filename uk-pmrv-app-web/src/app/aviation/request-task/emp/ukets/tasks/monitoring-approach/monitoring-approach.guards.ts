import { inject } from '@angular/core';
import { CanActivateFn, CanDeactivateFn } from '@angular/router';

import { map, take, tap } from 'rxjs';

import produce from 'immer';

import { EmissionsMonitoringPlanUkEts } from 'pmrv-api';

import { RequestTaskStore } from '../../../../store';
import { EmpUkEtsStoreDelegate } from '../../../../store/delegates';
import { TASK_FORM_PROVIDER } from '../../../../task-form.provider';
import { empQuery } from '../../../shared/emp.selectors';
import { MonitoringApproachFormProvider } from './monitoring-approach-form.provider';
import { EmissionsMonitoringApproach } from './monitoring-approach-types.interface';
import { monitoringApproachQuery } from './store/monitoring-approach.selectors';

export const canActivateMonitoringApproach: CanActivateFn = () => {
  const store = inject(RequestTaskStore);
  const formProvider = inject<MonitoringApproachFormProvider>(TASK_FORM_PROVIDER);
  const payload = store.getState().requestTaskItem.requestTask.payload;

  return store.pipe(
    empQuery.selectEmissionsMonitoringPlan,
    take(1),
    tap((emp) => {
      if (!emp) {
        store.setPayload({
          ...payload,
          emissionsMonitoringPlan: {
            emissionsMonitoringApproach: EmpUkEtsStoreDelegate.INITIAL_STATE.emissionsMonitoringApproach,
          },
        } as any);
      }

      if (!emp?.emissionsMonitoringApproach) {
        store.empUkEtsDelegate.setEmissionsMonitoringApproach(
          EmpUkEtsStoreDelegate.INITIAL_STATE.emissionsMonitoringApproach,
        );
      }

      formProvider.setFormValue(emp.emissionsMonitoringApproach as EmissionsMonitoringApproach);
    }),
    map(() => true),
  );
};

export const canDeactivateMonitoringApproach: CanDeactivateFn<any> = () => {
  inject<MonitoringApproachFormProvider>(TASK_FORM_PROVIDER).destroyForm();
  const store = inject(RequestTaskStore);
  const payload = store.empUkEtsDelegate.payload;

  if (
    (payload.emissionsMonitoringPlan as EmissionsMonitoringPlanUkEts).emissionsMonitoringApproach
      .monitoringApproachType === null
  ) {
    store.setPayload(
      produce(payload, (draft) => {
        delete (draft.emissionsMonitoringPlan as EmissionsMonitoringPlanUkEts).emissionsMonitoringApproach;
      }),
    );
  }
  return true;
};

export const canActivateSimplifiedApproach: CanActivateFn = () => {
  const store = inject(RequestTaskStore);
  return store.pipe(
    monitoringApproachQuery.selectMonitoringApproach,
    take(1),
    map((emissionsMonitoringApproach) => {
      return emissionsMonitoringApproach.monitoringApproachType !== 'FUEL_USE_MONITORING';
    }),
  );
};
