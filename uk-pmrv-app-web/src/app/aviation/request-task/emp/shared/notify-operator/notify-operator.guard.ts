import { inject } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivateFn, createUrlTreeFromSnapshot } from '@angular/router';

import { combineLatest, map, take } from 'rxjs';

import { empQuery } from '@aviation/request-task/emp/shared/emp.selectors';
import { areTasksCompletedForNotifyVariationRegLed } from '@aviation/request-task/emp/shared/util/emp.util';
import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { notifyOperatorRequestTaskActionTypes } from '@aviation/request-task/util';

export const canActivateEmpNotifyOperator: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const store = inject(RequestTaskStore);
  const allowedRequestTaskActions = store.getState().requestTaskItem.allowedRequestTaskActions;

  return store.pipe(
    empQuery.selectPayload,
    take(1),
    map((payload) => {
      return (
        (allowedRequestTaskActions.some((action) => notifyOperatorRequestTaskActionTypes.includes(action)) &&
          payload.reviewSectionsCompleted?.decision) ||
        createUrlTreeFromSnapshot(route, ['../../../'])
      );
    }),
  );
};

export const canActivateEmpVariationRegLedNotifyOperator: CanActivateFn = (route: ActivatedRouteSnapshot) => {
  const store = inject(RequestTaskStore);
  const allowedRequestTaskActions = store.getState().requestTaskItem.allowedRequestTaskActions;

  return combineLatest([
    store.pipe(empQuery.selectPayload),
    store.pipe(requestTaskQuery.selectRequestTaskType),
    store.pipe(empQuery.selectIsCorsia),
  ]).pipe(
    take(1),
    map(
      ([payload, taskType, isCorsia]) =>
        (allowedRequestTaskActions.some((action) => notifyOperatorRequestTaskActionTypes.includes(action)) &&
          areTasksCompletedForNotifyVariationRegLed(taskType, payload, isCorsia)) ||
        createUrlTreeFromSnapshot(route, ['../../../']),
    ),
  );
};
