import { inject } from '@angular/core';
import { CanActivateFn } from '@angular/router';

import { map, take, tap } from 'rxjs';

import { aerVerifyQuery } from '@aviation/request-task/aer/ukets/aer-verify/aer-verify.selector';
import { RequestTaskStore } from '@aviation/request-task/store';
import { AerVerifyUkEtsStoreDelegate } from '@aviation/request-task/store/delegates/aer-verify-ukets/aer-verify-ukets-store-delegate';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';

import { VerifierDetailsFormProvider } from './verifier-details-form.provider';

export const canActivateVerifierDetails: CanActivateFn = () => {
  const store = inject(RequestTaskStore);
  const formProvider = inject<VerifierDetailsFormProvider>(TASK_FORM_PROVIDER);
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

      if (!verificationReport.verifierContact || !verificationReport.verificationTeamDetails) {
        const verificationReportToSet = {
          ...verificationReport,
          verifierContact: AerVerifyUkEtsStoreDelegate.INITIAL_STATE.verificationReport.verifierContact,
          verificationTeamDetails: AerVerifyUkEtsStoreDelegate.INITIAL_STATE.verificationReport.verificationTeamDetails,
        };

        (store.aerVerifyDelegate as AerVerifyUkEtsStoreDelegate).setVerificationReport(verificationReportToSet);
      }

      formProvider.setFormValue(verificationReport);
    }),
    map(() => true),
  );
};

export const canDeactivateVerifierDetails: CanActivateFn = () => {
  inject<VerifierDetailsFormProvider>(TASK_FORM_PROVIDER).destroyForm();

  return true;
};
