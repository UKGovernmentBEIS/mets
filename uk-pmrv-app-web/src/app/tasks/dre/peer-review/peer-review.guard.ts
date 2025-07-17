import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { DreApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { CommonTasksStore } from '../../../tasks/store/common-tasks.store';
import { isFeeDueDateValid } from '../core/section-status';

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
        const isSectionCompleted = (state.requestTaskItem.requestTask.payload as DreApplicationSubmitRequestTaskPayload)
          ?.sectionCompleted;
        const payload = state.requestTaskItem.requestTask.payload as DreApplicationSubmitRequestTaskPayload;
        return (
          (!isFeeDueDateValid(payload) &&
            this.router.parseUrl(`/tasks/${route.paramMap.get('taskId')}/dre/submit/invalid-data`)) ||
          isSectionCompleted ||
          this.router.parseUrl(`/tasks/${route.paramMap.get('taskId')}/dre/submit`)
        );
      }),
    );
  }
}
