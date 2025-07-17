import { Observable, tap } from 'rxjs';

import { getSaveRequestTaskActionTypeForRequestTaskType } from '@aviation/request-task/util';
import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchTaskReassignedBadRequest } from '@error/business-errors';
import { catchNotFoundRequest, ErrorCode } from '@error/not-found-error';
import { requestTaskReassignedError, taskNotFoundError } from '@shared/errors/request-task-error';
import { TaskItemStatus } from '@shared/task-list/task-list.interface';
import produce from 'immer';

import { AviationDre, RequestTaskActionProcessDTO } from 'pmrv-api';

import { RequestTaskStore } from '../../request-task.store';
import { Dre, DreRequestTaskPayload, DreTask, DreTaskKey } from '../../request-task.types';
import { RequestTaskStoreDelegate } from '../store-delegate';

export class DreStoreDelegate implements RequestTaskStoreDelegate {
  static INITIAL_STATE: Partial<Dre> = {
    dre: null,
  };

  constructor(
    private store: RequestTaskStore,
    private readonly businessErrorService: BusinessErrorService,
  ) {}

  get payload(): DreRequestTaskPayload | null {
    return this.store.getState().requestTaskItem?.requestTask?.payload as DreRequestTaskPayload;
  }

  init() {
    if (!this.payload.dre) {
      this.store.setPayload(
        produce(this.payload, (payload) => {
          payload.dre = {} as AviationDre;
        }),
      );
    }

    return this;
  }

  setAviationDre(dre: AviationDre) {
    const state = this.store.getState();

    const newState = produce(state, (draft) => {
      (draft.requestTaskItem.requestTask.payload as DreRequestTaskPayload).dre = dre;
    });

    this.store.setState(newState);
  }

  setDreAttachments(attachments: { [key: string]: string }) {
    this.store.setPayload(
      produce(this.payload, (updatedPayload) => {
        updatedPayload.dreAttachments = attachments;
      }),
    );
  }

  addDreAttachment(attachment: { [key: string]: string }) {
    this.setDreAttachments({
      ...this.payload.dreAttachments,
      ...attachment,
    });
  }

  saveDre(dreTask: { [key in DreTaskKey]?: DreTask }, status: TaskItemStatus = 'complete'): Observable<any> {
    const taskKey = Object.keys(dreTask)[0];

    const {
      requestTaskItem: { requestTask },
    } = this.store.getState();

    const payloadToUpdate = produce(this.payload, (draft) => {
      draft[taskKey] = dreTask[taskKey];
      draft.sectionCompleted = status === 'complete' ? true : false;

      delete draft.dreAttachments;

      if (draft.sendEmailNotification) {
        delete draft.sendEmailNotification;
      }
    });

    const reqBody: RequestTaskActionProcessDTO = {
      requestTaskId: requestTask.id,
      requestTaskActionType: getSaveRequestTaskActionTypeForRequestTaskType(requestTask.type),
      requestTaskActionPayload: { ...payloadToUpdate, payloadType: 'AVIATION_DRE_UKETS_SAVE_APPLICATION_PAYLOAD' },
    };

    return this.store.tasksService.processRequestTaskAction(reqBody).pipe(
      catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
        this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
      ),
      catchTaskReassignedBadRequest(() =>
        this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
      ),
      tap(() => {
        this.store.setPayload({
          ...payloadToUpdate,
          dre: this.payload.dre,
        } as Dre);
      }),
    );
  }

  get baseFileAttachmentDownloadUrl(): string {
    return `/aviation/tasks/${this.store.requestTaskId}/file-download/attachment`;
  }
}
