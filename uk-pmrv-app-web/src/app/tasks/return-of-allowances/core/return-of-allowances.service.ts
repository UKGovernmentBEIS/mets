import { Injectable } from '@angular/core';

import { distinctUntilChanged, first, map, Observable, switchMap, tap } from 'rxjs';

import { catchTaskReassignedBadRequest } from '@error/business-errors';
import { catchNotFoundRequest, ErrorCode } from '@error/not-found-error';
import { requestTaskReassignedError, taskNotFoundError } from '@shared/errors/request-task-error';
import { TasksHelperService } from '@tasks/shared/services/tasks-helper.service';

import {
  RequestTaskActionPayload,
  ReturnOfAllowances,
  ReturnOfAllowancesApplicationSubmitRequestTaskPayload,
  ReturnOfAllowancesReturned,
  ReturnOfAllowancesReturnedApplicationSubmitRequestTaskPayload,
} from 'pmrv-api';

import { StatusKey } from './section-status';

@Injectable({ providedIn: 'root' })
export class ReturnOfAllowancesService extends TasksHelperService {
  get returnOfAllowances$(): Observable<ReturnOfAllowances> {
    return this.payload$.pipe(
      map((payload) => (payload as any)?.returnOfAllowances),
      distinctUntilChanged(),
    );
  }

  saveReturnOfAllowances(
    returnOfAllowances: Partial<ReturnOfAllowances>,
    statusValue?: boolean,
    statusKey?: StatusKey,
  ): Observable<any> {
    return this.store.pipe(
      first(),
      switchMap((state) => {
        const payload = state.requestTaskItem.requestTask
          .payload as ReturnOfAllowancesApplicationSubmitRequestTaskPayload;
        return this.tasksService
          .processRequestTaskAction({
            requestTaskActionType: 'RETURN_OF_ALLOWANCES_SAVE_APPLICATION',
            requestTaskId: state.requestTaskItem.requestTask.id,
            requestTaskActionPayload: {
              payloadType: 'RETURN_OF_ALLOWANCES_SAVE_APPLICATION_PAYLOAD',
              returnOfAllowances: {
                ...payload?.returnOfAllowances,
                ...returnOfAllowances,
              },
              sectionsCompleted: {
                ...(statusKey ? { [statusKey]: statusValue } : undefined),
              },
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
                      returnOfAllowances: {
                        ...payload?.returnOfAllowances,
                        ...returnOfAllowances,
                      },
                      sectionsCompleted: {
                        ...(statusKey ? { [statusKey]: statusValue } : undefined),
                      },
                    } as ReturnOfAllowancesApplicationSubmitRequestTaskPayload,
                  },
                },
              }),
            ),
          );
      }),
    );
  }

  saveSectionStatus(sectionCompleted: boolean): Observable<any> {
    return this.saveReturnOfAllowances(undefined, sectionCompleted, 'PROVIDE_DETAILS');
  }

  saveReturnOfAllowancesReturned(
    returnOfAllowancesReturned: Partial<ReturnOfAllowancesReturned>,
    statusValue?: boolean,
    statusKey?: StatusKey,
  ): Observable<any> {
    return this.store.pipe(
      first(),
      switchMap((state) => {
        const payload = state.requestTaskItem.requestTask
          .payload as ReturnOfAllowancesReturnedApplicationSubmitRequestTaskPayload;
        return this.tasksService
          .processRequestTaskAction({
            requestTaskActionType: 'RETURN_OF_ALLOWANCES_RETURNED_SAVE_APPLICATION',
            requestTaskId: state.requestTaskItem.requestTask.id,
            requestTaskActionPayload: {
              payloadType: 'RETURN_OF_ALLOWANCES_RETURNED_SAVE_APPLICATION_PAYLOAD',
              returnOfAllowancesReturned: {
                ...payload?.returnOfAllowancesReturned,
                ...returnOfAllowancesReturned,
              },
              sectionsCompleted: {
                ...(statusKey ? { [statusKey]: statusValue } : undefined),
              },
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
                      returnOfAllowancesReturned: {
                        ...payload?.returnOfAllowancesReturned,
                        ...returnOfAllowancesReturned,
                      },
                      sectionsCompleted: {
                        ...(statusKey ? { [statusKey]: statusValue } : undefined),
                      },
                    } as ReturnOfAllowancesReturnedApplicationSubmitRequestTaskPayload,
                  },
                },
              }),
            ),
          );
      }),
    );
  }

  saveReturnOfAllowancesReturnedSectionStatus(sectionCompleted: boolean): Observable<any> {
    return this.saveReturnOfAllowancesReturned(undefined, sectionCompleted, 'PROVIDE_RETURNED_DETAILS');
  }

  submitReturnOfAllowancesReturned(): Observable<any> {
    return this.store.pipe(
      first(),
      switchMap((state) => {
        return this.tasksService
          .processRequestTaskAction({
            requestTaskActionType: 'RETURN_OF_ALLOWANCES_RETURNED_SUBMIT_APPLICATION',
            requestTaskId: state.requestTaskItem.requestTask.id,
            requestTaskActionPayload: {
              payloadType: 'EMPTY_PAYLOAD',
            } as RequestTaskActionPayload,
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
}
