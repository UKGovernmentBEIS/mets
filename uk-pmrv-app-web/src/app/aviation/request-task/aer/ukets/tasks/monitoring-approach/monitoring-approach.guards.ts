import { inject } from '@angular/core';
import { CanActivateFn, CanDeactivateFn } from '@angular/router';

import { map, take, tap } from 'rxjs';

import { EmissionSmallEmittersSupportFacilityFormValues } from '@aviation/shared/components/aer/monitoring-approach-summary-template/monitoring-approach.interfaces';
import produce from 'immer';

import { RequestTaskStore } from '../../../../store';
import { AerStoreDelegate } from '../../../../store/delegates';
import { TASK_FORM_PROVIDER } from '../../../../task-form.provider';
import { aerQuery } from '../../../shared/aer.selectors';
import { AerMonitoringApproachFormProvider } from './monitoring-approach-form.provider';
import { monitoringPlanChangesQuery } from './store/monitoring-approach.selectors';

export const canActivateMonitoringApproach: CanActivateFn = () => {
  const store = inject(RequestTaskStore);
  const formProvider = inject<AerMonitoringApproachFormProvider>(TASK_FORM_PROVIDER);
  const payload = store.getState().requestTaskItem.requestTask.payload;

  return store.pipe(
    aerQuery.selectAer,
    take(1),
    tap((aer) => {
      if (!aer) {
        store.setPayload({
          ...payload,
          aer: {
            monitoringApproach: AerStoreDelegate.INITIAL_STATE.monitoringApproach,
          },
        } as any);
      }

      if (!aer?.monitoringApproach) {
        store.aerDelegate.setMonitoringApproach(
          AerStoreDelegate.INITIAL_STATE.monitoringApproach as EmissionSmallEmittersSupportFacilityFormValues,
        );
      }

      formProvider.setFormValue(aer.monitoringApproach as EmissionSmallEmittersSupportFacilityFormValues);
    }),
    map(() => true),
  );
};

export const canDeactivateMonitoringApproach: CanDeactivateFn<boolean> = () => {
  inject<AerMonitoringApproachFormProvider>(TASK_FORM_PROVIDER).destroyForm();

  const store = inject(RequestTaskStore);
  const payload = store.aerDelegate.payload;

  if (payload.aer.monitoringApproach.monitoringApproachType === null) {
    store.setPayload(
      produce(payload, (draft) => {
        delete draft.aer.monitoringApproach;
      }),
    );
  }

  return true;
};

export const canActivateSchemeYearTotalEmissions: CanActivateFn = () => {
  const store = inject(RequestTaskStore);
  return store.pipe(
    monitoringPlanChangesQuery.selectMonitoringApproach,
    take(1),
    map((emissionsMonitoringApproach) => {
      return emissionsMonitoringApproach.monitoringApproachType === 'EUROCONTROL_SUPPORT_FACILITY';
    }),
  );
};

export const canActivateTotalNumberFullScopeFlights: CanActivateFn = () => {
  const store = inject(RequestTaskStore);
  return store.pipe(
    monitoringPlanChangesQuery.selectMonitoringApproach,
    take(1),
    map((emissionsMonitoringApproach) => {
      return emissionsMonitoringApproach.monitoringApproachType === 'EUROCONTROL_SMALL_EMITTERS';
    }),
  );
};
