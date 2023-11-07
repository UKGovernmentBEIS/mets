import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router } from '@angular/router';

import { map } from 'rxjs';

import { notInNeedsForReview } from '@permit-revocation/core/section-status';
import { PermitRevocationState } from '@permit-revocation/store/permit-revocation.state';
import { PermitRevocationStore } from '@permit-revocation/store/permit-revocation-store';

import { RequestTaskActionProcessDTO } from 'pmrv-api';

@Injectable({
  providedIn: 'root',
})
export class NotifyOperatorGuard implements CanActivate {
  constructor(private readonly store: PermitRevocationStore, private readonly router: Router) {}

  canActivate(route: ActivatedRouteSnapshot) {
    const requestTaskActionType: RequestTaskActionProcessDTO['requestTaskActionType'] =
      route.data?.requestTaskActionType;

    return this.store.pipe(
      map(
        (state) =>
          (!notInNeedsForReview(state) &&
            this.router.parseUrl(`/permit-revocation/${route.paramMap.get('taskId')}/invalid-data`)) ||
          ((!state.allowedRequestTaskActions.includes(requestTaskActionType) ||
            !this.sectionStatus(state, requestTaskActionType)) &&
            this.router.parseUrl(this.returnUrl(requestTaskActionType, route.paramMap.get('taskId')))) ||
          true,
      ),
    );
  }

  sectionStatus(
    state: PermitRevocationState,
    requestTaskActionType: RequestTaskActionProcessDTO['requestTaskActionType'],
  ): boolean {
    switch (requestTaskActionType) {
      case 'PERMIT_REVOCATION_NOTIFY_OPERATOR_FOR_SUBMISSION':
        return state?.sectionsCompleted.REVOCATION_APPLY;
      case 'PERMIT_REVOCATION_NOTIFY_OPERATOR_FOR_WITHDRAWAL':
        return !!state?.reason;
      case 'PERMIT_REVOCATION_NOTIFY_OPERATOR_FOR_CESSATION':
        return state.cessationCompleted;
    }
  }

  returnUrl(requestTaskActionType: RequestTaskActionProcessDTO['requestTaskActionType'], taskId: string): string {
    switch (requestTaskActionType) {
      case 'PERMIT_REVOCATION_NOTIFY_OPERATOR_FOR_SUBMISSION':
        return `/permit-revocation/${taskId}`;
      case 'PERMIT_REVOCATION_NOTIFY_OPERATOR_FOR_WITHDRAWAL':
        return `/permit-revocation/${taskId}/wait-for-appeal`;
    }
  }
}
