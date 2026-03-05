import { Injectable } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';

import { distinctUntilChanged, EMPTY, first, map, Observable, of, switchMap, tap } from 'rxjs';

import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchTaskReassignedBadRequest } from '@error/business-errors';
import { catchNotFoundRequest, ErrorCode } from '@error/not-found-error';
import {
  requestTaskReassignedError,
  taskNotFoundError,
  taskSubmitNotFoundError,
} from '@shared/errors/request-task-error';
import { StatusKey } from '@tasks/aer/core/aer-task.type';
import { TasksHelperService } from '@tasks/shared/services/tasks-helper.service';
import { CommonTasksState } from '@tasks/store/common-tasks.state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import {
  Aer,
  AerApplicationAmendsSubmitRequestTaskPayload,
  AerApplicationReviewRequestTaskPayload,
  AerApplicationSubmitRequestTaskPayload,
  AerApplicationVerificationSubmitRequestTaskPayload,
  AerInherentReceivingTransferringInstallation,
  AerVerificationReportDataReviewDecision,
  AerVerificationReturnToOperatorRequestTaskActionPayload,
  CalculationOfCO2Emissions,
  CalculationRegionalDataCalculationMethod,
  CalculationSourceStreamEmission,
  ChargingZoneDTO,
  ReportingDataService,
  RequestTaskActionPayload,
  RequestTaskActionProcessDTO,
  SourceStreamCalculationParametersInfo,
  SourceStreamsService,
  SourceStreamTypeCategory,
  TasksService,
} from 'pmrv-api';

import { AER_AMEND_STATUS_PREFIX, amendTasksPerReviewSection } from './aer.amend.types';

const sourceStreamEmissionTypes = [
  'COMBUSTION_COMMERCIAL_STANDARD_FUELS',
  'COMBUSTION_OTHER_GASEOUS_LIQUID_FUELS',
  'COMBUSTION_SOLID_FUELS',
  'COMBUSTION_FLARES',
  'OTHER',
];

@Injectable({ providedIn: 'root' })
export class AerService extends TasksHelperService {
  constructor(
    store: CommonTasksStore,
    tasksService: TasksService,
    businessErrorService: BusinessErrorService,
    private readonly reportingDataService: ReportingDataService,
    private readonly sourceStreamsService: SourceStreamsService,
  ) {
    super(store, tasksService, businessErrorService);
  }

  private get attachments() {
    let attachments: { [key: string]: string };
    const requestTaskType = this.store.requestTaskType;
    switch (requestTaskType) {
      case 'AER_APPLICATION_VERIFICATION_SUBMIT':
      case 'AER_AMEND_APPLICATION_VERIFICATION_SUBMIT':
      case 'AER_APPLICATION_SUBMIT':
        attachments = (
          this.store.getValue().requestTaskItem.requestTask.payload as AerApplicationSubmitRequestTaskPayload
        ).aerAttachments;
        break;
      case 'AER_WAIT_FOR_AMENDS':
      case 'AER_APPLICATION_AMENDS_SUBMIT':
      case 'AER_APPLICATION_REVIEW': {
        attachments = {
          ...(this.store.getValue().requestTaskItem.requestTask.payload as AerApplicationReviewRequestTaskPayload)
            .reviewAttachments,
          ...(this.store.getValue().requestTaskItem.requestTask.payload as AerApplicationReviewRequestTaskPayload)
            .aerAttachments,
        };
        break;
      }
      default:
        throw Error('Unhandled task payload type: ' + requestTaskType);
    }
    return attachments;
  }

  private get verificationAttachments() {
    return (
      this.store.getValue().requestTaskItem.requestTask.payload as AerApplicationVerificationSubmitRequestTaskPayload
    )?.verificationAttachments;
  }

  getMappedPayload<T>(pathTree: string[]): Observable<T> {
    return this.store.payload$.pipe(
      first(),
      map((payload) =>
        pathTree.reduce((currentProp, pathString) => {
          return currentProp?.[pathString];
        }, payload),
      ),
    ) as Observable<T>;
  }

  getTask<K extends keyof Aer>(aerTask: K): Observable<Aer[K]> {
    return this.getPayload().pipe(
      map((x) => x?.['aer']?.[aerTask]),
      distinctUntilChanged(),
    );
  }

  getDownloadUrlFiles(files: string[], isVerification = false): { downloadUrl: string; fileName: string }[] {
    const attachments: { [key: string]: string } = isVerification ? this.verificationAttachments : this.attachments;
    const url = this.getBaseFileDownloadUrl();
    return (
      files?.map((id) => ({
        downloadUrl: url + `${id}`,
        fileName: attachments[id],
      })) ?? []
    );
  }

  get requestTaskId() {
    return this.store.requestTaskId;
  }

