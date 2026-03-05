import { Location } from '@angular/common';
import { ChangeDetectionStrategy, Component, Signal, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import { RouterLink } from '@angular/router';

import { first } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchTaskReassignedBadRequest } from '@error/business-errors';
import { catchNotFoundRequest, ErrorCode } from '@error/not-found-error';
import { requestTaskReassignedError, taskNotFoundError } from '@shared/errors/request-task-error';
import { SharedModule } from '@shared/shared.module';
import { resolveRequestType } from '@shared/store-resolver/request-type.resolver';
import { StoreContextResolver } from '@shared/store-resolver/store-context.resolver';
import { CommonTasksState } from '@tasks/store/common-tasks.state';

import { TasksService } from 'pmrv-api';

import { recallActionTypeMap, recallReturnToTextMap } from './recall';

@Component({
  selector: 'app-shared-recall',
  standalone: true,
  imports: [SharedModule, RouterLink],
  templateUrl: './recall.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class RecallSharedComponent {
  isRecallSubmitted = signal(false);
  requestType = resolveRequestType(this.location.path());
  state: Signal<CommonTasksState> = toSignal(this.storeResolver.getStore(this.requestType, false).pipe(first()));
  returnToText = recallReturnToTextMap[this.state().requestTaskItem.requestTask.type];

  constructor(
    private readonly storeResolver: StoreContextResolver,
    private readonly tasksService: TasksService,
    private readonly pendingRequest: PendingRequestService,
    private readonly location: Location,
    protected readonly businessErrorService: BusinessErrorService,
  ) {}

  onRecall() {
    const state = this.state();
    const actionType = recallActionTypeMap[state.requestTaskItem.requestTask.type];

    this.tasksService
      .processRequestTaskAction({
        requestTaskActionType: actionType,
        requestTaskId: state.requestTaskItem.requestTask.id,
        requestTaskActionPayload: { payloadType: 'EMPTY_PAYLOAD' },
      })
      .pipe(
        catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
          this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
        ),
        catchTaskReassignedBadRequest(() =>
          this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
        ),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.isRecallSubmitted.set(true));
  }
}
