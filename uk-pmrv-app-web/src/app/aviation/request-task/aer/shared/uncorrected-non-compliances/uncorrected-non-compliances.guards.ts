import { inject } from '@angular/core';
import { CanActivateFn, CanDeactivateFn } from '@angular/router';

import { map, take, tap } from 'rxjs';

import { uncorrectedNonCompliancesQuery } from '@aviation/request-task/aer/shared/uncorrected-non-compliances/uncorrected-non-compliances.selector';
import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';

import { UncorrectedNonCompliancesFormProvider } from './uncorrected-non-compliances-form.provider';

export const canActivateUncorrectedNonCompliances: CanActivateFn = () => {
  const store = inject(RequestTaskStore);
  const formProvider = inject<UncorrectedNonCompliancesFormProvider>(TASK_FORM_PROVIDER);

  return store.pipe(
    uncorrectedNonCompliancesQuery.selectUncorrectedNonCompliances,
    take(1),
    tap((uncorrectedNonCompliances) => {
      if (uncorrectedNonCompliances) {
        formProvider.setFormValue(uncorrectedNonCompliances);
      }
    }),
    map(() => true),
  );
};

export const canDeactivateUncorrectedNonCompliances: CanDeactivateFn<any> = () => {
  inject<UncorrectedNonCompliancesFormProvider>(TASK_FORM_PROVIDER).destroyForm();
  return true;
};
