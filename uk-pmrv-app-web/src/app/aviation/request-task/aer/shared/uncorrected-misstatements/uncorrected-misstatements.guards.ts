import { inject } from '@angular/core';
import { CanActivateFn, CanDeactivateFn } from '@angular/router';

import { map, take, tap } from 'rxjs';

import { uncorrectedMisstatementsQuery } from '@aviation/request-task/aer/shared/uncorrected-misstatements/uncorrected-misstatements.selector';
import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';

import { UncorrectedMisstatementsFormProvider } from './uncorrected-misstatements-form.provider';

export const canActivateUncorrectedMisstatements: CanActivateFn = () => {
  const store = inject(RequestTaskStore);
  const formProvider = inject<UncorrectedMisstatementsFormProvider>(TASK_FORM_PROVIDER);

  return store.pipe(
    uncorrectedMisstatementsQuery.selectUncorrectedMisstatements,
    take(1),
    tap((uncorrectedMisstatements) => {
      if (uncorrectedMisstatements) {
        formProvider.setFormValue(uncorrectedMisstatements);
      }
    }),
    map(() => true),
  );
};

export const canDeactivateUncorrectedMisstatements: CanDeactivateFn<any> = () => {
  inject<UncorrectedMisstatementsFormProvider>(TASK_FORM_PROVIDER).destroyForm();
  return true;
};
