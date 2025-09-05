import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { isDeterminationPopulated, resolveRegulatorSectionStatus } from '@tasks/alr/utils';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import {
  ALRApplicationRegulatorReviewSubmitRequestTaskPayload,
  ALRClosedDetermination,
  DoalProceedToAuthorityDetermination,
} from 'pmrv-api';

@Injectable({ providedIn: 'root' })
export class AlrDeterminationGuard {
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
          const determinationProceeAuthoritySummaryBaseUrl = `${taskBaseUrl}/determination/proceed-authority/summary`;
          const determinationCloseSummaryBaseUrl = `${taskBaseUrl}/determination/close/summary`;
          const determinationStatus = resolveRegulatorSectionStatus(payload, 'DETERMINATION');

          return (
            (determinationStatus === 'cannot start yet' && this.router.parseUrl(taskBaseUrl)) ||
            ((payload?.regulatorReviewSectionsCompleted['DETERMINATION'] ||
              isDeterminationPopulated(
                payload.regulatorReviewOutcome?.determination as
                  | DoalProceedToAuthorityDetermination
                  | ALRClosedDetermination,
              )) &&
              this.router.parseUrl(
                payload.regulatorReviewOutcome?.determination?.type === 'PROCEED_TO_AUTHORITY'
                  ? determinationProceeAuthoritySummaryBaseUrl
                  : determinationCloseSummaryBaseUrl,
              )) ||
            true
          );
        }),
      )
    );
  }
}
