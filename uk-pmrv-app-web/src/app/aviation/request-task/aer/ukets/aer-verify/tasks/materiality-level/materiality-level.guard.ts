import { inject } from '@angular/core';
import { CanActivateFn, CanDeactivateFn } from '@angular/router';

import { map, take, tap } from 'rxjs';

import { aerVerifyQuery } from '@aviation/request-task/aer/ukets/aer-verify/aer-verify.selector';
import { MaterialityLevelFormProvider } from '@aviation/request-task/aer/ukets/aer-verify/tasks/materiality-level/materiality-level-form.provider';
import { RequestTaskStore } from '@aviation/request-task/store';
import { AerVerifyUkEtsStoreDelegate } from '@aviation/request-task/store/delegates/aer-verify-ukets/aer-verify-ukets-store-delegate';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';

export const canActivateMaterialityLevel: CanActivateFn = () => {
  const store = inject(RequestTaskStore);
  const formProvider = inject<MaterialityLevelFormProvider>(TASK_FORM_PROVIDER);
  const payload = store.getState().requestTaskItem.requestTask.payload;

  return store.pipe(
    aerVerifyQuery.selectVerificationReport,
    take(1),
    tap((verificationReport) => {
      if (!verificationReport) {
        store.setPayload({
          ...payload,
          materialityLevel: AerVerifyUkEtsStoreDelegate.INITIAL_STATE.verificationReport?.materialityLevel,
        } as any);
      }

      if (!verificationReport?.materialityLevel) {
        (store.aerVerifyDelegate as AerVerifyUkEtsStoreDelegate).setMaterialityLevel(
          AerVerifyUkEtsStoreDelegate.INITIAL_STATE.verificationReport.materialityLevel,
        );
      }

      formProvider.setFormValue(verificationReport?.materialityLevel);
    }),
    map(() => true),
  );
};

export const canDeactivateMaterialityLevel: CanDeactivateFn<any> = () => {
  inject<MaterialityLevelFormProvider>(TASK_FORM_PROVIDER).destroyForm();
  return true;
};
