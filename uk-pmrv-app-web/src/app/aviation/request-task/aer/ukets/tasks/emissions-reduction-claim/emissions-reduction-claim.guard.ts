import { inject } from '@angular/core';
import { CanActivateFn, CanDeactivateFn } from '@angular/router';

import { map, take, tap } from 'rxjs';

import { RequestTaskStore } from '../../../../store';
import { AerStoreDelegate } from '../../../../store/delegates';
import { TASK_FORM_PROVIDER } from '../../../../task-form.provider';
import { aerQuery } from '../../../shared/aer.selectors';
import { aerEmissionsReductionClaimFormProvider } from './emissions-reduction-claim-form.provider';

export const canActivateAerEmissionsReductionClaim: CanActivateFn = () => {
  const store = inject(RequestTaskStore);
  const formProvider = inject<aerEmissionsReductionClaimFormProvider>(TASK_FORM_PROVIDER);
  const payload = store.getState().requestTaskItem.requestTask.payload;

  return store.pipe(
    aerQuery.selectAer,
    take(1),
    tap((aer) => {
      if (!aer) {
        store.setPayload({
          ...payload,
          aer: {
            saf: AerStoreDelegate.INITIAL_STATE.saf,
          },
        } as any);
      }

      if (!aer?.saf) {
        store.aerDelegate.setSaf(AerStoreDelegate.INITIAL_STATE.saf);
      }

      formProvider.setFormValue(aer.saf);
    }),
    map(() => true),
  );
};

export const canDeactivateAerEmissionsReductionClaim: CanDeactivateFn<any> = () => {
  inject<aerEmissionsReductionClaimFormProvider>(TASK_FORM_PROVIDER).destroyForm();
  return true;
};
