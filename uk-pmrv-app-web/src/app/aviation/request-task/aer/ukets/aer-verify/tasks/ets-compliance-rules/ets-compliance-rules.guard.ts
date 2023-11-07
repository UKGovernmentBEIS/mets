import { inject } from '@angular/core';
import { CanActivateFn } from '@angular/router';

import { map, take, tap } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { AerVerifyStoreDelegate } from '@aviation/request-task/store/delegates/aer-verify';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';

import { aerVerifyQuery } from '../../aer-verify.selector';
import { EtsComplianceRulesFormProvider } from './ets-compliance-rules-form.provider';

export const canActivateEtsComplianceRules: CanActivateFn = () => {
  const store = inject(RequestTaskStore);
  const formProvider = inject<EtsComplianceRulesFormProvider>(TASK_FORM_PROVIDER);
  const payload = store.getState().requestTaskItem.requestTask.payload;

  return store.pipe(
    aerVerifyQuery.selectVerificationReport,
    take(1),
    tap((verificationReport) => {
      if (!verificationReport) {
        store.setPayload({
          ...payload,
          verificationReport: AerVerifyStoreDelegate.INITIAL_STATE.verificationReport,
        } as any);
      }

      if (!verificationReport?.etsComplianceRules) {
        (store.aerVerifyDelegate as AerVerifyStoreDelegate).setEtsComplianceRules(
          AerVerifyStoreDelegate.INITIAL_STATE.verificationReport.etsComplianceRules,
        );
      }

      formProvider.setFormValue(verificationReport.etsComplianceRules);
    }),
    map(() => true),
  );
};

export const canDeactivateEtsComplianceRules: CanActivateFn = () => {
  inject<EtsComplianceRulesFormProvider>(TASK_FORM_PROVIDER).destroyForm();

  return true;
};
