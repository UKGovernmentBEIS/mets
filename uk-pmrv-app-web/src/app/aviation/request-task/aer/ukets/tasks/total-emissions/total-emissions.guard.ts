import { inject } from '@angular/core';
import { CanActivateFn, CanDeactivateFn } from '@angular/router';

import { map, take, tap } from 'rxjs';

import { TotalEmissionsFormProvider } from '@aviation/request-task/aer/ukets/tasks/total-emissions/total-emissions-form.provider';

import { RequestTaskStore } from '../../../../store';
import { AerStoreDelegate } from '../../../../store/delegates';
import { TASK_FORM_PROVIDER } from '../../../../task-form.provider';
import { aerQuery } from '../../../shared/aer.selectors';

export const canActivateTotalEmissions: CanActivateFn = () => {
  const store = inject(RequestTaskStore);
  const formProvider = inject<TotalEmissionsFormProvider>(TASK_FORM_PROVIDER);
  const payload = store.getState().requestTaskItem.requestTask.payload;

  return store.pipe(
    aerQuery.selectAer,
    take(1),
    tap((aer) => {
      if (!aer) {
        store.setPayload({
          ...payload,
          aer: {
            aviationAerTotalEmissionsConfidentiality:
              AerStoreDelegate.INITIAL_STATE.aviationAerTotalEmissionsConfidentiality,
          },
        } as any);
      }

      if (!aer?.aviationAerTotalEmissionsConfidentiality) {
        store.aerDelegate.setTotalEmissions(AerStoreDelegate.INITIAL_STATE.aviationAerTotalEmissionsConfidentiality);
      }

      formProvider.setFormValue(aer.aviationAerTotalEmissionsConfidentiality);
    }),
    map(() => true),
  );
};

export const canDeactivateTotalEmissions: CanDeactivateFn<any> = () => {
  inject<TotalEmissionsFormProvider>(TASK_FORM_PROVIDER).destroyForm();
  return true;
};
