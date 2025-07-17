import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { NonComplianceCivilPenaltyRequestTaskPayload } from 'pmrv-api';

@Injectable({ providedIn: 'root' })
export class NotifyOperatorGuard {
  constructor(
    private readonly store: CommonTasksStore,
    private readonly router: Router,
  ) {}

  canActivate(_route: ActivatedRouteSnapshot, routerState: RouterStateSnapshot): Observable<boolean | UrlTree> {
    return this.store.pipe(
      map((state) => {
        return (
          (!(
            state?.requestTaskItem?.allowedRequestTaskActions.includes(
              'NON_COMPLIANCE_CIVIL_PENALTY_NOTIFY_OPERATOR',
            ) &&
            (state?.requestTaskItem?.requestTask?.payload as NonComplianceCivilPenaltyRequestTaskPayload)?.civilPenalty
          ) &&
            this.router.parseUrl(`${routerState.url.slice(0, routerState.url.lastIndexOf('/'))}`)) ||
          true
        );
      }),
    );
  }
}
