import { Injectable } from '@angular/core';

import { combineLatest, first, map, Observable, switchMap, tap } from 'rxjs';

import { catchTaskReassignedBadRequest } from '@error/business-errors';
import { catchNotFoundRequest, ErrorCode } from '@error/not-found-error';
import { requestTaskReassignedError, taskNotFoundError } from '@shared/errors/request-task-error';
import { ItemNamePipe } from '@shared/pipes/item-name.pipe';
import { AttachedFile } from '@shared/types/attached-file.type';
import { TasksHelperService } from '@tasks/shared/services/tasks-helper.service';
import { CommonTasksState } from '@tasks/store/common-tasks.state';

import {
  InstallationInspectionOperatorRespondRequestTaskPayload,
  InstallationInspectionOperatorRespondSaveRequestTaskActionPayload,
  InstallationInspectionRequestMetadata,
} from 'pmrv-api';

import {
  InspectionSaveRequestTaskActionPayload,
  InspectionSubmitRequestTaskPayload,
  InspectionType,
} from '../shared/inspection';

@Injectable({ providedIn: 'root' })
export class InspectionService extends TasksHelperService {
  private type: InspectionType;

  setType(type: InspectionType) {
    this.type = type;
  }

  get accountId$() {
    return this.store.requestTaskItem$.pipe(map((task) => task.requestInfo.accountId));
  }

  get auditYear() {
    return (
      this.store.getValue().requestTaskItem?.requestInfo?.requestMetadata as InstallationInspectionRequestMetadata
    )?.year;
  }

  get title$(): Observable<string> {
    const itemNamePipe = new ItemNamePipe();

    return combineLatest([this.requestTaskItem$, this.requestMetadata$]).pipe(
      map(([requestTaskItem, metadata]) =>
        itemNamePipe.transform(
          requestTaskItem?.requestTask?.type,
          (metadata as InstallationInspectionRequestMetadata)?.year,
        ),
      ),
    );
  }

  postInspectionTaskSave(
    data: InspectionSaveRequestTaskActionPayload,
    attachments?: InspectionSubmitRequestTaskPayload['inspectionAttachments'],
  ) {
    return this.store.pipe(
      first(),
      switchMap((state) => this.postInspection(state, data, attachments)),
    );
  }

  private postInspection(
    state: CommonTasksState,
    data: InspectionSaveRequestTaskActionPayload,
    attachments?: InspectionSubmitRequestTaskPayload['inspectionAttachments'],
  ) {
    return this.tasksService
      .processRequestTaskAction({
        requestTaskActionType:
          this.type === 'audit'
            ? 'INSTALLATION_AUDIT_SAVE_APPLICATION'
            : 'INSTALLATION_ONSITE_INSPECTION_SAVE_APPLICATION',
        requestTaskId: state.requestTaskItem.requestTask.id,
        requestTaskActionPayload: {
          payloadType:
            this.type === 'audit'
              ? 'INSTALLATION_AUDIT_APPLICATION_SAVE_PAYLOAD'
              : 'INSTALLATION_ONSITE_INSPECTION_APPLICATION_SAVE_PAYLOAD',
          installationInspection: data.installationInspection,
          installationInspectionSectionsCompleted: data.installationInspectionSectionsCompleted,
        } as InspectionSaveRequestTaskActionPayload,
      })
      .pipe(
        catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
          this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
        ),
        catchTaskReassignedBadRequest(() =>
          this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
        ),
        tap(() => {
          return this.store.setState({
            ...state,
            requestTaskItem: {
              ...state.requestTaskItem,
              requestTask: {
                ...state.requestTaskItem.requestTask,
                payload: {
                  ...state.requestTaskItem.requestTask.payload,
                  installationInspection: data.installationInspection,
                  installationInspectionSectionsCompleted: data.installationInspectionSectionsCompleted,
                  inspectionAttachments: {
                    ...(this.store.getValue().requestTaskItem.requestTask.payload as InspectionSubmitRequestTaskPayload)
                      ?.inspectionAttachments,
                    ...attachments,
                  },
                } as InspectionSubmitRequestTaskPayload,
              },
            },
          });
        }),
      );
  }

  postInspectionForRespondTaskSaveOrSend(
    data: InstallationInspectionOperatorRespondSaveRequestTaskActionPayload,
    attachments?: InstallationInspectionOperatorRespondRequestTaskPayload['inspectionAttachments'],
    isSend = false,
  ) {
    return this.store.pipe(
      first(),
      switchMap((state) => this.postInspectionForRespond(state, data, isSend, attachments)),
    );
  }

  private postInspectionForRespond(
    state: CommonTasksState,
    data: InstallationInspectionOperatorRespondSaveRequestTaskActionPayload,
    isSend: boolean,
    attachments?: InstallationInspectionOperatorRespondRequestTaskPayload['inspectionAttachments'],
  ) {
    const payloadData = !isSend
      ? {
          followupActionsResponses: data.followupActionsResponses,
          installationInspectionOperatorRespondSectionsCompleted:
            data.installationInspectionOperatorRespondSectionsCompleted,
        }
      : {};

    return this.tasksService
      .processRequestTaskAction({
        requestTaskActionType:
          this.type === 'audit'
            ? isSend
              ? 'INSTALLATION_AUDIT_OPERATOR_RESPOND_SUBMIT'
              : 'INSTALLATION_AUDIT_OPERATOR_RESPOND_SAVE'
            : isSend
              ? 'INSTALLATION_ONSITE_INSPECTION_OPERATOR_RESPOND_SUBMIT'
              : 'INSTALLATION_ONSITE_INSPECTION_OPERATOR_RESPOND_SAVE',
        requestTaskId: state.requestTaskItem.requestTask.id,
        requestTaskActionPayload: {
          payloadType: isSend
            ? 'EMPTY_PAYLOAD'
            : this.type === 'audit'
              ? 'INSTALLATION_AUDIT_OPERATOR_RESPOND_SAVE_PAYLOAD'
              : 'INSTALLATION_ONSITE_INSPECTION_OPERATOR_RESPOND_SAVE_PAYLOAD',
          ...payloadData,
        } as InstallationInspectionOperatorRespondSaveRequestTaskActionPayload,
      })
      .pipe(
        catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
          this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
        ),
        catchTaskReassignedBadRequest(() =>
          this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
        ),
        tap(() => {
          return this.store.setState({
            ...state,
            requestTaskItem: {
              ...state.requestTaskItem,
              requestTask: {
                ...state.requestTaskItem.requestTask,
                payload: {
                  ...state.requestTaskItem.requestTask.payload,
                  followupActionsResponses: data.followupActionsResponses,
                  installationInspectionOperatorRespondSectionsCompleted:
                    data.installationInspectionOperatorRespondSectionsCompleted,
                  inspectionAttachments: {
                    ...(this.store.getValue().requestTaskItem.requestTask.payload as InspectionSubmitRequestTaskPayload)
                      ?.inspectionAttachments,
                    ...attachments,
                  },
                } as InstallationInspectionOperatorRespondRequestTaskPayload,
              },
            },
          });
        }),
      );
  }

  getOperatorDownloadUrlFiles(files: string[]): AttachedFile[] {
    const attachments: { [key: string]: string } = (
      this.store.getValue().requestTaskItem.requestTask.payload as InspectionSubmitRequestTaskPayload
    )?.inspectionAttachments;

    const url = this.getBaseFileDownloadUrl();

    return (
      files?.map((id) => ({
        downloadUrl: url + `${id}`,
        fileName: attachments[id],
      })) ?? []
    );
  }
}
