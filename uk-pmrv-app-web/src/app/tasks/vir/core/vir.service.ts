import { Injectable } from '@angular/core';

import { first, Observable, switchMap, tap } from 'rxjs';

import { catchTaskReassignedBadRequest } from '@error/business-errors';
import { catchNotFoundRequest, ErrorCode } from '@error/not-found-error';
import {
  requestTaskReassignedError,
  taskNotFoundError,
  taskSubmitNotFoundError,
} from '@shared/errors/request-task-error';
import { AttachedFile } from '@shared/types/attached-file.type';
import { TasksHelperService } from '@tasks/shared/services/tasks-helper.service';
import { CommonTasksState } from '@tasks/store/common-tasks.state';

import {
  VirApplicationRespondToRegulatorCommentsRequestTaskPayload,
  VirApplicationReviewRequestTaskPayload,
  VirApplicationSubmitRequestTaskPayload,
  VirSaveApplicationRequestTaskActionPayload,
  VirSaveRespondToRegulatorCommentsRequestTaskActionPayload,
  VirSaveReviewRequestTaskActionPayload,
  VirSubmitRespondToRegulatorCommentsRequestTaskActionPayload,
} from 'pmrv-api';

@Injectable({
  providedIn: 'root',
})
export class VirService extends TasksHelperService {
  get payload$(): Observable<
    | VirApplicationSubmitRequestTaskPayload
    | VirApplicationReviewRequestTaskPayload
    | VirApplicationRespondToRegulatorCommentsRequestTaskPayload
  > {
    return this.store.payload$;
  }

  get requestId() {
    return this.store.requestId;
  }

  postVirTaskSave(
    data: VirSaveApplicationRequestTaskActionPayload,
    attachments?: VirApplicationSubmitRequestTaskPayload['virAttachments'],
  ) {
    return this.store.pipe(
      first(),
      switchMap((state) => this.postVir(state, data, attachments)),
    );
  }

  postVirReviewTaskSave(data: VirSaveReviewRequestTaskActionPayload) {
    return this.store.pipe(
      first(),
      switchMap((state) => this.postVirReview(state, data)),
    );
  }

  postVirRespondTaskSave(data: VirSaveRespondToRegulatorCommentsRequestTaskActionPayload) {
    return this.store.pipe(
      first(),
      switchMap((state) => this.postVirRespond(state, data)),
    );
  }

