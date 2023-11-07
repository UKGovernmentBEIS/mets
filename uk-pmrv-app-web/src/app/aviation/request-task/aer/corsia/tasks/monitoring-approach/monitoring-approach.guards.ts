import { inject } from '@angular/core';
import { CanActivateFn, CanDeactivateFn } from '@angular/router';

import { map, take, tap } from 'rxjs';

import { monitoringApproachCorsiaQuery } from '@aviation/request-task/aer/corsia/tasks/monitoring-approach/store/monitoring-approach.selectors';
import { AerCorsiaStoreDelegate } from '@aviation/request-task/store/delegates/aer-corsia/aer-corsia-store-delegate';

import { AviationAerCorsia } from 'pmrv-api';

import { RequestTaskStore } from '../../../../store';
import { TASK_FORM_PROVIDER } from '../../../../task-form.provider';
import { aerQuery } from '../../../shared/aer.selectors';
import { AviationAerCorsiaMonitoringApproachFormProvider } from './monitoring-approach-form.provider';

export const canActivateMonitoringApproach: CanActivateFn = () => {
  const store = inject(RequestTaskStore);
  const formProvider = inject<AviationAerCorsiaMonitoringApproachFormProvider>(TASK_FORM_PROVIDER);
  const payload = store.getState().requestTaskItem.requestTask.payload;

  return store.pipe(
    aerQuery.selectAer,
    take(1),
    map((aer) => aer as unknown as AviationAerCorsia),
    tap((aer) => {
      const delegate = store.aerDelegate as AerCorsiaStoreDelegate;
      if (!aer) {
        store.setPayload({
          ...payload,
          aer: {
            monitoringApproach: AerCorsiaStoreDelegate.INITIAL_STATE.monitoringApproach,
          },
        } as any);
      }

      if (!aer?.monitoringApproach) {
        delegate.setMonitoringApproach(AerCorsiaStoreDelegate.INITIAL_STATE.monitoringApproach);
      }

      formProvider.setFormValue(aer.monitoringApproach);
    }),
    map(() => true),
  );
};

export const canDeactivateMonitoringApproach: CanDeactivateFn<boolean> = () => {
  inject<AviationAerCorsiaMonitoringApproachFormProvider>(TASK_FORM_PROVIDER).destroyForm();
  return true;
};

export const canActivateCertUsage: CanActivateFn = () => {
  const store = inject(RequestTaskStore);
  return store.pipe(
    monitoringApproachCorsiaQuery.selectMonitoringApproach,
    take(1),
    map((monitoringApproach) => {
      return monitoringApproach?.certUsed === true;
    }),
  );
};

export const canActivateFuelUsage: CanActivateFn = () => {
  const store = inject(RequestTaskStore);
  return store.pipe(
    monitoringApproachCorsiaQuery.selectMonitoringApproach,
    take(1),
    map((monitoringApproach) => {
      return (
        monitoringApproach?.certUsed !== undefined &&
        monitoringApproach?.certDetails?.flightType !== 'ALL_INTERNATIONAL_FLIGHTS'
      );
    }),
  );
};

export const canActivateFuelAllocationBlockHour: CanActivateFn = () => {
  const store = inject(RequestTaskStore);
  return store.pipe(
    monitoringApproachCorsiaQuery.selectMonitoringApproach,
    take(1),
    map((monitoringApproach) => {
      return (
        monitoringApproach?.certUsed !== undefined &&
        monitoringApproach?.certDetails?.flightType !== 'ALL_INTERNATIONAL_FLIGHTS' &&
        monitoringApproach?.fuelUseMonitoringDetails?.blockHourUsed === true
      );
    }),
  );
};
