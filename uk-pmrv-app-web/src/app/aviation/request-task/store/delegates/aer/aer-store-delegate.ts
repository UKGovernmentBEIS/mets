import { Observable, tap } from 'rxjs';

import { ReportingObligation } from '@aviation/request-task/aer/shared/reporting-obligation/reporting-obligation.interface';
import { aerReviewGroupMap, AerUkEtsReviewGroup } from '@aviation/request-task/aer/shared/util/aer.util';
import { aerVerifyReviewGroupMap } from '@aviation/request-task/aer/shared/util/aer-verify-tasks.util';
import { getSaveRequestTaskActionTypeForRequestTaskType } from '@aviation/request-task/util';
import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchTaskReassignedBadRequest } from '@error/business-errors';
import { catchNotFoundRequest, ErrorCode } from '@error/not-found-error';
import {
  requestTaskReassignedError,
  taskNotFoundError,
  taskSubmitNotFoundError,
} from '@shared/errors/request-task-error';
import { TaskItemStatus } from '@shared/task-list/task-list.interface';
import produce from 'immer';

import {
  AviationAerAircraftData,
  AviationAerCorsiaAggregatedEmissionsData,
  AviationAerDataGaps,
  AviationAerEmissionsMonitoringApproach,
  AviationAerMonitoringPlanChanges,
  AviationAerSaf,
  AviationAerTotalEmissionsConfidentiality,
  AviationAerUkEtsAggregatedEmissionsData,
  AviationOperatorDetails,
  EmpAdditionalDocuments,
  RequestTaskActionProcessDTO,
  RequestTaskDTO,
} from 'pmrv-api';

import { RequestTaskStore } from '../../request-task.store';
import {
  Aer,
  AerRequestTaskPayload,
  AerTask,
  AerTaskKey,
  AerUkEtsRequestTaskPayload,
  AerVerifyTaskKey,
  OperatorDetails,
} from '../../request-task.types';
import { RequestTaskStoreDelegate } from '../store-delegate';
import { AerStoreSideEffectsHandler } from './aer-store-side-effects.handler';

const ROOT_AER_PAYLOAD_TASKS = {
  serviceContactDetails: false,
};

export class AerStoreDelegate implements RequestTaskStoreDelegate {
  static INITIAL_STATE: Partial<Aer> = {
    additionalDocuments: { exist: null },
    aerMonitoringPlanChanges: { notCoveredChangesExist: null },
    aggregatedEmissionsData: { aggregatedEmissionDataDetails: null },
    dataGaps: { exist: null },
    operatorDetails: {
      operatorName: null,
      crcoCode: null,
      flightIdentification: null,
      airOperatingCertificate: null,
      operatingLicense: null,
      organisationStructure: null,
    },
    monitoringApproach: { monitoringApproachType: null },
    saf: { exist: null },
    aviationAerTotalEmissionsConfidentiality: { confidential: null },
    aviationAerAircraftData: { aviationAerAircraftDataDetails: null },
  };

  private sideEffectsHandler = new AerStoreSideEffectsHandler(this.store);

  constructor(private store: RequestTaskStore, private readonly businessErrorService: BusinessErrorService) {}

  get payload(): AerRequestTaskPayload | null {
    return this.store.getState().requestTaskItem?.requestTask?.payload as AerRequestTaskPayload;
  }

  get operatorDetails(): AviationOperatorDetails {
    return (this.store.getState().requestTaskItem?.requestTask?.payload as AerRequestTaskPayload).empOriginatedData
      ?.operatorDetails;
  }

  init() {
    if (this.payload && !this.payload.aer) {
      this.store.setPayload(
        produce(this.payload, (payload) => {
          payload.aer = { operatorDetails: this.operatorDetails } as any;
        }),
      );
    }

    return this;
  }

  setAerSectionCompletionStatus(task: AerTaskKey, completion: [boolean]) {
    const state = this.store.getState();

    const newState = produce(state, (draft) => {
      (draft.requestTaskItem.requestTask.payload as AerRequestTaskPayload).aerSectionsCompleted[task] = completion;
    });

    this.store.setState(newState);
  }

  setAdditionalDocuments(additionalDocuments: EmpAdditionalDocuments) {
    const state = this.store.getState();

    const newState = produce(state, (draft) => {
      (draft.requestTaskItem.requestTask.payload as AerRequestTaskPayload).aer.additionalDocuments =
        additionalDocuments;
    });

    this.store.setState(newState);
  }

