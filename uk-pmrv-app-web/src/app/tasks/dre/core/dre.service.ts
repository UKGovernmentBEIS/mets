import { Injectable } from '@angular/core';

import { distinctUntilChanged, first, map, Observable, switchMap, tap } from 'rxjs';

import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchTaskReassignedBadRequest } from '@error/business-errors';
import { catchNotFoundRequest, ErrorCode } from '@error/not-found-error';
import { requestTaskReassignedError, taskNotFoundError } from '@shared/errors/request-task-error';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import {
  Dre,
  DreApplicationSubmitRequestTaskPayload,
  RequestMetadata,
  RequestTaskActionPayload,
  RequestTaskItemDTO,
  RequestTaskPayload,
  TasksService,
} from 'pmrv-api';

@Injectable({ providedIn: 'root' })
export class DreService {
  constructor(
    private readonly store: CommonTasksStore,
    private readonly tasksService: TasksService,
    private readonly businessErrorService: BusinessErrorService,
  ) {}

  get requestTaskItem$(): Observable<RequestTaskItemDTO> {
    return this.store.requestTaskItem$;
  }

  get payload$(): Observable<RequestTaskPayload> {
    return this.store.payload$;
  }

  get requestMetadata$(): Observable<RequestMetadata> {
    return this.store.requestMetadata$;
  }

  get dre$(): Observable<Dre> {
    return this.payload$.pipe(
      map((payload) => (payload as any)?.dre),
      distinctUntilChanged(),
    );
  }

  get isEditable$(): Observable<boolean> {
    return this.store.isEditable$;
  }

  get dreAttachments() {
    return (this.store.getValue().requestTaskItem.requestTask.payload as DreApplicationSubmitRequestTaskPayload)
      .dreAttachments;
  }

  getDownloadUrlFiles(files: string[]): { downloadUrl: string; fileName: string }[] {
    const attachments: { [key: string]: string } = this.dreAttachments;
    return (
      files?.map((id) => ({
        downloadUrl: this.getBaseFileDownloadUrl() + `${id}`,
        fileName: attachments[id],
      })) ?? []
    );
  }

  getBaseFileDownloadUrl() {
    const requestTaskId = this.store.requestTaskId;
    return `/tasks/${requestTaskId}/file-download/`;
  }

  saveDre(dre: Partial<Dre>, sectionCompleted: boolean, attachments?: { [key: string]: string }): Observable<any> {
    return this.store.pipe(
      first(),
      switchMap((state) => {
        const payload = state.requestTaskItem.requestTask.payload as DreApplicationSubmitRequestTaskPayload;
        return this.tasksService
          .processRequestTaskAction({
            requestTaskActionType: 'DRE_SAVE_APPLICATION',
            requestTaskId: state.requestTaskItem.requestTask.id,
            requestTaskActionPayload: {
              payloadType: 'DRE_SAVE_APPLICATION_PAYLOAD',
              dre: {
                ...payload?.dre,
                ...dre,
              },
              sectionCompleted: sectionCompleted,
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
                      dre: {
                        ...payload?.dre,
                        ...dre,
                      },
                      dreAttachments: {
                        ...payload?.dreAttachments,
                        ...attachments,
                      },
                      sectionCompleted: sectionCompleted,
                    } as DreApplicationSubmitRequestTaskPayload,
                  },
                },
              }),
            ),
          );
      }),
    );
  }

  saveSectionStatus(sectionCompleted: boolean): Observable<any> {
    return this.saveDre(undefined, sectionCompleted);
  }
}
