import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchTaskReassignedBadRequest } from '@error/business-errors';
import { catchNotFoundRequest, ErrorCode } from '@error/not-found-error';
import { requestTaskReassignedError, taskNotFoundError } from '@shared/errors/request-task-error';

import { RequestTaskItemDTO } from 'pmrv-api';

import { requestTaskQuery, RequestTaskStore } from '../../store';

interface ViewModel {
  info: RequestTaskItemDTO;
}

@Component({
  selector: 'app-skip-review-aviation',
  template: `
    <app-page-heading size="xl">Are you sure you want to skip the review and complete the report?</app-page-heading>
    <p class="govuk-body">
      By selecting ‘Confirm and complete’ you confirm that the information in the report is correct to the best of your
      knowledge.
    </p>
    <div class="govuk-button-group">
      <button type="button" appPendingButton govukButton (click)="confirm()">Confirm and complete</button>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SkipReviewComponent {
  taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));

  vm$: Observable<ViewModel> = combineLatest([this.store.pipe(requestTaskQuery.selectRequestTaskItem)]).pipe(
    map(([info]) => ({ info })),
  );

  constructor(
    private readonly pendingRequest: PendingRequestService,
    private readonly businessErrorService: BusinessErrorService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly store: RequestTaskStore,
  ) {}

  confirm(): void {
    this.store
      .skipReview()
      .pipe(
        this.pendingRequest.trackRequest(),
        catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
          this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
        ),
        catchTaskReassignedBadRequest(() =>
          this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
        ),
      )
      .subscribe(() => this.router.navigate(['confirmation'], { relativeTo: this.route }));
  }
}
