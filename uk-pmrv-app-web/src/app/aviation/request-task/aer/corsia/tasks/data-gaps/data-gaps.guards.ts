import { inject } from '@angular/core';
import { CanActivateFn, createUrlTreeFromSnapshot } from '@angular/router';

import { map, take, tap } from 'rxjs';

import { aerQuery } from '@aviation/request-task/aer/shared/aer.selectors';
import { RequestTaskStore } from '@aviation/request-task/store';
import { AerStoreDelegate } from '@aviation/request-task/store/delegates';
import { TASK_FORM_PROVIDER } from '@aviation/request-task/task-form.provider';

import { DataGapsFormProvider } from './data-gaps-form.provider';

export const canActivateAviationAerCorsiaDataGaps: CanActivateFn = () => {
  const store = inject(RequestTaskStore);
  const formProvider = inject<DataGapsFormProvider>(TASK_FORM_PROVIDER);
  const payload = store.getState().requestTaskItem.requestTask.payload;

  return store.pipe(
    aerQuery.selectAerCorsia,
    take(1),
    tap((aer) => {
      if (!aer) {
        store.setPayload({
          ...payload,
          aer: {
            dataGaps: AerStoreDelegate.INITIAL_STATE.dataGaps,
          },
        } as any);
      }

      if (!aer?.dataGaps) {
        store.aerDelegate.setDataGaps(AerStoreDelegate.INITIAL_STATE.dataGaps);
      }

      formProvider.setFormValue(aer.dataGaps);
    }),
    map(() => true),
  );
};

export const canActivateDataGapsList: CanActivateFn = (route) => {
  const formProvider = inject<DataGapsFormProvider>(TASK_FORM_PROVIDER);
  const addUrl = createUrlTreeFromSnapshot(route, ['add-data-gap-information']);

  return formProvider.getFormValue()?.dataGapsDetails?.dataGaps?.length > 0 || addUrl;
};

export const canActivateDataGapEdit: CanActivateFn = (route) => {
  const index = +route.paramMap.get('index');
  const dataGaps = inject<DataGapsFormProvider>(TASK_FORM_PROVIDER).getFormValue()?.dataGapsDetails?.dataGaps;

  if (dataGaps) {
    return !!dataGaps[index] || createUrlTreeFromSnapshot(route, ['../..']);
  }
};

export const canActivateDataGapAdd: CanActivateFn = (route) => {
  const formProvider = inject<DataGapsFormProvider>(TASK_FORM_PROVIDER);

  return formProvider.existCtrl.value || createUrlTreeFromSnapshot(route, ['../..']);
};

export const canDeactivateAviationAerDataGaps: CanActivateFn = () => {
  inject<DataGapsFormProvider>(TASK_FORM_PROVIDER).destroyForm();

  return true;
};
