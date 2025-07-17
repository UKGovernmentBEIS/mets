import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

import { catchError, concatMap, of } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { skipReviewMap } from './skip-review.map';

export const canSkipReview: CanActivateFn = (route) => {
  const tasksService = inject(TasksService);
  const router = inject(Router);

  const id = +route.paramMap.get('taskId');
  if (!route.paramMap.has('taskId') || Number.isNaN(id)) {
    return router.createUrlTree(['dashboard']);
  }

  return tasksService.getTaskItemInfoById(id).pipe(
    concatMap((requestTaskItem) => {
      if (requestTaskItem.allowedRequestTaskActions.includes(skipReviewMap[requestTaskItem.requestTask.type]))
        return of(true);
      else return of(router.createUrlTree(['dashboard']));
    }),
    catchError(() => {
      return of(router.createUrlTree(['dashboard']));
    }),
  );
};
