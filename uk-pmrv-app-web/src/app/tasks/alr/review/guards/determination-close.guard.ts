import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { isDeterminationPopulated } from '@tasks/alr/utils';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import {
  ALRApplicationRegulatorReviewSubmitRequestTaskPayload,
  ALRClosedDetermination,
  DoalProceedToAuthorityDetermination,
} from 'pmrv-api';

@Injectable({ providedIn: 'root' })
export class AlrDeterminationCloseGuard {
  constructor(
    private readonly store: CommonTasksStore,
    private readonly router: Router,
  ) {}

  canActivate(_route: ActivatedRouteSnapshot): boolean | UrlTree | Observable<boolean | UrlTree> {
    return (
      this.router.getCurrentNavigation().extras?.state?.changing ||
      this.store.pipe(
        map((storeState) => {
          const payload = storeState.requestTaskItem.requestTask
            .payload as ALRApplicationRegulatorReviewSubmitRequestTaskPayload;
          const taskBaseUrl = `/tasks/${_route.paramMap.get('taskId')}/alr/review`;
          const determinationBaseUrl = `${taskBaseUrl}/determination`;
          const closeSummaryUrl = `${taskBaseUrl}/determination/close/summary`;

          const type = payload.regulatorReviewOutcome?.determination?.type;

          return (
            (type !== 'CLOSED_ALR' && this.router.parseUrl(determinationBaseUrl)) ||
            (isDeterminationPopulated(
              payload.regulatorReviewOutcome?.determination as
                | DoalProceedToAuthorityDetermination
                | ALRClosedDetermination,
            ) &&
              this.router.parseUrl(closeSummaryUrl)) ||
            true
          );
        }),
      )
    );
  }
}
