import { inject } from '@angular/core';
import { CanActivateFn, CanDeactivateFn } from '@angular/router';

import { map, take, tap } from 'rxjs';

import { RequestTaskStore } from '../../../store';
import { EmpUkEtsStoreDelegate } from '../../../store/delegates';
import { TASK_FORM_PROVIDER } from '../../../task-form.provider';
import { empQuery } from '../../shared/emp.selectors';
import { AbbreviationsFormProvider } from './abbreviations-form.provider';

export const canActivateAbbreviations: CanActivateFn = () => {
  const store = inject(RequestTaskStore);
  const formProvider = inject<AbbreviationsFormProvider>(TASK_FORM_PROVIDER);
  const payload = store.getState().requestTaskItem.requestTask.payload;

  return store.pipe(
    empQuery.selectEmissionsMonitoringPlan,
    take(1),
    tap((emp) => {
      if (!emp) {
        store.setPayload({
          ...payload,
          emissionsMonitoringPlan: {
            abbreviations: EmpUkEtsStoreDelegate.INITIAL_STATE.abbreviations, //TODO consider corsia as well
          },
        } as any);
      }

      if (!emp?.abbreviations) {
        store.empDelegate.setAbbreviations(EmpUkEtsStoreDelegate.INITIAL_STATE.abbreviations);
      }

      formProvider.setFormValue(emp.abbreviations);
    }),
    map(() => true),
  );
};

export const canDeactivateAbbreviations: CanDeactivateFn<any> = () => {
  inject<AbbreviationsFormProvider>(TASK_FORM_PROVIDER).destroyForm();
  return true;
};
