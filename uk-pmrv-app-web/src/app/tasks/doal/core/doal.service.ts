import { Injectable } from '@angular/core';

import { first, Observable, switchMap, tap } from 'rxjs';

import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchTaskReassignedBadRequest } from '@error/business-errors';
import { catchNotFoundRequest, ErrorCode } from '@error/not-found-error';
import { requestTaskReassignedError, taskNotFoundError } from '@shared/errors/request-task-error';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import {
  Doal,
  DoalApplicationSubmitRequestTaskPayload,
  DoalAuthority,
  DoalAuthorityResponseRequestTaskPayload,
  DoalRequestMetadata,
  RequestTaskActionEmptyPayload,
  RequestTaskActionPayload,
  RequestTaskItemDTO,
  TasksService,
} from 'pmrv-api';

import { DoalAuthorityTaskSectionKey, DoalTaskSectionKey } from './doal-task.type';

@Injectable({ providedIn: 'root' })
export class DoalService {
  constructor(
    private readonly store: CommonTasksStore,
    private readonly tasksService: TasksService,
    private readonly businessErrorService: BusinessErrorService,
  ) {}

  get requestTaskItem$(): Observable<RequestTaskItemDTO> {
    return this.store.requestTaskItem$;
  }

  get payload$(): Observable<DoalApplicationSubmitRequestTaskPayload> {
    return this.store.payload$ as Observable<DoalApplicationSubmitRequestTaskPayload>;
  }

  get authorityPayload$(): Observable<DoalAuthorityResponseRequestTaskPayload> {
    return this.store.payload$ as Observable<DoalAuthorityResponseRequestTaskPayload>;
  }

  get payloadState(): DoalApplicationSubmitRequestTaskPayload {
    return this.store.getValue().requestTaskItem.requestTask.payload as DoalApplicationSubmitRequestTaskPayload;
  }

  get requestMetadata$(): Observable<DoalRequestMetadata> {
    return this.store.requestMetadata$ as Observable<DoalRequestMetadata>;
  }

  get isEditable$(): Observable<boolean> {
    return this.store.isEditable$;
  }

  get doalAttachments() {
    return (this.store.getValue().requestTaskItem.requestTask.payload as DoalApplicationSubmitRequestTaskPayload)
      .doalAttachments;
  }

  getDownloadUrlFiles(files: string[]): { downloadUrl: string; fileName: string }[] {
    return files?.map((id) => this.getDownloadUrlFile(id)) ?? [];
  }

  getDownloadUrlFile(file: string): { downloadUrl: string; fileName: string } {
    const attachments: { [key: string]: string } = this.doalAttachments;
    return {
      downloadUrl: this.getBaseFileDownloadUrl() + `${file}`,
      fileName: attachments[file],
    };
  }

  getBaseFileDownloadUrl() {
    return `/tasks/${this.store.requestTaskId}/file-download/`;
  }

