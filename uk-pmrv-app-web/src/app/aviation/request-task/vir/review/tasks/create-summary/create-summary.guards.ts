import { inject } from '@angular/core';
import { CanActivateFn, CanDeactivateFn } from '@angular/router';

import { map, take, tap } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';
import { CreateSummaryFormProvider } from '@aviation/request-task/vir/review/tasks/create-summary/create-summary-form.provider';
import { virQuery } from '@aviation/request-task/vir/vir.selectors';

export const canActivateCreateSummary: CanActivateFn = () => {
  const store = inject(RequestTaskStore);
  const formProvider = inject<CreateSummaryFormProvider>(TASK_FORM_PROVIDER);

  return store.pipe(virQuery.selectRegulatorReviewResponse).pipe(
    take(1),
    tap((regulatorImprovementResponse) => {
      const reportSummary = regulatorImprovementResponse.reportSummary;
      if (reportSummary) {
        formProvider.setFormValue(reportSummary);
      }
    }),
    map(() => true),
  );
};

export const canDeactivateCreateSummary: CanDeactivateFn<unknown> = () => {
  inject<CreateSummaryFormProvider>(TASK_FORM_PROVIDER).destroyForm();
  return true;
};
