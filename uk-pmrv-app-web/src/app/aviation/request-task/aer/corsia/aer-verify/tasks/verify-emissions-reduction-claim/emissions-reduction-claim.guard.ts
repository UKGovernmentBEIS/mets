import { inject } from '@angular/core';
import { CanActivateFn } from '@angular/router';

import { map, take, tap } from 'rxjs';

import { aerVerifyCorsiaQuery } from '@aviation/request-task/aer/corsia/aer-verify/aer-verify-corsia.selector';
import { EmissionsReductionClaimFormProvider } from '@aviation/request-task/aer/corsia/aer-verify/tasks/verify-emissions-reduction-claim/emissions-reduction-claim-form.provider';
import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';

export const canActivateEmissionsReductionClaim: CanActivateFn = () => {
  const store = inject(RequestTaskStore);
  const formProvider = inject<EmissionsReductionClaimFormProvider>(TASK_FORM_PROVIDER);

  return store.pipe(
    aerVerifyCorsiaQuery.selectEmissionsReductionClaimVerification,
    take(1),
    tap((reductionClaim) => {
      if (reductionClaim) {
        formProvider.setFormValue(reductionClaim);
      }
    }),
    map(() => true),
  );
};

export const canDeactivateEmissionsReductionClaim: CanActivateFn = () => {
  inject<EmissionsReductionClaimFormProvider>(TASK_FORM_PROVIDER).destroyForm();
  return true;
};