  saveDoal(
    doal: Partial<Doal>,
    sectionKey: DoalTaskSectionKey,
    sectionCompleted: boolean,
    attachments?: { [key: string]: string },
  ): Observable<any> {
    return this.store.pipe(
      first(),
      switchMap((state) => {
        const payload = state.requestTaskItem.requestTask.payload as DoalApplicationSubmitRequestTaskPayload;
        return this.tasksService
          .processRequestTaskAction({
            requestTaskActionType: 'DOAL_SAVE_APPLICATION',
            requestTaskId: state.requestTaskItem.requestTask.id,
            requestTaskActionPayload: {
              payloadType: 'DOAL_SAVE_APPLICATION_PAYLOAD',
              doal: {
                ...payload?.doal,
                ...doal,
              },
              doalSectionsCompleted: this.buildSectionsCompleted(payload, sectionKey, sectionCompleted),
            } as RequestTaskActionPayload,
          })
          .pipe(
            catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
              this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
            ),
            catchTaskReassignedBadRequest(() =>
              this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
            ),
            tap(() =>
              this.store.setState({
                ...state,
                requestTaskItem: {
                  ...state.requestTaskItem,
                  requestTask: {
                    ...state.requestTaskItem.requestTask,
                    payload: {
                      ...state.requestTaskItem.requestTask.payload,
                      doal: {
                        ...payload?.doal,
                        ...doal,
                      },
                      doalAttachments: {
                        ...payload?.doalAttachments,
                        ...attachments,
                      },
                      doalSectionsCompleted: this.buildSectionsCompleted(payload, sectionKey, sectionCompleted),
                    } as DoalApplicationSubmitRequestTaskPayload,
                  },
                },
              }),
            ),
          );
      }),
    );
  }

  complete() {
    return this.store.pipe(
      first(),
      switchMap((state) => {
        return this.tasksService
          .processRequestTaskAction({
            requestTaskActionType:
              (state.requestTaskItem.requestTask.payload as DoalApplicationSubmitRequestTaskPayload).doal.determination
                .type === 'CLOSED'
                ? 'DOAL_CLOSE_APPLICATION'
                : 'DOAL_PROCEED_TO_AUTHORITY_APPLICATION',
            requestTaskId: state.requestTaskItem.requestTask.id,
            requestTaskActionPayload: {
              payloadType: 'EMPTY_PAYLOAD',
            } as RequestTaskActionEmptyPayload,
          })
          .pipe(
            catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
              this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
            ),
            catchTaskReassignedBadRequest(() =>
              this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
            ),
          );
      }),
    );
  }

  saveDoalAuthority(
    doalAuthority: Partial<DoalAuthority>,
    sectionKey: DoalAuthorityTaskSectionKey,
    sectionCompleted: boolean,
    attachments?: { [key: string]: string },
  ): Observable<any> {
    return this.store.pipe(
      first(),
      switchMap((state) => {
        const payload = state.requestTaskItem.requestTask.payload as DoalAuthorityResponseRequestTaskPayload;
        return this.tasksService
          .processRequestTaskAction({
            requestTaskActionType: 'DOAL_SAVE_AUTHORITY_RESPONSE',
            requestTaskId: state.requestTaskItem.requestTask.id,
            requestTaskActionPayload: {
              payloadType: 'DOAL_SAVE_AUTHORITY_RESPONSE_PAYLOAD',
              doalAuthority: {
                ...payload?.doalAuthority,
                ...doalAuthority,
              },
              doalSectionsCompleted: this.buildSectionsCompleted(payload, sectionKey, sectionCompleted),
            } as RequestTaskActionPayload,
          })
          .pipe(
            catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
              this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
            ),
            catchTaskReassignedBadRequest(() =>
              this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
            ),
            tap(() =>
              this.store.setState({
                ...state,
                requestTaskItem: {
                  ...state.requestTaskItem,
                  requestTask: {
                    ...state.requestTaskItem.requestTask,
                    payload: {
                      ...state.requestTaskItem.requestTask.payload,
                      doalAuthority: {
                        ...payload?.doalAuthority,
                        ...doalAuthority,
                      },
                      doalAttachments: {
                        ...payload?.doalAttachments,
                        ...attachments,
                      },
                      doalSectionsCompleted: this.buildSectionsCompleted(payload, sectionKey, sectionCompleted),
                    } as DoalAuthorityResponseRequestTaskPayload,
                  },
                },
              }),
            ),
          );
      }),
    );
  }

  private buildSectionsCompleted(
    payload: DoalApplicationSubmitRequestTaskPayload | DoalAuthorityResponseRequestTaskPayload,
    sectionKey: DoalTaskSectionKey | DoalAuthorityTaskSectionKey,
    sectionCompleted: boolean,
  ) {
    return {
      ...payload?.doalSectionsCompleted,
      ...(sectionKey ? { [sectionKey]: sectionCompleted } : undefined),
    };
  }
}
