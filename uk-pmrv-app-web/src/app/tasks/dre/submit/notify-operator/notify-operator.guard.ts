import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { DreApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { isFeeDueDateValid } from '../../../../tasks/dre/core/section-status';

@Injectable({ providedIn: 'root' })
export class NotifyOperatorGuard implements CanActivate {
  constructor(private readonly store: CommonTasksStore, private readonly router: Router) {}

  canActivate(_route: ActivatedRouteSnapshot, routerState: RouterStateSnapshot): Observable<boolean | UrlTree> {
    return this.store.pipe(
      map((state) => {
        return (
          (!isFeeDueDateValid(state.requestTaskItem.requestTask.payload as DreApplicationSubmitRequestTaskPayload) &&
            this.router.parseUrl(`/tasks/${_route.paramMap.get('taskId')}/dre/submit/invalid-data`)) ||
          (!(
            state?.requestTaskItem?.allowedRequestTaskActions.includes('DRE_SUBMIT_NOTIFY_OPERATOR') &&
            (state?.requestTaskItem?.requestTask?.payload as DreApplicationSubmitRequestTaskPayload)?.sectionCompleted
          ) &&
            this.router.parseUrl(`${routerState.url.slice(0, routerState.url.lastIndexOf('/'))}`)) ||
          true
        );
      }),
    );
  }
}
