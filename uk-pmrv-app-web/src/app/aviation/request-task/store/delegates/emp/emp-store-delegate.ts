import { Observable, tap } from 'rxjs';

import { EmpReviewGroup, empReviewGroupMap } from '@aviation/request-task/emp/shared/util/emp.util';
import { RequestTaskStoreDelegate } from '@aviation/request-task/store/delegates/store-delegate';
import { RequestTaskStore } from '@aviation/request-task/store/request-task.store';
import { EmpRequestTaskPayload, EmpTaskKey } from '@aviation/request-task/store/request-task.types';
import { CorsiaRequestTypes } from '@aviation/request-task/util';
import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchTaskReassignedBadRequest } from '@error/business-errors';
import { catchNotFoundRequest, ErrorCode } from '@error/not-found-error';
import { requestTaskReassignedError, taskNotFoundError } from '@shared/errors/request-task-error';
import produce from 'immer';

import {
  EmpAbbreviations,
  EmpAcceptedVariationDecisionDetails,
  EmpAdditionalDocuments,
  EmpBlockHourMethodProcedures,
  EmpBlockOnBlockOffMethodProcedures,
  EmpFuelUpliftMethodProcedures,
  EmpIssuanceDetermination,
  EmpMethodAProcedures,
  EmpMethodBProcedures,
  EmpVariationReviewDecision,
  RequestTaskActionProcessDTO,
  RequestTaskDTO,
} from 'pmrv-api';

export abstract class EmpStoreDelegate implements RequestTaskStoreDelegate {
  constructor(protected store: RequestTaskStore, protected readonly businessErrorService: BusinessErrorService) {}

  abstract init(): this;

  abstract get payload(): EmpRequestTaskPayload;

  setEmpAttachments(attachments: { [key: string]: string }) {
    this.store.setPayload(
      produce<EmpRequestTaskPayload>(this.payload, (updatedPayload) => {
        updatedPayload.empAttachments = attachments;
      }),
    );
  }

  addEmpAttachment(attachment: { [key: string]: string }) {
    this.setEmpAttachments({
      ...this.payload.empAttachments,
      ...attachment,
    });
  }

  setReviewAttachments(attachments: { [key: string]: string }) {
    this.store.setPayload(
      produce(this.payload, (updatedPayload) => {
        updatedPayload.reviewAttachments = attachments;
      }),
    );
  }

  addReviewAttachment(attachment: { [key: string]: string }) {
    this.setReviewAttachments({
      ...this.payload.reviewAttachments,
      ...attachment,
    });
  }

  setEmpSectionCompletionStatus(task: EmpTaskKey, completion: [boolean]) {
    const state = this.store.getState();
    const newState = produce(state, (draft) => {
      (draft.requestTaskItem.requestTask.payload as EmpRequestTaskPayload).empSectionsCompleted[task] = completion;
    });
    this.store.setState(newState);
  }

  setAbbreviations(abbreviations: EmpAbbreviations) {
    const state = this.store.getState();
    const newState = produce(state, (draft) => {
      (draft.requestTaskItem.requestTask.payload as EmpRequestTaskPayload).emissionsMonitoringPlan.abbreviations =
        abbreviations;
    });
    this.store.setState(newState);
  }

  setMethodAProcedures(empMethodAProcedures: EmpMethodAProcedures) {
    const state = this.store.getState();
    const newState = produce(state, (draft) => {
      (draft.requestTaskItem.requestTask.payload as EmpRequestTaskPayload).emissionsMonitoringPlan.methodAProcedures =
        empMethodAProcedures;
    });
    this.store.setState(newState);
  }

  setMethodBProcedures(empMethodBProcedures: EmpMethodBProcedures) {
    const state = this.store.getState();
    const newState = produce(state, (draft) => {
      (draft.requestTaskItem.requestTask.payload as EmpRequestTaskPayload).emissionsMonitoringPlan.methodBProcedures =
        empMethodBProcedures;
    });
    this.store.setState(newState);
  }

  setblockOnBlockOffMethodProcedures(blockOnBlockOffMethodProcedures: EmpBlockOnBlockOffMethodProcedures) {
    const state = this.store.getState();
    const newState = produce(state, (draft) => {
      (
        draft.requestTaskItem.requestTask.payload as EmpRequestTaskPayload
      ).emissionsMonitoringPlan.blockOnBlockOffMethodProcedures = blockOnBlockOffMethodProcedures;
    });
    this.store.setState(newState);
  }

