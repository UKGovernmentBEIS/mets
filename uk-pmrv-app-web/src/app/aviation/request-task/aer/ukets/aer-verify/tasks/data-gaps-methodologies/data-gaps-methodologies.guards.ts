import { inject } from '@angular/core';
import { CanActivateFn, CanDeactivateFn } from '@angular/router';

import { map, take, tap } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { AerVerifyStoreDelegate } from '@aviation/request-task/store/delegates/aer-verify';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';

import { aerVerifyQuery } from '../../aer-verify.selector';
import { DataGapsMethodologiesFormProvider } from './data-gaps-methodologies-form.provider';

export const canActivateDataGapsMethodologies: CanActivateFn = () => {
  const store = inject(RequestTaskStore);
  const formProvider = inject<DataGapsMethodologiesFormProvider>(TASK_FORM_PROVIDER);
  const payload = store.getState().requestTaskItem.requestTask.payload;

  return store.pipe(
    aerVerifyQuery.selectVerificationReport,
    take(1),
    tap((verificationReport) => {
      if (!verificationReport) {
        store.setPayload({
          ...payload,
          dataGapsMethodologies: AerVerifyStoreDelegate.INITIAL_STATE.verificationReport?.dataGapsMethodologies,
        } as any);
      }

      if (!verificationReport?.dataGapsMethodologies) {
        (store.aerVerifyDelegate as AerVerifyStoreDelegate).setDataGapsMethodologies(
          AerVerifyStoreDelegate.INITIAL_STATE.verificationReport.dataGapsMethodologies,
        );
      }

      formProvider.setFormValue(verificationReport?.dataGapsMethodologies);
    }),
    map(() => true),
  );
};

export const canDeactivateDataGapsMethodologies: CanDeactivateFn<any> = () => {
  inject<DataGapsMethodologiesFormProvider>(TASK_FORM_PROVIDER).destroyForm();
  return true;
};
