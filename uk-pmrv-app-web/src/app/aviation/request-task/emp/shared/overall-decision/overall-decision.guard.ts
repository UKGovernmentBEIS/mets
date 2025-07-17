import { inject } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  CanActivateFn,
  CanDeactivateFn,
  createUrlTreeFromSnapshot,
  Router,
} from '@angular/router';

import { map, take, tap } from 'rxjs';

import { EmpReviewRequestTaskPayload } from '@aviation/request-task/emp/shared/util/emp.util';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';

import { empQuery } from '../emp.selectors';
import { OverallDecisionFormProvider } from './overall-decision-form.provider';
import { reasonRequiredValidator } from './overall-decision-form.util';

export const canActivateOverallDecision: CanActivateFn = () => {
  const store = inject(RequestTaskStore);
  const formProvider = inject<OverallDecisionFormProvider>(TASK_FORM_PROVIDER);
  const payload = store.getState().requestTaskItem.requestTask.payload;

  return store.pipe(
    empQuery.selectDetermination,
    take(1),
    tap((determination) => {
      if (!determination) {
        store.setPayload({
          ...payload,
          determination: { type: null, reason: null },
        } as any);
      }

      if (!determination) {
        store.empDelegate.setOverallDecision({ type: null, reason: null });
      }

      formProvider.setFormValue(determination);
    }),
    map(() => true),
  );
};

export const canDeactivateOverallDecision: CanDeactivateFn<any> = () => {
  inject<OverallDecisionFormProvider>(TASK_FORM_PROVIDER).destroyForm();
  return true;
};

export const canActivateOverallDecisionReason = () => {
  const formProvider = inject<OverallDecisionFormProvider>(TASK_FORM_PROVIDER);

  if (
    formProvider.form.controls.type.value === 'DEEMED_WITHDRAWN' ||
    formProvider.form.controls.type.value === 'REJECTED'
  ) {
    formProvider.form.controls.reason.addValidators(reasonRequiredValidator);
  } else {
    formProvider.form.controls.reason.removeValidators(reasonRequiredValidator);
  }

  formProvider.form.controls.reason.updateValueAndValidity();
};

export const canActivateOverallDecisionForms = (route: ActivatedRouteSnapshot) => {
  const store = inject(RequestTaskStore);
  const change = route.queryParamMap.get('change') === 'true';

  return store.pipe(
    requestTaskQuery.selectRequestTaskPayload,
    take(1),
    map((payload) => {
      return (
        change ||
        (payload as EmpReviewRequestTaskPayload).reviewSectionsCompleted['decision'] !== true ||
        createUrlTreeFromSnapshot(route, ['reason'].includes(route.url.toString()) ? ['../', 'summary'] : ['summary'])
      );
    }),
  );
};

export const canActivateOverallDecisionSummaryPage: CanActivateFn = (route) => {
  const force = inject(Router).getCurrentNavigation().extras?.state?.force;
  return inject(RequestTaskStore).pipe(
    requestTaskQuery.selectRequestTaskPayload,
    take(1),
    map(
      (payload) =>
        force === true ||
        (payload as EmpReviewRequestTaskPayload).reviewSectionsCompleted['decision'] === true ||
        createUrlTreeFromSnapshot(route, ['../']),
    ),
  );
};
