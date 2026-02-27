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
import { WasteQdrService } from '@tasks/waste-qdr/core';
import { WasteQdrTaskReviewComponent } from '@tasks/waste-qdr/shared/components/waste-qdr-task-review/waste-qdr-task-review.component';

import { NotifyOperatorForDecisionRequestTaskActionPayload, RequestTaskActionPayload, TasksService } from 'pmrv-api';

@Component({
  selector: 'app-waste-qdr-complete-task',
  imports: [SharedModule, WasteQdrTaskReviewComponent],
  templateUrl: './complete-task.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class WasteQdrCompleteTaskComponent {
  private readonly taskId = toSignal(this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId')))));
  private readonly requestTaskItem = this.wasteQdrService.requestTaskItem;
  isSubmitted = signal(false);

  constructor(
    private readonly tasksService: TasksService,
    readonly wasteQdrService: WasteQdrService,
    private readonly route: ActivatedRoute,
    private readonly businessErrorService: BusinessErrorService,
    readonly pendingRequest: PendingRequestService,
  ) {}

  complete() {
    const requestTaskItem = this.requestTaskItem();
    const signatory = requestTaskItem.requestTask.assigneeUserId;

    const requestTaskActionPayload = {
      payloadType: 'WASTE_QDR_REGULATOR_REVIEW_SUBMIT_PAYLOAD',
      decisionNotification: {
        operators: [],
        externalContacts: [],
        signatory,
      } as NotifyOperatorForDecisionRequestTaskActionPayload['decisionNotification'],
    } as RequestTaskActionPayload;

    this.tasksService
      .processRequestTaskAction({
        requestTaskActionType: 'WASTE_QDR_REGULATOR_REVIEW_SUBMIT',
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
