import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { canNotifyOperator } from '@tasks/doal/authority-response/section-status';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { DoalAuthorityResponseRequestTaskPayload } from 'pmrv-api';

@Injectable({
  providedIn: 'root',
})
export class NotifyOperatorGuard {
  constructor(
    private readonly store: CommonTasksStore,
    private readonly router: Router,
  ) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean | UrlTree> {
    return this.store.pipe(
      map((storeState) => {
        return (
          canNotifyOperator(
            storeState.requestTaskItem.requestTask.payload as DoalAuthorityResponseRequestTaskPayload,
            storeState.requestTaskItem.allowedRequestTaskActions,
          ) || this.router.parseUrl(`${state.url.slice(0, state.url.lastIndexOf('/'))}`)
        );
      }),
    );
  }
}