  get competentAuthority$() {
    return this.store.requestInfo$.pipe(map((info) => info.competentAuthority));
  }

  get yearEqualAfter2025$(): Observable<boolean> {
    return this.store.requestInfo$.pipe(map((info) => info?.requestMetadata?.['year'] >= 2025));
  }

  get requestAccountId$() {
    return this.store.requestInfo$.pipe(map((info) => info.accountId));
  }

  get requestId() {
    return this.store.requestId;
  }

  get requestTaskType() {
    return this.store.requestTaskType;
  }

  get aerInherentInstallations$(): Observable<AerInherentReceivingTransferringInstallation[]> {
    return this.getMappedPayload<AerInherentReceivingTransferringInstallation[]>([
      'aer',
      'monitoringApproachEmissions',
      'INHERENT_CO2',
      'inherentReceivingTransferringInstallations',
    ]);
  }

  get reviewGroupsForAmend$(): Observable<any[]> {
    return this.getPayload().pipe(map((payload) => Object.keys(payload?.reviewGroupDecisions ?? [])));
  }
  get reviewGroupsForAmendData$(): Observable<any[]> {
    return this.getPayload().pipe(map((payload) => payload?.reviewGroupDecisions));
  }

  getSourceStreamEmission$(index): Observable<CalculationSourceStreamEmission> {
    return this.getPayload().pipe(
      map(
        (payload) =>
          (payload.aer.monitoringApproachEmissions.CALCULATION_CO2 as CalculationOfCO2Emissions)
            ?.sourceStreamEmissions?.[index],
      ),
    );
  }

  sourceStreamEmissionType$ = this.getTask('sourceStreams').pipe(
    first(),
    map((sourceStreams) => sourceStreams.some((sourceStream) => sourceStreamEmissionTypes.includes(sourceStream.type))),
  );

  monitoringTiers$(index: number): Observable<boolean> {
    return this.getSourceStreamEmission$(index).pipe(
      map((sourceStreamEmission) => {
        return (
          ['TIER_2', 'TIER_2A'].includes(
            sourceStreamEmission?.parameterMonitoringTiers?.find((tier) => tier.type === 'EMISSION_FACTOR')?.tier,
          ) &&
          ['TIER_2A'].includes(
            sourceStreamEmission?.parameterMonitoringTiers?.find((tier) => tier.type === 'NET_CALORIFIC_VALUE')?.tier,
          ) &&
          (sourceStreamEmission?.parameterCalculationMethod as CalculationRegionalDataCalculationMethod)
            ?.fuelMeteringConditionType === 'CELSIUS_15'
        );
      }),
    );
  }

  getIsSm3$(index$: Observable<number>): Observable<boolean> {
    return index$.pipe(
      first(),
      switchMap((index) =>
        this.sourceStreamEmissionType$.pipe(
          switchMap((isSm3) => {
            if (isSm3) {
              return this.monitoringTiers$(index);
            }
            return of(false);
          }),
          map((isSm3) => isSm3),
          distinctUntilChanged(),
        ),
      ),
    );
  }

  getIsSm3(index$: Observable<number>) {
    return toSignal(this.getIsSm3$(index$));
  }

