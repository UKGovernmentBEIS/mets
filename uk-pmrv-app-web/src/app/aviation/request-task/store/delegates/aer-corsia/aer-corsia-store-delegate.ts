import { Observable, tap } from 'rxjs';

import {
  AerCorsiaReviewGroup,
  aerCorsiaReviewGroupMap,
} from '@aviation/request-task/aer/corsia/shared/aer-review-corsia.types';
import {
  refreshReviewSectionsCompletedUponRequestVerification,
  refreshReviewSectionsCompletedUponSubmitAmend,
  refreshVerificationSectionsCompletedUponSubmitAmendToRegulator,
  refreshVerificationSectionsCompletedUponSubmitToRegulator,
} from '@aviation/request-task/aer/corsia/tasks/send-report/send-report.utils';
import { ReportingObligation } from '@aviation/request-task/aer/shared/reporting-obligation/reporting-obligation.interface';
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
  AviationAerCorsiaApplicationAmendsSubmitRequestTaskPayload,
  AviationAerCorsiaApplicationSubmitRequestTaskPayload,
  AviationAerCorsiaConfidentiality,
  AviationAerCorsiaDataGaps,
  AviationAerCorsiaEmissionsReductionClaim,
  AviationAerCorsiaMonitoringApproach,
  AviationAerMonitoringPlanChanges,
  AviationAerTotalEmissionsConfidentiality,
  AviationCorsiaOperatorDetails,
  EmpAdditionalDocuments,
  EmpCorsiaOriginatedData,
  RequestTaskActionProcessDTO,
  RequestTaskDTO,
} from 'pmrv-api';

import { RequestTaskStore } from '../../request-task.store';
import {
  AerCorsia,
  AerCorsiaRequestTaskPayload,
  AerReviewCorsiaTaskKey,
  AerTask,
  AerTaskKey,
  OperatorDetails,
} from '../../request-task.types';
import { RequestTaskStoreDelegate } from '../store-delegate';
import { AerCorsiaStoreSideEffectsHandler } from './aer-corsia-store-side-effects.handler';

const ROOT_AER_PAYLOAD_TASKS = {
  serviceContactDetails: false,
};

export class AerCorsiaStoreDelegate implements RequestTaskStoreDelegate {
  static INITIAL_STATE: Partial<AerCorsia> = {
    additionalDocuments: { exist: null },
    aerMonitoringPlanChanges: { notCoveredChangesExist: null },
    operatorDetails: {
      operatorName: null,
      flightIdentification: null,
      airOperatingCertificate: null,
      organisationStructure: null,
    },
    monitoringApproach: { certUsed: null },
    confidentiality: {
      totalEmissionsPublished: null,
      aggregatedStatePairDataPublished: null,
    },
  };

  private sideEffectsHandler = new AerCorsiaStoreSideEffectsHandler(this.store);

  constructor(
    private store: RequestTaskStore,
    private readonly businessErrorService: BusinessErrorService,
  ) {}

  get payload(): AerCorsiaRequestTaskPayload | null {
    return this.store.getState().requestTaskItem?.requestTask?.payload as AerCorsiaRequestTaskPayload;
  }

  get empOriginatedData(): EmpCorsiaOriginatedData {
    return (this.store.getState().requestTaskItem?.requestTask?.payload as AerCorsiaRequestTaskPayload)
      .empOriginatedData;
  }

  init() {
    return this;
  }

