import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';

import { first, map, Observable } from 'rxjs';

import { PermitTransferStore } from '../store/permit-transfer.store';
import { transferDetailsStatus } from '../transfer-status';

@Injectable({
  providedIn: 'root',
})
export class TransferDetailsGuard implements CanActivate {
  constructor(private readonly router: Router, private readonly store: PermitTransferStore) {}

  canActivate(route: ActivatedRouteSnapshot, routerState: RouterStateSnapshot): Observable<boolean | UrlTree> {
    return (
      this.router.getCurrentNavigation().extras?.state?.changing ||
      this.store.pipe(
        first(),
        map((permitState) => {
          const transferDetailsConfirmationCompleted = transferDetailsStatus(permitState) === 'complete';
          const taskBaseUrl = `permit-transfer/${route.paramMap.get('taskId')}/transfer-details`;
          const taskSummaryUrl = `/${taskBaseUrl}/summary`;
          const isCurrentSummaryPageTask = routerState.url.includes(taskSummaryUrl);
          const isRequestAction = !permitState.isRequestTask;

          return (
            isRequestAction ||
            (!transferDetailsConfirmationCompleted && !isCurrentSummaryPageTask) ||
            (!transferDetailsConfirmationCompleted && isCurrentSummaryPageTask && this.router.parseUrl(taskBaseUrl)) ||
            (transferDetailsConfirmationCompleted &&
              !isCurrentSummaryPageTask &&
              this.router.parseUrl(taskSummaryUrl)) ||
            true
          );
        }),
      )
    );
  }
}
