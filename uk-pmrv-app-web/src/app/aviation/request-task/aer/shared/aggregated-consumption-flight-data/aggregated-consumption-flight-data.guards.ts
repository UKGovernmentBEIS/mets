import { inject } from '@angular/core';
import { CanActivateFn, CanDeactivateFn } from '@angular/router';

import { combineLatest, map, take } from 'rxjs';

import { AerCorsiaStoreDelegate } from '@aviation/request-task/store/delegates/aer-corsia/aer-corsia-store-delegate';

import { RequestTaskStore } from '../../../store';
import { AerUkEtsStoreDelegate } from '../../../store/delegates';
import { TASK_FORM_PROVIDER } from '../../../task-form.provider';
import { aerQuery } from '../../shared/aer.selectors';
import { AggregatedConsumptionFlightDataFormProvider } from './aggregated-consumption-flight-data-form.provider';

export const canActivateAggregatedConsumptionFlightData: CanActivateFn = () => {
  const store = inject(RequestTaskStore);
  const formProvider = inject<AggregatedConsumptionFlightDataFormProvider>(TASK_FORM_PROVIDER);
  const payload = store.getState().requestTaskItem.requestTask.payload;

  return combineLatest([store.pipe(aerQuery.selectAer, take(1)), store.pipe(aerQuery.selectIsCorsia, take(1))]).pipe(
    map(([aer, isCorsia]) => {
      const initialState = isCorsia
        ? AerCorsiaStoreDelegate.INITIAL_STATE.aggregatedEmissionsData
        : AerUkEtsStoreDelegate.INITIAL_STATE.aggregatedEmissionsData;

      if (!aer) {
        store.setPayload({
          ...payload,
          aer: {
            aggregatedEmissionsData: initialState,
          },
        } as any);
      }

      if (!aer?.aggregatedEmissionsData) {
        store.aerDelegate.setAggregatedEmissionsData(initialState as any);
      }

      formProvider.setFormValue(aer.aggregatedEmissionsData);

      return true;
    }),
  );
};

export const canDeactivateAggregatedConsumptionFlightData: CanDeactivateFn<any> = () => {
  inject<AggregatedConsumptionFlightDataFormProvider>(TASK_FORM_PROVIDER).destroyForm();
  return true;
};
