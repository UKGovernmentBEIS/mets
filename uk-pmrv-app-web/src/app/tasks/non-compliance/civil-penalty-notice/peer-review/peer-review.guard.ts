import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { NonComplianceCivilPenaltyRequestTaskPayload } from 'pmrv-api';

import { CommonTasksStore } from '../../../store/common-tasks.store';

@Injectable({
  providedIn: 'root',
})
export class PeerReviewGuard implements CanActivate {
  constructor(private readonly store: CommonTasksStore, private readonly router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.store.pipe(
      map((state) => {
        const isSectionCompleted = (
          state.requestTaskItem.requestTask.payload as NonComplianceCivilPenaltyRequestTaskPayload
        )?.civilPenaltyCompleted;

        return (
          isSectionCompleted ||
          this.router.parseUrl(`/tasks/${route.paramMap.get('taskId')}/non-compliance/civil-penalty-notice`)
        );
      }),
    );
  }
}
