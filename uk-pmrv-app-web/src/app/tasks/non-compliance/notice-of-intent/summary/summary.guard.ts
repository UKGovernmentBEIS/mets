import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { NonComplianceNoticeOfIntentRequestTaskPayload } from 'pmrv-api';

@Injectable({ providedIn: 'root' })
export class SummaryGuard implements CanActivate {
  constructor(private readonly store: CommonTasksStore, private readonly router: Router) {}

  canActivate(
    _route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot,
  ): boolean | UrlTree | Observable<boolean | UrlTree> {
    return this.store.pipe(
      map((storeState) => {
        const payload = storeState.requestTaskItem.requestTask.payload as NonComplianceNoticeOfIntentRequestTaskPayload;
        const firstStep = `${state.url.slice(0, state.url.lastIndexOf('/'))}/upload-notice-of-intent`;
        return (
          !storeState.isEditable ||
          payload?.noticeOfIntentCompleted ||
          (payload?.noticeOfIntent !== null && payload?.noticeOfIntent !== undefined) ||
          this.router.parseUrl(firstStep)
        );
      }),
    );
  }
}