  postTaskSave(
    value: any,
    attachments?: { [key: string]: string },
    statusValue?: boolean | boolean[],
    statusKey?: StatusKey,
  ) {
    const state = this.store.getState();

    const requestTaskType = state.requestTaskItem.requestTask.type;

    const getReviewSectionsCompleted = (reviewSectionsCompleted) => {
      const reviewSectionCompletedAffected = Object.keys(amendTasksPerReviewSection).find((key) =>
        amendTasksPerReviewSection[key].includes(statusKey),
      );

      const result = Object.keys(reviewSectionsCompleted).reduce((acc, key) => {
        if (key !== reviewSectionCompletedAffected) {
          acc[key] = reviewSectionsCompleted[key];
        }
        return acc;
      }, {});

      return result;
    };

    let actionType: RequestTaskActionProcessDTO['requestTaskActionType'];

    switch (requestTaskType) {
      case 'AER_APPLICATION_SUBMIT':
        actionType = 'AER_SAVE_APPLICATION';
        break;
      case 'AER_APPLICATION_AMENDS_SUBMIT':
        actionType = 'AER_SAVE_APPLICATION_AMEND';
        break;
    }

    return this.store.pipe(
      first(),
      switchMap((state) => {
        const oldAer = (state.requestTaskItem.requestTask.payload as AerApplicationSubmitRequestTaskPayload).aer;
        if (statusKey === 'prtrCodes') {
          delete oldAer.pollutantRegisterActivities;
        }

        const postAerState = {
          ...state,
          requestTaskItem: {
            ...state.requestTaskItem,
            requestTask: {
              ...state.requestTaskItem.requestTask,
              payload: {
                ...state.requestTaskItem.requestTask.payload,
                aer: {
                  ...oldAer,
                  ...value,
                },
                aerAttachments: {
                  ...(state.requestTaskItem.requestTask.payload as AerApplicationSubmitRequestTaskPayload)
                    ?.aerAttachments,
                  ...attachments,
                },
                aerSectionsCompleted: {
                  ...(state.requestTaskItem.requestTask.payload as AerApplicationSubmitRequestTaskPayload)
                    ?.aerSectionsCompleted,
                  ...(statusKey
                    ? { [statusKey]: Array.isArray(statusValue) ? statusValue : [statusValue] }
                    : undefined),
                },
                ...(requestTaskType === 'AER_APPLICATION_AMENDS_SUBMIT'
                  ? {
                      reviewSectionsCompleted: {
                        ...getReviewSectionsCompleted(
                          (state.requestTaskItem.requestTask.payload as AerApplicationAmendsSubmitRequestTaskPayload)
                            ?.reviewSectionsCompleted,
                        ),
                      },
                    }
                  : null),
                verificationPerformed:
                  (state.requestTaskItem.requestTask.payload as AerApplicationSubmitRequestTaskPayload).permitType ===
                  'GHGE'
                    ? false
                    : (state.requestTaskItem.requestTask.payload as AerApplicationSubmitRequestTaskPayload)
                        .verificationPerformed,
              } as AerApplicationSubmitRequestTaskPayload,
            },
          },
        };

        return this.postAer(postAerState, actionType);
      }),
    );
  }

  postVerificationTaskSave(
    value: any,
    statusValue?: boolean | boolean[],
    statusKey?: StatusKey,
    attachments?: { [key: string]: string },
  ) {
    return this.store.pipe(
      first(),
      switchMap((state) =>
        this.postAer(
          {
            ...state,
            requestTaskItem: {
              ...state.requestTaskItem,
              requestTask: {
                ...state.requestTaskItem.requestTask,
                payload: {
                  ...state.requestTaskItem.requestTask.payload,
                  verificationReport: {
                    ...(state.requestTaskItem.requestTask.payload as AerApplicationVerificationSubmitRequestTaskPayload)
                      .verificationReport,
                    ...value,
                  },
                  verificationSectionsCompleted: {
                    ...(state.requestTaskItem.requestTask.payload as AerApplicationVerificationSubmitRequestTaskPayload)
                      ?.verificationSectionsCompleted,
                    ...(statusKey
                      ? { [statusKey]: Array.isArray(statusValue) ? statusValue : [statusValue] }
                      : undefined),
                  },
                  verificationAttachments: {
                    ...(state.requestTaskItem.requestTask.payload as AerApplicationVerificationSubmitRequestTaskPayload)
                      ?.verificationAttachments,
                    ...attachments,
                  },
                } as AerApplicationVerificationSubmitRequestTaskPayload,
              },
            },
          },
          'AER_SAVE_APPLICATION_VERIFICATION',
        ),
      ),
    );
  }

