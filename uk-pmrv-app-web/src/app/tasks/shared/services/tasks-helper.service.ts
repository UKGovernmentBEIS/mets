import { Injectable } from '@angular/core';

import { first, map, Observable, switchMap, tap } from 'rxjs';

import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchTaskReassignedBadRequest } from '@error/business-errors';
import { catchNotFoundRequest, ErrorCode } from '@error/not-found-error';
import { requestTaskReassignedError, taskNotFoundError } from '@shared/errors/request-task-error';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import {
  ALRApplicationRegulatorReviewSubmitRequestTaskPayload,
  ALRReviewDecision,
  NERApplicationRegulatorReviewSubmitRequestTaskPayload,
  NERReviewDecision,
  RequestMetadata,
  RequestTaskActionPayload,
  RequestTaskActionProcessDTO,
  RequestTaskDTO,
  RequestTaskItemDTO,
  TasksService,
} from 'pmrv-api';

export type ReviewDecisionType = NERReviewDecision['reviewDataType'] | ALRReviewDecision['reviewDataType'];
export type ReviewPayload =
  | NERApplicationRegulatorReviewSubmitRequestTaskPayload
  | ALRApplicationRegulatorReviewSubmitRequestTaskPayload;

@Injectable()
export abstract class TasksHelperService {
  constructor(
    protected readonly store: CommonTasksStore,
    protected readonly tasksService: TasksService,
    protected readonly businessErrorService: BusinessErrorService,
  ) {}

  get requestTaskItem$(): Observable<RequestTaskItemDTO> {
    return this.store.requestTaskItem$;
  }

  get requestTaskType$(): Observable<RequestTaskDTO['type']> {
    return this.requestTaskItem$.pipe(map((requestTaskItem) => requestTaskItem?.requestTask?.type));
  }

  get payload$(): Observable<any> {
    return this.store.payload$;
  }

  get requestMetadata$(): Observable<RequestMetadata> {
    return this.store.requestMetadata$;
  }

  get isEditable$(): Observable<boolean> {
    return this.store.isEditable$;
  }

  get daysRemaining$() {
    return this.store.requestTaskItem$.pipe(map((task) => task?.requestTask?.daysRemaining));
  }

  getPayload(): Observable<any> {
    return this.store.payload$;
  }

  getBaseFileDownloadUrl() {
    const requestTaskId = this.store.requestTaskId;
    return `/tasks/${requestTaskId}/file-download/`;
  }

  postGroupReviewDecision(
    value: any,
    dataType: ReviewDecisionType,
    groupKey: string,
    actionType: RequestTaskActionProcessDTO['requestTaskActionType'],
    attachments?: { uuid: string; file: File }[],
  ) {
    return this.store.pipe(
      first(),
      switchMap((state) =>
        this.tasksService.processRequestTaskAction({
          requestTaskActionType: actionType,
          requestTaskId: state.requestTaskItem.requestTask.id,
          requestTaskActionPayload: {
            payloadType: `${actionType}_PAYLOAD`,
            group: groupKey,
            decision: {
              ...value,
              reviewDataType: dataType,
            },
            regulatorReviewSectionsCompleted: {
              ...(state.requestTaskItem.requestTask.payload as ReviewPayload)?.regulatorReviewSectionsCompleted,
              ...{ [groupKey]: true },
            },
          } as RequestTaskActionPayload,
        }),
      ),
      catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
        this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
      ),
      catchTaskReassignedBadRequest(() =>
        this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
      ),
      tap(() => {
        const state = this.store.getState();
        this.store.setState({
          ...state,
          requestTaskItem: {
            ...state.requestTaskItem,
            requestTask: {
              ...state.requestTaskItem.requestTask,
              payload: {
                ...state.requestTaskItem.requestTask.payload,
                regulatorReviewGroupDecisions: {
                  ...(
                    state.requestTaskItem.requestTask.payload as NERApplicationRegulatorReviewSubmitRequestTaskPayload
                  ).regulatorReviewGroupDecisions,
                  [groupKey]: {
                    reviewDataType: dataType,
                    ...value,
                  },
                },
                regulatorReviewAttachments: {
                  ...(
                    state.requestTaskItem.requestTask.payload as NERApplicationRegulatorReviewSubmitRequestTaskPayload
                  ).regulatorReviewAttachments,
                  ...attachments?.reduce((result, item) => ({ ...result, [item.uuid]: item.file.name }), {}),
                },
                regulatorReviewSectionsCompleted: {
                  ...(
                    state.requestTaskItem.requestTask.payload as NERApplicationRegulatorReviewSubmitRequestTaskPayload
                  )?.regulatorReviewSectionsCompleted,
                  ...{ [groupKey]: true },
                },
              } as NERApplicationRegulatorReviewSubmitRequestTaskPayload,
            },
          },
        });
      }),
    );
  }
}