  setMonitoringApproach(monitoringApproach: AviationAerCorsiaMonitoringApproach) {
    const state = this.store.getState();

    const newState = produce(state, (draft) => {
      (draft.requestTaskItem.requestTask.payload as AerCorsiaRequestTaskPayload).aer.monitoringApproach = <
        AviationAerCorsiaMonitoringApproach
      >monitoringApproach;
    });

    this.store.setState(newState);
  }

  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  setAggregatedEmissionsData(aviationAerCorsiaAggregatedEmissionsData: AviationAerCorsiaAggregatedEmissionsData) {
    const state = this.store.getState();
    const newState = produce(state, (draft) => {
      (draft.requestTaskItem.requestTask.payload as AerCorsiaRequestTaskPayload).aer.aggregatedEmissionsData =
        aviationAerCorsiaAggregatedEmissionsData;
    });
    this.store.setState(newState);
  }

  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  setAviationAerAircraftData(aviationAerAircraftData: AviationAerAircraftData) {
    const state = this.store.getState();
    const newState = produce(state, (draft) => {
      (draft.requestTaskItem.requestTask.payload as AerCorsiaRequestTaskPayload).aer.aviationAerAircraftData =
        aviationAerAircraftData;
    });
    this.store.setState(newState);
  }

  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  setSaf(saf: AviationAerCorsiaEmissionsReductionClaim) {
    const state = this.store.getState();
    const newState = produce(state, (draft) => {
      (draft.requestTaskItem.requestTask.payload as AerCorsiaRequestTaskPayload).aer.emissionsReductionClaim = saf;
    });
    this.store.setState(newState);
  }

  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  setTotalEmissions(aviationAerTotalEmissionsConfidentiality: AviationAerTotalEmissionsConfidentiality) {
    throw new Error('Not yet implemented');
  }

  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  setDataGaps(dataGaps: AviationAerCorsiaDataGaps) {
    const state = this.store.getState();

    const newState = produce(state, (draft) => {
      (draft.requestTaskItem.requestTask.payload as AerCorsiaRequestTaskPayload).aer.dataGaps = dataGaps;
    });

    this.store.setState(newState);
  }

  setAdditionalDocuments(additionalDocuments: EmpAdditionalDocuments) {
    const state = this.store.getState();

    const newState = produce(state, (draft) => {
      (draft.requestTaskItem.requestTask.payload as AerCorsiaRequestTaskPayload).aer.additionalDocuments =
        additionalDocuments;
    });

    this.store.setState(newState);
  }

  setMonitoringPlanChanges(monitoringPlanChanges: AviationAerMonitoringPlanChanges) {
    const state = this.store.getState();
    const newState = produce(state, (draft) => {
      (draft.requestTaskItem.requestTask.payload as AerCorsiaRequestTaskPayload).aer.aerMonitoringPlanChanges =
        monitoringPlanChanges;
    });
    this.store.setState(newState);
  }

  setReportingObligation(reportingObligation: ReportingObligation) {
    const state = this.store.getState();

    const newState = produce(state, (draft) => {
      (draft.requestTaskItem.requestTask.payload as AerCorsiaRequestTaskPayload).reportingRequired =
        reportingObligation.reportingRequired;

      if (reportingObligation.reportingObligationDetails) {
        (draft.requestTaskItem.requestTask.payload as AerCorsiaRequestTaskPayload).reportingObligationDetails =
          reportingObligation.reportingObligationDetails;
      }
    });

    this.store.setState(newState);
  }

  setOperatorDetails(operatorDetails: AviationCorsiaOperatorDetails) {
    const state = this.store.getState();
    const newState = produce(state, (draft) => {
      (draft.requestTaskItem.requestTask.payload as AerCorsiaRequestTaskPayload).aer.operatorDetails =
        operatorDetails as OperatorDetails;
    });
    this.store.setState(newState);
  }

  setEmissionsReductionClaim(emissionsReductionClaim: AviationAerCorsiaEmissionsReductionClaim) {
    const state = this.store.getState();
    const newState = produce(state, (draft) => {
      (draft.requestTaskItem.requestTask.payload as AerCorsiaRequestTaskPayload).aer.emissionsReductionClaim =
        emissionsReductionClaim as AviationAerCorsiaEmissionsReductionClaim;
    });
    this.store.setState(newState);
  }

  setConfidentiality(confidentiality: AviationAerCorsiaConfidentiality) {
    const state = this.store.getState();

    const newState = produce(state, (draft) => {
      (draft.requestTaskItem.requestTask.payload as AerCorsiaRequestTaskPayload).aer.confidentiality =
        confidentiality as AviationAerCorsiaConfidentiality;
    });

    this.store.setState(newState);
  }

