import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, first, map, switchMap, withLatestFrom } from 'rxjs';

import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { recallFromAmendsSubmitTaskActionTypes } from '@aviation/request-task/util';
import { PendingRequestService } from '@core/guards/pending-request.service';
import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchTaskReassignedBadRequest } from '@error/business-errors';
import { catchNotFoundRequest, ErrorCode } from '@error/not-found-error';
import { BreadcrumbService } from '@shared/breadcrumbs/breadcrumb.service';
import { requestTaskReassignedError, taskNotFoundError } from '@shared/errors/request-task-error';
import { SharedModule } from '@shared/shared.module';

import { RequestTaskActionPayload, TasksService } from 'pmrv-api';

@Component({
  selector: 'app-aviation-recall-from-amends',
  standalone: true,
  imports: [SharedModule],
  templateUrl: './recall-from-amends.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AviationRecallFromAmendsComponent {
  private readonly taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));
  isRecallSubmitted$ = new BehaviorSubject<boolean>(false);

  constructor(
    private readonly store: RequestTaskStore,
    readonly pendingRequest: PendingRequestService,
    private readonly route: ActivatedRoute,
    private readonly tasksService: TasksService,
    private readonly businessErrorService: BusinessErrorService,
    private readonly breadcrumbs: BreadcrumbService,
    private readonly router: Router,
  ) {}

  recall(): void {
    this.taskId$
      .pipe(
        first(),
        withLatestFrom(this.store.pipe(requestTaskQuery.selectRequestTaskType)),
        switchMap(([taskId, requestTaskType]) =>
          this.tasksService.processRequestTaskAction({
            requestTaskActionType: recallFromAmendsSubmitTaskActionTypes[requestTaskType],
            requestTaskId: taskId,
            requestTaskActionPayload: {
              payloadType: 'EMPTY_PAYLOAD',
            } as RequestTaskActionPayload,
          }),
        ),
        this.pendingRequest.trackRequest(),
        catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
          this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
        ),
        catchTaskReassignedBadRequest(() =>
          this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
        ),
      )
      .subscribe(() => {
        this.isRecallSubmitted$.next(true);
        this.breadcrumbs.showDashboardBreadcrumb(this.router.url);
      });
  }
}
