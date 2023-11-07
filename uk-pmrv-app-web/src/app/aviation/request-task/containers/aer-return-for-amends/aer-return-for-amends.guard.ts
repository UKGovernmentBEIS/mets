import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, createUrlTreeFromSnapshot } from '@angular/router';

import { map, take } from 'rxjs';

import { aerQuery } from '@aviation/request-task/aer/shared/aer.selectors';
import { RequestTaskStore } from '@aviation/request-task/store';
import { returnForAmendsRequestTaskActionTypes } from '@aviation/request-task/util';

import { AerDataReviewDecision } from 'pmrv-api';

export const canActivateAerReturnForAmends: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const store = inject(RequestTaskStore);
  const allowedRequestTaskActions = store.getState().requestTaskItem.allowedRequestTaskActions;
  return store.pipe(
    aerQuery.selectPayload,
    take(1),
    map((payload) => {
      return (
        (allowedRequestTaskActions.some((action) => returnForAmendsRequestTaskActionTypes.includes(action)) &&
          Object.keys(payload?.reviewGroupDecisions ?? []).some(
            (key) =>
              (payload.reviewGroupDecisions[key] as AerDataReviewDecision).type === 'OPERATOR_AMENDS_NEEDED' &&
              payload.reviewSectionsCompleted[key],
          )) ||
        createUrlTreeFromSnapshot(route, ['../../../'])
      );
    }),
  );
};
