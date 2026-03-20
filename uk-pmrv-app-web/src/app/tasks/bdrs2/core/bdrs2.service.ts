import { computed, Injectable, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';

import { first, map, Observable, switchMap, tap } from 'rxjs';

import { AuthStore, selectUserRoleType } from '@core/store';
import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchTaskReassignedBadRequest } from '@error/business-errors';
import { catchNotFoundRequest, ErrorCode } from '@error/not-found-error';
import {
  requestTaskReassignedError,
  taskNotFoundError,
  taskSubmitNotFoundError,
} from '@shared/errors/request-task-error';
import { CapitalizeFirstPipe } from '@shared/pipes/capitalize-first.pipe';
import { ItemNamePipe } from '@shared/pipes/item-name.pipe';
import { AttachedFile } from '@shared/types/attached-file.type';
import { TasksHelperService } from '@tasks/shared/services/tasks-helper.service';
import { CommonTasksState } from '@tasks/store/common-tasks.state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import {
  BDRS2ApplicationAmendsSubmitRequestTaskPayload,
  BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload,
  BDRS2ApplicationSubmitRequestTaskPayload,
  BDRS2ApplicationVerificationSubmitRequestTaskPayload,
  BDRS2RequestMetadata,
  BDRS2VerificationReportDataRegulatorReviewDecision,
  InstallationAccountViewService,
  RequestMetadata,
  RequestTaskActionPayload,
  RequestTaskActionProcessDTO,
  RequestTaskDTO,
  TasksService,
} from 'pmrv-api';

@Injectable()
export class BdrS2Service extends TasksHelperService {
  constructor(
    store: CommonTasksStore,
    tasksService: TasksService,
    businessErrorService: BusinessErrorService,
    private readonly itemNamePipe: ItemNamePipe,
    private installationAccountViewService: InstallationAccountViewService,
    private authStore: AuthStore,
    private capitalizeFirstPipe: CapitalizeFirstPipe,
  ) {
    super(store, tasksService, businessErrorService);
  }

  get payload$(): Observable<BDRS2ApplicationSubmitRequestTaskPayload> {
    return this.store.payload$ as Observable<BDRS2ApplicationSubmitRequestTaskPayload>;
  }

  get payload(): Signal<BDRS2ApplicationSubmitRequestTaskPayload> {
    return toSignal(this.payload$);
  }

  get requestTaskType(): Signal<RequestTaskDTO['type']> {
    return toSignal(this.requestTaskType$);
  }

  get requestMetadata(): Signal<RequestMetadata> {
    return toSignal(this.requestMetadata$);
  }

  get requestId() {
    return this.store.requestId;
  }

  get requestAccountId$() {
    return this.store.requestInfo$.pipe(map((info) => info.accountId));
  }

  get competentAuthority$() {
    return this.store.requestInfo$.pipe(map((info) => info.competentAuthority));
  }

  get title$(): Observable<string> {
    return this.requestTaskItem$.pipe(
      map((requestTaskItem) => {
        const year = (requestTaskItem?.requestInfo?.requestMetadata as BDRS2RequestMetadata)?.year;
        return this.itemNamePipe.transform(requestTaskItem?.requestTask?.type, year, String(year));
      }),
    );
  }
  get title(): Signal<string> {
    return toSignal(this.title$);
  }

  get isEditable(): Signal<boolean> {
    return toSignal(this.isEditable$);
  }

  get daysRemaining(): Signal<number> {
    return toSignal(this.daysRemaining$);
  }

  installationAccountId$ = this.requestTaskItem$.pipe(
    switchMap((requestTaskItem) =>
      this.installationAccountViewService.getInstallationAccountById(requestTaskItem.requestInfo.accountId),
    ),
  );

  userRoleType = toSignal(this.authStore.pipe(selectUserRoleType), { initialValue: null });

  installationAccount = toSignal(this.installationAccountId$, { initialValue: null });
  installationName = computed(() =>
    this.installationAccount()?.accountPermitDto.account.name
      ? this.stripSpecialChars(this.installationAccount()?.accountPermitDto.account.name)
      : 'Unknown',
  );

  fileName(fileVersion: number, suffix: string): string {
    const bdrs2Id = this.requestId;
    const shortInstallationName = this.installationName().substring(0, 10);
    const roleType = this.capitalizeFirstPipe.transform(this.userRoleType());
    return `${bdrs2Id}-v${fileVersion}-uploaded by ${roleType}-${shortInstallationName}${suffix}`;
  }

  stripSpecialChars(str: string): string {
    return str.replace(/[^a-zA-Z0-9 _-]/g, '');
  }

