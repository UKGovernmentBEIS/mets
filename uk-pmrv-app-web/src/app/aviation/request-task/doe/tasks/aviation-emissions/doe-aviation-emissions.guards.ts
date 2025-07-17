import { inject } from '@angular/core';
import { CanActivateFn, CanDeactivateFn } from '@angular/router';

import { map, take, tap } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { DoeStoreDelegate } from '@aviation/request-task/store/delegates/doe';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';

import { doeQuery } from '../../doe.selectors';
import { DoeCorsiaEmissionsFormProvider } from './doe-corsia-emissions-form.provider';

export const canActivateDoeAviationEmissions: CanActivateFn = () => {
  const store = inject(RequestTaskStore);
  const formProvider = inject<DoeCorsiaEmissionsFormProvider>(TASK_FORM_PROVIDER);
  const payload = store.getState().requestTaskItem.requestTask.payload;

  return store.pipe(
    doeQuery.selectDoe,
    take(1),
    tap((doe) => {
      if (!doe) {
        store.setPayload({
          ...payload,
          doe: DoeStoreDelegate.INITIAL_STATE.doe,
        } as any);
      }

      if (!doe) {
        store.doeDelegate.setAviationDoe(DoeStoreDelegate.INITIAL_STATE.doe);
      }

      formProvider.setFormValue(doe);
    }),
    map(() => true),
  );
};

export const canDeactivateDoeAviationEmissions: CanDeactivateFn<any> = () => {
  inject<DoeCorsiaEmissionsFormProvider>(TASK_FORM_PROVIDER).destroyForm();
  return true;
};

export const canActivateDoeEmissionsChargesCalculate: CanActivateFn = () => {
  const store = inject(RequestTaskStore);
  return store.pipe(
    doeQuery.selectDoe,
    take(1),
    map((doe) => {
      return !!doe?.fee?.chargeOperator;
    }),
  );
};
