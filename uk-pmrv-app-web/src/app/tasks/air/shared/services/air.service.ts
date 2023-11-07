import { Injectable } from '@angular/core';

import { combineLatest, first, map, Observable, switchMap, tap } from 'rxjs';

import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchTaskReassignedBadRequest } from '@error/business-errors';
import { catchNotFoundRequest, ErrorCode } from '@error/not-found-error';
import {
  requestTaskReassignedError,
  taskNotFoundError,
  taskSubmitNotFoundError,
} from '@shared/errors/request-task-error';
import { ItemNamePipe } from '@shared/pipes/item-name.pipe';
import { AttachedFile } from '@shared/types/attached-file.type';
import { CommonTasksState } from '@tasks/store/common-tasks.state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import {
  AirApplicationRespondToRegulatorCommentsRequestTaskPayload,
  AirApplicationReviewRequestTaskPayload,
  AirApplicationSubmitRequestTaskPayload,
  AirRequestMetadata,
  AirSaveApplicationRequestTaskActionPayload,
  AirSaveRespondToRegulatorCommentsRequestTaskActionPayload,
  AirSaveReviewRequestTaskActionPayload,
  AirSubmitRespondToRegulatorCommentsRequestTaskActionPayload,
  RequestMetadata,
  RequestTaskItemDTO,
  TasksService,
} from 'pmrv-api';

@Injectable()
export class AirService {
  constructor(
    private readonly store: CommonTasksStore,
    private readonly tasksService: TasksService,
    private readonly businessErrorService: BusinessErrorService,
    private readonly itemNamePipe: ItemNamePipe,
  ) {}

  get payload$(): Observable<AirApplicationSubmitRequestTaskPayload> {
    return this.store.payload$ as Observable<AirApplicationSubmitRequestTaskPayload>;
  }

  get requestTaskItem$(): Observable<RequestTaskItemDTO> {
    return this.store.requestTaskItem$;
  }

  get requestId() {
    return this.store.requestId;
  }

  get isEditable$(): Observable<boolean> {
    return this.store.isEditable$;
  }

  get requestMetadata$(): Observable<RequestMetadata> {
    return this.store.requestMetadata$;
  }

  get title$(): Observable<string> {
    return combineLatest([this.requestTaskItem$, this.requestMetadata$]).pipe(
      map(([requestTaskItem, metadata]) =>
        this.itemNamePipe.transform(requestTaskItem?.requestTask?.type, (metadata as AirRequestMetadata)?.year),
      ),
    );
  }

  get daysRemaining$() {
    return this.store.requestTaskItem$.pipe(map((task) => task.requestTask.daysRemaining));
  }

  postAirTaskSave(
    data: AirSaveApplicationRequestTaskActionPayload,
    attachments?: AirApplicationSubmitRequestTaskPayload['airAttachments'],
  ) {
    return this.store.pipe(
      first(),
      switchMap((state) => this.postAir(state, data, attachments)),
    );
  }

  postAirReviewTaskSave(
    data: AirSaveReviewRequestTaskActionPayload,
    reviewAttachments?: AirApplicationReviewRequestTaskPayload['reviewAttachments'],
  ) {
    return this.store.pipe(
      first(),
      switchMap((state) => this.postAirReview(state, data, reviewAttachments)),
    );
  }

  postAirRespondTaskSave(
    data: AirSaveRespondToRegulatorCommentsRequestTaskActionPayload,
    attachments?: AirApplicationRespondToRegulatorCommentsRequestTaskPayload['airAttachments'],
  ) {
    return this.store.pipe(
      first(),
      switchMap((state) => this.postAirRespond(state, data, attachments)),
    );
  }