  getOperatorDownloadUrlFiles(files: string[]): AttachedFile[] {
    const attachments: { [key: string]: string } = (
      this.store.getValue().requestTaskItem.requestTask.payload as BDRS2ApplicationSubmitRequestTaskPayload
    )?.bdrs2Attachments;
    const url = this.getBaseFileDownloadUrl();
    return (
      files?.map((id) => ({
        downloadUrl: url + `${id}`,
        fileName: attachments[id],
      })) ?? []
    );
  }

  getVerifierDownloadUrlFiles(files: string[]): AttachedFile[] {
    const attachments: { [key: string]: string } = (
      this.store.getValue().requestTaskItem.requestTask.payload as BDRS2ApplicationVerificationSubmitRequestTaskPayload
    )?.verificationAttachments;
    const url = this.getBaseFileDownloadUrl();
    return (
      files?.map((id) => ({
        downloadUrl: url + `${id}`,
        fileName: attachments[id],
      })) ?? []
    );
  }

  getVerifierDownloadUrlFile(file: string): AttachedFile {
    const attachments: { [key: string]: string } = (
      this.store.getValue().requestTaskItem.requestTask.payload as BDRS2ApplicationVerificationSubmitRequestTaskPayload
    )?.verificationAttachments;
    const url = this.getBaseFileDownloadUrl();

    return file
      ? {
          downloadUrl: url + `${file}`,
          fileName: attachments[file],
        }
      : null;
  }

  getOperatorDownloadUrlBdrFile(bdrFile: string): AttachedFile {
    const attachments: { [key: string]: string } = (
      this.store.getValue().requestTaskItem.requestTask.payload as BDRS2ApplicationSubmitRequestTaskPayload
    )?.bdrs2Attachments;
    const url = this.getBaseFileDownloadUrl();

    return bdrFile
      ? {
          downloadUrl: url + `${bdrFile}`,
          fileName: attachments[bdrFile],
        }
      : null;
  }

  getRegulatorDownloadUrlFiles(files: string[]): AttachedFile[] {
    const attachments: { [key: string]: string } = (
      this.store.getValue().requestTaskItem.requestTask
        .payload as BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload
    )?.regulatorReviewAttachments;
    const url = this.getBaseFileDownloadUrl();
    return (
      files?.map((id) => ({
        downloadUrl: url + `${id}`,
        fileName: attachments[id],
      })) ?? []
    );
  }

  postTaskSave(
    value: any,
    attachments?: { [key: string]: string },
    statusValue?: boolean,
    statusKey?: string | 'sendReport',
  ) {
    const state = this.store.getState();

    const requestTaskType = state.requestTaskItem.requestTask.type;

    let actionType: RequestTaskActionProcessDTO['requestTaskActionType'];

    switch (requestTaskType) {
      case 'BDRS2_APPLICATION_SUBMIT':
        actionType = 'BDRS2_SAVE_APPLICATION';
        break;
      case 'BDRS2_APPLICATION_AMENDS_SUBMIT':
        actionType = 'BDRS2_APPLICATION_AMENDS_SAVE';
        break;
    }

    return this.store.pipe(
      first(),
      switchMap((state) => {
        const bdrs2FileVersion = (state.requestTaskItem.requestTask.payload as BDRS2ApplicationSubmitRequestTaskPayload)
          ?.bdrs2FileVersion;
        const regSectionsCompleted = (
          state.requestTaskItem.requestTask.payload as BDRS2ApplicationAmendsSubmitRequestTaskPayload
        )?.regulatorReviewSectionsCompleted;

        const postBdrs2State = {
          ...state,
          requestTaskItem: {
            ...state.requestTaskItem,
            requestTask: {
              ...state.requestTaskItem.requestTask,
              payload: {
                ...state.requestTaskItem.requestTask.payload,
                bdrs2: {
                  ...(state.requestTaskItem.requestTask.payload as BDRS2ApplicationSubmitRequestTaskPayload).bdrs2,
                  ...value,
                },
                bdrs2Attachments: {
                  ...(state.requestTaskItem.requestTask.payload as BDRS2ApplicationSubmitRequestTaskPayload)
                    ?.bdrs2Attachments,
                  ...attachments,
                },
                bdrs2SectionsCompleted: {
                  ...(state.requestTaskItem.requestTask.payload as BDRS2ApplicationSubmitRequestTaskPayload)
                    ?.bdrs2SectionsCompleted,
                  ...(statusKey ? { [statusKey]: statusValue } : undefined),
                },
                verificationPerformed: false,
                bdrs2FileVersion: bdrs2FileVersion !== undefined ? bdrs2FileVersion : undefined,
                ...(requestTaskType === 'BDRS2_APPLICATION_AMENDS_SUBMIT'
                  ? {
                      regulatorReviewSectionsCompleted: {
                        ...regSectionsCompleted,
                      },
                    }
                  : null),
              } as BDRS2ApplicationSubmitRequestTaskPayload,
            },
          },
        };

        return this.postBdrs2(postBdrs2State, actionType);
      }),
    );
  }

