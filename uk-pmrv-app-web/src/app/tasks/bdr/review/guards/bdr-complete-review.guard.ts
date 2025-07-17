import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { BDRApplicationRegulatorReviewSubmitRequestTaskPayload } from 'pmrv-api';

import { submitReviewWizardComplete } from '../review.wizard';

@Injectable({
  providedIn: 'root',
})
export class BdrCompleteReviewGuard {
  constructor(
    private readonly store: CommonTasksStore,
    private readonly router: Router,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.store.pipe(
      map(
        (state) =>
          (state.requestTaskItem.allowedRequestTaskActions.includes('BDR_REGULATOR_REVIEW_SUBMIT') &&
            submitReviewWizardComplete(
              state.requestTaskItem.requestTask.payload as BDRApplicationRegulatorReviewSubmitRequestTaskPayload,
            ) &&
            (state.requestTaskItem.requestTask.payload as BDRApplicationRegulatorReviewSubmitRequestTaskPayload)
              ?.regulatorReviewSectionsCompleted?.['outcome']) ||
          this.router.parseUrl(`/tasks/${route.paramMap.get('taskId')}/bdr/review`),
      ),
    );
  }
}
