import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, CanDeactivateFn } from '@angular/router';

import { map, take, tap } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { RecommendationResponseItemFormProvider } from '@aviation/request-task/vir/review/tasks/recommendation-response-item/recommendation-response-item-form.provider';
import { virQuery } from '@aviation/request-task/vir/vir.selectors';

export const canActivateRecommendationResponseItem: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const store = inject(RequestTaskStore);
  const formProvider = inject<RecommendationResponseItemFormProvider>(TASK_FORM_PROVIDER);

  return store.pipe(virQuery.selectRegulatorImprovementResponses).pipe(
    take(1),
    tap((regulatorImprovementResponses) => {
      const response = regulatorImprovementResponses[route.paramMap.get('id')];
      if (response) {
        formProvider.setFormValue(response);
      }
    }),
    map(() => true),
  );
};

export const canDeactivateRecommendationResponseItem: CanDeactivateFn<unknown> = () => {
  inject<RecommendationResponseItemFormProvider>(TASK_FORM_PROVIDER).destroyForm();
  return true;
};
