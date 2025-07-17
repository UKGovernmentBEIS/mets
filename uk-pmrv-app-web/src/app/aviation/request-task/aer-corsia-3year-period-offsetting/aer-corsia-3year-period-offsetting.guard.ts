import { inject } from '@angular/core';
import { CanActivateFn, CanDeactivateFn } from '@angular/router';

import { map, take, tap } from 'rxjs';

import { RequestTaskStore } from '../store';
import { AerCorsia3YearOffsettingStoreDelegate } from '../store/delegates/aer-corsia-3year-period-offsetting/aer-corsia-3year-period-offsetting-store-delegate';
import { TASK_FORM_PROVIDER } from '../task-form.provider';
import { aerCorsia3YearOffsettingQuery } from './aer-corsia-3year-period-offsetting.selectors';
import { ThreeYearOffsettingRequirementsFormProvider } from './aer-corsia-3year-period-offsetting-form.provider';

export const canActivateAerCorsia3YearOffsetting: CanActivateFn = () => {
  const store = inject(RequestTaskStore);
  const formProvider = inject<ThreeYearOffsettingRequirementsFormProvider>(TASK_FORM_PROVIDER);

  return store.pipe(
    aerCorsia3YearOffsettingQuery.selectPayload,
    take(1),
    tap((payload) => {
      if (!payload.aviationAerCorsia3YearPeriodOffsetting) {
        store.setPayload({
          ...payload,
          aviationAerCorsia3YearPeriodOffsetting: {
            ...AerCorsia3YearOffsettingStoreDelegate.INITIAL_STATE,
            ...payload.aviationAerCorsia3YearPeriodOffsetting,
          },
        } as any);
      }

      formProvider.setFormValue(payload.aviationAerCorsia3YearPeriodOffsetting);
    }),
    map(() => true),
  );
};

export const canDeactivateAerCorsia3YearOffsetting: CanDeactivateFn<any> = () => {
  inject<ThreeYearOffsettingRequirementsFormProvider>(TASK_FORM_PROVIDER).destroyForm();
  return true;
};
