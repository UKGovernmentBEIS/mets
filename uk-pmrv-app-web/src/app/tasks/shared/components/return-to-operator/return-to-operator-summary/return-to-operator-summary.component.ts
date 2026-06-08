import { ChangeDetectionStrategy, Component, computed, inject, Signal, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { RouterLink } from '@angular/router';

import { first, map, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchTaskReassignedBadRequest } from '@error/business-errors';
import { catchNotFoundRequest, ErrorCode } from '@error/not-found-error';
import { requestTaskReassignedError, taskNotFoundError } from '@shared/errors/request-task-error';
import { SharedModule } from '@shared/shared.module';
import {
  createRequestTaskActionPayload,
  getRequestTaskActionType,
  getWorkflowTypeText,
  TASKS_RETURN_TO_OPERATOR_FORM,
} from '@tasks/shared/core';
import { TaskSharedModule } from '@tasks/shared/task-shared-module';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import {
  NERApplicationVerificationReturnToOperatorRequestTaskActionPayload,
  RequestTaskDTO,
  TasksService,
} from 'pmrv-api';

interface ViewModel {
  heading: string;
  caption: string;
  taskType: RequestTaskDTO['type'];
  returnLinkLevelsUp: number;
  isEditable: boolean;
  changesRequired: NERApplicationVerificationReturnToOperatorRequestTaskActionPayload['changesRequired'];
  isSubmitted: boolean;
  workflowTypeText: string;
}

@Component({
  selector: 'app-tasks-return-to-operator-summary',
  imports: [TaskSharedModule, SharedModule, RouterLink],
  templateUrl: './return-to-operator-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TasksReturnToOperatorSummaryComponent {
  private readonly store = inject(CommonTasksStore);
  private readonly tasksService = inject(TasksService);
  private readonly businessErrorService = inject(BusinessErrorService);
  private readonly pendingRequest = inject(PendingRequestService);
  private readonly isEditable = toSignal(this.store.pipe(map((state) => state.isEditable)));
  private readonly requestTaskType = toSignal(this.store.pipe(map((state) => state.requestTaskItem.requestTask.type)));
  private readonly isSubmitted = signal(false);

  form = inject(TASKS_RETURN_TO_OPERATOR_FORM);

  vm: Signal<ViewModel> = computed(() => ({
    heading: 'Check your answers',
    caption: 'Return to operator for changes',
    taskType: this.requestTaskType(),
    returnLinkLevelsUp: 2,
    isEditable: this.isEditable(),
    changesRequired: this.form.value.changesRequired,
    isSubmitted: this.isSubmitted(),
    workflowTypeText: getWorkflowTypeText(this.requestTaskType()),
  }));

  onConfirm() {
    const requestTaskType = this.requestTaskType();
    const requestTaskActionType = getRequestTaskActionType(requestTaskType);

    this.store
      .pipe(
        first(),
        switchMap((state) =>
          this.tasksService.processRequestTaskAction({
            requestTaskActionType,
            requestTaskId: state.requestTaskItem.requestTask.id,
            requestTaskActionPayload: createRequestTaskActionPayload(
              requestTaskActionType,
              this.form.value.changesRequired,
            ),
          }),
        ),
        catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
          this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
        ),
        catchTaskReassignedBadRequest(() =>
          this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
        ),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.isSubmitted.set(true));
  }
}
