import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';
import { Router } from '@angular/router';

import { BehaviorSubject } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchTaskReassignedBadRequest } from '@error/business-errors';
import { catchNotFoundRequest, ErrorCode } from '@error/not-found-error';
import { BreadcrumbService } from '@shared/breadcrumbs/breadcrumb.service';
import { requestTaskReassignedError, taskNotFoundError } from '@shared/errors/request-task-error';
import {
  AER_SKIP_REVIEW_FORM,
  aerSkipReviewFormProvider,
} from '@tasks/aer/review/skip-review/skip-review-form.provider';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { AerSkipReviewDecision } from 'pmrv-api';

@Component({
  selector: 'app-skip-review',
  templateUrl: './skip-review.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [aerSkipReviewFormProvider],
})
export class SkipReviewComponent {
  requestTaskType$ = this.store.requestTaskType$;
  protected requestInfo$ = this.store.requestInfo$;
  isSkipSubmitted$ = new BehaviorSubject<boolean>(false);

  constructor(
    @Inject(AER_SKIP_REVIEW_FORM) readonly form: UntypedFormGroup,
    private readonly pendingRequest: PendingRequestService,
    private readonly businessErrorService: BusinessErrorService,
    private readonly router: Router,
    private readonly store: CommonTasksStore,
    private readonly breadcrumbs: BreadcrumbService,
  ) {}

  confirm(): void {
    if (this.form.valid) {
      const payloadValue: AerSkipReviewDecision = this.form.value;
      this.store
        .skipReview(payloadValue)
        ?.pipe(
          this.pendingRequest.trackRequest(),
          catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
            this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
          ),
          catchTaskReassignedBadRequest(() =>
            this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
          ),
        )
        .subscribe(() => {
          this.isSkipSubmitted$.next(true);
          this.breadcrumbs.showDashboardBreadcrumb(this.router.url);
        });
    }
  }
}
