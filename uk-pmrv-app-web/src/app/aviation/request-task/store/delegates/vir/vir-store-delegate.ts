import { Observable, tap } from 'rxjs';

import { RequestTaskStore, VirRequestTaskPayload } from '@aviation/request-task/store';
import { RequestTaskStoreDelegate } from '@aviation/request-task/store/delegates';
import { getSaveRequestTaskActionTypeForRequestTaskType } from '@aviation/request-task/util';
import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchTaskReassignedBadRequest } from '@error/business-errors';
import { catchNotFoundRequest, ErrorCode } from '@error/not-found-error';
import { requestTaskReassignedError, taskNotFoundError } from '@shared/errors/request-task-error';
import { TaskItemStatus } from '@shared/task-list/task-list.interface';
import produce from 'immer';

import {
  OperatorImprovementFollowUpResponse,
  OperatorImprovementResponse,
  RegulatorImprovementResponse,
  RequestTaskActionProcessDTO,
  RequestTaskDTO,
} from 'pmrv-api';

export class VirStoreDelegate implements RequestTaskStoreDelegate {
  static INITIAL_STATE: Partial<{ [key: string]: OperatorImprovementResponse }> = {
    operatorImprovementResponses: null,
  };

  constructor(private store: RequestTaskStore, private readonly businessErrorService: BusinessErrorService) {}

  get payload(): VirRequestTaskPayload | null {
    return this.store.getState().requestTaskItem?.requestTask?.payload as VirRequestTaskPayload;
  }

  get baseFileAttachmentDownloadUrl(): string {
    return `/aviation/tasks/${this.store.requestTaskId}/file-download/attachment`;
  }

  init() {
    if (this?.payload && !this.payload.operatorImprovementResponses) {
      this.store.setPayload(
        produce(this.payload, (payload) => {
          payload.operatorImprovementResponses = {} as any;
        }),
      );
    }

    if (
      this?.payload &&
      this.store.getState().requestTaskItem?.requestTask.type === 'AVIATION_VIR_APPLICATION_REVIEW' &&
      !this.payload.regulatorReviewResponse
    ) {
      this.store.setPayload(
        produce(this.payload, (payload) => {
          payload.regulatorReviewResponse = {
            regulatorImprovementResponses: {},
          } as any;
        }),
      );
    }

    if (
      this?.payload &&
      this.store.getState().requestTaskItem?.requestTask.type === 'AVIATION_VIR_RESPOND_TO_REGULATOR_COMMENTS' &&
      !this.payload.operatorImprovementFollowUpResponses
    ) {
      this.store.setPayload(
        produce(this.payload, (payload) => {
          payload.operatorImprovementFollowUpResponses = {} as any;
        }),
      );
    }

    return this;
  }

  setVirAttachments(attachments: { [key: string]: string }) {
    this.store.setPayload(
      produce(this.payload, (updatedPayload) => {
        updatedPayload.virAttachments = attachments;
      }),
    );
  }

  addVirAttachment(attachment: { [key: string]: string }) {
    this.setVirAttachments({
      ...this.payload.virAttachments,
      ...attachment,
    });
  }

  saveRespondVir(
    followUpResponse: OperatorImprovementFollowUpResponse,
    referenceCode: string,
    status: TaskItemStatus = 'complete',
  ): Observable<any> {
    const {
      requestTaskItem: { requestTask },
    } = this.store.getState();

    const payloadToUpdate = produce(this.payload, (draft) => {
      const operatorImprovementFollowUpResponses = draft.operatorImprovementFollowUpResponses;

      draft.operatorImprovementFollowUpResponses = operatorImprovementFollowUpResponses[referenceCode]
        ? Object.keys(operatorImprovementFollowUpResponses)
            .map((key) => ({
              [key]: key === referenceCode ? followUpResponse : operatorImprovementFollowUpResponses[key],
            }))
            .reduce((prev, cur) => ({ ...prev, ...cur }), {})
        : {
            ...operatorImprovementFollowUpResponses,
            [referenceCode]: followUpResponse,
          };

      draft.virRespondToRegulatorCommentsSectionsCompleted[referenceCode] = status === 'complete';
    });

    return this.sendProcessRequestTaskAction(requestTask, payloadToUpdate, referenceCode);
  }

