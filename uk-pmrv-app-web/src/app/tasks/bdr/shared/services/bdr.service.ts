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
  BDRApplicationAmendsSubmitRequestTaskPayload,
  BDRApplicationRegulatorReviewSubmitRequestTaskPayload,
  BDRApplicationSubmitRequestTaskPayload,
  BDRApplicationVerificationSubmitRequestTaskPayload,
  BDRVerificationReportDataRegulatorReviewDecision,
  InstallationAccountViewService,
  RequestInfoDTO,
  RequestMetadata,
  RequestTaskActionPayload,
  RequestTaskActionProcessDTO,
  RequestTaskDTO,
  TasksService,
} from 'pmrv-api';

@Injectable()
export class BdrService extends TasksHelperService {
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

  get payload$(): Observable<
    | BDRApplicationSubmitRequestTaskPayload
    | BDRApplicationVerificationSubmitRequestTaskPayload
    | BDRApplicationRegulatorReviewSubmitRequestTaskPayload
  > {
    return this.store.payload$ as Observable<
      | BDRApplicationSubmitRequestTaskPayload
      | BDRApplicationVerificationSubmitRequestTaskPayload
      | BDRApplicationRegulatorReviewSubmitRequestTaskPayload
    >;
  }

  get payload(): Signal<
    | BDRApplicationSubmitRequestTaskPayload
    | BDRApplicationVerificationSubmitRequestTaskPayload
    | BDRApplicationRegulatorReviewSubmitRequestTaskPayload
  > {
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
      map((requestTaskItem) => this.itemNamePipe.transform(requestTaskItem?.requestTask?.type)),
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
    this.installationAccount()?.account.name
      ? this.stripSpecialChars(this.installationAccount()?.account.name)
      : 'Unknown',
  );

  fileName(fileVersion: number, suffix: string): string {
    const requestInfo: RequestInfoDTO = this.store.getState().requestTaskItem.requestInfo;
    const bdrId = requestInfo.id;
    const shortInstallationName = this.installationName().substring(0, 10);
    const roleType = this.capitalizeFirstPipe.transform(this.userRoleType());
    return `${bdrId}-v${fileVersion}-uploaded by ${roleType}-${shortInstallationName}${suffix}`;
  }

  stripSpecialChars(str: string): string {
    return str.replace(/[^a-zA-Z0-9 _-]/g, '');
  }

  getOperatorDownloadUrlFiles(files: string[]): AttachedFile[] {
    const attachments: { [key: string]: string } = (
      this.store.getValue().requestTaskItem.requestTask.payload as BDRApplicationSubmitRequestTaskPayload
    )?.bdrAttachments;
    const url = this.getBaseFileDownloadUrl();
    return (
      files?.map((id) => ({
        downloadUrl: url + `${id}`,
        fileName: attachments[id],
      })) ?? []
    );
  }

  getOperatorDownloadUrlBdrFile(bdrFile: string): AttachedFile {
    const attachments: { [key: string]: string } = (
      this.store.getValue().requestTaskItem.requestTask.payload as BDRApplicationSubmitRequestTaskPayload
    )?.bdrAttachments;
    const url = this.getBaseFileDownloadUrl();

    return bdrFile
      ? {
          downloadUrl: url + `${bdrFile}`,
          fileName: attachments[bdrFile],
        }
      : null;
  }

  getVerifierDownloadUrlFiles(files: string[]): AttachedFile[] {
    const attachments: { [key: string]: string } = (
      this.store.getValue().requestTaskItem.requestTask.payload as BDRApplicationVerificationSubmitRequestTaskPayload
    )?.verificationAttachments;
    const url = this.getBaseFileDownloadUrl();
    return (
      files?.map((id) => ({
        downloadUrl: url + `${id}`,
        fileName: attachments[id],
      })) ?? []
    );
  }

