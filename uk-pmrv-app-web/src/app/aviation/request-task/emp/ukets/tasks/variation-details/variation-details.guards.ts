import { inject } from '@angular/core';
import { CanActivateFn, CanDeactivateFn } from '@angular/router';

import { combineLatest, map, take, tap } from 'rxjs';

import { RequestTaskStore } from '../../../../store';
import { TASK_FORM_PROVIDER } from '../../../../task-form.provider';
import { empQuery } from '../../../shared/emp.selectors';
import { VariationDetailsFormProvider } from './variation-details-form.provider';

export const canActivateVariationDetails: CanActivateFn = () => {
  const store = inject(RequestTaskStore);
  const formProvider = inject<VariationDetailsFormProvider>(TASK_FORM_PROVIDER);
  const payload = store.getState().requestTaskItem.requestTask.payload;

  return combineLatest([
    store.pipe(empQuery.selectVariationDetails),
    store.pipe(empQuery.selectVariationRegulatorLedReason),
  ]).pipe(
    take(1),
    tap(([variationDetails, variationRegulatorLedReason]) => {
      if (!variationDetails) {
        store.setPayload({
          ...payload,
          empVariationDetails: variationDetails ?? null,
        } as any);
      }

      store.empUkEtsDelegate.setVariationDetails(variationDetails ?? null, variationRegulatorLedReason ?? null);
      formProvider.setFormValue({
        ...variationDetails,
        ...variationRegulatorLedReason,
      });
    }),
    map(() => true),
  );
};

export const canDeactivateVariationDetails: CanDeactivateFn<any> = () => {
  inject<VariationDetailsFormProvider>(TASK_FORM_PROVIDER).destroyForm();
  return true;
};
