import { inject } from '@angular/core';
import { CanActivateFn, CanDeactivateFn } from '@angular/router';

import { map, take, tap } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { AerCorsiaStoreDelegate } from '@aviation/request-task/store/delegates/aer-corsia/aer-corsia-store-delegate';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';

import { aerQuery } from '../../../shared/aer.selectors';
import { EmissionsReductionClaimFormProvider } from './emissions-reduction-claim-form.provider';

export const canActivateEmissionsReductionClaim: CanActivateFn = () => {
  const store = inject(RequestTaskStore);
  const formProvider = inject<EmissionsReductionClaimFormProvider>(TASK_FORM_PROVIDER);
  const payload = store.getState().requestTaskItem.requestTask.payload;

  return store.pipe(
    aerQuery.selectAerCorsia,
    take(1),
    tap((aer) => {
      if (!aer) {
        store.setPayload({
          ...payload,
          aer: {
            emissionsReductionClaim: AerCorsiaStoreDelegate.INITIAL_STATE.emissionsReductionClaim,
          },
        } as any);
      }

      if (!aer?.emissionsReductionClaim) {
        (store.aerDelegate as AerCorsiaStoreDelegate).setEmissionsReductionClaim(
          AerCorsiaStoreDelegate.INITIAL_STATE.emissionsReductionClaim,
        );
      }
      formProvider.setFormValue(aer.emissionsReductionClaim);
    }),
    map(() => true),
  );
};

export const canDeactivateEmissionsReductionClaim: CanDeactivateFn<any> = () => {
  inject<EmissionsReductionClaimFormProvider>(TASK_FORM_PROVIDER).destroyForm();
  return true;
};
