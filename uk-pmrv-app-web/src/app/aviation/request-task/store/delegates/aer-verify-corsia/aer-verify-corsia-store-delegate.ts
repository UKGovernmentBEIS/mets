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

import { AviationAerCorsiaVerificationReport, RequestTaskActionProcessDTO, RequestTaskDTO } from 'pmrv-api';

import { RequestTaskStore } from '../../request-task.store';
import {
  AerVerifyCorsia,
  AerVerifyCorsiaTask,
  AerVerifyCorsiaTaskKey,
  AerVerifyCorsiaTaskPayload,
} from '../../request-task.types';
import { RequestTaskStoreDelegate } from '../store-delegate';

export class AerVerifyCorsiaStoreDelegate implements RequestTaskStoreDelegate {
  static INITIAL_STATE: Partial<AerVerifyCorsia> = {
    verificationReport: {
      verifierDetails: null,
      timeAllocationScope: null,
      generalInformation: null,
      processAnalysis: null,
      opinionStatement: null,
      emissionsReductionClaimVerification: null,
      uncorrectedMisstatements: null,
      uncorrectedNonCompliances: null,
      recommendedImprovements: null,
      verifiersConclusions: null,
      overallDecision: null,
      independentReview: null,
    } as AviationAerCorsiaVerificationReport,
  };

  get payload(): AerVerifyCorsiaTaskPayload | null {
    return this.store.getState().requestTaskItem?.requestTask?.payload as AerVerifyCorsiaTaskPayload;
  }

  get requestTaskType(): RequestTaskDTO['type'] | null {
    return this.store.getState().requestTaskItem?.requestTask?.type;
  }

  get baseFileAttachmentDownloadUrl(): string {
    return `/aviation/tasks/${this.store.requestTaskId}/file-download/attachment`;
  }

  constructor(
    private store: RequestTaskStore,
    private readonly businessErrorService: BusinessErrorService,
  ) {}

  init() {
    if (
      this.payload &&
      !this.payload.verificationReport &&
      this.requestTaskType !== 'AVIATION_AER_CORSIA_APPLICATION_SUBMIT'
    ) {
      this.store.setPayload(
        produce(this.payload, (payload) => {
          payload.verificationReport = {} as AviationAerCorsiaVerificationReport;
        }),
      );
    }

    return this;
  }

  saveAerVerify(
    aerVerifyCorsiaTask: { [key in AerVerifyCorsiaTaskKey]?: AerVerifyCorsiaTask },
    status: TaskItemStatus = 'complete',
  ): Observable<any> {
    const taskKey = Object.keys(aerVerifyCorsiaTask)[0];

    const {
      requestTaskItem: { requestTask },
    } = this.store.getState();

    const payloadToUpdate = produce(this.payload, (draft) => {
      draft.verificationReport[taskKey] = aerVerifyCorsiaTask[taskKey];
      draft.verificationSectionsCompleted[taskKey] = status === 'complete' ? [true] : [false];

      delete draft.verificationReport.verificationBodyDetails;
      delete draft.verificationReport.verificationBodyId;

      if (draft.sendEmailNotification) {
        delete draft.sendEmailNotification;
      }
    });

    const { verificationReport, verificationSectionsCompleted } = payloadToUpdate;

    const reqBody: RequestTaskActionProcessDTO = {
      requestTaskId: requestTask.id,
      requestTaskActionType: getSaveRequestTaskActionTypeForRequestTaskType(requestTask.type),
      requestTaskActionPayload: {
        ...verificationReport,
        verificationSectionsCompleted,
        payloadType: 'AVIATION_AER_CORSIA_SAVE_APPLICATION_VERIFICATION_PAYLOAD',
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
          verificationReport: {
            ...payloadToUpdate.verificationReport,
            verificationBodyDetails: this.payload.verificationReport.verificationBodyDetails,
            verificationBodyId: this.payload.verificationReport.verificationBodyId,
          },
        } as AerVerifyCorsiaTaskPayload);
      }),
    );
  }

  submitAerVerify(): Observable<any> {
    const {
      requestTaskItem: { requestTask },
    } = this.store.getState();

    const reqBody: RequestTaskActionProcessDTO = {
      requestTaskActionType: 'AVIATION_AER_CORSIA_SUBMIT_APPLICATION_VERIFICATION',
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
