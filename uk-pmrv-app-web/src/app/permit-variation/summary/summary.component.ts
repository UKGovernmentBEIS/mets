import { Location } from '@angular/common';
import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { combineLatest, first, map, Observable, Subject, switchMap, withLatestFrom } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';
import { requestTaskReassignedError, taskNotFoundError } from '@shared/errors/request-task-error';

import { PermitVariationApplicationSubmitApplicationAmendRequestTaskActionPayload, TasksService } from 'pmrv-api';

import { BusinessErrorService } from '../../error/business-error/business-error.service';
import { catchTaskReassignedBadRequest } from '../../error/business-errors';
import { catchNotFoundRequest, ErrorCode } from '../../error/not-found-error';
import { TaskStatusPipe } from '../../permit-application/shared/pipes/task-status.pipe';
import { PERMIT_AMEND_STATUS_PREFIX } from '../../permit-application/shared/types/amend.permit.type';
import { StatusKey } from '../../permit-application/shared/types/permit-task.type';
import { getAvailableSections } from '../../permit-application/shared/utils/available-sections';
import { TaskItemStatus } from '../../shared/task-list/task-list.interface';
import { PermitVariationStore } from '../store/permit-variation.store';
import { variationDetailsStatus } from '../variation-status';

@Component({
  selector: 'app-summary',
  templateUrl: './summary.component.html',
  providers: [TaskStatusPipe],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryComponent implements PendingRequest {
  private readonly taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));

  variationDetailsStatus$: Observable<TaskItemStatus> = this.store.pipe(map((state) => variationDetailsStatus(state)));

  allowSubmission$ = this.store.pipe(
    switchMap((state) =>
      combineLatest(getAvailableSections(state).map((section: StatusKey) => this.taskStatus.transform(section))),
    ),
    withLatestFrom(this.variationDetailsStatus$),
    map(
      ([permitStatuses, variationDetailsStatus]) =>
        permitStatuses.every((status) => status === 'complete') && variationDetailsStatus === 'complete',
    ),
  );
  competentAuthority$ = this.store.select('competentAuthority');

  variationSubmitted$ = new Subject<boolean>();

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly store: PermitVariationStore,
    private readonly tasksService: TasksService,
    private readonly route: ActivatedRoute,
    private readonly taskStatus: TaskStatusPipe,
    private readonly businessErrorService: BusinessErrorService,
    readonly location: Location,
  ) {}

  onSubmit(): void {
    combineLatest([this.store, this.taskId$])
      .pipe(
        first(),
        switchMap(([state, taskId]) =>
          this.tasksService.processRequestTaskAction({
            requestTaskActionType:
              state.requestTaskType === 'PERMIT_VARIATION_APPLICATION_AMENDS_SUBMIT'
                ? 'PERMIT_VARIATION_SUBMIT_APPLICATION_AMEND'
                : 'PERMIT_VARIATION_SUBMIT_APPLICATION',
            requestTaskId: taskId,
            requestTaskActionPayload:
              state.requestTaskType === 'PERMIT_VARIATION_APPLICATION_AMENDS_SUBMIT'
                ? ({
                    payloadType: 'PERMIT_VARIATION_SUBMIT_APPLICATION_AMEND_PAYLOAD',
                    permitSectionsCompleted: {
                      ...Object.keys(state.permitSectionsCompleted ?? [])
                        .filter((statusKey) => !statusKey.startsWith(PERMIT_AMEND_STATUS_PREFIX))
                        .reduce((res, key) => ((res[key] = state.permitSectionsCompleted[key]), res), {}),
                    },
                  } as PermitVariationApplicationSubmitApplicationAmendRequestTaskActionPayload)
                : {
                    payloadType: 'EMPTY_PAYLOAD',
                  },
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
      .subscribe(() => this.variationSubmitted$.next(true));
  }
}