  postBdrs2(state: CommonTasksState, actionType: RequestTaskActionProcessDTO['requestTaskActionType']) {
    const payload = state.requestTaskItem.requestTask.payload as BDRS2ApplicationSubmitRequestTaskPayload;
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

  private createRequestTaskActionPayload(
    actionType: RequestTaskActionProcessDTO['requestTaskActionType'],
    payload?: any,
  ) {
    switch (actionType) {
      case 'BDRS2_SAVE_APPLICATION':
        return {
          payloadType: 'BDRS2_APPLICATION_SAVE_PAYLOAD',
          bdrs2: payload.bdrs2,
          bdrs2SectionsCompleted: payload.bdrs2SectionsCompleted,
          bdrs2FileVersion: payload.bdrs2FileVersion,
        } as RequestTaskActionPayload;
      case 'BDRS2_SUBMIT_TO_VERIFIER':
        return {
          payloadType: 'BDRS2_SUBMIT_TO_VERIFIER_PAYLOAD',
          verificationSectionsCompleted: payload?.['verificationSectionsCompleted'],
        } as RequestTaskActionPayload;
      case 'BDRS2_SAVE_APPLICATION_VERIFICATION':
        return {
          ...(payload as BDRS2ApplicationVerificationSubmitRequestTaskPayload).verificationReport,
          payloadType: 'BDRS2_APPLICATION_SAVE_VERIFICATION_PAYLOAD',
          verificationSectionsCompleted: payload?.['verificationSectionsCompleted'],
        } as RequestTaskActionPayload;
      case 'BDRS2_VERIFICATION_RETURN_TO_OPERATOR':
        return {
          payloadType: 'BDRS2_VERIFICATION_RETURN_TO_OPERATOR_PAYLOAD',
          changesRequired: payload.changesRequired,
        } as RequestTaskActionPayload;
      case 'BDRS2_REGULATOR_REVIEW_SAVE':
        return {
          payloadType: 'BDRS2_REGULATOR_REVIEW_SAVE_PAYLOAD',
          regulatorReviewOutcome: payload.regulatorReviewOutcome,
          regulatorReviewSectionsCompleted: payload.regulatorReviewSectionsCompleted,
        } as RequestTaskActionPayload;
      case 'BDRS2_APPLICATION_AMENDS_SUBMIT_TO_REGULATOR': {
        const bdrs2SectionsCompleted = (() => {
          const { changesRequested, ...rest } = payload.bdrs2SectionsCompleted;
          return rest;
        })();
        return {
          payloadType: 'BDRS2_APPLICATION_AMENDS_SUBMIT_TO_REGULATOR_PAYLOAD',
          bdrs2SectionsCompleted: bdrs2SectionsCompleted,
          regulatorReviewSectionsCompleted: {
            ...payload.regulatorReviewSectionsCompleted,
            ...(payload.regulatorReviewSectionsCompleted?.outcome ? { outcome: false } : {}),
          },
        } as RequestTaskActionPayload;
      }
      case 'BDRS2_APPLICATION_AMENDS_SUBMIT_TO_VERIFIER': {
        return {
          payloadType: 'BDRS2_APPLICATION_AMENDS_SUBMIT_TO_VERIFIER_PAYLOAD',
          verificationSectionsCompleted: payload.verificationSectionsCompleted,
        } as RequestTaskActionPayload;
      }
      case 'BDRS2_APPLICATION_AMENDS_SAVE':
        return {
          payloadType: 'BDRS2_APPLICATION_AMENDS_SAVE_PAYLOAD',
          bdrs2: payload.bdrs2,
          bdrs2SectionsCompleted: payload.bdrs2SectionsCompleted,
          regulatorReviewSectionsCompleted: payload.regulatorReviewSectionsCompleted,
        } as RequestTaskActionPayload;
      case 'BDRS2_REGULATOR_REVIEW_RETURN_FOR_AMENDS':
        return {
          payloadType: 'BDRS2_REGULATOR_REVIEW_RETURN_FOR_AMENDS_PAYLOAD',
          bdrs2SectionsCompleted: { ...payload.bdrs2SectionsCompleted, baseline: false },
        } as RequestTaskActionPayload;
      default:
        return {
          payloadType: 'EMPTY_PAYLOAD',
        } as RequestTaskActionPayload;
    }
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
        actionType === 'BDRS2_SUBMIT_TO_VERIFIER'
          ? this.businessErrorService.showErrorForceNavigation(taskSubmitNotFoundError)
          : this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
      ),
      catchTaskReassignedBadRequest(() =>
        this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
      ),
    );
  }

  postVerificationTaskSave(
    value: any,
    statusValue?: boolean | boolean[],
    statusKey?: string | 'sendReport',
    attachments?: { [key: string]: string },
  ) {
    return this.store.pipe(
      first(),
      switchMap((state) =>
        this.postBdrs2(
          {
            ...state,
            requestTaskItem: {
              ...state.requestTaskItem,
              requestTask: {
                ...state.requestTaskItem.requestTask,
                payload: {
                  ...state.requestTaskItem.requestTask.payload,
                  verificationReport: {
                    ...(
                      state.requestTaskItem.requestTask.payload as BDRS2ApplicationVerificationSubmitRequestTaskPayload
                    ).verificationReport,
                    ...value,
                  },
                  verificationSectionsCompleted: {
                    ...(
                      state.requestTaskItem.requestTask.payload as BDRS2ApplicationVerificationSubmitRequestTaskPayload
                    )?.verificationSectionsCompleted,
                    ...(statusKey
                      ? { [statusKey]: Array.isArray(statusValue) ? statusValue : [statusValue] }
                      : undefined),
                  },
                  verificationAttachments: {
                    ...(
                      state.requestTaskItem.requestTask.payload as BDRS2ApplicationVerificationSubmitRequestTaskPayload
                    )?.verificationAttachments,
                    ...attachments,
                  },
                } as BDRS2ApplicationVerificationSubmitRequestTaskPayload,
              },
            },
          },
          'BDRS2_SAVE_APPLICATION_VERIFICATION',
        ),
      ),
    );
  }

  postGroupDecisionReview(
    value: any,
    dataType: BDRS2VerificationReportDataRegulatorReviewDecision['reviewDataType'],
    groupKey: string,
    attachments?: { uuid: string; file: File }[],
  ) {
    return this.store.pipe(
      first(),
      switchMap((state) =>
        this.tasksService.processRequestTaskAction({
          requestTaskActionType: 'BDRS2_SAVE_REGULATOR_REVIEW_GROUP_DECISION',
          requestTaskId: state.requestTaskItem.requestTask.id,
          requestTaskActionPayload: {
            payloadType: 'BDRS2_SAVE_REGULATOR_REVIEW_GROUP_DECISION_PAYLOAD',
            group: groupKey,
            decision: {
              ...value,
              reviewDataType: dataType,
            },
            regulatorReviewSectionsCompleted: {
              ...(state.requestTaskItem.requestTask.payload as BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload)
                ?.regulatorReviewSectionsCompleted,
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
                regulatorReviewGroupDecisions: {
                  ...(
                    state.requestTaskItem.requestTask.payload as BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload
                  ).regulatorReviewGroupDecisions,
                  [groupKey]: {
                    reviewDataType: dataType,
                    ...value,
                  },
                },
                regulatorReviewAttachments: {
                  ...(
                    state.requestTaskItem.requestTask.payload as BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload
                  ).regulatorReviewAttachments,
                  ...attachments?.reduce((result, item) => ({ ...result, [item.uuid]: item.file.name }), {}),
                },
                regulatorReviewSectionsCompleted: {
                  ...(
                    state.requestTaskItem.requestTask.payload as BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload
                  )?.regulatorReviewSectionsCompleted,
                  ...{ [groupKey]: true },
                },
              } as BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload,
            },
          },
        });
      }),
    );
  }

  postRegulatorTaskSave(
    value: any,
    statusValue?: boolean | boolean[],
    statusKey?: string | 'sendReport',
    attachments?: { [key: string]: string },
  ) {
    return this.store.pipe(
      first(),
      switchMap((state) =>
        this.postBdrs2(
          {
            ...state,
            requestTaskItem: {
              ...state.requestTaskItem,
              requestTask: {
                ...state.requestTaskItem.requestTask,
                payload: {
                  ...state.requestTaskItem.requestTask.payload,
                  regulatorReviewOutcome: {
                    ...(
                      state.requestTaskItem.requestTask
                        .payload as BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload
                    ).regulatorReviewOutcome,
                    ...value,
                  },
                  regulatorReviewAttachments: {
                    ...(
                      state.requestTaskItem.requestTask
                        .payload as BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload
                    ).regulatorReviewAttachments,
                    ...attachments,
                  },
                  regulatorReviewSectionsCompleted: {
                    ...(
                      state.requestTaskItem.requestTask
                        .payload as BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload
                    )?.regulatorReviewSectionsCompleted,
                    ...{ [statusKey]: statusValue },
                  },
                } as BDRS2ApplicationRegulatorReviewSubmitRequestTaskPayload,
              },
            },
          },
          'BDRS2_REGULATOR_REVIEW_SAVE',
        ),
      ),
    );
  }
}
