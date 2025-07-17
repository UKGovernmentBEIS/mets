import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { isEverySectionAccepted } from '@tasks/aer/core/aer-task-statuses';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { AerApplicationReviewRequestTaskPayload } from 'pmrv-api';

@Injectable({
  providedIn: 'root',
})
export class CompleteReviewGuard {
  constructor(
    private readonly store: CommonTasksStore,
    private readonly router: Router,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.store.pipe(
      map(
        (state) =>
          (state.requestTaskItem.allowedRequestTaskActions.includes('AER_COMPLETE_REVIEW') &&
            isEverySectionAccepted(
              state.requestTaskItem.requestTask.payload as AerApplicationReviewRequestTaskPayload,
            )) ||
          this.router.parseUrl(`/tasks/${route.paramMap.get('taskId')}/aer/review`),
      ),
    );
  }
}
