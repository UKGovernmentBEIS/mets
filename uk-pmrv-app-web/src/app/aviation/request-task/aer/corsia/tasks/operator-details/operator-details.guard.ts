import { inject } from '@angular/core';
import { CanActivateFn, CanDeactivateFn } from '@angular/router';

import { map, take, tap } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { AerCorsiaStoreDelegate } from '@aviation/request-task/store/delegates/aer-corsia/aer-corsia-store-delegate';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';

import { aerQuery } from '../../../shared/aer.selectors';
import { OperatorDetailsFormProvider } from './operator-details-form.provider';

export const canActivateOperatorDetails: CanActivateFn = () => {
  const store = inject(RequestTaskStore);
  const formProvider = inject<OperatorDetailsFormProvider>(TASK_FORM_PROVIDER);
  const payload = store.getState().requestTaskItem.requestTask.payload;

  return store.pipe(
    aerQuery.selectAerCorsia,
    take(1),
    tap((aer) => {
      if (!aer) {
        store.setPayload({
          ...payload,
          emissionsMonitoringPlan: {
            operatorDetails: AerCorsiaStoreDelegate.INITIAL_STATE.operatorDetails,
          },
        } as any);
      }

      if (!aer?.operatorDetails) {
        (store.aerDelegate as AerCorsiaStoreDelegate).setOperatorDetails(
          AerCorsiaStoreDelegate.INITIAL_STATE.operatorDetails,
        );
      }

      formProvider.setFormValue(aer?.operatorDetails);
    }),
    map(() => true),
  );
};

export const canDeactivateOperatorDetails: CanDeactivateFn<any> = () => {
  inject<OperatorDetailsFormProvider>(TASK_FORM_PROVIDER).destroyForm();
  return true;
};
