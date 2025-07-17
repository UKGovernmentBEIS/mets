import { inject } from '@angular/core';
import { CanActivateFn } from '@angular/router';

import { map, take, tap } from 'rxjs';

import { aerVerifyQuery } from '@aviation/request-task/aer/ukets/aer-verify/aer-verify.selector';
import { RequestTaskStore } from '@aviation/request-task/store';
import { AerVerifyUkEtsStoreDelegate } from '@aviation/request-task/store/delegates/aer-verify-ukets/aer-verify-ukets-store-delegate';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';

import { VerifyEmissionsReductionClaimFormProvider } from './verify-emissions-reduction-claim-form.provider';

export const canActivateVerifyEmissionsReductionClaim: CanActivateFn = () => {
  const store = inject(RequestTaskStore);
  const formProvider = inject<VerifyEmissionsReductionClaimFormProvider>(TASK_FORM_PROVIDER);
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

      if (!verificationReport?.emissionsReductionClaimVerification) {
        (store.aerVerifyDelegate as AerVerifyUkEtsStoreDelegate).setEmissionsReductionClaimVerification(
          AerVerifyUkEtsStoreDelegate.INITIAL_STATE.verificationReport.emissionsReductionClaimVerification,
        );
      }

      formProvider.setFormValue(verificationReport.emissionsReductionClaimVerification);
    }),
    map(() => true),
  );
};

export const canDeactivateVerifyEmissionsReductionClaim: CanActivateFn = () => {
  inject<VerifyEmissionsReductionClaimFormProvider>(TASK_FORM_PROVIDER).destroyForm();

  return true;
};
