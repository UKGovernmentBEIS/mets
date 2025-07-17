import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { InspectionSubmitRequestTaskPayload } from '../shared/inspection';
import { canSendPeerReview } from './submit-actions';

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
        const type = route.paramMap.get('type');
        const payload = state.requestTaskItem.requestTask.payload as InspectionSubmitRequestTaskPayload;

        return (
          canSendPeerReview(payload, state.requestTaskItem.allowedRequestTaskActions) ||
          this.router.parseUrl(`/tasks/${taskId}/inspection/${type}/submit`)
        );
      }),
    );
  }
}
