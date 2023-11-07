import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { NonComplianceFinalDeterminationRequestTaskPayload } from 'pmrv-api';

import { CommonTasksStore } from '../../../store/common-tasks.store';

@Injectable({ providedIn: 'root' })
export class ProvideConclusionGuard implements CanActivate {
  constructor(private readonly store: CommonTasksStore, private readonly router: Router) {}

  canActivate(
    _route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot,
  ): boolean | UrlTree | Observable<boolean | UrlTree> {
    return (
      this.router.getCurrentNavigation().extras?.state?.changing ||
      this.store.pipe(
        map((storeState) => {
          const payload = storeState.requestTaskItem.requestTask
            .payload as NonComplianceFinalDeterminationRequestTaskPayload;
          const summary = `${state.url.slice(0, state.url.lastIndexOf('/'))}/summary`;

          return (
            ((payload?.determinationCompleted || payload?.complianceRestored !== undefined) &&
              this.router.parseUrl(summary)) ||
            true
          );
        }),
      )
    );
  }
}
