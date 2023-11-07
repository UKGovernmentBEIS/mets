import { inject } from '@angular/core';
import { CanActivateFn, CanDeactivateFn } from '@angular/router';

import { map, take, tap } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';

import { TASK_FORM_PROVIDER } from '../../../task-form.provider';
import { aerQuery } from '../aer.selectors';
import { ReportingObligationFormProvider } from './reporting-obligation-form.provider';

export const canActivateReportingObligation: CanActivateFn = () => {
  const store = inject(RequestTaskStore);
  const formProvider = inject<ReportingObligationFormProvider>(TASK_FORM_PROVIDER);
  const payload = store.getState().requestTaskItem.requestTask.payload;

  return store.pipe(
    aerQuery.selectReportingObligation,
    take(1),
    tap((reportingObligation) => {
      store.setPayload({
        ...payload,
        reportingRequired: reportingObligation.reportingRequired ?? null,
        reportingObligationDetails: reportingObligation.reportingObligationDetails ?? null,
      } as any);

      store.aerDelegate.setReportingObligation(reportingObligation);

      formProvider.setFormValue(reportingObligation);
    }),
    map(() => true),
  );
};

export const canDeactivateReportingObligation: CanDeactivateFn<boolean> = () => {
  inject<ReportingObligationFormProvider>(TASK_FORM_PROVIDER).destroyForm();

  return true;
};
