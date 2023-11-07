import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, CanDeactivateFn } from '@angular/router';

import { map, take, tap } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { RespondItemFormProvider } from '@aviation/request-task/vir/respond/tasks/respond-item/respond-item-form.provider';
import { virQuery } from '@aviation/request-task/vir/vir.selectors';

export const canActivateRespondItem: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const store = inject(RequestTaskStore);
  const formProvider = inject<RespondItemFormProvider>(TASK_FORM_PROVIDER);

  return store.pipe(virQuery.selectOperatorImprovementFollowUpResponses).pipe(
    take(1),
    tap((followUpResponses) => {
      const response = followUpResponses[route.paramMap.get('id')];
      if (response) {
        formProvider.setFormValue(response);
      }
    }),
    map(() => true),
  );
};

export const canDeactivateRespondItem: CanDeactivateFn<unknown> = () => {
  inject<RespondItemFormProvider>(TASK_FORM_PROVIDER).destroyForm();
  return true;
};
