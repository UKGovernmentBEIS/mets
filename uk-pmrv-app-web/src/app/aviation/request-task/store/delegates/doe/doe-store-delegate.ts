import { Observable, tap } from 'rxjs';

import { getSaveRequestTaskActionTypeForRequestTaskType } from '@aviation/request-task/util';
import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchTaskReassignedBadRequest } from '@error/business-errors';
import { catchNotFoundRequest, ErrorCode } from '@error/not-found-error';
import { requestTaskReassignedError, taskNotFoundError } from '@shared/errors/request-task-error';
import { TaskItemStatus } from '@shared/task-list/task-list.interface';
import produce from 'immer';

import { AviationDoECorsia, RequestTaskActionProcessDTO } from 'pmrv-api';

import { RequestTaskStore } from '../../request-task.store';
import { Doe, DoeRequestTaskPayload, DoeTask, DoeTaskKey } from '../../request-task.types';
import { RequestTaskStoreDelegate } from '../store-delegate';

export class DoeStoreDelegate implements RequestTaskStoreDelegate {
  static INITIAL_STATE: Partial<Doe> = {
    doe: null,
  };

  constructor(
    private store: RequestTaskStore,
    private readonly businessErrorService: BusinessErrorService,
  ) {}

  get payload(): DoeRequestTaskPayload | null {
    return this.store.getState().requestTaskItem?.requestTask?.payload as DoeRequestTaskPayload;
  }

  init() {
    if (!this.payload.doe) {
      this.store.setPayload(
        produce(this.payload, (payload) => {
          payload.doe = {} as AviationDoECorsia;
        }),
      );
    }

    return this;
  }

  setAviationDoe(doe: AviationDoECorsia) {
    const state = this.store.getState();

    const newState = produce(state, (draft) => {
      (draft.requestTaskItem.requestTask.payload as DoeRequestTaskPayload).doe = doe;
    });

    this.store.setState(newState);
  }

  setDoeAttachments(attachments: { [key: string]: string }) {
    this.store.setPayload(
      produce(this.payload, (updatedPayload) => {
        updatedPayload.doeAttachments = attachments;
      }),
    );
  }

  addDoeAttachment(attachment: { [key: string]: string }) {
    this.setDoeAttachments({
      ...this.payload.doeAttachments,
      ...attachment,
    });
  }

  saveDoe(doeTask: { [key in DoeTaskKey]?: DoeTask }, status: TaskItemStatus = 'complete'): Observable<any> {
    const taskKey = Object.keys(doeTask)[0];

    const {
      requestTaskItem: { requestTask },
    } = this.store.getState();

    const payloadToUpdate = produce(this.payload, (draft) => {
      draft[taskKey] = doeTask[taskKey];
      draft.sectionCompleted = status === 'complete' ? true : false;

      delete draft.doeAttachments;

      if (draft.sendEmailNotification) {
        delete draft.sendEmailNotification;
      }
    });

    const reqBody: RequestTaskActionProcessDTO = {
      requestTaskId: requestTask.id,
      requestTaskActionType: getSaveRequestTaskActionTypeForRequestTaskType(requestTask.type),
      requestTaskActionPayload: {
        ...payloadToUpdate,
        payloadType: 'AVIATION_DOE_CORSIA_SUBMIT_SAVE_PAYLOAD',
      },
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
          doe: this.payload.doe,
        } as Doe);
      }),
    );
  }

  get baseFileAttachmentDownloadUrl(): string {
    return `/aviation/tasks/${this.store.requestTaskId}/file-download/attachment`;
  }
}
