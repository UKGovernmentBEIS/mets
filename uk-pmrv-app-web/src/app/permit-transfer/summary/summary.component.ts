import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { BehaviorSubject, combineLatest, first, map, Observable, switchMap, withLatestFrom } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchTaskReassignedBadRequest } from '@error/business-errors';
import { catchNotFoundRequest, ErrorCode } from '@error/not-found-error';
import { TaskStatusPipe } from '@permit-application/shared/pipes/task-status.pipe';
import { PERMIT_AMEND_STATUS_PREFIX } from '@permit-application/shared/types/amend.permit.type';
import { StatusKey } from '@permit-application/shared/types/permit-task.type';
import { getAvailableSections } from '@permit-application/shared/utils/available-sections';
import { requestTaskReassignedError, taskNotFoundError } from '@shared/errors/request-task-error';
import { TaskItemStatus } from '@shared/task-list/task-list.interface';

import { PermitIssuanceSubmitApplicationAmendRequestTaskActionPayload, TasksService } from 'pmrv-api';

import { PermitTransferStore } from '../store/permit-transfer.store';
import { transferDetailsStatus } from '../transfer-status';

@Component({
  selector: 'app-summary',
  templateUrl: './summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PermitTransferSummaryComponent {
  private readonly taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));

  requestId$ = this.store.getRequestId();
  competentAuthority$ = this.store.getCompetentAuthority();
  transferDetailsStatus$: Observable<TaskItemStatus> = this.store.pipe(map((state) => transferDetailsStatus(state)));
  transferSubmitted$ = new BehaviorSubject(false);

  allowSubmission$ = this.store.pipe(
    switchMap((state) =>
      combineLatest(getAvailableSections(state).map((section: StatusKey) => this.taskStatus.transform(section))),
    ),
    withLatestFrom(this.transferDetailsStatus$),
    map(
      ([permitStatuses, transferDetailsStatus]) =>
        permitStatuses.every((status) => status === 'complete') && transferDetailsStatus === 'complete',
    ),
  );

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly store: PermitTransferStore,
    private readonly tasksService: TasksService,
    private readonly route: ActivatedRoute,
    private readonly businessErrorService: BusinessErrorService,
    private readonly taskStatus: TaskStatusPipe,
  ) {}

  onSubmit(): void {
    combineLatest([this.store, this.taskId$])
      .pipe(
        first(),
        switchMap(([state, taskId]) =>
          this.tasksService.processRequestTaskAction({
            requestTaskActionType:
              state.requestTaskType === 'PERMIT_TRANSFER_B_APPLICATION_AMENDS_SUBMIT'
                ? 'PERMIT_TRANSFER_B_SUBMIT_APPLICATION_AMEND'
                : 'PERMIT_TRANSFER_B_SUBMIT_APPLICATION',
            requestTaskId: taskId,
            requestTaskActionPayload:
              state.requestTaskType === 'PERMIT_TRANSFER_B_APPLICATION_AMENDS_SUBMIT'
                ? ({
                    payloadType: 'PERMIT_TRANSFER_B_SUBMIT_APPLICATION_AMEND_PAYLOAD',
                    permitSectionsCompleted: {
                      ...Object.keys(state.permitSectionsCompleted ?? [])
                        .filter((statusKey) => !statusKey.startsWith(PERMIT_AMEND_STATUS_PREFIX))
                        .reduce((res, key) => ((res[key] = state.permitSectionsCompleted[key]), res), {}),
                    },
                  } as PermitIssuanceSubmitApplicationAmendRequestTaskActionPayload)
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
      .subscribe(() => {
        this.transferSubmitted$.next(true);
      });
  }
}
