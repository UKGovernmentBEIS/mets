import { inject } from '@angular/core';
import { CanActivateFn, CanDeactivateFn } from '@angular/router';

import { map, take, tap } from 'rxjs';

import { recommendedImprovementsQuery } from '@aviation/request-task/aer/shared/recommended-improvements/recommended-improvements.selector';
import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';

import { RecommendedImprovementsFormProvider } from './recommended-improvements-form.provider';

export const canActivateRecommendedImprovements: CanActivateFn = () => {
  const store = inject(RequestTaskStore);
  const formProvider = inject<RecommendedImprovementsFormProvider>(TASK_FORM_PROVIDER);

  return store.pipe(
    recommendedImprovementsQuery.selectRecommendedImprovements,
    take(1),
    tap((recommendedImprovement) => {
      if (recommendedImprovement) {
        formProvider.setFormValue(recommendedImprovement);
      }
    }),
    map(() => true),
  );
};

export const canDeactivateRecommendedImprovements: CanDeactivateFn<any> = () => {
  inject<RecommendedImprovementsFormProvider>(TASK_FORM_PROVIDER).destroyForm();
  return true;
};
