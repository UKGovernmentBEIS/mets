import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { notInNeedsForReview } from '@permit-revocation/core/section-status';
import { PermitRevocationStore } from '@permit-revocation/store/permit-revocation-store';

import { RequestTaskActionProcessDTO } from 'pmrv-api';

@Injectable({
  providedIn: 'root',
})
export class PeerReviewGuard {
  constructor(
    private readonly store: PermitRevocationStore,
    private readonly router: Router,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    const requestTaskActionType: RequestTaskActionProcessDTO['requestTaskActionType'] =
      route.data?.requestTaskActionType;

    return this.store.pipe(
      map(
        (state) =>
          (!notInNeedsForReview(state) &&
            this.router.parseUrl(`/permit-revocation/${route.paramMap.get('taskId')}/invalid-data`)) ||
          ((!state.allowedRequestTaskActions.includes(requestTaskActionType) ||
            !state?.sectionsCompleted.REVOCATION_APPLY) &&
            this.router.parseUrl(`/permit-revocation/${route.paramMap.get('taskId')}`)) ||
          true,
      ),
    );
  }
}
