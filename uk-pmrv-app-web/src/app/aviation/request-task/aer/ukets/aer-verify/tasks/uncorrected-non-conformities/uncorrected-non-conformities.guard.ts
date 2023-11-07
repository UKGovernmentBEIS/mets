import { inject } from '@angular/core';
import { CanActivateFn, CanDeactivateFn, createUrlTreeFromSnapshot } from '@angular/router';

import { map, take, tap } from 'rxjs';

import { aerVerifyQuery } from '@aviation/request-task/aer/ukets/aer-verify/aer-verify.selector';
import { RequestTaskStore } from '@aviation/request-task/store';
import { AerVerifyStoreDelegate } from '@aviation/request-task/store/delegates/aer-verify';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';

import { UncorrectedNonConformitiesFormProvider } from './uncorrected-non-conformities-form.provider';

export const canActivateUncorrectedNonConformities: CanActivateFn = () => {
  const store = inject(RequestTaskStore);
  const formProvider = inject<UncorrectedNonConformitiesFormProvider>(TASK_FORM_PROVIDER);
  const payload = store.getState().requestTaskItem.requestTask.payload;

  return store.pipe(
    aerVerifyQuery.selectVerificationReport,
    take(1),
    tap((verificationReport) => {
      if (!verificationReport) {
        store.setPayload({
          ...payload,
          uncorrectedNonConformities:
            AerVerifyStoreDelegate.INITIAL_STATE.verificationReport?.uncorrectedNonConformities,
        } as any);
      }

      if (!verificationReport?.uncorrectedNonConformities) {
        (store.aerVerifyDelegate as AerVerifyStoreDelegate).setUncorrectedNonConformities(
          AerVerifyStoreDelegate.INITIAL_STATE.verificationReport.uncorrectedNonConformities,
        );
      }

      formProvider.setFormValue(verificationReport?.uncorrectedNonConformities);
    }),
    map(() => true),
  );
};

export const canActivateUncorrectedList: CanActivateFn = (route) => {
  const uncorrectedNonConformities =
    inject<UncorrectedNonConformitiesFormProvider>(TASK_FORM_PROVIDER).getFormValue()?.uncorrectedNonConformities;

  const addUrl = createUrlTreeFromSnapshot(route, ['0']);

  return uncorrectedNonConformities?.length > 0 || addUrl;
};

export const canActivatePriorYearIssuesList: CanActivateFn = (route) => {
  const priorYearIssues =
    inject<UncorrectedNonConformitiesFormProvider>(TASK_FORM_PROVIDER).getFormValue()?.priorYearIssues;

  const addUrl = createUrlTreeFromSnapshot(route, ['0']);

  return priorYearIssues?.length > 0 || addUrl;
};

export const canDeactivateUncorrectedNonConformities: CanDeactivateFn<any> = () => {
  inject<UncorrectedNonConformitiesFormProvider>(TASK_FORM_PROVIDER).destroyForm();
  return true;
};
