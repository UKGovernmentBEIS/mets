import { inject } from '@angular/core';
import { CanActivateFn } from '@angular/router';

import { map, take, tap } from 'rxjs';

import { aerVerifyCorsiaQuery } from '@aviation/request-task/aer/corsia/aer-verify/aer-verify-corsia.selector';
import { VerifierDetailsFormProvider } from '@aviation/request-task/aer/corsia/aer-verify/tasks/verifier-details/verifier-details-form.provider';
import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';

export const canActivateVerifierDetails: CanActivateFn = () => {
  const store = inject(RequestTaskStore);
  const formProvider = inject<VerifierDetailsFormProvider>(TASK_FORM_PROVIDER);

  return store.pipe(
    aerVerifyCorsiaQuery.selectVerifierDetails,
    take(1),
    tap((verifierDetails) => {
      if (verifierDetails) {
        formProvider.setFormValue(verifierDetails);
      }
    }),
    map(() => true),
  );
};

export const canDeactivateVerifierDetails: CanActivateFn = () => {
  inject<VerifierDetailsFormProvider>(TASK_FORM_PROVIDER).destroyForm();
  return true;
};