  postGroupDecisionReview(
    value: any,
    dataType: AerVerificationReportDataReviewDecision['reviewDataType'],
    groupKey: StatusKey,
    attachments?: { uuid: string; file: File }[],
  ) {
    return this.store.pipe(
      first(),
      switchMap((state) =>
        this.tasksService.processRequestTaskAction({
          requestTaskActionType: 'AER_SAVE_REVIEW_GROUP_DECISION',
          requestTaskId: state.requestTaskItem.requestTask.id,
          requestTaskActionPayload: {
            payloadType: 'AER_SAVE_REVIEW_GROUP_DECISION_PAYLOAD',
            group: groupKey,
            decision: {
              ...value,
              reviewDataType: dataType,
            },
            reviewSectionsCompleted: {
              ...(state.requestTaskItem.requestTask.payload as AerApplicationReviewRequestTaskPayload)
                ?.reviewSectionsCompleted,
              ...{ [groupKey]: true },
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
                reviewGroupDecisions: {
                  ...(state.requestTaskItem.requestTask.payload as AerApplicationReviewRequestTaskPayload)
                    .reviewGroupDecisions,
                  [groupKey]: {
                    reviewDataType: dataType,
                    ...value,
                  },
                },
                reviewAttachments: {
                  ...(state.requestTaskItem.requestTask.payload as AerApplicationReviewRequestTaskPayload)
                    .reviewAttachments,
                  ...attachments?.reduce((result, item) => ({ ...result, [item.uuid]: item.file.name }), {}),
                },
                reviewSectionsCompleted: {
                  ...(state.requestTaskItem.requestTask.payload as AerApplicationReviewRequestTaskPayload)
                    ?.reviewSectionsCompleted,
                  ...{ [groupKey]: true },
                },
              } as AerApplicationReviewRequestTaskPayload,
            },
          },
        });
      }),
    );
  }

  postAer(state: CommonTasksState, actionType: RequestTaskActionProcessDTO['requestTaskActionType']) {
    const payload = state.requestTaskItem.requestTask.payload as AerApplicationSubmitRequestTaskPayload;
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

  postSubmit(actionType: RequestTaskActionProcessDTO['requestTaskActionType'], payload?: any) {
    return this.store.pipe(
      first(),
      switchMap((state) =>
        this.tasksService.processRequestTaskAction({
          requestTaskActionType: actionType,
          requestTaskId: state.requestTaskItem.requestTask.id,
          requestTaskActionPayload: this.createRequestTaskActionPayload(
            actionType,
            payload || state.requestTaskItem.requestTask.payload,
          ),
        }),
      ),
      catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
        actionType === 'AER_SUBMIT_APPLICATION_VERIFICATION'
          ? this.businessErrorService.showErrorForceNavigation(taskSubmitNotFoundError)
          : this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
      ),
      catchTaskReassignedBadRequest(() =>
        this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
      ),
    );
  }

  private createRequestTaskActionPayload(
    actionType: RequestTaskActionProcessDTO['requestTaskActionType'],
    payload?: any,
  ) {
    switch (actionType) {
      case 'AER_SAVE_APPLICATION':
        return {
          payloadType: 'AER_SAVE_APPLICATION_PAYLOAD',
          aer: payload.aer,
          aerSectionsCompleted: payload.aerSectionsCompleted,
        } as RequestTaskActionPayload;
      case 'AER_SAVE_APPLICATION_AMEND':
        return {
          payloadType: 'AER_SAVE_APPLICATION_AMEND_PAYLOAD',
          aer: payload.aer,
          aerSectionsCompleted: payload.aerSectionsCompleted,
          reviewSectionsCompleted: payload.reviewSectionsCompleted,
        } as RequestTaskActionPayload;
      case 'AER_SUBMIT_APPLICATION_AMEND':
        return {
          payloadType: 'AER_SUBMIT_APPLICATION_AMEND_PAYLOAD',
          aerSectionsCompleted: {
            ...Object.keys((payload as AerApplicationSubmitRequestTaskPayload)?.aerSectionsCompleted ?? [])
              .filter((statusKey) => !statusKey.startsWith(AER_AMEND_STATUS_PREFIX))
              .reduce(
                (res, key) => (
                  (res[key] = (payload as AerApplicationSubmitRequestTaskPayload)?.aerSectionsCompleted[key]), res
                ),
                {},
              ),
          },
        } as RequestTaskActionPayload;

      case 'AER_SAVE_APPLICATION_VERIFICATION':
        return {
          ...(payload as AerApplicationVerificationSubmitRequestTaskPayload).verificationReport,
          payloadType: 'AER_SAVE_APPLICATION_VERIFICATION_PAYLOAD',
          verificationSectionsCompleted: payload.verificationSectionsCompleted,
        } as RequestTaskActionPayload;
      case 'AER_REQUEST_VERIFICATION':
        return {
          payloadType: 'AER_REQUEST_VERIFICATION_PAYLOAD',
          verificationSectionsCompleted: payload.verificationSectionsCompleted,
        } as RequestTaskActionPayload;
      case 'AER_REQUEST_AMENDS_VERIFICATION':
        return {
          payloadType: 'AER_REQUEST_AMENDS_VERIFICATION_PAYLOAD',
          verificationSectionsCompleted: payload.verificationSectionsCompleted,
        } as RequestTaskActionPayload;
      case 'AER_VERIFICATION_RETURN_TO_OPERATOR':
        return {
          payloadType: 'AER_VERIFICATION_RETURN_TO_OPERATOR_PAYLOAD',
          changesRequired: payload.changesRequired,
        } as AerVerificationReturnToOperatorRequestTaskActionPayload;

      default:
        return {
          payloadType: 'EMPTY_PAYLOAD',
        } as RequestTaskActionPayload;
    }
  }

  getCalculationParameterTypes(sourceStreamType): Observable<SourceStreamCalculationParametersInfo> {
    return sourceStreamType
      ? this.reportingDataService.getSourceStreamEmissionsCalculationInfoByType(sourceStreamType)
      : EMPTY;
  }

  getSourceStreamCategories(): Observable<Array<SourceStreamTypeCategory>> {
    return this.sourceStreamsService.getSourceStreamCategories();
  }

  getDeliveryZones(postCode: string): Observable<Array<ChargingZoneDTO>> {
    return this.reportingDataService.getChargingZonesByPostCode(postCode);
  }
}
