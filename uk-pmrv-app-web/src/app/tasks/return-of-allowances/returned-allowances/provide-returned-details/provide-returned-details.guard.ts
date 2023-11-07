import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { ReturnOfAllowancesReturnedApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { CommonTasksStore } from '../../../store/common-tasks.store';

@Injectable({ providedIn: 'root' })
export class ProvideReturnedDetailsGuard implements CanActivate {
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
            .payload as ReturnOfAllowancesReturnedApplicationSubmitRequestTaskPayload;
          const summary = `${state.url.slice(0, state.url.lastIndexOf('/'))}/summary`;

          return (
            ((payload?.sectionsCompleted['PROVIDE_RETURNED_DETAILS'] ||
              (payload?.returnOfAllowancesReturned?.isAllowancesReturned !== null &&
                payload?.returnOfAllowancesReturned?.isAllowancesReturned !== undefined)) &&
              this.router.parseUrl(summary)) ||
            true
          );
        }),
      )
    );
  }
}
