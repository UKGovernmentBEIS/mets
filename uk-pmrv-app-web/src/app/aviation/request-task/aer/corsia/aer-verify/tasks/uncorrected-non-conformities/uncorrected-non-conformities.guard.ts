import { inject } from '@angular/core';
import { CanActivateFn } from '@angular/router';

import { map, take, tap } from 'rxjs';

import { aerVerifyCorsiaQuery } from '@aviation/request-task/aer/corsia/aer-verify/aer-verify-corsia.selector';
import { UncorrectedNonConformitiesFormProvider } from '@aviation/request-task/aer/corsia/aer-verify/tasks/uncorrected-non-conformities/uncorrected-non-conformities-form.provider';
import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';

export const canActivateUncorrectedNonConformities: CanActivateFn = () => {
  const store = inject(RequestTaskStore);
  const formProvider = inject<UncorrectedNonConformitiesFormProvider>(TASK_FORM_PROVIDER);

  return store.pipe(
    aerVerifyCorsiaQuery.selectUncorrectedNonConformities,
    take(1),
    tap((uncorrectedNonConformities) => {
      if (uncorrectedNonConformities) {
        formProvider.setFormValue(uncorrectedNonConformities);
      }
    }),
    map(() => true),
  );
};

export const canDeactivateUncorrectedNonConformities: CanActivateFn = () => {
  inject<UncorrectedNonConformitiesFormProvider>(TASK_FORM_PROVIDER).destroyForm();
  return true;
};
