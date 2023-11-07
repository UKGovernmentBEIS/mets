import { inject } from '@angular/core';
import { CanActivateFn, CanDeactivateFn } from '@angular/router';

import { map, take, tap } from 'rxjs';

import { overallDecisionQuery } from '@aviation/request-task/aer/shared/overall-decision/overall-decision.selector';
import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';

import { OverallDecisionFormProvider } from './overall-decision-form.provider';

export const canActivateOverallDecision: CanActivateFn = () => {
  const store = inject(RequestTaskStore);
  const formProvider = inject<OverallDecisionFormProvider>(TASK_FORM_PROVIDER);

  return store.pipe(
    overallDecisionQuery.selectOverallDecision,
    take(1),
    tap((overallDecision) => {
      if (overallDecision) {
        formProvider.setFormValue(overallDecision);
      }
    }),
    map(() => true),
  );
};

export const canDeactivateOverallDecision: CanDeactivateFn<any> = () => {
  inject<OverallDecisionFormProvider>(TASK_FORM_PROVIDER).destroyForm();
  return true;
};
