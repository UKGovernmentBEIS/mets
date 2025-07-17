import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { NonComplianceFinalDeterminationRequestTaskPayload } from 'pmrv-api';

@Injectable({ providedIn: 'root' })
export class SummaryGuard {
  constructor(
    private readonly store: CommonTasksStore,
    private readonly router: Router,
  ) {}

  canActivate(
    _route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot,
  ): boolean | UrlTree | Observable<boolean | UrlTree> {
    return this.store.pipe(
      map((storeState) => {
        const payload = storeState.requestTaskItem.requestTask
          .payload as NonComplianceFinalDeterminationRequestTaskPayload;
        const firstStep = `${state.url.slice(0, state.url.lastIndexOf('/'))}/provide-conclusion`;
        return (
          !storeState.isEditable ||
          payload?.determinationCompleted ||
          (payload?.complianceRestored !== null && payload?.complianceRestored !== undefined) ||
          this.router.parseUrl(firstStep)
        );
      }),
    );
  }
}
