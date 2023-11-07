import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, createUrlTreeFromSnapshot } from '@angular/router';

import { map, take } from 'rxjs';

import { RequestTaskStore } from '@aviation/request-task/store';
import { returnForAmendsRequestTaskActionTypes, variationDetailsSentIsForAmends } from '@aviation/request-task/util';

import { empQuery } from '../emp.selectors';

export const canActivateEmpReturnForAmends: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const store = inject(RequestTaskStore);
  const allowedRequestTaskActions = store.getState().requestTaskItem.allowedRequestTaskActions;

  return store.pipe(
    empQuery.selectPayload,
    take(1),
    map((payload) => {
      return (
        (allowedRequestTaskActions.some((action) => returnForAmendsRequestTaskActionTypes.includes(action)) &&
          Object.keys(payload?.reviewGroupDecisions ?? []).some(
            (key) =>
              (payload.reviewGroupDecisions[key].type === 'OPERATOR_AMENDS_NEEDED' &&
                payload.reviewSectionsCompleted[key]) ||
              variationDetailsSentIsForAmends(payload),
          )) ||
        createUrlTreeFromSnapshot(route, ['../../../'])
      );
    }),
  );
};