  setAerSectionCompletionStatus(task: AerTaskKey, completion: [boolean]) {
    const state = this.store.getState();

    const newState = produce(state, (draft) => {
      (draft.requestTaskItem.requestTask.payload as AerCorsiaRequestTaskPayload).aerSectionsCompleted[task] =
        completion;
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

  saveAer(aerTask: { [key in AerTaskKey]?: AerTask }, status: TaskItemStatus = 'complete'): Observable<any> {
    const taskKey = Object.keys(aerTask)[0];

    const {
      requestTaskItem: { requestTask },
    } = this.store.getState();

    const payloadAfterSideEffects = this.sideEffectsHandler.applySideEffects(aerTask);

    const payloadToUpdate = produce(payloadAfterSideEffects, (draft) => {
      if (draft.sendEmailNotification) {
        delete draft.sendEmailNotification;
      }

      if (Object.keys(ROOT_AER_PAYLOAD_TASKS).includes(taskKey)) {
        if (ROOT_AER_PAYLOAD_TASKS[taskKey]) {
          draft[taskKey] = aerTask[taskKey];
        }
      } else {
        if (taskKey === 'reportingObligation') {
          draft.reportingRequired = (aerTask.reportingObligation as ReportingObligation).reportingRequired;

          draft.reportingObligationDetails = (
            aerTask.reportingObligation as ReportingObligation
          ).reportingObligationDetails;
        } else {
          if (draft.aer) {
            draft.aer[taskKey] = aerTask[taskKey];
          }
        }
      }

      if (['AVIATION_AER_CORSIA_APPLICATION_AMENDS_SUBMIT_PAYLOAD'].includes(draft.payloadType)) {
        //TODO if taskKey not exists then saving the changesRequest section. Please refactor the whole logic
        if (!taskKey) {
          draft.aerSectionsCompleted['changesRequested'] = [true];
        } else {
          const aerReviewGroupName = aerCorsiaReviewGroupMap[taskKey];
          draft.reviewSectionsCompleted[aerReviewGroupName] = false;
        }
      }

      //TODO if taskKey not exists then saving the changesRequest section. Please refactor the whole logic
      if (taskKey) {
        draft.aerSectionsCompleted[taskKey] = status === 'complete' ? [true] : [false];
      }
    });

    const reqBody = this.constructSaveActionReqBody(
      requestTask,
      produce(payloadToUpdate, (draft) => {
        delete draft.aerAttachments;
        delete draft.reportingYear;
        delete draft.serviceContactDetails;
        delete draft.aerMonitoringPlanVersions;
        delete draft.verificationBodyId;
        delete draft.verificationPerformed;
        delete draft.verificationSectionsCompleted;
        delete draft.empOriginatedData;
        delete draft.reviewGroupDecisions;
        delete draft.reviewAttachments;
      }),
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
          ...requestTask.payload,
          ...updatedPayload,
          verificationPerformed: false, //reset
        } as AerCorsiaRequestTaskPayload);
      }),
    );
  }

  private constructSubmitActionReqBody(
    requestTask: RequestTaskDTO,
    requestTaskActionType: RequestTaskActionProcessDTO['requestTaskActionType'],
  ): RequestTaskActionProcessDTO {
    switch (requestTaskActionType) {
      case 'AVIATION_AER_CORSIA_SUBMIT_APPLICATION':
        return {
          requestTaskActionType: requestTaskActionType,
          requestTaskId: requestTask.id,
          requestTaskActionPayload: {
            payloadType: 'AVIATION_AER_CORSIA_SUBMIT_APPLICATION_PAYLOAD',
            verificationSectionsCompleted: refreshVerificationSectionsCompletedUponSubmitToRegulator(
              requestTask.payload as AviationAerCorsiaApplicationSubmitRequestTaskPayload,
            ),
          },
        };
      case 'AVIATION_AER_CORSIA_REQUEST_VERIFICATION':
        return {
          requestTaskActionType: requestTaskActionType,
          requestTaskId: requestTask.id,
          requestTaskActionPayload: {
            payloadType: 'AVIATION_AER_CORSIA_REQUEST_VERIFICATION_PAYLOAD',
            verificationSectionsCompleted: (requestTask.payload as AviationAerCorsiaApplicationSubmitRequestTaskPayload)
              .verificationSectionsCompleted,
          },
        };
      case 'AVIATION_AER_CORSIA_REQUEST_AMENDS_VERIFICATION': {
        const requestTaskPayload = requestTask.payload as AviationAerCorsiaApplicationAmendsSubmitRequestTaskPayload;
        return {
          requestTaskActionType: requestTaskActionType,
          requestTaskId: requestTask.id,
          requestTaskActionPayload: {
            payloadType: 'AVIATION_AER_CORSIA_REQUEST_AMENDS_VERIFICATION_PAYLOAD',
            verificationSectionsCompleted: requestTaskPayload.verificationSectionsCompleted,
            reviewSectionsCompleted: refreshReviewSectionsCompletedUponRequestVerification(requestTaskPayload),
          },
        };
      }
      case 'AVIATION_AER_CORSIA_SUBMIT_APPLICATION_AMEND': {
        const requestTaskPayload = requestTask.payload as AviationAerCorsiaApplicationAmendsSubmitRequestTaskPayload;
        const aerSectionsCompleted = (() => {
          const { changesRequested, ...rest } = requestTaskPayload.aerSectionsCompleted;
          return rest;
        })();
        return {
          requestTaskActionType: requestTaskActionType,
          requestTaskId: requestTask.id,
          requestTaskActionPayload: {
            payloadType: 'AVIATION_AER_CORSIA_SUBMIT_APPLICATION_AMEND_PAYLOAD',
            aerSectionsCompleted,
            reviewSectionsCompleted: refreshReviewSectionsCompletedUponSubmitAmend(requestTaskPayload),
            verificationSectionsCompleted: refreshVerificationSectionsCompletedUponSubmitAmendToRegulator(
              requestTask.payload as AviationAerCorsiaApplicationAmendsSubmitRequestTaskPayload,
            ),
          },
        };
      }
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
        actionType === 'AVIATION_AER_CORSIA_SUBMIT_APPLICATION_VERIFICATION'
          ? this.businessErrorService.showErrorForceNavigation(taskSubmitNotFoundError)
          : this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
      ),
      catchTaskReassignedBadRequest(() =>
        this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
      ),
    );
  }

  private constructSaveActionReqBody(
    requestTask: RequestTaskDTO,
    payloadToUpdate: AerCorsiaRequestTaskPayload,
    requestTaskActionType: RequestTaskActionProcessDTO['requestTaskActionType'],
    groupKey?: AerCorsiaReviewGroup,
  ): RequestTaskActionProcessDTO {
    switch (requestTaskActionType) {
      case 'AVIATION_AER_CORSIA_SAVE_REVIEW_GROUP_DECISION':
        return {
          requestTaskId: requestTask.id,
          requestTaskActionType: requestTaskActionType,
          requestTaskActionPayload: {
            group: groupKey as any,
            decision: payloadToUpdate.reviewGroupDecisions[groupKey] as any,
            reviewSectionsCompleted: payloadToUpdate.reviewSectionsCompleted,
            payloadType: 'AVIATION_AER_CORSIA_SAVE_REVIEW_GROUP_DECISION_PAYLOAD',
          },
        };
      case 'AVIATION_AER_CORSIA_SAVE_APPLICATION':
        return {
          requestTaskId: requestTask.id,
          requestTaskActionType: requestTaskActionType,
          requestTaskActionPayload: {
            ...payloadToUpdate,
            payloadType: 'AVIATION_AER_CORSIA_SAVE_APPLICATION_PAYLOAD',
          },
        };
      case 'AVIATION_AER_CORSIA_SAVE_APPLICATION_AMEND':
        return {
          requestTaskId: requestTask.id,
          requestTaskActionType: requestTaskActionType,
          requestTaskActionPayload: {
            ...payloadToUpdate,
            payloadType: 'AVIATION_AER_CORSIA_SAVE_APPLICATION_AMEND_PAYLOAD',
          },
        };
      default:
        return {
          requestTaskId: requestTask.id,
          requestTaskActionType: requestTaskActionType,
          requestTaskActionPayload: {
            ...payloadToUpdate,
            payloadType: 'AVIATION_AER_CORSIA_SAVE_APPLICATION_PAYLOAD',
          },
        };
    }
  }

  saveAerReviewDecision(decision: any, taskKey: AerReviewCorsiaTaskKey): Observable<any> {
    const {
      requestTaskItem: { requestTask },
    } = this.store.getState();
    const groupKey = aerCorsiaReviewGroupMap[taskKey] ?? aerVerifyReviewGroupMap[taskKey];
    decision = aerCorsiaReviewGroupMap[taskKey]
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
        } as AerCorsiaRequestTaskPayload),
      ),
    );
  }

  get baseFileAttachmentDownloadUrl(): string {
    return `/aviation/tasks/${this.store.requestTaskId}/file-download/attachment`;
  }
}
