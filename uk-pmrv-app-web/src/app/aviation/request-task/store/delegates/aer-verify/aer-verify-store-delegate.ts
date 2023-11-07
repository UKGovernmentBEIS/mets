import { Observable, tap } from 'rxjs';

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
  AviationAerComplianceMonitoringReportingRules,
  AviationAerDataGapsMethodologies,
  AviationAerEmissionsReductionClaimVerification,
  AviationAerEtsComplianceRules,
  AviationAerMaterialityLevel,
  AviationAerOpinionStatement,
  AviationAerUkEtsVerificationReport,
  AviationAerUncorrectedNonConformities,
  RequestTaskActionProcessDTO,
  RequestTaskDTO,
} from 'pmrv-api';

import { RequestTaskStore } from '../../request-task.store';
import {
  AerRequestTaskPayload,
  AerVerify,
  AerVerifyTask,
  AerVerifyTaskKey,
  AerVerifyTaskPayload,
} from '../../request-task.types';
import { RequestTaskStoreDelegate } from '../store-delegate';
import { AerVerifyStoreSideEffectsHandler } from './aer-verify-store-side-effects.handler';

export class AerVerifyStoreDelegate implements RequestTaskStoreDelegate {
  static INITIAL_STATE: Partial<AerVerify> = {
    verificationReport: {
      verifierContact: null,
      verificationTeamDetails: null,
      etsComplianceRules: null,
      overallDecision: null,
      complianceMonitoringReportingRules: null,
      opinionStatement: null,
      uncorrectedMisstatements: null,
      uncorrectedNonCompliances: null,
      uncorrectedNonConformities: null,
      recommendedImprovements: null,
      emissionsReductionClaimVerification: null,
      dataGapsMethodologies: null,
      materialityLevel: null,
    } as AviationAerUkEtsVerificationReport,
  };

  private sideEffectsHandler = new AerVerifyStoreSideEffectsHandler(this.store);

  get payload(): AerVerifyTaskPayload | null {
    return this.store.getState().requestTaskItem?.requestTask?.payload as AerVerifyTaskPayload;
  }

  get requestTaskType(): RequestTaskDTO['type'] | null {
    return this.store.getState().requestTaskItem?.requestTask?.type;
  }

  get baseFileAttachmentDownloadUrl(): string {
    return `/aviation/tasks/${this.store.requestTaskId}/file-download/attachment`;
  }

  constructor(private store: RequestTaskStore, private readonly businessErrorService: BusinessErrorService) {}

  init() {
    if (
      this.payload &&
      !this.payload.verificationReport &&
      this.requestTaskType !== 'AVIATION_AER_UKETS_APPLICATION_SUBMIT'
    ) {
      this.store.setPayload(
        produce(this.payload, (payload) => {
          payload.verificationReport = {} as AviationAerUkEtsVerificationReport;
        }),
      );
    }

    return this;
  }

  setVerificationReport(verificationReport: AviationAerUkEtsVerificationReport) {
    const state = this.store.getState();

    const newState = produce(state, (draft) => {
      (draft.requestTaskItem.requestTask.payload as AerVerifyTaskPayload).verificationReport = verificationReport;
    });

    this.store.setState(newState);
  }

  setOpinionStatement(opinionStatement: AviationAerOpinionStatement) {
    const state = this.store.getState();

    const newState = produce(state, (draft) => {
      (draft.requestTaskItem.requestTask.payload as AerVerifyTaskPayload).verificationReport.opinionStatement =
        opinionStatement;
    });

    this.store.setState(newState);
  }

  setEtsComplianceRules(etsComplianceRules: AviationAerEtsComplianceRules) {
    const state = this.store.getState();

    const newState = produce(state, (draft) => {
      (draft.requestTaskItem.requestTask.payload as AerVerifyTaskPayload).verificationReport.etsComplianceRules =
        etsComplianceRules;
    });

    this.store.setState(newState);
  }

  setEmissionsReductionClaimVerification(
    emissionsReductionClaimVerification: AviationAerEmissionsReductionClaimVerification,
  ) {
    const state = this.store.getState();

    const newState = produce(state, (draft) => {
      (
        draft.requestTaskItem.requestTask.payload as AerVerifyTaskPayload
      ).verificationReport.emissionsReductionClaimVerification = emissionsReductionClaimVerification;
    });

    this.store.setState(newState);
  }

