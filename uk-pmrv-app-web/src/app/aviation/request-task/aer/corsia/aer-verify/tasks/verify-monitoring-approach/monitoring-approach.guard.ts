import { inject } from '@angular/core';
import { CanActivateFn } from '@angular/router';

import { combineLatest, map, take, tap } from 'rxjs';

import { aerVerifyCorsiaQuery } from '@aviation/request-task/aer/corsia/aer-verify/aer-verify-corsia.selector';
import { MonitoringApproachFormProvider } from '@aviation/request-task/aer/corsia/aer-verify/tasks/verify-monitoring-approach/monitoring-approach-form.provider';
import { RequestTaskStore } from '@aviation/request-task/store';
import { AerVerifyCorsiaStoreDelegate } from '@aviation/request-task/store/delegates/aer-verify-corsia';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';

export const canActivateMonitoringApproach: CanActivateFn = () => {
  const store = inject(RequestTaskStore);
  const formProvider = inject<MonitoringApproachFormProvider>(TASK_FORM_PROVIDER);

  return combineLatest([
    store.pipe(aerVerifyCorsiaQuery.selectAer),
    store.pipe(aerVerifyCorsiaQuery.selectOpinionStatement),
  ]).pipe(
    take(1),
    tap(([aer, opinionStatement]) => {
      if (opinionStatement) {
        formProvider.setFormValue(opinionStatement);
      } else {
        const fuelTypes = aer.aggregatedEmissionsData.aggregatedEmissionDataDetails.map((details) => details.fuelType);
        formProvider.setFormValue({
          ...AerVerifyCorsiaStoreDelegate.INITIAL_STATE.verificationReport.opinionStatement,
          fuelTypes: fuelTypes.filter(function (fuel, index) {
            return fuelTypes.indexOf(fuel) == index;
          }),
        });
      }
    }),
    map(() => true),
  );
};

export const canDeactivateMonitoringApproach: CanActivateFn = () => {
  inject<MonitoringApproachFormProvider>(TASK_FORM_PROVIDER).destroyForm();
  return true;
};