  submitRespondVir(reference: string): Observable<any> {
    const {
      requestTaskItem: { requestTask },
    } = this.store.getState();

    return this.store.tasksService
      .processRequestTaskAction(
        this.constructSubmitActionReqBody(requestTask, 'AVIATION_VIR_SUBMIT_RESPOND_TO_REGULATOR_COMMENTS', reference),
      )
      .pipe(
        catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
          this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
        ),
        catchTaskReassignedBadRequest(() =>
          this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
        ),
      );
  }

  saveReviewVir(
    referenceCode: string | 'createSummary',
    status: TaskItemStatus = 'complete',
    regulatorImprovementResponse?: RegulatorImprovementResponse,
    reportSummary?: string,
  ): Observable<any> {
    const {
      requestTaskItem: { requestTask },
    } = this.store.getState();

    const payloadToUpdate = produce(this.payload, (draft) => {
      if (regulatorImprovementResponse) {
        const regulatorImprovementResponses = draft.regulatorReviewResponse.regulatorImprovementResponses;

        draft.regulatorReviewResponse.regulatorImprovementResponses = regulatorImprovementResponses[referenceCode]
          ? Object.keys(regulatorImprovementResponses)
              .map((key) => ({
                [key]: key === referenceCode ? regulatorImprovementResponse : regulatorImprovementResponses[key],
              }))
              .reduce((prev, cur) => ({ ...prev, ...cur }), {})
          : {
              ...regulatorImprovementResponses,
              [referenceCode]: regulatorImprovementResponse,
            };
      } else {
        draft.regulatorReviewResponse.reportSummary = reportSummary;
      }

      draft.reviewSectionsCompleted[referenceCode] = status === 'complete';
    });

    return this.sendProcessRequestTaskAction(requestTask, payloadToUpdate);
  }

  saveVir(
    operatorImprovementResponse: OperatorImprovementResponse,
    referenceCode: string,
    status: TaskItemStatus = 'complete',
  ): Observable<any> {
    const {
      requestTaskItem: { requestTask },
    } = this.store.getState();

    const payloadToUpdate = produce(this.payload, (draft) => {
      if (draft.operatorImprovementResponses[referenceCode]) {
        draft.operatorImprovementResponses[referenceCode] = operatorImprovementResponse;
      } else {
        draft.operatorImprovementResponses = {
          ...draft.operatorImprovementResponses,
          [referenceCode]: operatorImprovementResponse,
        };
      }
      draft.virSectionsCompleted[referenceCode] = status === 'complete';
    });

    return this.sendProcessRequestTaskAction(requestTask, payloadToUpdate);
  }

  submitVir(): Observable<any> {
    const {
      requestTaskItem: { requestTask },
    } = this.store.getState();

    return this.store.tasksService
      .processRequestTaskAction(this.constructSubmitActionReqBody(requestTask, 'AVIATION_VIR_SUBMIT_APPLICATION'))
      .pipe(
        catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
          this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
        ),
        catchTaskReassignedBadRequest(() =>
          this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
        ),
      );
  }

  private sendProcessRequestTaskAction(
    requestTask: RequestTaskDTO,
    payloadToUpdate: VirRequestTaskPayload,
    reference?: string,
  ): Observable<any> {
    return this.store.tasksService
      .processRequestTaskAction(
        this.constructSaveActionReqBody(
          requestTask,
          payloadToUpdate,
          getSaveRequestTaskActionTypeForRequestTaskType(requestTask.type),
          reference,
        ),
      )
      .pipe(
        catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
          this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
        ),
        catchTaskReassignedBadRequest(() =>
          this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
        ),
        tap(() => {
          this.store.setPayload({
            ...payloadToUpdate,
          } as VirRequestTaskPayload);
        }),
      );
  }

  private constructSaveActionReqBody(
    requestTask: RequestTaskDTO,
    payloadToUpdate: VirRequestTaskPayload,
    requestTaskActionType: RequestTaskActionProcessDTO['requestTaskActionType'],
    reference?: string,
  ): RequestTaskActionProcessDTO {
    switch (requestTaskActionType) {
      case 'AVIATION_VIR_SAVE_APPLICATION':
        return {
          requestTaskId: requestTask.id,
          requestTaskActionType: requestTaskActionType,
          requestTaskActionPayload: {
            operatorImprovementResponses: payloadToUpdate.operatorImprovementResponses,
            virSectionsCompleted: payloadToUpdate.virSectionsCompleted,
            payloadType: 'AVIATION_VIR_SAVE_APPLICATION_PAYLOAD',
          },
        };
      case 'AVIATION_VIR_SAVE_REVIEW':
        return {
          requestTaskId: requestTask.id,
          requestTaskActionType: requestTaskActionType,
          requestTaskActionPayload: {
            regulatorReviewResponse: payloadToUpdate.regulatorReviewResponse,
            reviewSectionsCompleted: payloadToUpdate.reviewSectionsCompleted,
            payloadType: 'AVIATION_VIR_SAVE_REVIEW_PAYLOAD',
          },
        };
      case 'AVIATION_VIR_SAVE_RESPOND_TO_REGULATOR_COMMENTS':
        return {
          requestTaskId: requestTask.id,
          requestTaskActionType: requestTaskActionType,
          requestTaskActionPayload: {
            reference: reference,
            operatorImprovementFollowUpResponse: payloadToUpdate.operatorImprovementFollowUpResponses[reference],
            virRespondToRegulatorCommentsSectionsCompleted:
              payloadToUpdate.virRespondToRegulatorCommentsSectionsCompleted,
            payloadType: 'AVIATION_VIR_SAVE_RESPOND_TO_REGULATOR_COMMENTS_PAYLOAD',
          },
        };
    }
  }

  private constructSubmitActionReqBody(
    requestTask: RequestTaskDTO,
    requestTaskActionType: RequestTaskActionProcessDTO['requestTaskActionType'],
    reference?: string,
  ): RequestTaskActionProcessDTO {
    switch (requestTaskActionType) {
      case 'AVIATION_VIR_SUBMIT_APPLICATION':
        return {
          requestTaskId: requestTask.id,
          requestTaskActionType: requestTaskActionType,
          requestTaskActionPayload: {
            payloadType: 'EMPTY_PAYLOAD',
          },
        };
      case 'AVIATION_VIR_SUBMIT_RESPOND_TO_REGULATOR_COMMENTS':
        return {
          requestTaskId: requestTask.id,
          requestTaskActionType: requestTaskActionType,
          requestTaskActionPayload: {
            reference: reference,
            virRespondToRegulatorCommentsSectionsCompleted: this.payload.virRespondToRegulatorCommentsSectionsCompleted,
            payloadType: 'AVIATION_VIR_SUBMIT_RESPOND_TO_REGULATOR_COMMENTS_PAYLOAD',
          },
        };
    }
  }
}
