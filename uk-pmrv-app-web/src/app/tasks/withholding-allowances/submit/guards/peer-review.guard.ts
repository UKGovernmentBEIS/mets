import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { WithholdingOfAllowancesApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { CommonTasksStore } from '../../../store/common-tasks.store';
import { getSectionStatus } from '../submit';

@Injectable({
  providedIn: 'root',
})
export class PeerReviewGuard {
  constructor(
    private readonly store: CommonTasksStore,
    private readonly router: Router,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.store.pipe(
      map((state) => {
        const payload = state?.requestTaskItem?.requestTask
          .payload as WithholdingOfAllowancesApplicationSubmitRequestTaskPayload;

        return (
          (state?.requestTaskItem?.allowedRequestTaskActions.includes(
            'WITHHOLDING_OF_ALLOWANCES_REQUEST_PEER_REVIEW',
          ) &&
            getSectionStatus(payload) === 'complete') ||
          this.router.parseUrl(`/tasks/${route.paramMap.get('taskId')}/withholding-allowances/submit`)
        );
      }),
    );
  }
}