  setFuelUpliftMethodProcedures(fuelUpliftMethodProcedures: EmpFuelUpliftMethodProcedures) {
    const state = this.store.getState();
    const newState = produce(state, (draft) => {
      (
        draft.requestTaskItem.requestTask.payload as EmpRequestTaskPayload
      ).emissionsMonitoringPlan.fuelUpliftMethodProcedures = fuelUpliftMethodProcedures;
    });
    this.store.setState(newState);
  }

  setBlockHourMethodProcedures(blockHourMethodProcedures: EmpBlockHourMethodProcedures) {
    const state = this.store.getState();
    const newState = produce(state, (draft) => {
      (
        draft.requestTaskItem.requestTask.payload as EmpRequestTaskPayload
      ).emissionsMonitoringPlan.blockHourMethodProcedures = blockHourMethodProcedures;
    });
    this.store.setState(newState);
  }

  setAdditionalDocuments(additionalDocuments: EmpAdditionalDocuments) {
    this.store.setPayload(
      produce<EmpRequestTaskPayload>(this.payload as EmpRequestTaskPayload, (updatedPayload) => {
        updatedPayload.emissionsMonitoringPlan.additionalDocuments = additionalDocuments;
      }),
    );
  }

  setOverallDecision(determination: EmpIssuanceDetermination) {
    const state = this.store.getState();

    const newState = produce(state, (draft) => {
      (draft.requestTaskItem.requestTask.payload as EmpRequestTaskPayload).determination = determination;
    });

    this.store.setState(newState);
  }

