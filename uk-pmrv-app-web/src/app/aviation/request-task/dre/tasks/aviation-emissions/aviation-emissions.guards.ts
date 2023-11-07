import { inject } from '@angular/core';
import { CanActivateFn, CanDeactivateFn } from '@angular/router';

import { map, take, tap } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { DreStoreDelegate } from '@aviation/request-task/store/delegates';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';

import { dreQuery } from '../../dre.selectors';
import { AviationEmissionsFormProvider } from './aviation-emissions-form.provider';

export const canActivateAviationEmissions: CanActivateFn = () => {
  const store = inject(RequestTaskStore);
  const formProvider = inject<AviationEmissionsFormProvider>(TASK_FORM_PROVIDER);
  const payload = store.getState().requestTaskItem.requestTask.payload;

  return store.pipe(
    dreQuery.selectDre,
    take(1),
    tap((dre) => {
      if (!dre) {
        store.setPayload({
          ...payload,
          dre: DreStoreDelegate.INITIAL_STATE.dre,
        } as any);
      }

      if (!dre) {
        store.dreDelegate.setAviationDre(DreStoreDelegate.INITIAL_STATE.dre);
      }

      formProvider.setFormValue(dre);
    }),
    map(() => true),
  );
};

export const canDeactivateAviationEmissions: CanDeactivateFn<any> = () => {
  inject<AviationEmissionsFormProvider>(TASK_FORM_PROVIDER).destroyForm();
  return true;
};

export const canActivateaviationEmissionsChargesCalculate: CanActivateFn = () => {
  const store = inject(RequestTaskStore);
  return store.pipe(
    dreQuery.selectDre,
    take(1),
    map((dre) => {
      return !!dre?.fee?.chargeOperator;
    }),
  );
};
