import { inject } from '@angular/core';
import { CanActivateFn, CanDeactivateFn } from '@angular/router';

import { combineLatest, map, take } from 'rxjs';

import { AerCorsiaStoreDelegate } from '@aviation/request-task/store/delegates/aer-corsia/aer-corsia-store-delegate';

import { RequestTaskStore } from '../../../store';
import { AerUkEtsStoreDelegate } from '../../../store/delegates';
import { TASK_FORM_PROVIDER } from '../../../task-form.provider';
import { aerQuery } from '../aer.selectors';
import { AircraftTypesDataFormProvider } from './aircraft-types-data-form.provider';

export const canActivateAircraftTypesData: CanActivateFn = () => {
  const store = inject(RequestTaskStore);
  const formProvider = inject<AircraftTypesDataFormProvider>(TASK_FORM_PROVIDER);
  const payload = store.getState().requestTaskItem.requestTask.payload;

  return combineLatest([store.pipe(aerQuery.selectAer, take(1)), store.pipe(aerQuery.selectIsCorsia, take(1))]).pipe(
    map(([aer, isCorsia]) => {
      const initialState = isCorsia
        ? AerCorsiaStoreDelegate.INITIAL_STATE.aviationAerAircraftData
        : AerUkEtsStoreDelegate.INITIAL_STATE.aviationAerAircraftData;

      if (!aer) {
        store.setPayload({
          ...payload,
          aer: {
            aviationAer: initialState,
          },
        } as any);
      }

      if (!aer?.aviationAerAircraftData) {
        store.aerDelegate.setAviationAerAircraftData(initialState);
      }

      formProvider.setFormValue(aer.aviationAerAircraftData);
    }),
    map(() => true),
  );
};

export const canDeactivateAircraftTypesData: CanDeactivateFn<any> = () => {
  inject<AircraftTypesDataFormProvider>(TASK_FORM_PROVIDER).destroyForm();
  return true;
};