  saveEmpVariationRegLedDecision(
    decision: EmpAcceptedVariationDecisionDetails,
    taskKey: EmpTaskKey,
    requestTaskType: RequestTaskDTO['type'],
  ): Observable<any> {
    const {
      requestTaskItem: { requestTask },
    } = this.store.getState();

    const groupKey = empReviewGroupMap[taskKey] as EmpReviewGroup;
    const payloadToUpdate = produce(this.payload, (draft) => {
      (draft.reviewGroupDecisions as any) = {
        ...draft.reviewGroupDecisions,
        [groupKey]: decision,
      };

      draft.empSectionsCompleted[taskKey] = [true];
    });

    return this.store.tasksService
      .processRequestTaskAction({
        requestTaskId: requestTask.id,
        requestTaskActionType:
          requestTaskType === 'EMP_VARIATION_UKETS_REGULATOR_LED_APPLICATION_SUBMIT'
            ? 'EMP_VARIATION_UKETS_SAVE_REVIEW_GROUP_DECISION_REGULATOR_LED'
            : 'EMP_VARIATION_CORSIA_SAVE_REVIEW_GROUP_DECISION_REGULATOR_LED',
        requestTaskActionPayload: {
          group: groupKey,
          decision: payloadToUpdate.reviewGroupDecisions[groupKey],
          empSectionsCompleted: payloadToUpdate.empSectionsCompleted,
          payloadType:
            requestTaskType === 'EMP_VARIATION_UKETS_REGULATOR_LED_APPLICATION_SUBMIT'
              ? 'EMP_VARIATION_UKETS_SAVE_REVIEW_GROUP_DECISION_REGULATOR_LED_PAYLOAD'
              : 'EMP_VARIATION_CORSIA_SAVE_REVIEW_GROUP_DECISION_REGULATOR_LED_PAYLOAD',
        },
      })
      .pipe(
        catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
          this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
        ),
        catchTaskReassignedBadRequest(() =>
          this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
        ),
        tap(() =>
          this.store.setPayload({
            ...payloadToUpdate,
            serviceContactDetails: this.payload.serviceContactDetails,
          } as EmpRequestTaskPayload),
        ),
      );
  }

  saveEmpVariationReviewDecision(decision: EmpVariationReviewDecision, taskKey: EmpTaskKey): Observable<any> {
    const {
      requestTaskItem: { requestTask },
    } = this.store.getState();
    const payload = this.store.getState().requestTaskItem?.requestTask?.payload as EmpRequestTaskPayload;
    const isCorsia = CorsiaRequestTypes.includes(this.store.getState().requestTaskItem?.requestInfo?.type);

    let groupKey: EmpReviewGroup | 'VARIATION_DETAILS';
    let actionType: RequestTaskActionProcessDTO['requestTaskActionType'];

    if (taskKey == 'empVariationDetails') {
      groupKey = 'VARIATION_DETAILS';
      actionType = isCorsia
        ? 'EMP_VARIATION_CORSIA_SAVE_DETAILS_REVIEW_GROUP_DECISION'
        : 'EMP_VARIATION_UKETS_SAVE_DETAILS_REVIEW_GROUP_DECISION';
    } else {
      groupKey = empReviewGroupMap[taskKey];
      actionType = isCorsia
        ? 'EMP_VARIATION_CORSIA_SAVE_REVIEW_GROUP_DECISION'
        : 'EMP_VARIATION_UKETS_SAVE_REVIEW_GROUP_DECISION';
    }
    const payloadToUpdate = produce(payload, (draft) => {
      if (taskKey === 'empVariationDetails') {
        draft.empVariationDetailsReviewCompleted = true;
        draft.empVariationDetailsReviewDecision = decision;
      } else {
        (draft.reviewGroupDecisions as any) = {
          ...draft.reviewGroupDecisions,
          [groupKey]: decision,
        };

        draft.empSectionsCompleted[taskKey] = [true];
        draft.reviewSectionsCompleted[groupKey] = true;
      }

      if (
        (draft.determination?.type === 'APPROVED' &&
          (decision.type === 'OPERATOR_AMENDS_NEEDED' || decision.type === 'REJECTED')) ||
        ((draft.determination?.type as EmpVariationReviewDecision['type']) === 'REJECTED' &&
          (decision.type === 'OPERATOR_AMENDS_NEEDED' || decision.type === 'ACCEPTED'))
      ) {
        delete draft.determination;
        draft.reviewSectionsCompleted['decision'] = false;
      }
    });

    let requestTaskActionProcessDTO = null;
    switch (actionType as RequestTaskActionProcessDTO['requestTaskActionType']) {
      case 'EMP_VARIATION_UKETS_SAVE_REVIEW_GROUP_DECISION':
        requestTaskActionProcessDTO = {
          requestTaskId: requestTask.id,
          requestTaskActionType: actionType,
          requestTaskActionPayload: {
            group: groupKey,
            decision: payloadToUpdate.reviewGroupDecisions[groupKey],
            reviewSectionsCompleted: payloadToUpdate.reviewSectionsCompleted,
            empSectionsCompleted: payloadToUpdate.empSectionsCompleted,
            payloadType: 'EMP_VARIATION_UKETS_SAVE_REVIEW_GROUP_DECISION_PAYLOAD',
          },
        };
        break;
      case 'EMP_VARIATION_UKETS_SAVE_DETAILS_REVIEW_GROUP_DECISION':
        requestTaskActionProcessDTO = {
          requestTaskId: requestTask.id,
          requestTaskActionType: actionType,
          requestTaskActionPayload: {
            decision: payloadToUpdate.empVariationDetailsReviewDecision,
            empVariationDetailsCompleted: payloadToUpdate.empVariationDetailsCompleted,
            empVariationDetailsReviewCompleted: payloadToUpdate.empVariationDetailsReviewCompleted,
            reviewSectionsCompleted: payloadToUpdate.reviewSectionsCompleted,
            payloadType: 'EMP_VARIATION_UKETS_SAVE_DETAILS_REVIEW_GROUP_DECISION_PAYLOAD',
          },
        };
        break;
      case 'EMP_VARIATION_CORSIA_SAVE_REVIEW_GROUP_DECISION':
        requestTaskActionProcessDTO = {
          requestTaskId: requestTask.id,
          requestTaskActionType: actionType,
          requestTaskActionPayload: {
            group: groupKey,
            decision: payloadToUpdate.reviewGroupDecisions[groupKey],
            reviewSectionsCompleted: payloadToUpdate.reviewSectionsCompleted,
            empSectionsCompleted: payloadToUpdate.empSectionsCompleted,
            payloadType: 'EMP_VARIATION_CORSIA_SAVE_REVIEW_GROUP_DECISION_PAYLOAD',
          },
        };
        break;
      case 'EMP_VARIATION_CORSIA_SAVE_DETAILS_REVIEW_GROUP_DECISION':
        requestTaskActionProcessDTO = {
          requestTaskId: requestTask.id,
          requestTaskActionType: actionType,
          requestTaskActionPayload: {
            decision: payloadToUpdate.empVariationDetailsReviewDecision,
            empVariationDetailsCompleted: payloadToUpdate.empVariationDetailsCompleted,
            empVariationDetailsReviewCompleted: payloadToUpdate.empVariationDetailsReviewCompleted,
            reviewSectionsCompleted: payloadToUpdate.reviewSectionsCompleted,
            payloadType: 'EMP_VARIATION_CORSIA_SAVE_DETAILS_REVIEW_GROUP_DECISION_PAYLOAD',
          },
        };
        break;
    }

    return this.store.tasksService.processRequestTaskAction(requestTaskActionProcessDTO).pipe(
      catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
        this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
      ),
      catchTaskReassignedBadRequest(() =>
        this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
      ),
      tap(() => this.store.setPayload(payloadToUpdate)),
    );
  }
}
