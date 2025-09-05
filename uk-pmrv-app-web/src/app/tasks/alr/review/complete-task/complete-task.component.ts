import { ChangeDetectionStrategy, Component, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute } from '@angular/router';

import { map } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchTaskReassignedBadRequest } from '@error/business-errors';
import { catchNotFoundRequest, ErrorCode } from '@error/not-found-error';
import { requestTaskReassignedError, taskNotFoundError } from '@shared/errors/request-task-error';
import { SharedModule } from '@shared/shared.module';
import { AlrService } from '@tasks/alr/core';
import { AlrTaskSharedModule } from '@tasks/alr/shared/alr-task-shared.module';

import {
  ALRApplicationProceededToAuthorityRequestActionPayload,
  ALRApplicationRegulatorReviewSubmitRequestTaskPayload,
  RequestTaskActionPayload,
  RequestTaskActionProcessDTO,
  TasksService,
} from 'pmrv-api';

@Component({
  selector: 'app-alr-complete-task',
  standalone: true,
  imports: [SharedModule, AlrTaskSharedModule],
  templateUrl: './complete-task.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AlrCompleteTaskComponent {
  private readonly taskId = toSignal(this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId')))));
  private readonly requestTaskItem = this.alrService.requestTaskItem;
  isSubmitted = signal(false);

  constructor(
    private readonly tasksService: TasksService,
    private readonly alrService: AlrService,
    private readonly route: ActivatedRoute,
    private readonly businessErrorService: BusinessErrorService,
    readonly pendingRequest: PendingRequestService,
  ) {}

  complete() {
    const requestTaskItem = this.requestTaskItem();
    const signatory = requestTaskItem.requestTask.assigneeUserId;
    const determinationType = (
      requestTaskItem.requestTask.payload as ALRApplicationRegulatorReviewSubmitRequestTaskPayload
    ).regulatorReviewOutcome.determination.type;
    const requestTaskActionType: RequestTaskActionProcessDTO['requestTaskActionType'] =
      determinationType === 'PROCEED_TO_AUTHORITY' ? 'ALR_PROCEED_TO_AUTHORITY' : 'ALR_CLOSE_APPLICATION';

    const requestTaskActionPayload =
      determinationType === 'PROCEED_TO_AUTHORITY'
        ? ({
            payloadType: 'ALR_REGULATOR_REVIEW_SUBMIT_APPLICATION_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD',
            decisionNotification: {
              operators: [],
              externalContacts: [],
              signatory,
            } as ALRApplicationProceededToAuthorityRequestActionPayload['decisionNotification'],
          } as RequestTaskActionPayload)
        : ({ payloadType: 'EMPTY_PAYLOAD' } as RequestTaskActionPayload);

    this.tasksService
      .processRequestTaskAction({
        requestTaskActionType,
        requestTaskId: this.taskId(),
        requestTaskActionPayload,
      })
      .pipe(
        this.pendingRequest.trackRequest(),
        catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
          this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
        ),
        catchTaskReassignedBadRequest(() =>
          this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
        ),
      )
      .subscribe(() => {
        this.isSubmitted.set(true);
      });
  }
}
