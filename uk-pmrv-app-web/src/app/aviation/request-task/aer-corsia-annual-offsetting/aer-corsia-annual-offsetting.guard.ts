import { inject } from '@angular/core';
import { CanActivateFn, CanDeactivateFn } from '@angular/router';

import { map, take, tap } from 'rxjs';

import { RequestTaskStore } from '../store';
import { AerCorsiaAnnualOffsettingStoreDelegate } from '../store/delegates/aer-corsia-annual-offsetting/aer-corsia-annual-offsetting-store-delegate';
import { TASK_FORM_PROVIDER } from '../task-form.provider';
import { aerCorsiaAnnualOffsettingQuery } from './aer-corsia-annual-offsetting.selectors';
import { AnnualOffsettingRequirementsFormProvider } from './aer-corsia-annual-offsetting-form.provider';

export const canActivateAerCorsiaAnnualOffsetting: CanActivateFn = () => {
  const store = inject(RequestTaskStore);
  const formProvider = inject<AnnualOffsettingRequirementsFormProvider>(TASK_FORM_PROVIDER);

  return store.pipe(
    aerCorsiaAnnualOffsettingQuery.selectPayload,
    take(1),
    tap((payload) => {
      if (!payload.aviationAerCorsiaAnnualOffsetting) {
        store.setPayload({
          ...payload,
          aviationAerCorsiaAnnualOffsetting: {
            ...AerCorsiaAnnualOffsettingStoreDelegate.INITIAL_STATE,
          },
        } as any);
      }

      formProvider.setFormValue(payload.aviationAerCorsiaAnnualOffsetting);
    }),
    map(() => true),
  );
};

export const canDeactivateAerCorsiaAnnualOffsetting: CanDeactivateFn<any> = () => {
  inject<AnnualOffsettingRequirementsFormProvider>(TASK_FORM_PROVIDER).destroyForm();
  return true;
};
