import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { canSendPeerReview } from '@tasks/doal/submit/submit-actions';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { DoalApplicationSubmitRequestTaskPayload } from 'pmrv-api';

@Injectable({
  providedIn: 'root',
})
export class SendPeerReviewGuard {
  constructor(
    private readonly store: CommonTasksStore,
    private readonly router: Router,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.store.pipe(
      map((state) => {
        const taskId = route.paramMap.get('taskId');
        const payload = state.requestTaskItem.requestTask.payload as DoalApplicationSubmitRequestTaskPayload;
        return (
          canSendPeerReview(payload, state.requestTaskItem.allowedRequestTaskActions) ||
          this.router.parseUrl(`/tasks/${taskId}/doal/submit`)
        );
      }),
    );
  }
}
