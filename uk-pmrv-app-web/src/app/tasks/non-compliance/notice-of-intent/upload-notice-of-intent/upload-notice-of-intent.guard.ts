import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { NonComplianceNoticeOfIntentRequestTaskPayload } from 'pmrv-api';

import { CommonTasksStore } from '../../../store/common-tasks.store';

@Injectable({ providedIn: 'root' })
export class UploadNoticeOfIntentGuard {
  constructor(
    private readonly store: CommonTasksStore,
    private readonly router: Router,
  ) {}

  canActivate(
    _route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot,
  ): boolean | UrlTree | Observable<boolean | UrlTree> {
    return (
      this.router.getCurrentNavigation().extras?.state?.changing ||
      this.store.pipe(
        map((storeState) => {
          const payload = storeState.requestTaskItem.requestTask
            .payload as NonComplianceNoticeOfIntentRequestTaskPayload;
          const summary = `${state.url.slice(0, state.url.lastIndexOf('/'))}/summary`;

          return (
            ((payload?.noticeOfIntentCompleted || payload?.noticeOfIntent) && this.router.parseUrl(summary)) || true
          );
        }),
      )
    );
  }
}
