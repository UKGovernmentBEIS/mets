import { inject } from '@angular/core';
import { CanActivateFn, CanDeactivateFn } from '@angular/router';

import { map, take, tap } from 'rxjs';

import { EmpRequestTaskPayloadUkEts, RequestTaskStore } from '../../../../store';
import { TASK_FORM_PROVIDER } from '../../../../task-form.provider';
import { empQuery } from '../../../shared/emp.selectors';
import { DataGapsFormModel } from './data-gaps-form.model';

export const canActivateDataGaps: CanActivateFn = () => {
  const store = inject(RequestTaskStore);
  const form = inject<DataGapsFormModel>(TASK_FORM_PROVIDER);
  const payload = store.getState().requestTaskItem.requestTask.payload as EmpRequestTaskPayloadUkEts;

  return store.pipe(
    empQuery.selectEmissionsMonitoringPlan,
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
    }),
    map(() => true),
  );
};

export const canDeactivateDataGaps: CanDeactivateFn<unknown> = () => {
  const form = inject<DataGapsFormModel>(TASK_FORM_PROVIDER);
  form.reset();
  return true;
};
