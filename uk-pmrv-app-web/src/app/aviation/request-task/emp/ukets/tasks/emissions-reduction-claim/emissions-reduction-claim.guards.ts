import { inject } from '@angular/core';
import { CanActivateFn, CanDeactivateFn } from '@angular/router';

import { map, take, tap } from 'rxjs';

import { RequestTaskStore } from '../../../../store';
import { EmpUkEtsStoreDelegate } from '../../../../store/delegates';
import { TASK_FORM_PROVIDER } from '../../../../task-form.provider';
import { empQuery } from '../../../shared/emp.selectors';
import { EmissionsReductionClaimFormProvider } from './emissions-reduction-claim-form.provider';

export const canActivateEmissionsReductionClaim: CanActivateFn = () => {
  const store = inject(RequestTaskStore);
  const formProvider = inject<EmissionsReductionClaimFormProvider>(TASK_FORM_PROVIDER);
  const payload = store.getState().requestTaskItem.requestTask.payload;

  return store.pipe(
    empQuery.selectEmissionsMonitoringPlan,
    take(1),
    tap((emp) => {
      if (!emp) {
        store.setPayload({
          ...payload,
          emissionsMonitoringPlan: {
            emissionsReductionClaim: EmpUkEtsStoreDelegate.INITIAL_STATE.emissionsReductionClaim,
          },
        } as any);
      }

      if (!emp?.emissionsReductionClaim) {
        store.empUkEtsDelegate.setEmissionsReductionClaim(EmpUkEtsStoreDelegate.INITIAL_STATE.emissionsReductionClaim);
      }

      formProvider.setFormValue(emp.emissionsReductionClaim);
    }),
    map(() => true),
  );
};

export const canDeactivateEmissionsReductionClaim: CanDeactivateFn<any> = () => {
  inject<EmissionsReductionClaimFormProvider>(TASK_FORM_PROVIDER).destroyForm();
  return true;
};
