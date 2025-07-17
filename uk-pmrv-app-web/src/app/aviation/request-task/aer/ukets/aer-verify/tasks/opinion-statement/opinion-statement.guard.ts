import { inject } from '@angular/core';
import { CanActivateFn } from '@angular/router';

import { map, take, tap } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { AerVerifyUkEtsStoreDelegate } from '@aviation/request-task/store/delegates/aer-verify-ukets/aer-verify-ukets-store-delegate';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';

import { aerVerifyQuery } from '../../aer-verify.selector';
import { OpinionStatementFormProvider } from './opinion-statement-form.provider';

export const canActivateOpinionStatement: CanActivateFn = () => {
  const store = inject(RequestTaskStore);
  const formProvider = inject<OpinionStatementFormProvider>(TASK_FORM_PROVIDER);
  const payload = store.getState().requestTaskItem.requestTask.payload;

  return store.pipe(
    aerVerifyQuery.selectVerificationReport,
    take(1),
    tap((verificationReport) => {
      if (!verificationReport) {
        store.setPayload({
          ...payload,
          verificationReport: AerVerifyUkEtsStoreDelegate.INITIAL_STATE.verificationReport,
        } as any);
      }

      if (!verificationReport?.opinionStatement) {
        (store.aerVerifyDelegate as AerVerifyUkEtsStoreDelegate).setOpinionStatement(
          AerVerifyUkEtsStoreDelegate.INITIAL_STATE.verificationReport.opinionStatement,
        );
      }

      formProvider.setFormValue(verificationReport.opinionStatement);
    }),
    map(() => true),
  );
};

export const canDeactivateOpinionStatement: CanActivateFn = () => {
  inject<OpinionStatementFormProvider>(TASK_FORM_PROVIDER).destroyForm();

  return true;
};
