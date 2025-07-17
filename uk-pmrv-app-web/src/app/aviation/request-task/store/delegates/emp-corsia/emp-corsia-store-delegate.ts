import { Observable, tap } from 'rxjs';

import {
  allEmpApplicationTasks,
  EmpCorsiaReviewGroup,
  EmpDetermination,
  empReviewGroupMap,
  getAvailableSubTasks,
} from '@aviation/request-task/emp/shared/util/emp.util';
import { EmpStoreDelegate } from '@aviation/request-task/store/delegates/emp/emp-store-delegate';
import {
  amendsRequestedTaskActionTypes,
  getSaveRequestTaskActionTypeForRequestTaskType,
} from '@aviation/request-task/util';
import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchTaskReassignedBadRequest } from '@error/business-errors';
import { catchNotFoundRequest, ErrorCode } from '@error/not-found-error';
import { requestTaskReassignedError, taskNotFoundError } from '@shared/errors/request-task-error';
import { TaskItemStatus } from '@shared/task-list/task-list.interface';
import produce from 'immer';

import {
  EmissionsMonitoringPlanCorsia,
  EmpCorsiaOperatorDetails,
  EmpEmissionsMonitoringApproachCorsia,
  EmpEmissionSourcesCorsia,
  EmpFlightAndAircraftProceduresCorsia,
  EmpIssuanceReviewDecision,
  EmpManagementProceduresCorsia,
  EmpVariationCorsiaDetails,
  EmpVariationDetermination,
  RequestTaskActionPayload,
  RequestTaskActionProcessDTO,
  RequestTaskDTO,
  RequestTaskPayload,
} from 'pmrv-api';

import { RequestTaskStore } from '../../request-task.store';
import {
  EmpCorsiaTask,
  EmpRequestTaskPayload,
  EmpRequestTaskPayloadCorsia,
  EmpTaskKey,
} from '../../request-task.types';
import { EmpCorsiaStoreSideEffectsHandler } from './emp-corsia-store-side-effects.handler';

/** Tasks in the payload root (not inside the `emissionsMonitorinPlan`) and whether they should be saved or not */
const ROOT_EMP_PAYLOAD_TASKS = {
  serviceContactDetails: false,
  empVariationDetails: true,
  changesRequested: true,
  reasonRegulatorLed: true,
};

export class EmpCorsiaStoreDelegate extends EmpStoreDelegate {
  static INITIAL_STATE: Partial<EmissionsMonitoringPlanCorsia> = {
    abbreviations: { exist: null },
    additionalDocuments: { exist: null },
    operatorDetails: {
      operatorName: null,
      flightIdentification: null,
      airOperatingCertificate: null,
      organisationStructure: null,
      activitiesDescription: null,
      subsidiaryCompanyExist: null,
      subsidiaryCompanies: [],
    },
    emissionsMonitoringApproach: {
      monitoringApproachType: null,
    },
    managementProcedures: {
      monitoringReportingRoles: null,
      dataManagement: null,
      recordKeepingAndDocumentation: {
        description: null,
      },
      riskExplanation: {
        description: null,
      },
      empRevisions: {
        description: null,
      },
    },
    emissionSources: {
      aircraftTypes: null,
    },
    flightAndAircraftProcedures: {
      aircraftUsedDetails: null,
      flightListCompletenessDetails: null,
      internationalFlightsDetermination: null,
      operatingStatePairs: null,
      internationalFlightsDeterminationOffset: null,
      internationalFlightsDeterminationNoMonitoring: null,
    },
    methodAProcedures: {
      fuelConsumptionPerFlight: null,
      fuelDensity: null,
    },
    methodBProcedures: {
      fuelConsumptionPerFlight: null,
      fuelDensity: null,
    },
    blockOnBlockOffMethodProcedures: {
      fuelConsumptionPerFlight: null,
    },
    fuelUpliftMethodProcedures: {
      blockHoursPerFlight: null,
      zeroFuelUplift: null,
      fuelUpliftSupplierRecordType: null,
      fuelDensity: null,
    },
    blockHourMethodProcedures: {
      fuelBurnCalculationTypes: null,
      blockHoursMeasurement: null,
      fuelUpliftSupplierRecordType: null,
      fuelDensity: null,
    },
    dataGaps: {
      secondarySourcesDataGapsExist: null,
      secondaryDataSources: null,
      dataGaps: null,
    },
  };