  getRegulatorDownloadUrlFiles(files: string[]): AttachedFile[] {
    const attachments: { [key: string]: string } = (
      this.store.getValue().requestTaskItem.requestTask.payload as BDRApplicationRegulatorReviewSubmitRequestTaskPayload
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
      case 'BDR_APPLICATION_SUBMIT':
        actionType = 'BDR_SAVE_APPLICATION';
        break;
      case 'BDR_APPLICATION_AMENDS_SUBMIT':
        actionType = 'BDR_APPLICATION_AMENDS_SAVE';
        break;
    }

    return this.store.pipe(
      first(),
      switchMap((state) => {
        const postBdrState = {
          ...state,
          requestTaskItem: {
            ...state.requestTaskItem,
            requestTask: {
              ...state.requestTaskItem.requestTask,
              payload: {
                ...state.requestTaskItem.requestTask.payload,
                bdr: {
                  ...(state.requestTaskItem.requestTask.payload as BDRApplicationSubmitRequestTaskPayload).bdr,
                  ...value,
                },
                bdrAttachments: {
                  ...(state.requestTaskItem.requestTask.payload as BDRApplicationSubmitRequestTaskPayload)
                    ?.bdrAttachments,
                  ...attachments,
                },
                bdrSectionsCompleted: {
                  ...(state.requestTaskItem.requestTask.payload as BDRApplicationSubmitRequestTaskPayload)
                    ?.bdrSectionsCompleted,
                  ...(statusKey ? { [statusKey]: statusValue } : undefined),
                },
                verificationPerformed: false,
                ...(requestTaskType === 'BDR_APPLICATION_AMENDS_SUBMIT'
                  ? {
                      reviewSectionsCompleted: {
                        ...(state.requestTaskItem.requestTask.payload as BDRApplicationAmendsSubmitRequestTaskPayload)
                          ?.regulatorReviewSectionsCompleted,
                      },
                    }
                  : null),
              } as BDRApplicationSubmitRequestTaskPayload,
            },
          },
        };

        return this.postBdr(postBdrState, actionType);
      }),
    );
  }

