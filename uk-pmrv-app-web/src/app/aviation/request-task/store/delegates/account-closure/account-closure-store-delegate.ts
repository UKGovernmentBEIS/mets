import { Observable, tap } from 'rxjs';

import { getSaveRequestTaskActionTypeForRequestTaskType } from "@aviation/request-task/util";
import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchTaskReassignedBadRequest } from '@error/business-errors';
import { catchNotFoundRequest, ErrorCode } from '@error/not-found-error';
import { requestTaskReassignedError, taskNotFoundError } from '@shared/errors/request-task-error';
import produce from 'immer';

import { AviationAccountClosureSaveRequestTaskActionPayload, RequestTaskActionProcessDTO } from 'pmrv-api';

import { RequestTaskStore } from '../../request-task.store';
import { AccountClosure, AccountClosureTask, AccountClosureTaskPayload } from '../../request-task.types';
import { RequestTaskStoreDelegate } from '../store-delegate';

export class AccountClosureStoreDelegate implements RequestTaskStoreDelegate {
  static INITIAL_STATE: Partial<AccountClosure> = {};

  constructor(private store: RequestTaskStore, private readonly businessErrorService: BusinessErrorService) {}

  get payload(): AccountClosureTaskPayload | null {
    return this.store.getState().requestTaskItem?.requestTask?.payload as AccountClosureTaskPayload;
  }

  init() {
    if (!this.payload.aviationAccountClosure) {
      this.store.setPayload(
        produce(this.payload, (payload) => {
          payload.aviationAccountClosure = {} as any;
        }),
      );
    }

    return this;
  }

  setReason(payload: AccountClosureTask) {
    const state = this.store.getState();
    const newState = produce(state, (draft) => {
      (
        draft.requestTaskItem.requestTask.payload as AviationAccountClosureSaveRequestTaskActionPayload
      ).aviationAccountClosure = payload.aviationAccountClosure;
    });
    this.store.setState(newState);
  }

  saveAccountClosure(accountClosureTask: AccountClosureTask): Observable<any> {
    const {
      requestTaskItem: { requestTask },
    } = this.store.getState();

    const reqBody: RequestTaskActionProcessDTO = {
      requestTaskId: requestTask.id,
      requestTaskActionType: getSaveRequestTaskActionTypeForRequestTaskType(requestTask.type),
      requestTaskActionPayload: {
        ...accountClosureTask,
        payloadType: 'AVIATION_ACCOUNT_CLOSURE_SAVE_APPLICATION_PAYLOAD',
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
        this.setReason(accountClosureTask);
      }),
    );
  }
}
