import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { PermitNotificationFollowUpApplicationReviewRequestTaskPayload } from 'pmrv-api';

import { CommonTasksStore } from '../../../../store/common-tasks.store';
import { resolveFollowUpReviewDecisionStatus } from '../../../core/section-status';

@Injectable({
  providedIn: 'root',
})
export class FollowUpReviewNotifyOperatorGuard {
  constructor(
    private readonly store: CommonTasksStore,
    private readonly router: Router,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.store.pipe(
      map((state) => {
        const decisionStatus = resolveFollowUpReviewDecisionStatus(
          state.requestTaskItem.requestTask.payload as PermitNotificationFollowUpApplicationReviewRequestTaskPayload,
        );

        return (
          (state.requestTaskItem.allowedRequestTaskActions.includes(
            'PERMIT_NOTIFICATION_FOLLOW_UP_NOTIFY_OPERATOR_FOR_DECISION',
          ) &&
            decisionStatus &&
            decisionStatus === 'accepted') ||
          this.router.parseUrl(
            `/tasks/${route.paramMap.get('taskId')}/permit-notification/follow-up/review/invalid-data`,
          )
        );
      }),
    );
  }
}
