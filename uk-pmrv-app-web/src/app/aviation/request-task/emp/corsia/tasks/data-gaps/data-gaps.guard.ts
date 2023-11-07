import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, CanDeactivateFn, Router, RouterStateSnapshot } from '@angular/router';

import { map, take, tap } from 'rxjs';

import { monitoringApproachCorsiaCompleted } from '@aviation/request-task/emp/shared/util/emp.util';
import { EmpCorsiaStoreDelegate } from '@aviation/request-task/store/delegates/emp-corsia';
import { extractMonitorinApproachType } from '@aviation/shared/components/emp/emission-sources/isFUMM';

import { EmissionsMonitoringPlanCorsia } from 'pmrv-api';

import { EmpRequestTaskPayloadCorsia, RequestTaskStore } from '../../../../store';
import { TASK_FORM_PROVIDER } from '../../../../task-form.provider';
import { empQuery } from '../../../shared/emp.selectors';
import { DataGapsFormModel } from './data-gaps-form.model';

export const canActivateDataGaps: CanActivateFn = (route: ActivatedRouteSnapshot, state: RouterStateSnapshot) => {
  const store = inject(RequestTaskStore);
  const form = inject<DataGapsFormModel>(TASK_FORM_PROVIDER);
  const router = inject(Router);
  const payload = store.getState().requestTaskItem.requestTask.payload as EmpRequestTaskPayloadCorsia;
  const monitoringApproachCompleted = monitoringApproachCorsiaCompleted(
    (store.empDelegate as EmpCorsiaStoreDelegate).payload,
  );
  if (!monitoringApproachCompleted) return false;
  return store.pipe(
    empQuery.selectEmissionsMonitoringPlanCorsia,
    take(1),
    tap((emp) => {
      if (!emp) {
        store.setPayload({
          ...payload,
          emissionsMonitoringPlan: {
            dataGaps: payload.emissionsMonitoringPlan.dataGaps,
          },
        } as any);
      }

      form.patchValue({ ...payload.emissionsMonitoringPlan.dataGaps });
      const dataGaps = payload.emissionsMonitoringPlan.dataGaps;
      if (dataGaps && !dataGaps.secondarySourcesDataGapsExist)
        form.controls.secondarySourcesDataGapsConditions.disable();
    }),
    map(() => {
      // Requirements depict that we should show the form steps in different order depending on monitoringApproachType
      const navigateTo = '/secondary-data-sources';
      if (form.valid) return true;
      const shouldNavigate =
        monitoringApproachCompleted &&
        monitorinApproachIsCert(payload.emissionsMonitoringPlan) &&
        !state.url.includes('summary') &&
        !state.url.includes(navigateTo);
      if (shouldNavigate) {
        router.navigateByUrl(state.url + navigateTo);
        return false;
      }
      return true;
    }),
  );
};

export const canDeactivateDataGaps: CanDeactivateFn<unknown> = () => {
  const form = inject<DataGapsFormModel>(TASK_FORM_PROVIDER);
  form.reset();
  return true;
};
function monitorinApproachIsCert(monitoringPlan: EmissionsMonitoringPlanCorsia): boolean {
  return extractMonitorinApproachType(monitoringPlan) === 'CERT_MONITORING';
}
