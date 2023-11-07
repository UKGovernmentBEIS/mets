import { Injectable } from '@angular/core';

import { first, Observable, of, switchMap, tap } from 'rxjs';

import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchTaskReassignedBadRequest } from '@error/business-errors';
import { catchNotFoundRequest, ErrorCode } from '@error/not-found-error';
import { requestTaskReassignedError, taskNotFoundError } from '@shared/errors/request-task-error';
import { CommonTasksState } from '@tasks/store/common-tasks.state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import {
  PermitTransferAApplicationRequestTaskPayload,
  RequestTaskActionPayload,
  RequestTaskActionProcessDTO,
  TasksService,
} from 'pmrv-api';

@Injectable({ providedIn: 'root' })
export class PermitTransferAService {
  constructor(
    private readonly store: CommonTasksStore,
    private readonly tasksService: TasksService,
    private readonly businessErrorService: BusinessErrorService,
  ) {}

  get isEditable$(): Observable<boolean> {
    return this.store.isEditable$;
  }

  get requestId(): Observable<string> {
    return of(this.store.requestId);
  }

  private get attachments() {
    let attachments: { [key: string]: string };
    const requestTaskType = this.store.requestTaskType;
    switch (requestTaskType) {
      case 'PERMIT_TRANSFER_A_APPLICATION_SUBMIT':
        attachments = (
          this.store.getValue().requestTaskItem.requestTask.payload as PermitTransferAApplicationRequestTaskPayload
        ).transferAttachments;
        break;
      default:
        throw Error('Unhandled task payload type: ' + requestTaskType);
    }
    return attachments;
  }

  getPayload(): Observable<any> {
    return this.store.payload$;
  }

  getDownloadUrlFiles(files: string[]): { downloadUrl: string; fileName: string }[] {
    const attachments: { [key: string]: string } = this.attachments;
    const url = this.createBaseFileDownloadUrl();
    return (
      files?.map((id) => ({
        downloadUrl: url + `${id}`,
        fileName: attachments[id],
      })) ?? []
    );
  }

  createBaseFileDownloadUrl() {
    const requestTaskId = this.store.requestTaskId;
    return `/tasks/${requestTaskId}/file-download/`;
  }

  sendDataForPost(
    value: Partial<PermitTransferAApplicationRequestTaskPayload>,
    sectionCompleted?: boolean,
    attachments?: { [key: string]: string },
  ) {
    return this.store.pipe(
      first(),
      switchMap((state) =>
        this.postTransfer(
          {
            ...state,
            requestTaskItem: {
              ...state.requestTaskItem,
              requestTask: {
                ...state.requestTaskItem.requestTask,
                payload: {
                  ...state.requestTaskItem.requestTask.payload,
                  ...value,
                  transferAttachments: {
                    ...(state.requestTaskItem.requestTask.payload as PermitTransferAApplicationRequestTaskPayload)
                      .transferAttachments,
                    ...attachments,
                  },
                  sectionCompleted,
                } as PermitTransferAApplicationRequestTaskPayload,
              },
            },
          },
          'PERMIT_TRANSFER_A_SAVE_APPLICATION',
        ),
      ),
    );
  }

  postTransfer(state: CommonTasksState, actionType: RequestTaskActionProcessDTO['requestTaskActionType']) {
    const { transferAttachments, ...payload } = state.requestTaskItem.requestTask
      .payload as PermitTransferAApplicationRequestTaskPayload;

    return this.tasksService
      .processRequestTaskAction({
        requestTaskActionType: actionType,
        requestTaskId: state.requestTaskItem.requestTask.id,
        requestTaskActionPayload:
          actionType === 'PERMIT_TRANSFER_A_SAVE_APPLICATION'
            ? ({
                ...payload,
                payloadType: 'PERMIT_TRANSFER_A_SAVE_APPLICATION_PAYLOAD',
              } as RequestTaskActionPayload)
            : ({
                payloadType: 'EMPTY_PAYLOAD',
              } as RequestTaskActionPayload),
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

  postSubmit() {
    return this.postTransfer(this.store.getState(), 'PERMIT_TRANSFER_A_SUBMIT_APPLICATION');
  }
}