  private sideEffectsHandler = new EmpCorsiaStoreSideEffectsHandler(this.store);

  constructor(
    protected store: RequestTaskStore,
    protected readonly businessErrorService: BusinessErrorService,
  ) {
    super(store, businessErrorService);
  }

  get payload(): EmpRequestTaskPayloadCorsia | null {
    return this.store.getState().requestTaskItem?.requestTask?.payload as EmpRequestTaskPayloadCorsia;
  }

  init() {
    if (this?.payload && !this.payload.emissionsMonitoringPlan) {
      this.store.setPayload(
        produce<EmpRequestTaskPayloadCorsia>(this.payload as EmpRequestTaskPayloadCorsia, (payload) => {
          payload.emissionsMonitoringPlan = {} as any;
        }),
      );
    }

    if (
      ['EMP_VARIATION_CORSIA_APPLICATION_SUBMIT', 'EMP_VARIATION_CORSIA_REGULATOR_LED_APPLICATION_SUBMIT'].includes(
        this.store.getState().requestTaskItem?.requestTask.type,
      ) &&
      Object.keys(this.payload.empSectionsCompleted).length === 0
    ) {
      this.initializeVariationEmpSectionsCompleted();
    }

    return this;
  }

  setManagementProcedures(managementProcedures: EmpManagementProceduresCorsia) {
    const state = this.store.getState();
    const newState = produce(state, (draft) => {
      (
        draft.requestTaskItem.requestTask.payload as EmpRequestTaskPayloadCorsia
      ).emissionsMonitoringPlan.managementProcedures = managementProcedures;
    });
    this.store.setState(newState);
  }

  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  setEmissionSources(emissionSources: EmpEmissionSourcesCorsia) {
    const state = this.store.getState();
    const newState = produce(state, (draft) => {
      (
        draft.requestTaskItem.requestTask.payload as EmpRequestTaskPayloadCorsia
      ).emissionsMonitoringPlan.emissionSources = emissionSources;
    });
    this.store.setState(newState);
  }

  setFlightProcedures(flightAndAircraftProcedures: EmpFlightAndAircraftProceduresCorsia) {
    const state = this.store.getState();

    const newState = produce(state, (draft) => {
      (
        draft.requestTaskItem.requestTask.payload as EmpRequestTaskPayloadCorsia
      ).emissionsMonitoringPlan.flightAndAircraftProcedures = flightAndAircraftProcedures;
    });

    this.store.setState(newState);
  }

  setEmissionsMonitoringApproach(emissionsMonitoringApproach: EmpEmissionsMonitoringApproachCorsia) {
    const state = this.store.getState();
    const newState = produce(state, (draft) => {
      (draft.requestTaskItem.requestTask.payload as any).emissionsMonitoringPlan.emissionsMonitoringApproach =
        emissionsMonitoringApproach;
    });

    this.store.setState(newState);
  }

