import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, CanDeactivateFn } from '@angular/router';

import { map, take, tap } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { ReferenceItemFormProvider } from '@aviation/request-task/vir/submit/tasks/reference-item/reference-item-form.provider';
import { virQuery } from '@aviation/request-task/vir/vir.selectors';

export const canActivateReferenceItem: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const store = inject(RequestTaskStore);
  const formProvider = inject<ReferenceItemFormProvider>(TASK_FORM_PROVIDER);

  return store.pipe(virQuery.selectOperatorImprovementResponses).pipe(
    take(1),
    tap((operatorImprovementResponses) => {
      const response = operatorImprovementResponses[route.paramMap.get('id')];
      if (response) {
        formProvider.setFormValue(response);
      }
    }),
    map(() => true),
  );
};

export const canDeactivateActivateReferenceItem: CanDeactivateFn<unknown> = () => {
  inject<ReferenceItemFormProvider>(TASK_FORM_PROVIDER).destroyForm();
  return true;
};