  setMonitoringApproach(monitoringApproach: AviationAerEmissionsMonitoringApproach) {
    const state = this.store.getState();

    const newState = produce(state, (draft) => {
      (draft.requestTaskItem.requestTask.payload as AerUkEtsRequestTaskPayload).aer.monitoringApproach =
        monitoringApproach;
    });

    this.store.setState(newState);
  }

  setAerAttachments(attachments: { [key: string]: string }) {
    this.store.setPayload(
      produce(this.payload, (updatedPayload) => {
        updatedPayload.aerAttachments = attachments;
      }),
    );
  }

  setMonitoringPlanChanges(monitoringPlanChanges: AviationAerMonitoringPlanChanges) {
    const state = this.store.getState();
    const newState = produce(state, (draft) => {
      (draft.requestTaskItem.requestTask.payload as AerRequestTaskPayload).aer.aerMonitoringPlanChanges =
        monitoringPlanChanges;
    });
    this.store.setState(newState);
  }

  setAggregatedEmissionsData(
    aviationAerUkEtsAggregatedEmissionsData:
      | AviationAerUkEtsAggregatedEmissionsData
      | AviationAerCorsiaAggregatedEmissionsData,
  ) {
    const state = this.store.getState();
    const newState = produce(state, (draft) => {
      (draft.requestTaskItem.requestTask.payload as any).aer.aggregatedEmissionsData =
        aviationAerUkEtsAggregatedEmissionsData;
    });
    this.store.setState(newState);
  }

  setAviationAerAircraftData(aviationAerAircraftData: AviationAerAircraftData) {
    const state = this.store.getState();
    const newState = produce(state, (draft) => {
      (draft.requestTaskItem.requestTask.payload as AerRequestTaskPayload).aer.aviationAerAircraftData =
        aviationAerAircraftData;
    });
    this.store.setState(newState);
  }

  setSaf(saf: AviationAerSaf) {
    const state = this.store.getState();
    const newState = produce(state, (draft) => {
      (draft.requestTaskItem.requestTask.payload as AerRequestTaskPayload).aer.saf = saf;
    });
    this.store.setState(newState);
  }

  setReportingObligation(reportingObligation: ReportingObligation) {
    const state = this.store.getState();

    const newState = produce(state, (draft) => {
      (draft.requestTaskItem.requestTask.payload as AerRequestTaskPayload).reportingRequired =
        reportingObligation.reportingRequired;

      if (reportingObligation.reportingObligationDetails) {
        (draft.requestTaskItem.requestTask.payload as AerRequestTaskPayload).reportingObligationDetails =
          reportingObligation.reportingObligationDetails;
      }
    });

    this.store.setState(newState);
  }

  setOperatorDetails(operatorDetails: AviationOperatorDetails) {
    const state = this.store.getState();
    const newState = produce(state, (draft) => {
      (draft.requestTaskItem.requestTask.payload as AerRequestTaskPayload).aer.operatorDetails =
        operatorDetails as OperatorDetails;
    });
    this.store.setState(newState);
  }

  setTotalEmissions(aviationAerTotalEmissionsConfidentiality: AviationAerTotalEmissionsConfidentiality) {
    const state = this.store.getState();
    const newState = produce(state, (draft) => {
      (
        draft.requestTaskItem.requestTask.payload as AerRequestTaskPayload
      ).aer.aviationAerTotalEmissionsConfidentiality = aviationAerTotalEmissionsConfidentiality;
    });
    this.store.setState(newState);
  }

  setDataGaps(dataGaps: AviationAerDataGaps) {
    const state = this.store.getState();

    const newState = produce(state, (draft) => {
      (draft.requestTaskItem.requestTask.payload as AerRequestTaskPayload).aer.dataGaps = dataGaps;
    });

    this.store.setState(newState);
  }

