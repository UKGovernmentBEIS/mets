import { Injectable } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';

import { first, Observable, switchMap, tap } from 'rxjs';

import { catchTaskReassignedBadRequest } from '@error/business-errors';
import { catchNotFoundRequest, ErrorCode } from '@error/not-found-error';
import { requestTaskReassignedError, taskNotFoundError } from '@shared/errors/request-task-error';
import { AttachedFile } from '@shared/types/attached-file.type';
import { TasksHelperService } from '@tasks/shared/services/tasks-helper.service';
import { CommonTasksState } from '@tasks/store/common-tasks.state';

import {
  RequestTaskActionPayload,
  RequestTaskActionProcessDTO,
  WasteQDRApplicationSubmitRequestTaskPayload,
  WasteQDRRequestMetaData,
} from 'pmrv-api';

@Injectable({ providedIn: 'root' })
export class WasteQdrService extends TasksHelperService {
  get payload() {
    return toSignal(this.store.payload$ as Observable<WasteQDRApplicationSubmitRequestTaskPayload>);
  }

  get requestTaskType() {
    return toSignal(this.store.requestTaskType$);
  }

  get requestMetadata() {
    return toSignal(this.requestMetadata$ as Observable<WasteQDRRequestMetaData>);
  }

  get daysRemaining() {
    return toSignal(this.daysRemaining$);
  }

  get isEditable() {
    return toSignal(this.isEditable$);
  }

  getDownloadUrlFile(file: string): AttachedFile {
    const attachments: { [key: string]: string } = (
      this.store.getValue().requestTaskItem.requestTask.payload as WasteQDRApplicationSubmitRequestTaskPayload
    )?.wasteQDRAttachments;
    const url = this.getBaseFileDownloadUrl();

    return file
      ? {
          downloadUrl: url + `${file}`,
          fileName: attachments[file],
        }
      : null;
  }

  getDownloadUrlFiles(files: string[]): AttachedFile[] {
    const attachments: { [key: string]: string } = (
      this.store.getValue().requestTaskItem.requestTask.payload as WasteQDRApplicationSubmitRequestTaskPayload
    )?.wasteQDRAttachments;
    const url = this.getBaseFileDownloadUrl();

    return (
      files?.map((id) => ({
        downloadUrl: url + `${id}`,
        fileName: attachments[id],
      })) ?? []
    );
  }

  postTaskSave(value: any, attachments?: { [key: string]: string }, statusValue?: boolean, statusKey?: string) {
    return this.store.pipe(
      first(),
      switchMap((state) => {
        const postWasteQdrState = {
          ...state,
          requestTaskItem: {
            ...state.requestTaskItem,
            requestTask: {
              ...state.requestTaskItem.requestTask,
              payload: {
                ...state.requestTaskItem.requestTask.payload,
                qdr: {
                  ...(state.requestTaskItem.requestTask.payload as WasteQDRApplicationSubmitRequestTaskPayload).qdr,
                  ...value,
                },
                wasteQDRAttachments: {
                  ...(state.requestTaskItem.requestTask.payload as WasteQDRApplicationSubmitRequestTaskPayload)
                    ?.wasteQDRAttachments,
                  ...attachments,
                },
                wasteQDRSectionsCompleted: {
                  ...(state.requestTaskItem.requestTask.payload as WasteQDRApplicationSubmitRequestTaskPayload)
                    ?.wasteQDRSectionsCompleted,
                  ...(statusKey ? { [statusKey]: statusValue } : undefined),
                },
              } as WasteQDRApplicationSubmitRequestTaskPayload,
            },
          },
        };

        return this.postWasteQdr(postWasteQdrState, 'WASTE_QDR_SAVE_APPLICATION');
      }),
    );
  }

  postWasteQdr(state: CommonTasksState, actionType: RequestTaskActionProcessDTO['requestTaskActionType']) {
    const payload = state.requestTaskItem.requestTask.payload as WasteQDRApplicationSubmitRequestTaskPayload;
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
      case 'WASTE_QDR_SAVE_APPLICATION':
        return {
          payloadType: 'WASTE_QDR_APPLICATION_SAVE_PAYLOAD',
          qdr: payload.qdr,
          wasteQDRSectionsCompleted: payload.wasteQDRSectionsCompleted,
        } as RequestTaskActionPayload;

      default:
        return {
          payloadType: 'EMPTY_PAYLOAD',
        } as RequestTaskActionPayload;
    }
  }
}