  saveEmpOverallDecision(decision: EmpDetermination, sectionsCompleted: boolean) {
    const {
      requestTaskItem: { requestTask },
    } = this.store.getState();

    const payloadToUpdate = produce(this.payload, (draft) => {
      delete draft.reviewAttachments;

      draft.determination = decision as any;
      draft.reviewSectionsCompleted.decision = sectionsCompleted;
    });

    let requestTaskActionType: RequestTaskActionProcessDTO['requestTaskActionType'];
    if (requestTask?.type === 'EMP_VARIATION_CORSIA_APPLICATION_REVIEW') {
      requestTaskActionType = 'EMP_VARIATION_CORSIA_SAVE_REVIEW_DETERMINATION';
    } else {
      requestTaskActionType = 'EMP_ISSUANCE_CORSIA_SAVE_REVIEW_DETERMINATION';
    }

    return this.store.tasksService
      .processRequestTaskAction(
        this.constructSaveActionReqBody(
          requestTask,
          payloadToUpdate as EmpRequestTaskPayloadCorsia,
          requestTaskActionType,
        ),
      )
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
            reviewAttachments: this.payload.reviewAttachments,
            serviceContactDetails: this.payload.serviceContactDetails,
          } as EmpRequestTaskPayloadCorsia),
        ),
      );
  }

  saveEmpReviewDecision(decision: EmpIssuanceReviewDecision, taskKey: EmpTaskKey): Observable<any> {
    const {
      requestTaskItem: { requestTask },
    } = this.store.getState();

    const groupKey = empReviewGroupMap[taskKey] as EmpCorsiaReviewGroup;

    const payloadToUpdate = produce(this.payload, (draft) => {
      delete draft.reviewAttachments;

      draft.reviewGroupDecisions = {
        ...draft.reviewGroupDecisions,
        [groupKey]: decision,
      };

      draft.empSectionsCompleted[taskKey] = [true];
      draft.reviewSectionsCompleted[groupKey] = true;

      if (draft.determination?.type === 'APPROVED' && decision.type === 'OPERATOR_AMENDS_NEEDED') {
        delete draft.determination;
        draft.reviewSectionsCompleted['decision'] = false;
      }
    });

    return this.store.tasksService
      .processRequestTaskAction(
        this.constructSaveActionReqBody(
          requestTask,
          payloadToUpdate as EmpRequestTaskPayloadCorsia,
          'EMP_ISSUANCE_CORSIA_SAVE_REVIEW_GROUP_DECISION',
          groupKey,
        ),
      )
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
            reviewAttachments: this.payload.reviewAttachments,
            serviceContactDetails: this.payload.serviceContactDetails,
          } as EmpRequestTaskPayloadCorsia),
        ),
      );
  }

  setOperatorDetails(operatorDetails: EmpCorsiaOperatorDetails) {
    const state = this.store.getState();

    const newState = produce(state, (draft) => {
      (draft.requestTaskItem.requestTask.payload as EmpRequestTaskPayload).emissionsMonitoringPlan.operatorDetails =
        operatorDetails;
    });

    this.store.setState(newState);
  }

  setVariationDetails(variationDetails: EmpVariationCorsiaDetails, variationRegulatorLedReason?: string) {
    const state = this.store.getState();
    const newState = produce(state, (draft) => {
      (draft.requestTaskItem.requestTask.payload as EmpRequestTaskPayloadCorsia).empVariationDetails = variationDetails;
      if (variationRegulatorLedReason) {
        (draft.requestTaskItem.requestTask.payload as EmpRequestTaskPayloadCorsia).reasonRegulatorLed =
          variationRegulatorLedReason;
      }
    });
    this.store.setState(newState);
  }

  saveEmp(
    empCorsiaTask: { [key in EmpTaskKey]?: EmpCorsiaTask },
    status: TaskItemStatus = 'complete',
  ): Observable<any> {
    const notEffectOnOperatorsSectionStatusesPayloadTypes: RequestTaskPayload['payloadType'][] = [
      'EMP_ISSUANCE_CORSIA_APPLICATION_REVIEW_PAYLOAD',
      'EMP_VARIATION_CORSIA_APPLICATION_REVIEW_PAYLOAD',
    ];
    const taskKey = Object.keys(empCorsiaTask)[0];
    const {
      requestTaskItem: { requestTask },
    } = this.store.getState();

    const payloadAfterSideEffects = this.sideEffectsHandler.applySideEffects(empCorsiaTask);

    const payloadToUpdate = produce(payloadAfterSideEffects, (draft) => {
      delete draft.empAttachments;
      delete draft.serviceContactDetails;

      if (draft.sendEmailNotification) {
        delete draft.sendEmailNotification;
      }

      if (Object.keys(ROOT_EMP_PAYLOAD_TASKS).includes(taskKey)) {
        if (ROOT_EMP_PAYLOAD_TASKS[taskKey]) {
          draft[taskKey] = empCorsiaTask[taskKey];
        }
      } else {
        draft.emissionsMonitoringPlan[taskKey] = empCorsiaTask[taskKey];
      }

      if (taskKey === 'empVariationDetails' || taskKey === 'reasonRegulatorLed') {
        draft.empVariationDetailsCompleted =
          status === 'complete'
            ? true
            : // this check is needed for payload types that has no effect on empVariationDetailsCompleted
              notEffectOnOperatorsSectionStatusesPayloadTypes.includes(draft.payloadType)
              ? draft.empVariationDetailsCompleted
              : false;
        if (
          draft.payloadType === 'EMP_VARIATION_CORSIA_APPLICATION_AMENDS_SUBMIT_PAYLOAD' ||
          // this check is needed for payload types that has effect on empVariationDetailsReviewCompleted
          notEffectOnOperatorsSectionStatusesPayloadTypes.includes(draft.payloadType)
        ) {
          draft.empVariationDetailsReviewCompleted = false;
        }
      } else if (taskKey) {
        draft.empSectionsCompleted = draft.empSectionsCompleted || {};
        draft.empSectionsCompleted[taskKey] =
          status === 'complete'
            ? [true]
            : // this check is needed for payload types that has no effect on empSectionsCompleted
              notEffectOnOperatorsSectionStatusesPayloadTypes.includes(draft.payloadType)
              ? draft.empSectionsCompleted[taskKey]
              : [false];
      }

      if (
        draft.payloadType === 'EMP_ISSUANCE_CORSIA_APPLICATION_REVIEW_PAYLOAD' ||
        (draft.payloadType === 'EMP_VARIATION_CORSIA_APPLICATION_SUBMIT_PAYLOAD' &&
          taskKey !== 'empVariationDetails') ||
        draft.payloadType === 'EMP_VARIATION_CORSIA_APPLICATION_REVIEW_PAYLOAD'
      ) {
        if (taskKey !== 'empVariationDetails') {
          draft.reviewSectionsCompleted[empReviewGroupMap[taskKey]] = false;
        }

        if (
          draft.determination?.type === 'APPROVED' ||
          (this.store.getState().requestTaskItem.requestInfo.type === 'EMP_VARIATION_CORSIA' &&
            (draft.determination as EmpVariationDetermination)?.type === 'REJECTED')
        ) {
          delete draft.determination;
          draft.reviewSectionsCompleted.decision = false;
        }
      }

      if (
        [
          'EMP_ISSUANCE_CORSIA_APPLICATION_AMENDS_SUBMIT_PAYLOAD',
          'EMP_VARIATION_CORSIA_APPLICATION_AMENDS_SUBMIT_PAYLOAD',
        ].includes(draft.payloadType)
      ) {
        if (!taskKey) {
          draft.empSectionsCompleted['changesRequested'] = [true];
        } else {
          const empReviewGroupName = empReviewGroupMap[taskKey];

          draft.reviewSectionsCompleted[empReviewGroupName] = false;
        }
      }
    });

    return this.store.tasksService
      .processRequestTaskAction(
        this.constructSaveActionReqBody(
          requestTask,
          payloadToUpdate,
          getSaveRequestTaskActionTypeForRequestTaskType(requestTask.type),
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
          let updatedPayload = payloadToUpdate;
          if (taskKey in ROOT_EMP_PAYLOAD_TASKS) {
            updatedPayload = produce(updatedPayload, (draft) => {
              draft[taskKey] = empCorsiaTask[taskKey];
            });
          }

          this.store.setPayload({
            ...updatedPayload,
            empAttachments: this.payload.empAttachments,
            serviceContactDetails: this.payload.serviceContactDetails,
          } as EmpRequestTaskPayloadCorsia);
        }),
      );
  }

  submitEmp(): Observable<any> {
    const {
      requestTaskItem: { requestTask },
    } = this.store.getState();

    let actionType: RequestTaskActionProcessDTO['requestTaskActionType'];
    let payloadTypeForAmends: RequestTaskActionPayload['payloadType'];

    switch (requestTask?.type) {
      case 'EMP_ISSUANCE_CORSIA_APPLICATION_SUBMIT':
        actionType = 'EMP_ISSUANCE_CORSIA_SUBMIT_APPLICATION';
        break;

      case 'EMP_ISSUANCE_CORSIA_APPLICATION_AMENDS_SUBMIT':
        actionType = 'EMP_ISSUANCE_CORSIA_SUBMIT_APPLICATION_AMEND';
        payloadTypeForAmends = 'EMP_ISSUANCE_CORSIA_SUBMIT_APPLICATION_AMEND_PAYLOAD';
        break;

      case 'EMP_VARIATION_CORSIA_APPLICATION_SUBMIT':
        actionType = 'EMP_VARIATION_CORSIA_SUBMIT_APPLICATION';
        break;

      case 'EMP_VARIATION_CORSIA_APPLICATION_AMENDS_SUBMIT':
        actionType = 'EMP_VARIATION_CORSIA_SUBMIT_APPLICATION_AMEND';
        payloadTypeForAmends = 'EMP_VARIATION_CORSIA_SUBMIT_APPLICATION_AMEND_PAYLOAD';
        break;
    }

    const isAmendsTask = amendsRequestedTaskActionTypes.includes(requestTask.type);

    const { changesRequested, ...empSectionsCompleted } = (requestTask.payload as EmpRequestTaskPayloadCorsia)
      .empSectionsCompleted;

    const reqBody: RequestTaskActionProcessDTO = {
      requestTaskId: requestTask.id,
      requestTaskActionType: actionType,
      requestTaskActionPayload: isAmendsTask
        ? {
            payloadType: payloadTypeForAmends,
            empSectionsCompleted,
          }
        : { payloadType: 'EMPTY_PAYLOAD' },
    };

    return this.store.tasksService.processRequestTaskAction(reqBody).pipe(
      catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
        this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
      ),
      catchTaskReassignedBadRequest(() =>
        this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
      ),
      tap(() => this.setEmpSectionCompletionStatus('submission', [true])),
    );
  }

  initializeVariationEmpSectionsCompleted() {
    const availableSubTasks = getAvailableSubTasks(allEmpApplicationTasks('variation', false, true), this.payload);

    const payloadToUpdate = produce(this.payload, (draft) => {
      availableSubTasks.forEach((task) => {
        draft.empSectionsCompleted[task] = [true];

        if (draft.reviewSectionsCompleted) {
          const groupKey = empReviewGroupMap[task];
          draft.reviewSectionsCompleted[groupKey] = true;
        }
      });
    });

    this.store.setPayload({
      ...payloadToUpdate,
    } as EmpRequestTaskPayloadCorsia);
  }

  get baseFileAttachmentDownloadUrl(): string {
    return `/aviation/tasks/${this.store.requestTaskId}/file-download/attachment`;
  }

  private constructSaveActionReqBody(
    requestTask: RequestTaskDTO,
    payloadToUpdate: EmpRequestTaskPayloadCorsia,
    requestTaskActionType: RequestTaskActionProcessDTO['requestTaskActionType'],
    key?: EmpCorsiaReviewGroup,
  ): RequestTaskActionProcessDTO {
    switch (requestTaskActionType) {
      case 'EMP_VARIATION_CORSIA_SAVE_APPLICATION_REGULATOR_LED':
        return {
          requestTaskId: requestTask.id,
          requestTaskActionType: requestTaskActionType,
          requestTaskActionPayload: {
            emissionsMonitoringPlan: payloadToUpdate.emissionsMonitoringPlan,
            empVariationDetails: payloadToUpdate.empVariationDetails,
            empVariationDetailsCompleted: payloadToUpdate.empVariationDetailsCompleted,
            empSectionsCompleted: payloadToUpdate.empSectionsCompleted,
            reasonRegulatorLed: payloadToUpdate.reasonRegulatorLed,
            payloadType: 'EMP_VARIATION_CORSIA_SAVE_APPLICATION_REGULATOR_LED_PAYLOAD',
          },
        };

      case 'EMP_ISSUANCE_CORSIA_SAVE_APPLICATION':
        return {
          requestTaskId: requestTask.id,
          requestTaskActionType: requestTaskActionType,
          requestTaskActionPayload: {
            ...payloadToUpdate,
            payloadType: 'EMP_ISSUANCE_CORSIA_SAVE_APPLICATION_PAYLOAD',
          },
        };

      case 'EMP_ISSUANCE_CORSIA_SAVE_APPLICATION_REVIEW':
        return {
          requestTaskId: requestTask.id,
          requestTaskActionType: requestTaskActionType,
          requestTaskActionPayload: {
            emissionsMonitoringPlan: payloadToUpdate.emissionsMonitoringPlan,
            empSectionsCompleted: payloadToUpdate.empSectionsCompleted,
            reviewSectionsCompleted: payloadToUpdate.reviewSectionsCompleted,
            payloadType: 'EMP_ISSUANCE_CORSIA_SAVE_APPLICATION_REVIEW_PAYLOAD',
          },
        };

      case 'EMP_VARIATION_CORSIA_SAVE_APPLICATION_REVIEW':
        return {
          requestTaskId: requestTask.id,
          requestTaskActionType: requestTaskActionType,
          requestTaskActionPayload: {
            emissionsMonitoringPlan: payloadToUpdate.emissionsMonitoringPlan,
            empSectionsCompleted: payloadToUpdate.empSectionsCompleted,
            reviewSectionsCompleted: payloadToUpdate.reviewSectionsCompleted,
            empVariationDetails: payloadToUpdate.empVariationDetails,
            empVariationDetailsCompleted: payloadToUpdate.empVariationDetailsCompleted,
            empVariationDetailsReviewCompleted: payloadToUpdate.empVariationDetailsReviewCompleted,
            payloadType: 'EMP_VARIATION_CORSIA_SAVE_APPLICATION_REVIEW_PAYLOAD',
          },
        };

      case 'EMP_VARIATION_CORSIA_SAVE_APPLICATION':
        return {
          requestTaskId: requestTask.id,
          requestTaskActionType: requestTaskActionType,
          requestTaskActionPayload: {
            ...payloadToUpdate,
            payloadType: 'EMP_VARIATION_CORSIA_SAVE_APPLICATION_PAYLOAD',
          },
        };

      case 'EMP_ISSUANCE_CORSIA_SAVE_REVIEW_GROUP_DECISION':
        return {
          requestTaskId: requestTask.id,
          requestTaskActionType: requestTaskActionType,
          requestTaskActionPayload: {
            reviewGroup: key as any,
            decision: payloadToUpdate.reviewGroupDecisions[key],
            reviewSectionsCompleted: payloadToUpdate.reviewSectionsCompleted,
            empSectionsCompleted: payloadToUpdate.empSectionsCompleted,
            payloadType: 'EMP_ISSUANCE_CORSIA_SAVE_REVIEW_GROUP_DECISION_PAYLOAD',
          },
        };

      case 'EMP_ISSUANCE_CORSIA_SAVE_REVIEW_DETERMINATION':
        return {
          requestTaskId: requestTask.id,
          requestTaskActionType: requestTaskActionType,
          requestTaskActionPayload: {
            determination: payloadToUpdate.determination,
            reviewSectionsCompleted: payloadToUpdate.reviewSectionsCompleted,
            payloadType: 'EMP_ISSUANCE_CORSIA_SAVE_REVIEW_DETERMINATION_PAYLOAD',
          },
        };

      case 'EMP_ISSUANCE_CORSIA_SAVE_APPLICATION_AMEND':
        return {
          requestTaskId: requestTask.id,
          requestTaskActionType: requestTaskActionType,
          requestTaskActionPayload: {
            emissionsMonitoringPlan: payloadToUpdate.emissionsMonitoringPlan,
            empSectionsCompleted: payloadToUpdate.empSectionsCompleted,
            reviewSectionsCompleted: payloadToUpdate.reviewSectionsCompleted,
            payloadType: 'EMP_ISSUANCE_CORSIA_SAVE_APPLICATION_AMEND_PAYLOAD',
          },
        };

      case 'EMP_VARIATION_CORSIA_SAVE_REVIEW_GROUP_DECISION':
        return {
          requestTaskId: requestTask.id,
          requestTaskActionType: requestTaskActionType,
          requestTaskActionPayload: {
            group: key as any,
            decision: payloadToUpdate.reviewGroupDecisions[key],
            reviewSectionsCompleted: payloadToUpdate.reviewSectionsCompleted,
            empSectionsCompleted: payloadToUpdate.empSectionsCompleted,
            payloadType: 'EMP_VARIATION_CORSIA_SAVE_REVIEW_GROUP_DECISION_PAYLOAD',
          },
        };

      case 'EMP_VARIATION_CORSIA_SAVE_DETAILS_REVIEW_GROUP_DECISION':
        return {
          requestTaskId: requestTask.id,
          requestTaskActionType: requestTaskActionType,
          requestTaskActionPayload: {
            decision: payloadToUpdate.empVariationDetailsReviewDecision,
            empVariationDetailsCompleted: payloadToUpdate.empVariationDetailsCompleted,
            empVariationDetailsReviewCompleted: payloadToUpdate.empVariationDetailsReviewCompleted,
            reviewSectionsCompleted: payloadToUpdate.reviewSectionsCompleted,
            payloadType: 'EMP_VARIATION_CORSIA_SAVE_DETAILS_REVIEW_GROUP_DECISION_PAYLOAD',
          },
        };

      case 'EMP_VARIATION_CORSIA_SAVE_REVIEW_DETERMINATION':
        return {
          requestTaskId: requestTask.id,
          requestTaskActionType: requestTaskActionType,
          requestTaskActionPayload: {
            determination: payloadToUpdate.determination,
            reviewSectionsCompleted: payloadToUpdate.reviewSectionsCompleted,
            payloadType: 'EMP_VARIATION_CORSIA_SAVE_REVIEW_DETERMINATION_PAYLOAD',
          },
        };

      case 'EMP_VARIATION_CORSIA_SAVE_APPLICATION_AMEND':
        return {
          requestTaskId: requestTask.id,
          requestTaskActionType: requestTaskActionType,
          requestTaskActionPayload: {
            emissionsMonitoringPlan: payloadToUpdate.emissionsMonitoringPlan,
            empSectionsCompleted: payloadToUpdate.empSectionsCompleted,
            empVariationDetails: payloadToUpdate.empVariationDetails,
            empVariationDetailsCompleted: payloadToUpdate.empVariationDetailsCompleted,
            empVariationDetailsReviewCompleted: payloadToUpdate.empVariationDetailsReviewCompleted,
            reviewSectionsCompleted: payloadToUpdate.reviewSectionsCompleted,
            payloadType: 'EMP_VARIATION_CORSIA_SAVE_APPLICATION_AMEND_PAYLOAD',
          },
        };
    }
  }
}
