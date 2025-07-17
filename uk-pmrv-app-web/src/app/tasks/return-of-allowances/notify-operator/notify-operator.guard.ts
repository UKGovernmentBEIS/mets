import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { ReturnOfAllowancesApplicationSubmitRequestTaskPayload } from 'pmrv-api';

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
              'RETURN_OF_ALLOWANCES_NOTIFY_OPERATOR_FOR_DECISION',
            ) &&
            (state?.requestTaskItem?.requestTask?.payload as ReturnOfAllowancesApplicationSubmitRequestTaskPayload)
              ?.sectionsCompleted['PROVIDE_DETAILS']
          ) &&
            this.router.parseUrl(`${routerState.url.slice(0, routerState.url.lastIndexOf('/'))}`)) ||
          true
        );
      }),
    );
  }
}
