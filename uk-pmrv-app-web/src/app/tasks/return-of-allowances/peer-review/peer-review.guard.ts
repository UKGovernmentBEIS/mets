import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { ReturnOfAllowancesApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { CommonTasksStore } from '../../store/common-tasks.store';

@Injectable({
  providedIn: 'root',
})
export class PeerReviewGuard implements CanActivate {
  constructor(private readonly store: CommonTasksStore, private readonly router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.store.pipe(
      map((state) => {
        const isSectionCompleted = (
          state.requestTaskItem.requestTask.payload as ReturnOfAllowancesApplicationSubmitRequestTaskPayload
        )?.sectionsCompleted['PROVIDE_DETAILS'];

        return (
          isSectionCompleted ||
          this.router.parseUrl(`/tasks/${route.paramMap.get('taskId')}/return-of-allowances/submit`)
        );
      }),
    );
  }
}