  postSubmit() {
    return this.store.pipe(
      first(),
      switchMap((state) =>
        this.tasksService.processRequestTaskAction({
          requestTaskActionType: 'AIR_SUBMIT_APPLICATION',
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
          requestTaskActionType: 'AIR_SUBMIT_RESPOND_TO_REGULATOR_COMMENTS',
          requestTaskId: state.requestTaskItem.requestTask.id,
          requestTaskActionPayload: {
            payloadType: 'AIR_SUBMIT_RESPOND_TO_REGULATOR_COMMENTS_PAYLOAD',
            reference: reference,
            airRespondToRegulatorCommentsSectionsCompleted: (
              state.requestTaskItem.requestTask.payload as AirSubmitRespondToRegulatorCommentsRequestTaskActionPayload
            ).airRespondToRegulatorCommentsSectionsCompleted,
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

  createBaseFileDownloadUrl(): string {
    const requestTaskId = this.store.requestTaskId;
    return `/tasks/${requestTaskId}/file-download/`;
  }

  getOperatorDownloadUrlFiles(files: string[]): AttachedFile[] {
    const attachments: { [key: string]: string } = (
      this.store.getValue().requestTaskItem.requestTask.payload as
        | AirApplicationRespondToRegulatorCommentsRequestTaskPayload
        | AirApplicationSubmitRequestTaskPayload
    )?.airAttachments;
    const url = this.createBaseFileDownloadUrl();
    return (
      files?.map((id) => ({
        downloadUrl: url + `${id}`,
        fileName: attachments[id],
      })) ?? []
    );
  }

  getRegulatorDownloadUrlFiles(files: string[]): AttachedFile[] {
    const attachments: { [key: string]: string } = (
      this.store.getValue().requestTaskItem.requestTask.payload as AirApplicationReviewRequestTaskPayload
    )?.reviewAttachments;
    const url = this.createBaseFileDownloadUrl();
    return (
      files?.map((id) => ({
        downloadUrl: url + `${id}`,
        fileName: attachments[id],
      })) ?? []
    );
  }

  private postAir(
    state: CommonTasksState,
    data: AirSaveApplicationRequestTaskActionPayload,
    attachments?: AirApplicationSubmitRequestTaskPayload['airAttachments'],
  ) {
    return this.tasksService
      .processRequestTaskAction({
        requestTaskActionType: 'AIR_SAVE_APPLICATION',
        requestTaskId: state.requestTaskItem.requestTask.id,
        requestTaskActionPayload: {
          payloadType: 'AIR_SAVE_APPLICATION_PAYLOAD',
          operatorImprovementResponses: data.operatorImprovementResponses,
          airSectionsCompleted: data.airSectionsCompleted,
        } as AirSaveApplicationRequestTaskActionPayload,
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
                  airSectionsCompleted: data.airSectionsCompleted,
                  airAttachments: {
                    ...(state.requestTaskItem.requestTask.payload as AirApplicationSubmitRequestTaskPayload)
                      ?.airAttachments,
                    ...attachments,
                  },
                } as AirApplicationSubmitRequestTaskPayload,
              },
            },
          });
        }),
      );
  }

  private postAirReview(
    state: CommonTasksState,
    data: AirSaveReviewRequestTaskActionPayload,
    reviewAttachments?: AirApplicationReviewRequestTaskPayload['reviewAttachments'],
  ) {
    return this.tasksService
      .processRequestTaskAction({
        requestTaskActionType: 'AIR_SAVE_REVIEW',
        requestTaskId: state.requestTaskItem.requestTask.id,
        requestTaskActionPayload: {
          payloadType: 'AIR_SAVE_REVIEW_PAYLOAD',
          regulatorReviewResponse: data.regulatorReviewResponse,
          reviewSectionsCompleted: data.reviewSectionsCompleted,
        } as AirSaveReviewRequestTaskActionPayload,
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
                  reviewAttachments: {
                    ...(state.requestTaskItem.requestTask.payload as AirApplicationReviewRequestTaskPayload)
                      ?.reviewAttachments,
                    ...reviewAttachments,
                  },
                } as AirApplicationReviewRequestTaskPayload,
              },
            },
          });
        }),
      );
  }

  private postAirRespond(
    state: CommonTasksState,
    data: AirSaveRespondToRegulatorCommentsRequestTaskActionPayload,
    attachments?: AirApplicationRespondToRegulatorCommentsRequestTaskPayload['airAttachments'],
  ) {
    return this.tasksService
      .processRequestTaskAction({
        requestTaskActionType: 'AIR_SAVE_RESPOND_TO_REGULATOR_COMMENTS',
        requestTaskId: state.requestTaskItem.requestTask.id,
        requestTaskActionPayload: {
          payloadType: 'AIR_SAVE_RESPOND_TO_REGULATOR_COMMENTS_PAYLOAD',
          reference: data.reference,
          operatorImprovementFollowUpResponse: data.operatorImprovementFollowUpResponse,
          airRespondToRegulatorCommentsSectionsCompleted: data.airRespondToRegulatorCommentsSectionsCompleted,
        } as AirSaveRespondToRegulatorCommentsRequestTaskActionPayload,
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
                        .payload as AirApplicationRespondToRegulatorCommentsRequestTaskPayload
                    )?.operatorImprovementFollowUpResponses,
                    [data.reference]: data.operatorImprovementFollowUpResponse,
                  },
                  airRespondToRegulatorCommentsSectionsCompleted: data.airRespondToRegulatorCommentsSectionsCompleted,
                  airAttachments: {
                    ...(state.requestTaskItem.requestTask.payload as AirApplicationSubmitRequestTaskPayload)
                      ?.airAttachments,
                    ...attachments,
                  },
                } as AirApplicationRespondToRegulatorCommentsRequestTaskPayload,
              },
            },
          });
        }),
      );
  }
}
