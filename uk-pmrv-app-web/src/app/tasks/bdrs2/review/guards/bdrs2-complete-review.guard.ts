import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload } from 'pmrv-api';

import { submitReviewWizardComplete } from '../review.wizard';

@Injectable({
  providedIn: 'root',
})
export class BdrS2CompleteReviewGuard {
  constructor(
    private readonly store: CommonTasksStore,
    private readonly router: Router,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.store.pipe(
      map(
        (state) =>
          (state.requestTaskItem.allowedRequestTaskActions.includes('BDRS2_REGULATOR_REVIEW_SUBMIT') &&
            submitReviewWizardComplete(
              state.requestTaskItem.requestTask.payload as BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload,
            ) &&
            (state.requestTaskItem.requestTask.payload as BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload)
              ?.regulatorReviewSectionsCompleted?.['outcome']) ||
          this.router.parseUrl(`/tasks/${route.paramMap.get('taskId')}/bdrs2/review`),
      ),
    );
  }
}