  postBdr(state: CommonTasksState, actionType: RequestTaskActionProcessDTO['requestTaskActionType']) {
    const payload = state.requestTaskItem.requestTask.payload as BDRApplicationSubmitRequestTaskPayload;
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
        actionType === 'BDR_SUBMIT_TO_VERIFIER'
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
        this.postBdr(
          {
            ...state,
            requestTaskItem: {
              ...state.requestTaskItem,
              requestTask: {
                ...state.requestTaskItem.requestTask,
                payload: {
                  ...state.requestTaskItem.requestTask.payload,
                  verificationReport: {
                    ...(state.requestTaskItem.requestTask.payload as BDRApplicationVerificationSubmitRequestTaskPayload)
                      .verificationReport,
                    ...value,
                  },
                  verificationSectionsCompleted: {
                    ...(state.requestTaskItem.requestTask.payload as BDRApplicationVerificationSubmitRequestTaskPayload)
                      ?.verificationSectionsCompleted,
                    ...(statusKey
                      ? { [statusKey]: Array.isArray(statusValue) ? statusValue : [statusValue] }
                      : undefined),
                  },
                  verificationAttachments: {
                    ...(state.requestTaskItem.requestTask.payload as BDRApplicationVerificationSubmitRequestTaskPayload)
                      ?.verificationAttachments,
                    ...attachments,
                  },
                } as BDRApplicationVerificationSubmitRequestTaskPayload,
              },
            },
          },
          'BDR_SAVE_APPLICATION_VERIFICATION',
        ),
      ),
    );
  }

  postGroupDecisionReview(
    value: any,
    dataType: BDRVerificationReportDataRegulatorReviewDecision['reviewDataType'],
    groupKey: string,
    attachments?: { uuid: string; file: File }[],
  ) {
    return this.store.pipe(
      first(),
      switchMap((state) =>
        this.tasksService.processRequestTaskAction({
          requestTaskActionType: 'BDR_SAVE_REGULATOR_REVIEW_GROUP_DECISION',
          requestTaskId: state.requestTaskItem.requestTask.id,
          requestTaskActionPayload: {
            payloadType: 'BDR_SAVE_REGULATOR_REVIEW_GROUP_DECISION_PAYLOAD',
            group: groupKey,
            decision: {
              ...value,
              reviewDataType: dataType,
            },
            regulatorReviewSectionsCompleted: {
              ...(state.requestTaskItem.requestTask.payload as BDRApplicationRegulatorReviewSubmitRequestTaskPayload)
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
                    state.requestTaskItem.requestTask.payload as BDRApplicationRegulatorReviewSubmitRequestTaskPayload
                  ).regulatorReviewGroupDecisions,
                  [groupKey]: {
                    reviewDataType: dataType,
                    ...value,
                  },
                },
                regulatorReviewAttachments: {
                  ...(
                    state.requestTaskItem.requestTask.payload as BDRApplicationRegulatorReviewSubmitRequestTaskPayload
                  ).regulatorReviewAttachments,
                  ...attachments?.reduce((result, item) => ({ ...result, [item.uuid]: item.file.name }), {}),
                },
                regulatorReviewSectionsCompleted: {
                  ...(
                    state.requestTaskItem.requestTask.payload as BDRApplicationRegulatorReviewSubmitRequestTaskPayload
                  )?.regulatorReviewSectionsCompleted,
                  ...{ [groupKey]: true },
                },
              } as BDRApplicationRegulatorReviewSubmitRequestTaskPayload,
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
        this.postBdr(
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
                      state.requestTaskItem.requestTask.payload as BDRApplicationRegulatorReviewSubmitRequestTaskPayload
                    ).regulatorReviewOutcome,
                    ...value,
                  },
                  regulatorReviewAttachments: {
                    ...(
                      state.requestTaskItem.requestTask.payload as BDRApplicationRegulatorReviewSubmitRequestTaskPayload
                    ).regulatorReviewAttachments,
                    ...attachments,
                  },
                  regulatorReviewSectionsCompleted: {
                    ...(
                      state.requestTaskItem.requestTask.payload as BDRApplicationRegulatorReviewSubmitRequestTaskPayload
                    )?.regulatorReviewSectionsCompleted,
                    ...{ [statusKey]: statusValue },
                  },
                } as BDRApplicationRegulatorReviewSubmitRequestTaskPayload,
              },
            },
          },
          'BDR_REGULATOR_REVIEW_SAVE',
        ),
      ),
    );
  }

  private createRequestTaskActionPayload(
    actionType: RequestTaskActionProcessDTO['requestTaskActionType'],
    payload?: any,
  ) {
    switch (actionType) {
      case 'BDR_SAVE_APPLICATION':
        return {
          payloadType: 'BDR_APPLICATION_SAVE_PAYLOAD',
          bdr: payload.bdr,
          bdrSectionsCompleted: payload.bdrSectionsCompleted,
        } as RequestTaskActionPayload;

      case 'BDR_SUBMIT_TO_VERIFIER':
        return {
          payloadType: 'BDR_SUBMIT_TO_VERIFIER_PAYLOAD',
          verificationSectionsCompleted: payload.verificationSectionsCompleted,
        } as RequestTaskActionPayload;
      case 'BDR_SAVE_APPLICATION_VERIFICATION':
        return {
          ...(payload as BDRApplicationVerificationSubmitRequestTaskPayload).verificationReport,
          payloadType: 'BDR_APPLICATION_SAVE_VERIFICATION_PAYLOAD',
          verificationSectionsCompleted: payload.verificationSectionsCompleted,
        } as RequestTaskActionPayload;
      case 'BDR_REGULATOR_REVIEW_SAVE':
        return {
          payloadType: 'BDR_REGULATOR_REVIEW_SAVE_PAYLOAD',
          regulatorReviewOutcome: payload.regulatorReviewOutcome,
          regulatorReviewSectionsCompleted: payload.regulatorReviewSectionsCompleted,
        } as RequestTaskActionPayload;
      case 'BDR_VERIFICATION_RETURN_TO_OPERATOR':
        return {
          payloadType: 'BDR_VERIFICATION_RETURN_TO_OPERATOR_PAYLOAD',
          changesRequired: payload.changesRequired,
        } as RequestTaskActionPayload;
      case 'BDR_APPLICATION_AMENDS_SAVE':
        return {
          payloadType: 'BDR_APPLICATION_AMENDS_SAVE_PAYLOAD',
          bdr: payload.bdr,
          bdrSectionsCompleted: payload.bdrSectionsCompleted,
          regulatorReviewSectionsCompleted: payload.regulatorReviewSectionsCompleted,
        } as RequestTaskActionPayload;
      case 'BDR_APPLICATION_AMENDS_SUBMIT_TO_REGULATOR': {
        const bdrSectionsCompleted = (() => {
          const { changesRequested, ...rest } = payload.bdrSectionsCompleted;
          return rest;
        })();
        return {
          payloadType: 'BDR_APPLICATION_AMENDS_SUBMIT_TO_REGULATOR_PAYLOAD',
          bdrSectionsCompleted: bdrSectionsCompleted,
        } as RequestTaskActionPayload;
      }
      case 'BDR_APPLICATION_AMENDS_SUBMIT_TO_VERIFIER': {
        return {
          payloadType: 'BDR_APPLICATION_AMENDS_SUBMIT_TO_VERIFIER_PAYLOAD',
          verificationSectionsCompleted: payload.verificationSectionsCompleted,
        } as RequestTaskActionPayload;
      }
      default:
        return {
          payloadType: 'EMPTY_PAYLOAD',
        } as RequestTaskActionPayload;
    }
  }
}
