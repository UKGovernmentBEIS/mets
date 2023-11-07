import { Observable, tap } from 'rxjs';

import { ReportingObligation } from '@aviation/request-task/aer/shared/reporting-obligation/reporting-obligation.interface';
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
  AviationAerCorsiaConfidentiality,
  AviationAerCorsiaDataGaps,
  AviationAerCorsiaEmissionsReductionClaim,
  AviationAerCorsiaMonitoringApproach,
  AviationAerEmissionsMonitoringApproach,
  AviationAerMonitoringPlanChanges,
  AviationAerTotalEmissionsConfidentiality,
  AviationCorsiaOperatorDetails,
  AviationOperatorDetails,
  EmpAdditionalDocuments,
  RequestTaskActionProcessDTO,
  RequestTaskDTO,
} from 'pmrv-api';

import { RequestTaskStore } from '../../request-task.store';
import {
  AerCorsia,
  AerCorsiaRequestTaskPayload,
  AerRequestTaskPayload,
  AerTask,
  AerTaskKey,
  AerVerifyCorsiaTaskKey,
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

  setMonitoringApproach(
    monitoringApproach: AviationAerEmissionsMonitoringApproach | AviationAerCorsiaMonitoringApproach,
  ) {
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
      (draft.requestTaskItem.requestTask.payload as AerRequestTaskPayload).aer.aviationAerAircraftData =
        aviationAerAircraftData;
    });
    this.store.setState(newState);
  }

  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  setSaf(saf: AviationAerCorsiaEmissionsReductionClaim) {
    const state = this.store.getState();
    const newState = produce(state, (draft) => {
      (draft.requestTaskItem.requestTask.payload as AerRequestTaskPayload).aer.emissionsReductionClaim = saf;
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
      (draft.requestTaskItem.requestTask.payload as AerRequestTaskPayload).aer.dataGaps = dataGaps;
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

  setMonitoringPlanChanges(monitoringPlanChanges: AviationAerMonitoringPlanChanges) {
    const state = this.store.getState();
    const newState = produce(state, (draft) => {
      (draft.requestTaskItem.requestTask.payload as AerRequestTaskPayload).aer.aerMonitoringPlanChanges =
        monitoringPlanChanges;
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

  setOperatorDetails(operatorDetails: AviationCorsiaOperatorDetails) {
    const state = this.store.getState();
    const newState = produce(state, (draft) => {
      (draft.requestTaskItem.requestTask.payload as AerRequestTaskPayload).aer.operatorDetails =
        operatorDetails as OperatorDetails;
    });
    this.store.setState(newState);
  }

  setEmissionsReductionClaim(emissionsReductionClaim: AviationAerCorsiaEmissionsReductionClaim) {
    const state = this.store.getState();
    const newState = produce(state, (draft) => {
      (draft.requestTaskItem.requestTask.payload as AerRequestTaskPayload).aer.emissionsReductionClaim =
        emissionsReductionClaim as AviationAerCorsiaEmissionsReductionClaim;
    });
    this.store.setState(newState);
  }

  setConfidentiality(confidentiality: AviationAerCorsiaConfidentiality) {
    const state = this.store.getState();

    const newState = produce(state, (draft) => {
      (draft.requestTaskItem.requestTask.payload as AerRequestTaskPayload).aer.confidentiality =
        confidentiality as AviationAerCorsiaConfidentiality;
    });

    this.store.setState(newState);
  }

  setAerSectionCompletionStatus(task: AerTaskKey, completion: [boolean]) {
    const state = this.store.getState();

    const newState = produce(state, (draft) => {
      (draft.requestTaskItem.requestTask.payload as AerRequestTaskPayload).aerSectionsCompleted[task] = completion;
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
          draft.reportingRequired = (aerTask.reportingObligation as ReportingObligation).reportingRequired;

          draft.reportingObligationDetails = (
            aerTask.reportingObligation as ReportingObligation
          ).reportingObligationDetails;
        } else {
          draft.aer[taskKey] = aerTask[taskKey];
        }
      }

      draft.aerSectionsCompleted[taskKey] = status === 'complete' ? [true] : [false];
    });

    const reqBody: RequestTaskActionProcessDTO = {
      requestTaskId: requestTask.id,
      requestTaskActionType: getSaveRequestTaskActionTypeForRequestTaskType(requestTask.type),
      requestTaskActionPayload: { ...payloadToUpdate, payloadType: 'AVIATION_AER_CORSIA_SAVE_APPLICATION_PAYLOAD' },
    };

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
          reportingYear: this.payload.reportingYear,
          verificationBodyId: this.payload.verificationBodyId,
        } as AerRequestTaskPayload);
      }),
    );
  }

  private constructSubmitActionReqBody(
    requestTask: RequestTaskDTO,
    requestTaskActionType: RequestTaskActionProcessDTO['requestTaskActionType'],
  ): RequestTaskActionProcessDTO {
    switch (requestTaskActionType) {
      case 'AVIATION_AER_CORSIA_REQUEST_VERIFICATION':
        return {
          requestTaskActionType: requestTaskActionType,
          requestTaskId: requestTask.id,
          requestTaskActionPayload: {
            payloadType: 'AVIATION_AER_CORSIA_REQUEST_VERIFICATION_PAYLOAD',
          },
        };

      case 'AVIATION_AER_CORSIA_SUBMIT_APPLICATION':
        return {
          requestTaskActionType: requestTaskActionType,
          requestTaskId: requestTask.id,
          requestTaskActionPayload: {
            payloadType: 'EMPTY_PAYLOAD',
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
        actionType === 'AVIATION_AER_CORSIA_SUBMIT_APPLICATION_VERIFICATION'
          ? this.businessErrorService.showErrorForceNavigation(taskSubmitNotFoundError)
          : this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
      ),
      catchTaskReassignedBadRequest(() =>
        this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
      ),
    );
  }

  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  saveAerReviewDecision(decision: any, taskKey: AerTaskKey | AerVerifyCorsiaTaskKey): Observable<any> {
    throw new Error('Not yet implemented');
  }

  get baseFileAttachmentDownloadUrl(): string {
    return `/aviation/tasks/${this.store.requestTaskId}/file-download/attachment`;
  }
}