  setVerificationSectionCompletionStatus(task: AerVerifyTaskKey, completion: [boolean]) {
    const state = this.store.getState();

    const newState = produce(state, (draft) => {
      (draft.requestTaskItem.requestTask.payload as AerVerifyTaskPayload).verificationSectionsCompleted[task] =
        completion;
    });

    this.store.setState(newState);
  }

  setComplianceMonitoring(complianceMonitoringReportingRules: AviationAerComplianceMonitoringReportingRules) {
    const state = this.store.getState();

    const newState = produce(state, (draft) => {
      (
        draft.requestTaskItem.requestTask.payload as AerVerifyTaskPayload
      ).verificationReport.complianceMonitoringReportingRules = complianceMonitoringReportingRules;
    });

    this.store.setState(newState);
  }

  setUncorrectedNonConformities(uncorrectedNonConformities: AviationAerUncorrectedNonConformities) {
    const state = this.store.getState();

    const newState = produce(state, (draft) => {
      (
        draft.requestTaskItem.requestTask.payload as AerVerifyTaskPayload
      ).verificationReport.uncorrectedNonConformities = uncorrectedNonConformities;
    });

    this.store.setState(newState);
  }

  setMaterialityLevel(materialityLevel: AviationAerMaterialityLevel) {
    const state = this.store.getState();

    const newState = produce(state, (draft) => {
      (draft.requestTaskItem.requestTask.payload as AerVerifyTaskPayload).verificationReport.materialityLevel =
        materialityLevel;
    });

    this.store.setState(newState);
  }

  setDataGapsMethodologies(dataGapsMethodologies: AviationAerDataGapsMethodologies) {
    const state = this.store.getState();

    const newState = produce(state, (draft) => {
      (draft.requestTaskItem.requestTask.payload as AerVerifyTaskPayload).verificationReport.dataGapsMethodologies =
        dataGapsMethodologies;
    });

    this.store.setState(newState);
  }

  saveAerVerify(
    aerVerifyTask: { [key in AerVerifyTaskKey]?: AerVerifyTask },
    status: TaskItemStatus = 'complete',
  ): Observable<any> {
    const taskKey = Object.keys(aerVerifyTask)[0];

    const {
      requestTaskItem: { requestTask },
    } = this.store.getState();

    const payloadAfterSideEffects = this.sideEffectsHandler.applySideEffects(aerVerifyTask);

    const payloadToUpdate = produce(payloadAfterSideEffects, (draft) => {
      if (taskKey === 'verificationReport') {
        draft[taskKey] = aerVerifyTask[taskKey] as any;
      } else {
        draft.verificationReport[taskKey] = aerVerifyTask[taskKey];
      }

      draft.verificationSectionsCompleted[taskKey] = status === 'complete' ? [true] : [false];
    });

    const { verificationReport, verificationSectionsCompleted } = payloadToUpdate;

    const reqBody: RequestTaskActionProcessDTO = {
      requestTaskId: requestTask.id,
      requestTaskActionType: getSaveRequestTaskActionTypeForRequestTaskType(requestTask.type),
      requestTaskActionPayload: {
        ...verificationReport,
        verificationSectionsCompleted,
        payloadType: 'AVIATION_AER_UKETS_SAVE_APPLICATION_VERIFICATION_PAYLOAD',
      },
    };

    return this.store.tasksService.processRequestTaskAction(reqBody).pipe(
      catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
        this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
      ),
      catchTaskReassignedBadRequest(() =>
        this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
      ),
      tap(() => {
        this.store.setPayload({
          ...payloadToUpdate,
        } as unknown as AerRequestTaskPayload);
      }),
    );
  }

  submitAerVerify(): Observable<any> {
    const {
      requestTaskItem: { requestTask },
    } = this.store.getState();

    const reqBody: RequestTaskActionProcessDTO = {
      requestTaskActionType: 'AVIATION_AER_UKETS_SUBMIT_APPLICATION_VERIFICATION',
      requestTaskId: requestTask.id,
      requestTaskActionPayload: {
        payloadType: 'EMPTY_PAYLOAD',
      },
    };

    return this.store.tasksService.processRequestTaskAction(reqBody).pipe(
      catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
        this.businessErrorService.showErrorForceNavigation(taskSubmitNotFoundError),
      ),
      catchTaskReassignedBadRequest(() =>
        this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
      ),
    );
  }
}
