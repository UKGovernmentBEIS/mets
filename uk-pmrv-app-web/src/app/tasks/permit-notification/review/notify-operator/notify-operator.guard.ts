import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { PermitNotificationApplicationReviewRequestTaskPayload } from 'pmrv-api';

import { CommonTasksStore } from '../../../store/common-tasks.store';
import { isReviewDecisionTakenValid } from '../../core/section-status';

@Injectable({
  providedIn: 'root',
})
export class NotifyOperatorGuard {
  constructor(
    private readonly store: CommonTasksStore,
    private readonly router: Router,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.store.pipe(
      map((state) => {
        const isDecisionTakenValid = isReviewDecisionTakenValid(
          (state.requestTaskItem.requestTask.payload as PermitNotificationApplicationReviewRequestTaskPayload)
            ?.reviewDecision,
        );

        return (
          (state.requestTaskItem.allowedRequestTaskActions.includes(
            'PERMIT_NOTIFICATION_NOTIFY_OPERATOR_FOR_DECISION',
          ) &&
            isDecisionTakenValid) ||
          this.router.parseUrl(`/tasks/${route.paramMap.get('taskId')}/permit-notification/review/invalid-data`)
        );
      }),
    );
  }
}
