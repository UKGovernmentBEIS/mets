import { inject } from '@angular/core';
import { CanActivateFn, CanDeactivateFn } from '@angular/router';

import { combineLatest, map, take } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { AerUkEtsStoreDelegate } from '@aviation/request-task/store/delegates';
import { AerCorsiaStoreDelegate } from '@aviation/request-task/store/delegates/aer-corsia/aer-corsia-store-delegate';

import { TASK_FORM_PROVIDER } from '../../../task-form.provider';
import { aerQuery } from '../aer.selectors';
import { MonitoringPlanChangesFormProvider } from './monitoring-plan-changes-form.provider';

export const canActivateMonitoringPlanChanges: CanActivateFn = () => {
  const store = inject(RequestTaskStore);
  const formProvider = inject<MonitoringPlanChangesFormProvider>(TASK_FORM_PROVIDER);
  const payload = store.getState().requestTaskItem.requestTask.payload;

  return combineLatest([store.pipe(aerQuery.selectAer, take(1)), store.pipe(aerQuery.selectIsCorsia, take(1))]).pipe(
    map(([aer, isCorsia]) => {
      const initialState = isCorsia
        ? AerCorsiaStoreDelegate.INITIAL_STATE.aerMonitoringPlanChanges
        : AerUkEtsStoreDelegate.INITIAL_STATE.aerMonitoringPlanChanges;

      if (!aer) {
        store.setPayload({
          ...payload,
          aer: {
            aerMonitoringPlanChanges: initialState,
          },
        } as any);
      }

      if (!aer?.aerMonitoringPlanChanges) {
        store.aerDelegate.setMonitoringPlanChanges(initialState);
      }

      formProvider.setFormValue(aer.aerMonitoringPlanChanges);

      return true;
    }),
  );
};

export const canDeactivateMonitoringPlanChanges: CanDeactivateFn<any> = () => {
  inject<MonitoringPlanChangesFormProvider>(TASK_FORM_PROVIDER).destroyForm();
  return true;
};
