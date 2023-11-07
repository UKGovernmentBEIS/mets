import { inject } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  CanActivateFn,
  CanDeactivateFn,
  createUrlTreeFromSnapshot,
  Router,
} from '@angular/router';

import { catchError, concatMap, map, of, take, tap, withLatestFrom } from 'rxjs';

import { AuthStore, selectUserId } from '@core/store';
import { IncorporateHeaderStore } from '@shared/incorporate-header/store/incorporate-header.store';
import { AVIATION_REQUEST_TYPES } from '@shared/utils/request.utils';

import { RequestActionsService, RequestItemsService, TasksService } from 'pmrv-api';

import { requestTaskQuery, RequestTaskStore } from '../store';
import { TASK_FORM_PROVIDER } from '../task-form.provider';
import { isNotEditableByRequestTaskType } from '../util';

export const canActivateRequestTask: CanActivateFn = (route) => {
  const tasksService = inject(TasksService);
  const requestActionsService = inject(RequestActionsService);
  const requestItemsService = inject(RequestItemsService);
  const store = inject(RequestTaskStore);
  const authStore = inject(AuthStore);
  const router = inject(Router);
  const incorporateHeaderStore = inject(IncorporateHeaderStore);

  const id = +route.paramMap.get('taskId');
  if (!route.paramMap.has('taskId') || Number.isNaN(id)) {
    return router.createUrlTree(['aviation', 'dashboard']);
  }

  return tasksService.getTaskItemInfoById(id).pipe(
    concatMap((requestTaskItem) => {
      if (!AVIATION_REQUEST_TYPES.includes(requestTaskItem.requestInfo.type)) {
        throw new Error(`Task with id: ${id} is not an aviation related task`);
      }

      return requestActionsService
        .getRequestActionsByRequestId(requestTaskItem.requestInfo.id)
        .pipe(map((timeline) => ({ requestTaskItem, timeline })));
    }),
    concatMap(({ requestTaskItem, timeline }) => {
      return requestItemsService.getItemsByRequest(requestTaskItem.requestInfo.id).pipe(
        map(({ items: relatedTasks }) => {
          store.setRequestTaskItem(requestTaskItem);
          store.setTimeline(timeline);
          store.setRelatedTasks(relatedTasks);
          incorporateHeaderStore.reset();
          incorporateHeaderStore.setState({
            ...incorporateHeaderStore.getState(),
            accountId: requestTaskItem.requestInfo.accountId,
          });
          return { requestTaskItem, timeline, relatedTasks };
        }),
      );
    }),
    withLatestFrom(authStore.pipe(selectUserId)),
    tap(([state]) => {
      store.initStoreDelegateByRequestType(state.requestTaskItem.requestInfo.type);
    }),
    map(([state, userId]) => {
      const isEditable = state.requestTaskItem.requestTask.assigneeUserId === userId;
      store.setIsEditable(
        isEditable && !isNotEditableByRequestTaskType.includes(state.requestTaskItem.requestTask.type),
      );
      return true;
    }),
    catchError(() => {
      return of(router.createUrlTree(['aviation', 'dashboard']));
    }),
  );
};

/**
 * This assumes that all subtask routes have a parent empty route and a `summary` route which is child of that parent
 * <br/>
 * @example
 * export const SUBTASK_ROUTES: Routes = [
 *   {
 *     path: '', // EMPTY PARENT ROUTE NEEDED
 *     canActivate: [...],
 *     canDeactivate: [...],
 *     children: [
 *       {
 *         path: '',
 *         canActivate: [canActivateTaskForm],
 *         loadComponent: () => ...,
 *       },
 *       {
 *         path: 'summary',
 *         loadComponent: () => ...,
 *       },
 *     ],
 *   },
 * ];
 */
export const canActivateTaskForm: CanActivateFn = (route) => {
  const formProvider = inject(TASK_FORM_PROVIDER);
  const formInvalid = 'form' in formProvider ? formProvider.form.invalid : formProvider.invalid;
  const change = route.queryParamMap.get('change') === 'true';

  return inject(RequestTaskStore).pipe(
    requestTaskQuery.selectIsEditable,
    take(1),
    map((isEditable) => {
      return (
        (isEditable && (formInvalid || change)) ||
        createUrlTreeFromSnapshot(getClosestParentEmptyRoute(route), ['summary'])
      );
    }),
  );
};

export const canActivateSummaryPage: CanActivateFn = (route) => {
  const formProvider = inject(TASK_FORM_PROVIDER);
  const formInvalid = 'form' in formProvider ? formProvider.form.invalid : formProvider.invalid;

  return inject(RequestTaskStore).pipe(
    requestTaskQuery.selectIsEditable,
    take(1),
    map((isEditable) => {
      return (isEditable && !formInvalid) || !isEditable || createUrlTreeFromSnapshot(route, ['../']);
    }),
  );
};

export function getClosestParentEmptyRoute(route: ActivatedRouteSnapshot): ActivatedRouteSnapshot {
  let currentRoute = route;
  while (currentRoute.url.length > 0) {
    currentRoute = currentRoute.parent;
  }

  return currentRoute;
}

export const canDeactivateRequestTask: CanDeactivateFn<any> = () => {
  const incorporateHeaderStore = inject(IncorporateHeaderStore);

  incorporateHeaderStore.reset();
  return true;
};
