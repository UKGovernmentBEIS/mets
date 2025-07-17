import { Injectable, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';

import { first, map, Observable, switchMap, tap } from 'rxjs';

import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchTaskReassignedBadRequest } from '@error/business-errors';
import { catchNotFoundRequest, ErrorCode } from '@error/not-found-error';
import { requestTaskReassignedError, taskNotFoundError } from '@shared/errors/request-task-error';
import { ItemNamePipe } from '@shared/pipes/item-name.pipe';
import { TasksHelperService } from '@tasks/shared/services/tasks-helper.service';
import { CommonTasksState } from '@tasks/store/common-tasks.state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import {
  PermanentCessation,
  PermanentCessationApplicationSubmitRequestTaskPayload,
  RequestMetadata,
  RequestTaskActionPayload,
  RequestTaskActionProcessDTO,
  RequestTaskDTO,
  TasksService,
} from 'pmrv-api';

@Injectable()
export class PermanentCessationService extends TasksHelperService {
  constructor(
    store: CommonTasksStore,
    tasksService: TasksService,
    businessErrorService: BusinessErrorService,
    private readonly itemNamePipe: ItemNamePipe,
  ) {
    super(store, tasksService, businessErrorService);
  }

  get payload$(): Observable<PermanentCessationApplicationSubmitRequestTaskPayload> {
    return this.store.payload$ as Observable<PermanentCessationApplicationSubmitRequestTaskPayload>;
  }

  get payload(): Signal<PermanentCessationApplicationSubmitRequestTaskPayload> {
    return toSignal(this.payload$);
  }

  get requestTaskType(): Signal<RequestTaskDTO['type']> {
    return toSignal(this.requestTaskType$);
  }

  get requestMetadata(): Signal<RequestMetadata> {
    return toSignal(this.requestMetadata$);
  }

  get requestId() {
    return this.store.requestId;
  }

  get requestAccountId$() {
    return this.store.requestInfo$.pipe(map((info) => info.accountId));
  }

  get competentAuthority$() {
    return this.store.requestInfo$.pipe(map((info) => info.competentAuthority));
  }

  get title$(): Observable<string> {
    return this.requestTaskItem$.pipe(
      map((requestTaskItem) => this.itemNamePipe.transform(requestTaskItem?.requestTask?.type)),
    );
  }
  get title(): Signal<string> {
    return toSignal(this.title$);
  }

  get isEditable(): Signal<boolean> {
    return toSignal(this.isEditable$);
  }

  get accountId$() {
    return this.store.requestTaskItem$.pipe(map((task) => task.requestInfo.accountId));
  }

  get allowedRequestTaskActions$() {
    return this.store.requestTaskItem$.pipe(map((task) => task.allowedRequestTaskActions));
  }

  get allowedRequestTaskActions() {
    return toSignal(this.allowedRequestTaskActions$);
  }

  savePermanentCessation(
    data: PermanentCessation,
    permanentCessationAttachments?: { [key: string]: string },
    statusValue?: boolean | boolean[],
    statusKey?: string | 'details',
  ) {
    return this.store.pipe(
      first(),
      switchMap((state) =>
        this.postPermanentCessation(
          {
            ...state,
            requestTaskItem: {
              ...state.requestTaskItem,
              requestTask: {
                ...state.requestTaskItem.requestTask,
                payload: {
                  ...state.requestTaskItem.requestTask.payload,
                  permanentCessation: {
                    ...(
                      state.requestTaskItem.requestTask.payload as PermanentCessationApplicationSubmitRequestTaskPayload
                    ).permanentCessation,
                    ...data,
                  },
                  permanentCessationSectionsCompleted: {
                    ...(
                      state.requestTaskItem.requestTask.payload as PermanentCessationApplicationSubmitRequestTaskPayload
                    ).permanentCessationSectionsCompleted,
                    ...{ [statusKey]: statusValue },
                  },
                  permanentCessationAttachments,
                } as PermanentCessationApplicationSubmitRequestTaskPayload,
              },
            },
          },
          'PERMANENT_CESSATION_SAVE_APPLICATION',
        ),
      ),
    );
  }

  getDownloadUrlFiles(files: string[]): { downloadUrl: string; fileName: string }[] {
    const url = this.getBaseFileDownloadUrl();
    const attachments: { [key: string]: string } = (
      this.store.getValue().requestTaskItem.requestTask.payload as PermanentCessationApplicationSubmitRequestTaskPayload
    )?.permanentCessationAttachments;

    return (
      files?.map((id) => ({
        downloadUrl: url + `${id}`,
        fileName: attachments[id],
      })) ?? []
    );
  }

  postPermanentCessation(state: CommonTasksState, actionType: RequestTaskActionProcessDTO['requestTaskActionType']) {
    const payload = state.requestTaskItem.requestTask.payload as PermanentCessationApplicationSubmitRequestTaskPayload;
    return this.tasksService
      .processRequestTaskAction({
        requestTaskActionType: actionType,
        requestTaskId: state.requestTaskItem.requestTask.id,
        requestTaskActionPayload: this.createRequestTaskActionPayload(actionType, payload),
      })
      .pipe(
        catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
          this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
        ),
        catchTaskReassignedBadRequest(() =>
          this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
        ),
        tap(() => this.store.setState(state)),
      );
  }

  private createRequestTaskActionPayload(
    actionType: RequestTaskActionProcessDTO['requestTaskActionType'],
    payload?: any,
  ) {
    switch (actionType) {
      case 'PERMANENT_CESSATION_SAVE_APPLICATION':
        return {
          payloadType: 'PERMANENT_CESSATION_SAVE_APPLICATION_PAYLOAD',
          permanentCessation: payload.permanentCessation,
          permanentCessationSectionsCompleted: payload.permanentCessationSectionsCompleted,
          permanentCessationAttachments: payload.permanentCessationAttachments,
        } as RequestTaskActionPayload;

      default:
        return {
          payloadType: 'EMPTY_PAYLOAD',
        } as RequestTaskActionPayload;
    }
  }
}