  postSubmit() {
    return this.store.pipe(
      first(),
      switchMap((state) =>
        this.tasksService.processRequestTaskAction({
          requestTaskActionType: 'VIR_SUBMIT_APPLICATION',
          requestTaskId: state.requestTaskItem.requestTask.id,
          requestTaskActionPayload: {
            payloadType: 'EMPTY_PAYLOAD',
          },
        }),
      ),
      catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
        this.businessErrorService.showErrorForceNavigation(taskSubmitNotFoundError),
      ),
      catchTaskReassignedBadRequest(() =>
        this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
      ),
    );
  }

  postSubmitRespondToRegulatorComments(reference: string) {
    return this.store.pipe(
      first(),
      switchMap((state) =>
        this.tasksService.processRequestTaskAction({
          requestTaskActionType: 'VIR_SUBMIT_RESPOND_TO_REGULATOR_COMMENTS',
          requestTaskId: state.requestTaskItem.requestTask.id,
          requestTaskActionPayload: {
            payloadType: 'VIR_SUBMIT_RESPOND_TO_REGULATOR_COMMENTS_PAYLOAD',
            reference: reference,
            virRespondToRegulatorCommentsSectionsCompleted: (
              state.requestTaskItem.requestTask.payload as VirSubmitRespondToRegulatorCommentsRequestTaskActionPayload
            ).virRespondToRegulatorCommentsSectionsCompleted,
          },
        }),
      ),
      catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
        this.businessErrorService.showErrorForceNavigation(taskSubmitNotFoundError),
      ),
      catchTaskReassignedBadRequest(() =>
        this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
      ),
    );
  }

  getDownloadUrlFiles(files: string[]): AttachedFile[] {
    const attachments: { [key: string]: string } = (
      this.store.getValue().requestTaskItem.requestTask.payload as VirApplicationSubmitRequestTaskPayload
    )?.virAttachments;
    const url = this.getBaseFileDownloadUrl();
    return (
      files?.map((id) => ({
        downloadUrl: url + `${id}`,
        fileName: attachments[id],
      })) ?? []
    );
  }

  private postVir(
    state: CommonTasksState,
    data: VirSaveApplicationRequestTaskActionPayload,
    attachments?: VirApplicationSubmitRequestTaskPayload['virAttachments'],
  ) {
    return this.tasksService
      .processRequestTaskAction({
        requestTaskActionType: 'VIR_SAVE_APPLICATION',
        requestTaskId: state.requestTaskItem.requestTask.id,
        requestTaskActionPayload: {
          payloadType: 'VIR_SAVE_APPLICATION_PAYLOAD',
          operatorImprovementResponses: data.operatorImprovementResponses,
          virSectionsCompleted: data.virSectionsCompleted,
        } as VirSaveApplicationRequestTaskActionPayload,
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
                  operatorImprovementResponses: data.operatorImprovementResponses,
                  virSectionsCompleted: data.virSectionsCompleted,
                  virAttachments: {
                    ...(state.requestTaskItem.requestTask.payload as VirApplicationSubmitRequestTaskPayload)
                      ?.virAttachments,
                    ...attachments,
                  },
                } as VirApplicationSubmitRequestTaskPayload,
              },
            },
          });
        }),
      );
  }

  private postVirReview(state: CommonTasksState, data: VirSaveReviewRequestTaskActionPayload) {
    return this.tasksService
      .processRequestTaskAction({
        requestTaskActionType: 'VIR_SAVE_REVIEW',
        requestTaskId: state.requestTaskItem.requestTask.id,
        requestTaskActionPayload: {
          payloadType: 'VIR_SAVE_REVIEW_PAYLOAD',
          regulatorReviewResponse: data.regulatorReviewResponse,
          reviewSectionsCompleted: data.reviewSectionsCompleted,
        } as VirSaveReviewRequestTaskActionPayload,
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
                  regulatorReviewResponse: data.regulatorReviewResponse,
                  reviewSectionsCompleted: data.reviewSectionsCompleted,
                } as VirApplicationReviewRequestTaskPayload,
              },
            },
          });
        }),
      );
  }

  private postVirRespond(state: CommonTasksState, data: VirSaveRespondToRegulatorCommentsRequestTaskActionPayload) {
    return this.tasksService
      .processRequestTaskAction({
        requestTaskActionType: 'VIR_SAVE_RESPOND_TO_REGULATOR_COMMENTS',
        requestTaskId: state.requestTaskItem.requestTask.id,
        requestTaskActionPayload: {
          payloadType: 'VIR_SAVE_RESPOND_TO_REGULATOR_COMMENTS_PAYLOAD',
          reference: data.reference,
          operatorImprovementFollowUpResponse: data.operatorImprovementFollowUpResponse,
          virRespondToRegulatorCommentsSectionsCompleted: data.virRespondToRegulatorCommentsSectionsCompleted,
        } as VirSaveRespondToRegulatorCommentsRequestTaskActionPayload,
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
                  operatorImprovementFollowUpResponses: {
                    ...(
                      state.requestTaskItem.requestTask
                        .payload as VirApplicationRespondToRegulatorCommentsRequestTaskPayload
                    )?.operatorImprovementFollowUpResponses,
                    [data.reference]: data.operatorImprovementFollowUpResponse,
                  },
                  virRespondToRegulatorCommentsSectionsCompleted: data.virRespondToRegulatorCommentsSectionsCompleted,
                } as VirApplicationRespondToRegulatorCommentsRequestTaskPayload,
              },
            },
          });
        }),
      );
  }
}