  addAerAttachment(attachment: { [key: string]: string }) {
    this.setAerAttachments({
      ...this.payload.aerAttachments,
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

  saveAerReviewDecision(decision: any, taskKey: AerTaskKey | AerVerifyTaskKey): Observable<any> {
    const {
      requestTaskItem: { requestTask },
    } = this.store.getState();
    const groupKey = aerReviewGroupMap[taskKey] ?? aerVerifyReviewGroupMap[taskKey];
    decision = aerReviewGroupMap[taskKey]
      ? { ...decision, reviewDataType: 'AER_DATA' }
      : { ...decision, reviewDataType: 'VERIFICATION_REPORT_DATA' };

    const payloadToUpdate = produce(this.payload, (draft) => {
      delete draft.reviewAttachments;

      draft.reviewGroupDecisions = {
        ...draft.reviewGroupDecisions,
        [groupKey]: decision,
      };

      draft.reviewSectionsCompleted[groupKey] = true;
    });

    const reqBody: RequestTaskActionProcessDTO = this.constructSaveActionReqBody(
      requestTask,
      payloadToUpdate,
      getSaveRequestTaskActionTypeForRequestTaskType(requestTask.type),
      groupKey,
    );

    return this.store.tasksService.processRequestTaskAction(reqBody).pipe(
      catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
        this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
      ),
      catchTaskReassignedBadRequest(() =>
        this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
      ),
      tap(() =>
        this.store.setPayload({
          ...payloadToUpdate,
          reviewAttachments: this.payload.reviewAttachments,
          serviceContactDetails: this.payload.serviceContactDetails,
        } as AerUkEtsRequestTaskPayload),
      ),
    );
  }

  private constructSaveActionReqBody(
    requestTask: RequestTaskDTO,
    payloadToUpdate: AerRequestTaskPayload,
    requestTaskActionType: RequestTaskActionProcessDTO['requestTaskActionType'],
    groupKey?: AerUkEtsReviewGroup,
  ): RequestTaskActionProcessDTO {
    switch (requestTaskActionType) {
      case 'AVIATION_AER_UKETS_SAVE_APPLICATION_AMEND':
        return {
          requestTaskId: requestTask.id,
          requestTaskActionType: requestTaskActionType,
          requestTaskActionPayload: {
            aer: payloadToUpdate.aer,
            aerSectionsCompleted: payloadToUpdate.aerSectionsCompleted,
            reviewSectionsCompleted: payloadToUpdate.reviewSectionsCompleted,
            payloadType: 'AVIATION_AER_UKETS_SAVE_APPLICATION_AMEND_PAYLOAD',
            reportingRequired: payloadToUpdate.reportingRequired,
            reportingObligationDetails: payloadToUpdate.reportingObligationDetails,
            verificationSectionsCompleted: payloadToUpdate.verificationSectionsCompleted,
          },
        };
      case 'AVIATION_AER_UKETS_SAVE_REVIEW_GROUP_DECISION':
        return {
          requestTaskId: requestTask.id,
          requestTaskActionType: requestTaskActionType,
          requestTaskActionPayload: {
            group: groupKey as any,
            decision: payloadToUpdate.reviewGroupDecisions[groupKey] as any,
            reviewSectionsCompleted: payloadToUpdate.reviewSectionsCompleted,
            payloadType: 'AVIATION_AER_UKETS_SAVE_REVIEW_GROUP_DECISION_PAYLOAD',
          },
        };
      default:
        return {
          requestTaskId: requestTask.id,
          requestTaskActionType: requestTaskActionType,
          requestTaskActionPayload: {
            ...payloadToUpdate,
            payloadType: 'AVIATION_AER_UKETS_SAVE_APPLICATION_PAYLOAD',
          },
        };
    }
  }

  saveAer(aerTask: { [key in AerTaskKey]?: AerTask }, status: TaskItemStatus = 'complete'): Observable<any> {
    const taskKey = Object.keys(aerTask)[0];

    const {
      requestTaskItem: { requestTask },
    } = this.store.getState();

    const payloadAfterSideEffects = this.sideEffectsHandler.applySideEffects(aerTask);

    const payloadToUpdate = produce(payloadAfterSideEffects, (draft) => {
      delete draft.aerAttachments;
      delete draft.reportingYear;
      delete draft.serviceContactDetails;
      delete draft.aerMonitoringPlanVersions;
      delete draft.verificationBodyId;
      delete draft.verificationPerformed;
      delete draft.verificationSectionsCompleted;
      delete draft.empOriginatedData;

      if (Object.keys(ROOT_AER_PAYLOAD_TASKS).includes(taskKey)) {
        if (ROOT_AER_PAYLOAD_TASKS[taskKey]) {
          draft[taskKey] = aerTask[taskKey];
        }
      } else {
        if (taskKey === 'reportingObligation') {
          draft.reportingRequired =
            draft?.reportingRequired ?? (aerTask?.reportingObligation as ReportingObligation)?.reportingRequired;

          draft.reportingObligationDetails =
            draft?.reportingObligationDetails ??
            (aerTask?.reportingObligation as ReportingObligation)?.reportingObligationDetails;

          if (draft.reportingRequired) {
            draft.reportingObligationDetails = null;
          }
        } else if (taskKey) {
          draft.aer[taskKey] = aerTask[taskKey];
        }
      }

      if (['AVIATION_AER_UKETS_APPLICATION_AMENDS_SUBMIT_PAYLOAD'].includes(draft.payloadType)) {
        if (!taskKey) {
          draft.aerSectionsCompleted['changesRequested'] = [true];
          if (!draft.reportingRequired) {
            delete draft.aer;
          }
        } else {
          const aerReviewGroupName = aerReviewGroupMap[taskKey];

          draft.reviewSectionsCompleted[aerReviewGroupName] = false;
          draft.aerSectionsCompleted[taskKey] = status === 'complete' ? [true] : [false];
        }
      } else {
        draft.aerSectionsCompleted[taskKey] = status === 'complete' ? [true] : [false];
      }
    });
    const reqBody: RequestTaskActionProcessDTO = this.constructSaveActionReqBody(
      requestTask,
      payloadToUpdate,
      getSaveRequestTaskActionTypeForRequestTaskType(requestTask.type),
    );

    return this.store.tasksService.processRequestTaskAction(reqBody).pipe(
      catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
        this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
      ),
      catchTaskReassignedBadRequest(() =>
        this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
      ),
      tap(() => {
        let updatedPayload = payloadToUpdate;

        if (taskKey in ROOT_AER_PAYLOAD_TASKS) {
          updatedPayload = produce(updatedPayload, (draft) => {
            draft[taskKey] = aerTask[taskKey];
          });
        }

        this.store.setPayload({
          ...updatedPayload,
          aerAttachments: this.payload.aerAttachments,
          serviceContactDetails: this.payload.serviceContactDetails,
          aerMonitoringPlanVersions: this.payload.aerMonitoringPlanVersions,
          empOriginatedData: this.payload.empOriginatedData,
          verificationSectionsCompleted: this.payload['verificationSectionsCompleted'],
        } as AerRequestTaskPayload);
      }),
    );
  }

  private constructSubmitActionReqBody(
    requestTask: RequestTaskDTO,
    requestTaskActionType: RequestTaskActionProcessDTO['requestTaskActionType'],
  ): RequestTaskActionProcessDTO {
    const { changesRequested, ...aerSectionsCompleted } = (requestTask.payload as AerRequestTaskPayload)
      ?.aerSectionsCompleted;
    switch (requestTaskActionType) {
      case 'AVIATION_AER_UKETS_REQUEST_VERIFICATION':
        return {
          requestTaskActionType: requestTaskActionType,
          requestTaskId: requestTask.id,
          requestTaskActionPayload: {
            payloadType: 'AVIATION_AER_UKETS_REQUEST_VERIFICATION_PAYLOAD',
            verificationSectionsCompleted: requestTask.payload['verificationSectionsCompleted'] ?? {},
          },
        };
      case 'AVIATION_AER_UKETS_SUBMIT_APPLICATION_AMEND':
        return {
          requestTaskActionType: requestTaskActionType,
          requestTaskId: requestTask.id,
          requestTaskActionPayload: {
            payloadType: 'AVIATION_AER_UKETS_SUBMIT_APPLICATION_AMEND_PAYLOAD',
            aerSectionsCompleted: aerSectionsCompleted,
          },
        };
      case 'AVIATION_AER_UKETS_REQUEST_AMENDS_VERIFICATION':
        return {
          requestTaskActionType: requestTaskActionType,
          requestTaskId: requestTask.id,
          requestTaskActionPayload: {
            payloadType: 'AVIATION_AER_UKETS_REQUEST_AMENDS_VERIFICATION_PAYLOAD',
            verificationSectionsCompleted: requestTask.payload['verificationSectionsCompleted'],
          },
        };

      default:
        return {
          requestTaskActionType: requestTaskActionType,
          requestTaskId: requestTask.id,
          requestTaskActionPayload: { payloadType: 'EMPTY_PAYLOAD' },
        };
    }
  }

  submitAer(actionType: RequestTaskActionProcessDTO['requestTaskActionType']): Observable<any> {
    const {
      requestTaskItem: { requestTask },
    } = this.store.getState();

    const reqBody: RequestTaskActionProcessDTO = this.constructSubmitActionReqBody(requestTask, actionType);

    return this.store.tasksService.processRequestTaskAction(reqBody).pipe(
      catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
        actionType === 'AVIATION_AER_UKETS_SUBMIT_APPLICATION_VERIFICATION'
          ? this.businessErrorService.showErrorForceNavigation(taskSubmitNotFoundError)
          : this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
      ),
      catchTaskReassignedBadRequest(() =>
        this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
      ),
      tap(() => this.store.aerDelegate.setAerSectionCompletionStatus('sendReport' as AerTaskKey, [true])),
    );
  }

  get baseFileAttachmentDownloadUrl(): string {
    return `/aviation/tasks/${this.store.requestTaskId}/file-download/attachment`;
  }
}
