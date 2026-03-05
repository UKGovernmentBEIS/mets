import { ChangeDetectionStrategy, Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchTaskReassignedBadRequest } from '@error/business-errors';
import { catchNotFoundRequest, ErrorCode } from '@error/not-found-error';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { requestTaskReassignedError, taskNotFoundError } from '@shared/errors/request-task-error';

import { RequestTaskItemDTO } from 'pmrv-api';

import { requestTaskQuery, RequestTaskStore } from '../../store';

interface ViewModel {
  info: RequestTaskItemDTO;
}

@Component({
  selector: 'app-cancel-task-aviation',
  template: `
    <app-page-heading size="xl">Are you sure you want to cancel this task?</app-page-heading>
    <p class="govuk-body">This task and its data will be deleted.</p>
    <div class="govuk-button-group">
      <button type="button" appPendingButton (click)="cancel()" govukWarnButton>Yes, cancel this task</button>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CancelComponent implements OnInit, OnDestroy {
  taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));

  vm$: Observable<ViewModel> = combineLatest([this.store.pipe(requestTaskQuery.selectRequestTaskItem)]).pipe(
    map(([info]) => ({ info })),
  );

  constructor(
    private readonly pendingRequest: PendingRequestService,
    private readonly businessErrorService: BusinessErrorService,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly backLinkService: BackLinkService,
    private readonly store: RequestTaskStore,
  ) {}

  ngOnInit(): void {
    this.backLinkService.show();
  }

  ngOnDestroy(): void {
    this.backLinkService.hide();
  }

  cancel(): void {
    this.store
      .cancelCurrentTask()
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
