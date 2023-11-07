import { inject } from '@angular/core';
import { CanActivateFn, CanDeactivateFn } from '@angular/router';

import { map, take, tap } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { AerVerifyStoreDelegate } from '@aviation/request-task/store/delegates/aer-verify';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';

import { aerVerifyQuery } from '../../aer-verify.selector';
import { ComplianceMonitoringFormProvider } from './compliance-monitoring-form.provider';

export const canActivateComplianceMonitoring: CanActivateFn = () => {
  const store = inject(RequestTaskStore);
  const formProvider = inject<ComplianceMonitoringFormProvider>(TASK_FORM_PROVIDER);
  const payload = store.getState().requestTaskItem.requestTask.payload;

  return store.pipe(
    aerVerifyQuery.selectVerificationReport,
    take(1),
    tap((verificationReport) => {
      if (!verificationReport) {
        store.setPayload({
          ...payload,
          complianceMonitoringReportingRules:
            AerVerifyStoreDelegate.INITIAL_STATE.verificationReport?.complianceMonitoringReportingRules,
        } as any);
      }

      if (!verificationReport?.complianceMonitoringReportingRules) {
        (store.aerVerifyDelegate as AerVerifyStoreDelegate).setComplianceMonitoring(
          AerVerifyStoreDelegate.INITIAL_STATE.verificationReport.complianceMonitoringReportingRules,
        );
      }

      formProvider.setFormValue(verificationReport?.complianceMonitoringReportingRules);
    }),
    map(() => true),
  );
};

export const canDeactivateComplianceMonitoring: CanDeactivateFn<any> = () => {
  inject<ComplianceMonitoringFormProvider>(TASK_FORM_PROVIDER).destroyForm();
  return true;
};
