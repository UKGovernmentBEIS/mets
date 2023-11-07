import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

import { catchError, concatMap, of } from 'rxjs';

import { AVIATION_REQUEST_TYPES } from '@shared/utils/request.utils';

import { TasksService } from 'pmrv-api';

import { cancelActionMap } from '../containers/cancel/cancel-action.map';

export const canCancelTask: CanActivateFn = (route) => {
  const tasksService = inject(TasksService);
  const router = inject(Router);

  const id = +route.paramMap.get('taskId');
  if (!route.paramMap.has('taskId') || Number.isNaN(id)) {
    return router.createUrlTree(['aviation', 'dashboard']);
  }

  return tasksService.getTaskItemInfoById(id).pipe(
    concatMap((requestTaskItem) => {
      if (!AVIATION_REQUEST_TYPES.includes(requestTaskItem.requestInfo.type)) {
        throw new Error(`Task with id: ${id} is not an aviation related task`);
      }

      if (requestTaskItem.allowedRequestTaskActions.includes(cancelActionMap[requestTaskItem.requestTask.type]))
        return of(true);
      else return of(router.createUrlTree(['aviation', 'dashboard']));
    }),
    catchError(() => {
      return of(router.createUrlTree(['aviation', 'dashboard']));
    }),
  );
};
