import { inject } from '@angular/core';
import { CanActivateFn, CanDeactivateFn } from '@angular/router';

import { map, take, tap } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { AerCorsiaStoreDelegate } from '@aviation/request-task/store/delegates/aer-corsia/aer-corsia-store-delegate';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';

import { aerQuery } from '../../../shared/aer.selectors';
import { ConfidentialityFormProvider } from './confidentiality-form.provider';

export const canActivateConfidentiality: CanActivateFn = () => {
  const store = inject(RequestTaskStore);
  const formProvider = inject<ConfidentialityFormProvider>(TASK_FORM_PROVIDER);
  const payload = store.getState().requestTaskItem.requestTask.payload;

  return store.pipe(
    aerQuery.selectAerCorsia,
    take(1),
    tap((aer) => {
      if (!aer) {
        store.setPayload({
          ...payload,
          aer: {
            confidentiality: AerCorsiaStoreDelegate.INITIAL_STATE.confidentiality,
          },
        } as any);
      }

      if (!aer?.confidentiality) {
        (store.aerDelegate as AerCorsiaStoreDelegate).setConfidentiality(
          AerCorsiaStoreDelegate.INITIAL_STATE.confidentiality,
        );
      }
      formProvider.setFormValue(aer.confidentiality);
    }),
    map(() => true),
  );
};

export const canDeactivateConfidentiality: CanDeactivateFn<any> = () => {
  inject<ConfidentialityFormProvider>(TASK_FORM_PROVIDER).destroyForm();
  return true;
};
