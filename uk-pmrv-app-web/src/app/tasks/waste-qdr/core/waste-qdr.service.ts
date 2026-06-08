import { Injectable, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';

import { first, map, Observable, switchMap, tap } from 'rxjs';

import { catchTaskReassignedBadRequest } from '@error/business-errors';
import { catchNotFoundRequest, ErrorCode } from '@error/not-found-error';
import { requestTaskReassignedError, taskNotFoundError } from '@shared/errors/request-task-error';
import { AttachedFile } from '@shared/types/attached-file.type';
import { TasksHelperService } from '@tasks/shared/services/tasks-helper.service';
import { CommonTasksState } from '@tasks/store/common-tasks.state';

import {
  RequestTaskActionPayload,
  RequestTaskActionProcessDTO,
  WasteQDRApplicationRegulatorReviewSubmitRequestTaskPayload,
  WasteQDRApplicationSubmitRequestTaskPayload,
  WasteQDRRequestMetaData,
} from 'pmrv-api';

@Injectable({ providedIn: 'root' })
export class WasteQdrService extends TasksHelperService {
  get payload(): Signal<
    WasteQDRApplicationSubmitRequestTaskPayload | WasteQDRApplicationRegulatorReviewSubmitRequestTaskPayload
  > {
    return toSignal(this.store.payload$);
  }

  get requestTaskType() {
    return toSignal(this.store.requestTaskType$);
  }

  get requestTaskItem() {
    return toSignal(this.store.requestTaskItem$);
  }

  get requestMetadata() {
    return toSignal(this.requestMetadata$ as Observable<WasteQDRRequestMetaData>);
  }

  get daysRemaining() {
    return toSignal(this.daysRemaining$);
  }

  get isEditable() {
    return toSignal(this.isEditable$);
  }

  get requestId() {
    return this.store.requestId;
  }

  get requestTaskId() {
    return this.store.requestTaskId;
  }

  getDownloadUrlFile(file: string): AttachedFile {
    const attachments: { [key: string]: string } =
      (this.store.getValue().requestTaskItem.requestTask.payload as WasteQDRApplicationSubmitRequestTaskPayload)
        ?.wasteQDRAttachments || {};
    const url = this.getBaseFileDownloadUrl();

    return file
      ? {
          downloadUrl: url + `${file}`,
          fileName: attachments[file],
        }
      : null;
  }

  getDownloadUrlFiles(files: string[]): AttachedFile[] {
    const attachments: { [key: string]: string } =
      (this.store.getValue().requestTaskItem.requestTask.payload as WasteQDRApplicationSubmitRequestTaskPayload)
        ?.wasteQDRAttachments || {};
    const url = this.getBaseFileDownloadUrl();

    return (
      files?.map((id) => ({
        downloadUrl: url + `${id}`,
        fileName: attachments[id],
      })) ?? []
    );
  }

  getRegulatorDownloadUrlFiles(files: string[]): AttachedFile[] {
    const url = this.getBaseFileDownloadUrl();
    const regulatorReviewAttachments: { [key: string]: string } = (
      this.store.getValue().requestTaskItem.requestTask
        .payload as WasteQDRApplicationRegulatorReviewSubmitRequestTaskPayload
    )?.regulatorReviewAttachments;

    return (
      files?.map((id) => ({
        downloadUrl: url + `${id}`,
        fileName: regulatorReviewAttachments[id],
      })) ?? []
    );
  }

  postTaskSave(value: any, attachments?: { [key: string]: string }, statusValue?: boolean, statusKey?: string) {
    const state = this.store.getState();
    const requestTaskType = state.requestTaskItem.requestTask.type;

    let actionType: RequestTaskActionProcessDTO['requestTaskActionType'];

    switch (requestTaskType) {
      case 'WASTE_QDR_APPLICATION_AMENDS_SUBMIT':
        actionType = 'WASTE_QDR_APPLICATION_AMENDS_SAVE';
        break;

      default:
        actionType = 'WASTE_QDR_SAVE_APPLICATION';
        break;
    }

    return this.store.pipe(
      first(),
      switchMap((state) => {
        const postWasteQdrState = {
          ...state,
          requestTaskItem: {
            ...state.requestTaskItem,
            requestTask: {
              ...state.requestTaskItem.requestTask,
              payload: {
                ...state.requestTaskItem.requestTask.payload,
                qdr: {
                  ...(state.requestTaskItem.requestTask.payload as WasteQDRApplicationSubmitRequestTaskPayload).qdr,
                  ...value,
                },
                wasteQDRAttachments: {
                  ...(state.requestTaskItem.requestTask.payload as WasteQDRApplicationSubmitRequestTaskPayload)
                    ?.wasteQDRAttachments,
                  ...attachments,
                },
                wasteQDRSectionsCompleted: {
                  ...(state.requestTaskItem.requestTask.payload as WasteQDRApplicationSubmitRequestTaskPayload)
                    ?.wasteQDRSectionsCompleted,
                  ...(statusKey ? { [statusKey]: statusValue } : undefined),
                },
              } as WasteQDRApplicationSubmitRequestTaskPayload,
            },
          },
        };

        return this.postWasteQdr(postWasteQdrState, actionType);
      }),
    );
  }

  postWasteQdr(state: CommonTasksState, actionType: RequestTaskActionProcessDTO['requestTaskActionType']) {
    const payload = state.requestTaskItem.requestTask.payload as WasteQDRApplicationSubmitRequestTaskPayload;
    return this.tasksService
      .processRequestTaskAction({
        requestTaskActionType: actionType,
        requestTaskId: state.requestTaskItem.requestTask.id,
        requestTaskActionPayload: this.createRequestTaskActionPayload(actionType, payload),
      })
      .pipe(
        catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
          this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
        ),
        catchTaskReassignedBadRequest(() =>
          this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
        ),
        tap(() => this.store.setState(state)),
      );
  }

  postWasteQdrSubmit() {
    const state = this.store.getState();
    const requestTaskType = state.requestTaskItem.requestTask.type;
    const payload = state.requestTaskItem.requestTask.payload;

    let actionType: RequestTaskActionProcessDTO['requestTaskActionType'];

    switch (requestTaskType) {
      case 'WASTE_QDR_APPLICATION_AMENDS_SUBMIT':
        actionType = 'WASTE_QDR_APPLICATION_AMENDS_SUBMIT_TO_REGULATOR';
        break;
      case 'WASTE_QDR_APPLICATION_REGULATOR_REVIEW_SUBMIT':
        actionType = 'WASTE_QDR_REGULATOR_REVIEW_RETURN_FOR_AMENDS';
        break;

      default:
        actionType = 'WASTE_QDR_SUBMIT_TO_REGULATOR';
        break;
    }
    return this.store.pipe(
      first(),
      switchMap((state) =>
        this.tasksService.processRequestTaskAction({
          requestTaskActionType: actionType,
          requestTaskId: state.requestTaskItem.requestTask.id,
          requestTaskActionPayload: this.createRequestTaskActionPayload(actionType, payload),
        }),
      ),
      catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
        this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
      ),
      catchTaskReassignedBadRequest(() =>
        this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
      ),
    );
  }

  postDecisionReview(value: any, statusKey, attachments?: { uuid: string; file: File }[]) {
    return this.store.pipe(
      first(),
      switchMap((state) =>
        this.tasksService.processRequestTaskAction({
          requestTaskActionType: 'WASTE_QDR_SAVE_REVIEW_GROUP_DECISION',
          requestTaskId: state.requestTaskItem.requestTask.id,
          requestTaskActionPayload: {
            payloadType: 'WASTE_QDR_SAVE_REVIEW_GROUP_DECISION_PAYLOAD',
            reviewDecision: {
              ...value,
            },
            regulatorReviewSectionsCompleted: {
              ...(
                state.requestTaskItem.requestTask.payload as WasteQDRApplicationRegulatorReviewSubmitRequestTaskPayload
              )?.regulatorReviewSectionsCompleted,
              [statusKey]: true,
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
                reviewDecision: {
                  ...(
                    state.requestTaskItem.requestTask
                      .payload as WasteQDRApplicationRegulatorReviewSubmitRequestTaskPayload
                  ).reviewDecision,
                  ...value,
                },
                regulatorReviewAttachments: {
                  ...(
                    state.requestTaskItem.requestTask
                      .payload as WasteQDRApplicationRegulatorReviewSubmitRequestTaskPayload
                  ).regulatorReviewAttachments,
                  ...attachments?.reduce((result, item) => ({ ...result, [item.uuid]: item.file.name }), {}),
                },
                regulatorReviewSectionsCompleted: {
                  ...(
                    state.requestTaskItem.requestTask
                      .payload as WasteQDRApplicationRegulatorReviewSubmitRequestTaskPayload
                  )?.regulatorReviewSectionsCompleted,
                  [statusKey]: true,
                },
              } as WasteQDRApplicationRegulatorReviewSubmitRequestTaskPayload,
            },
          },
        });
      }),
    );
  }

  private createRequestTaskActionPayload(
    actionType: RequestTaskActionProcessDTO['requestTaskActionType'],
    payload?: any,
  ) {
    switch (actionType) {
      case 'WASTE_QDR_SAVE_APPLICATION':
        return {
          payloadType: 'WASTE_QDR_APPLICATION_SAVE_PAYLOAD',
          qdr: payload.qdr,
          wasteQDRSectionsCompleted: payload.wasteQDRSectionsCompleted,
        } as RequestTaskActionPayload;
      case 'WASTE_QDR_APPLICATION_AMENDS_SAVE':
        return {
          payloadType: 'WASTE_QDR_APPLICATION_AMENDS_SAVE_PAYLOAD',
          qdr: payload.qdr,
          wasteQDRSectionsCompleted: payload.wasteQDRSectionsCompleted,
          regulatorReviewSectionsCompleted: payload.regulatorReviewSectionsCompleted,
        } as RequestTaskActionPayload;
      case 'WASTE_QDR_APPLICATION_AMENDS_SUBMIT_TO_REGULATOR':
        return {
          payloadType: 'WASTE_QDR_APPLICATION_AMENDS_SUBMIT_TO_REGULATOR_PAYLOAD',
          wasteQDRSectionsCompleted: { ...payload.wasteQDRSectionsCompleted, changesRequested: undefined },
        } as RequestTaskActionPayload;

      default:
        return {
          payloadType: 'EMPTY_PAYLOAD',
        } as RequestTaskActionPayload;
    }
  }

  get competentAuthority$() {
    return this.store.requestInfo$.pipe(map((info) => info.competentAuthority));
  }

  get competentAuthority() {
    return toSignal(this.competentAuthority$);
  }
}
