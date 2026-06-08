import { computed, Injectable, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';

import { first, map, Observable, switchMap, tap } from 'rxjs';

import { AuthStore, selectUserRoleType } from '@core/store';
import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchTaskReassignedBadRequest } from '@error/business-errors';
import { catchNotFoundRequest, ErrorCode } from '@error/not-found-error';
import { requestTaskReassignedError, taskNotFoundError } from '@shared/errors/request-task-error';
import { CapitalizeFirstPipe } from '@shared/pipes/capitalize-first.pipe';
import { AttachedFile } from '@shared/types/attached-file.type';
import { TasksHelperService } from '@tasks/shared/services/tasks-helper.service';
import { CommonTasksState } from '@tasks/store/common-tasks.state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import {
  ALRApplicationAmendsSubmitRequestTaskPayload,
  ALRApplicationAuthorityReviewOutcome,
  ALRApplicationRegulatorReviewSaveTaskActionPayload,
  ALRApplicationRegulatorReviewSubmitRequestTaskPayload,
  ALRApplicationSubmitRequestTaskPayload,
  ALRApplicationVerificationSubmitRequestTaskPayload,
  ALRAuthorityResponseSubmitRequestTaskPayload,
  ALRSaveAuthorityResponseTaskActionPayload,
  InstallationAccountViewService,
  RequestMetadata,
  RequestTaskActionPayload,
  RequestTaskActionProcessDTO,
  RequestTaskDTO,
  RequestTaskItemDTO,
  TasksService,
} from 'pmrv-api';

@Injectable()
export class AlrService extends TasksHelperService {
  constructor(
    store: CommonTasksStore,
    tasksService: TasksService,
    businessErrorService: BusinessErrorService,
    private readonly installationAccountViewService: InstallationAccountViewService,
    private readonly authStore: AuthStore,
    private readonly capitalizeFirstPipe: CapitalizeFirstPipe,
  ) {
    super(store, tasksService, businessErrorService);
  }

  get payload(): Signal<
    | ALRApplicationSubmitRequestTaskPayload
    | ALRApplicationVerificationSubmitRequestTaskPayload
    | ALRApplicationRegulatorReviewSubmitRequestTaskPayload
    | ALRApplicationAmendsSubmitRequestTaskPayload
    | ALRAuthorityResponseSubmitRequestTaskPayload
  > {
    return toSignal(this.store.payload$);
  }

  get authorityPayload$(): Observable<ALRAuthorityResponseSubmitRequestTaskPayload> {
    return this.store.payload$ as Observable<ALRAuthorityResponseSubmitRequestTaskPayload>;
  }

  get requestTaskType(): Signal<RequestTaskDTO['type']> {
    return toSignal(this.requestTaskType$);
  }

  get requestMetadata(): Signal<RequestMetadata> {
    return toSignal(this.requestMetadata$);
  }

  get daysRemaining(): Signal<number> {
    return toSignal(this.daysRemaining$);
  }

  get requestAccountId$() {
    return this.store.requestInfo$.pipe(map((info) => info.accountId));
  }

  get competentAuthority$() {
    return this.store.requestInfo$.pipe(map((info) => info.competentAuthority));
  }

  get competentAuthority() {
    return toSignal(this.competentAuthority$);
  }

  get requestTaskItem(): Signal<RequestTaskItemDTO> {
    return toSignal(this.store.requestTaskItem$);
  }

  get isEditable(): Signal<boolean> {
    return toSignal(this.isEditable$);
  }

  get requestId() {
    return this.store.requestId;
  }

  get requestTaskId() {
    return this.store.requestTaskId;
  }

  installationAccountId$ = this.requestTaskItem$.pipe(
    switchMap((requestTaskItem) =>
      this.installationAccountViewService.getInstallationAccountById(requestTaskItem.requestInfo.accountId),
    ),
  );

  userRoleType = toSignal(this.authStore.pipe(selectUserRoleType), { initialValue: null });

  installationAccount = toSignal(this.installationAccountId$, { initialValue: null });
  installationName = computed(() =>
    this.installationAccount()?.accountPermitDto?.account.name
      ? this.stripSpecialChars(this.installationAccount()?.accountPermitDto?.account.name)
      : 'Unknown',
  );

  fileName(fileVersion: number, suffix: string): string {
    const alrId = this.requestId;
    const shortInstallationName = this.installationName().substring(0, 10);
    const roleType = this.capitalizeFirstPipe.transform(this.userRoleType());
    return `${alrId}-v${fileVersion}-uploaded by ${roleType}-${shortInstallationName}${suffix}`;
  }

  stripSpecialChars(str: string): string {
    return str.replace(/[^a-zA-Z0-9 _-]/g, '');
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
      case 'ALR_APPLICATION_SUBMIT':
        actionType = 'ALR_SAVE_APPLICATION';
        break;
      case 'ALR_APPLICATION_AMENDS_SUBMIT':
        actionType = 'ALR_APPLICATION_AMENDS_SAVE';
        break;
    }

    return this.store.pipe(
      first(),
      switchMap((state) => {
        const postAlrState = {
          ...state,
          requestTaskItem: {
            ...state.requestTaskItem,
            requestTask: {
              ...state.requestTaskItem.requestTask,
              payload: {
                ...state.requestTaskItem.requestTask.payload,
                alr: {
                  ...(state.requestTaskItem.requestTask.payload as ALRApplicationSubmitRequestTaskPayload).alr,
                  ...value,
                },
                alrAttachments: {
                  ...(state.requestTaskItem.requestTask.payload as ALRApplicationSubmitRequestTaskPayload)
                    ?.alrAttachments,
                  ...attachments,
                },
                alrSectionsCompleted: {
                  ...(state.requestTaskItem.requestTask.payload as ALRApplicationSubmitRequestTaskPayload)
                    ?.alrSectionsCompleted,
                  ...(statusKey ? { [statusKey]: statusValue } : undefined),
                },
                verificationPerformed: false,
              } as ALRApplicationSubmitRequestTaskPayload,
            },
          },
        };

        return this.postAlr(postAlrState, actionType);
      }),
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
        this.postAlr(
          {
            ...state,
            requestTaskItem: {
              ...state.requestTaskItem,
              requestTask: {
                ...state.requestTaskItem.requestTask,
                payload: {
                  ...state.requestTaskItem.requestTask.payload,
                  verificationReport: {
                    ...(state.requestTaskItem.requestTask.payload as ALRApplicationVerificationSubmitRequestTaskPayload)
                      .verificationReport,
                    ...value,
                  },
                  verificationSectionsCompleted: {
                    ...(state.requestTaskItem.requestTask.payload as ALRApplicationVerificationSubmitRequestTaskPayload)
                      ?.verificationSectionsCompleted,
                    ...(statusKey
                      ? { [statusKey]: Array.isArray(statusValue) ? statusValue : [statusValue] }
                      : undefined),
                  },
                  verificationAttachments: {
                    ...(state.requestTaskItem.requestTask.payload as ALRApplicationVerificationSubmitRequestTaskPayload)
                      ?.verificationAttachments,
                    ...attachments,
                  },
                } as ALRApplicationVerificationSubmitRequestTaskPayload,
              },
            },
          },
          'ALR_SAVE_APPLICATION_VERIFICATION',
        ),
      ),
    );
  }

  postAlr(state: CommonTasksState, actionType: RequestTaskActionProcessDTO['requestTaskActionType']) {
    const payload = state.requestTaskItem.requestTask.payload as ALRApplicationSubmitRequestTaskPayload;
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

  postAlrSubmit(actionType: RequestTaskActionProcessDTO['requestTaskActionType'], payload?: any) {
    return this.store.pipe(
      first(),
      switchMap((state) =>
        this.tasksService.processRequestTaskAction({
          requestTaskActionType: actionType,
          requestTaskId: state.requestTaskItem.requestTask.id,
          requestTaskActionPayload: this.createRequestTaskActionPayload(
            actionType,
            payload ?? state.requestTaskItem.requestTask.payload,
          ),
        }),
      ),
      catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
        this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
      ),
      catchTaskReassignedBadRequest(() =>
        this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
      ),
    );
  }

  postAlrReview(value: any, groupKey: string, groupStatus: boolean, attachments?: { [key: string]: string }) {
    return this.store.pipe(
      first(),
      switchMap((state) => {
        const payload = state.requestTaskItem.requestTask.payload as
          | ALRApplicationRegulatorReviewSaveTaskActionPayload
          | ALRApplicationRegulatorReviewSubmitRequestTaskPayload;

        return this.tasksService.processRequestTaskAction({
          requestTaskActionType: 'ALR_REGULATOR_REVIEW_SAVE',
          requestTaskId: state.requestTaskItem.requestTask.id,
          requestTaskActionPayload: {
            payloadType: 'ALR_REGULATOR_REVIEW_SAVE_PAYLOAD',
            regulatorReviewOutcome: {
              ...payload.regulatorReviewOutcome,
              ...value,
            },
            regulatorReviewSectionsCompleted: {
              ...payload?.regulatorReviewSectionsCompleted,
              ...{ [groupKey]: groupStatus },
            },
          } as ALRApplicationRegulatorReviewSaveTaskActionPayload,
        });
      }),
      catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
        this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
      ),
      catchTaskReassignedBadRequest(() =>
        this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
      ),
      tap(() => {
        const state = this.store.getState();
        const payload = state.requestTaskItem.requestTask
          .payload as ALRApplicationRegulatorReviewSubmitRequestTaskPayload;

        this.store.setState({
          ...state,
          requestTaskItem: {
            ...state.requestTaskItem,
            requestTask: {
              ...state.requestTaskItem.requestTask,
              payload: {
                ...payload,
                regulatorReviewOutcome: {
                  ...payload.regulatorReviewOutcome,
                  ...value,
                },
                regulatorReviewSectionsCompleted: {
                  ...payload?.regulatorReviewSectionsCompleted,
                  ...{ [groupKey]: groupStatus },
                },
                regulatorReviewAttachments: { ...payload?.regulatorReviewAttachments, ...attachments },
              } as ALRApplicationRegulatorReviewSubmitRequestTaskPayload,
            },
          },
        });
      }),
    );
  }

  postAlrAuthority(
    value: Partial<ALRApplicationAuthorityReviewOutcome>,
    groupKey: string,
    groupStatus: boolean,
    attachments?: { [key: string]: string },
  ) {
    return this.store.pipe(
      first(),
      switchMap((state) => {
        const payload = state.requestTaskItem.requestTask.payload as ALRAuthorityResponseSubmitRequestTaskPayload;

        return this.tasksService.processRequestTaskAction({
          requestTaskActionType: 'ALR_SAVE_AUTHORITY_RESPONSE',
          requestTaskId: state.requestTaskItem.requestTask.id,
          requestTaskActionPayload: {
            payloadType: 'ALR_SAVE_AUTHORITY_RESPONSE_PAYLOAD',
            authorityReviewOutcome: {
              ...payload.authorityReviewOutcome,
              ...value,
            },
            authorityReviewSectionsCompleted: {
              ...payload?.authorityReviewSectionsCompleted,
              ...{ [groupKey]: groupStatus },
            },
          } as ALRSaveAuthorityResponseTaskActionPayload,
        });
      }),
      catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
        this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
      ),
      catchTaskReassignedBadRequest(() =>
        this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
      ),
      tap(() => {
        const state = this.store.getState();
        const payload = state.requestTaskItem.requestTask.payload as ALRAuthorityResponseSubmitRequestTaskPayload;

        this.store.setState({
          ...state,
          requestTaskItem: {
            ...state.requestTaskItem,
            requestTask: {
              ...state.requestTaskItem.requestTask,
              payload: {
                ...payload,
                authorityReviewOutcome: {
                  ...payload.authorityReviewOutcome,
                  ...value,
                },
                authorityReviewSectionsCompleted: {
                  ...payload?.authorityReviewSectionsCompleted,
                  ...{ [groupKey]: groupStatus },
                },
                alrAttachments: { ...payload?.alrAttachments, ...attachments },
              } as ALRAuthorityResponseSubmitRequestTaskPayload,
            },
          },
        });
      }),
    );
  }

  private createRequestTaskActionPayload(
    actionType: RequestTaskActionProcessDTO['requestTaskActionType'],
    payload?: any,
  ) {
    switch (actionType) {
      case 'ALR_SAVE_APPLICATION':
        return {
          payloadType: 'ALR_APPLICATION_SAVE_PAYLOAD',
          alr: payload.alr,
          alrSectionsCompleted: payload.alrSectionsCompleted,
        } as RequestTaskActionPayload;
      case 'ALR_SUBMIT_TO_VERIFIER':
        return {
          payloadType: 'ALR_SUBMIT_TO_VERIFIER_PAYLOAD',
          verificationSectionsCompleted: payload.verificationSectionsCompleted,
        } as RequestTaskActionPayload;
      case 'ALR_SAVE_APPLICATION_VERIFICATION':
        return {
          ...(payload as ALRApplicationVerificationSubmitRequestTaskPayload).verificationReport,
          payloadType: 'ALR_APPLICATION_SAVE_VERIFICATION_PAYLOAD',
          verificationSectionsCompleted: payload.verificationSectionsCompleted,
        } as RequestTaskActionPayload;
      case 'ALR_VERIFICATION_RETURN_TO_OPERATOR':
        return {
          payloadType: 'ALR_VERIFICATION_RETURN_TO_OPERATOR_PAYLOAD',
          changesRequired: payload.changesRequired,
        } as RequestTaskActionPayload;
      case 'ALR_REGULATOR_REVIEW_SAVE':
        return {
          payloadType: 'ALR_REGULATOR_REVIEW_SAVE_PAYLOAD',
          regulatorReviewOutcome: payload.regulatorReviewOutcome,
          regulatorReviewSectionsCompleted: payload.regulatorReviewSectionsCompleted,
        } as RequestTaskActionPayload;
      case 'ALR_APPLICATION_AMENDS_SAVE':
        return {
          payloadType: 'ALR_APPLICATION_AMENDS_SAVE_PAYLOAD',
          alr: payload.alr,
          alrSectionsCompleted: payload.alrSectionsCompleted,
          regulatorReviewSectionsCompleted: payload.regulatorReviewSectionsCompleted,
        } as RequestTaskActionPayload;
      case 'ALR_APPLICATION_AMENDS_SUBMIT_TO_REGULATOR': {
        const alrSectionsCompleted = (() => {
          const { changesRequested, ...rest } = payload.alrSectionsCompleted;
          return rest;
        })();
        return {
          payloadType: 'ALR_APPLICATION_AMENDS_SUBMIT_TO_REGULATOR_PAYLOAD',
          alrSectionsCompleted: alrSectionsCompleted,
        } as RequestTaskActionPayload;
      }
      case 'ALR_APPLICATION_AMENDS_SUBMIT_TO_VERIFIER': {
        return {
          payloadType: 'ALR_APPLICATION_AMENDS_SUBMIT_TO_VERIFIER_PAYLOAD',
          verificationSectionsCompleted: payload.verificationSectionsCompleted,
        } as RequestTaskActionPayload;
      }

      default:
        return {
          payloadType: 'EMPTY_PAYLOAD',
        } as RequestTaskActionPayload;
    }
  }

  getOperatorDownloadUrlFiles(files: string[]): AttachedFile[] {
    const attachments: { [key: string]: string } = (
      this.store.getValue().requestTaskItem.requestTask.payload as ALRApplicationSubmitRequestTaskPayload
    )?.alrAttachments;
    const url = this.getBaseFileDownloadUrl();

    return (
      files?.map((id) => ({
        downloadUrl: url + `${id}`,
        fileName: attachments[id],
      })) ?? []
    );
  }

  getOperatorDownloadUrlAlrFile(alrFile: string): AttachedFile {
    const attachments: { [key: string]: string } = (
      this.store.getValue().requestTaskItem.requestTask.payload as ALRApplicationSubmitRequestTaskPayload
    )?.alrAttachments;
    const url = this.getBaseFileDownloadUrl();

    return alrFile
      ? {
          downloadUrl: url + `${alrFile}`,
          fileName: attachments[alrFile],
        }
      : null;
  }

  getVerifierDownloadUrlFile(file: string): AttachedFile {
    const attachments: { [key: string]: string } = (
      this.store.getValue().requestTaskItem.requestTask.payload as ALRApplicationVerificationSubmitRequestTaskPayload
    )?.verificationAttachments;
    const url = this.getBaseFileDownloadUrl();

    return file
      ? {
          downloadUrl: url + `${file}`,
          fileName: attachments[file],
        }
      : null;
  }

  getVerifierDownloadUrlFiles(files: string[]): AttachedFile[] {
    const attachments: { [key: string]: string } = (
      this.store.getValue().requestTaskItem.requestTask.payload as ALRApplicationVerificationSubmitRequestTaskPayload
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
    const url = this.getBaseFileDownloadUrl();
    const regulatorReviewAttachments: { [key: string]: string } = (
      this.store.getValue().requestTaskItem.requestTask.payload as ALRApplicationRegulatorReviewSubmitRequestTaskPayload
    )?.regulatorReviewAttachments;

    const alrAttachments: { [key: string]: string } = (
      this.store.getValue().requestTaskItem.requestTask.payload as ALRApplicationRegulatorReviewSubmitRequestTaskPayload
    )?.alrAttachments;

    const attachments = { ...alrAttachments, ...regulatorReviewAttachments };

    return (
      files?.map((id) => ({
        downloadUrl: url + `${id}`,
        fileName: attachments[id],
      })) ?? []
    );
  }

  getRegulatorDownloadUrlAlrFile(alrFile: string): AttachedFile {
    const url = this.getBaseFileDownloadUrl();
    const regulatorReviewAttachments: { [key: string]: string } = (
      this.store.getValue().requestTaskItem.requestTask.payload as ALRApplicationRegulatorReviewSubmitRequestTaskPayload
    )?.regulatorReviewAttachments;

    const alrAttachments: { [key: string]: string } = (
      this.store.getValue().requestTaskItem.requestTask.payload as ALRApplicationRegulatorReviewSubmitRequestTaskPayload
    )?.alrAttachments;

    const attachments = { ...alrAttachments, ...regulatorReviewAttachments };

    return alrFile
      ? {
          downloadUrl: url + `${alrFile}`,
          fileName: attachments[alrFile],
        }
      : null;
  }
}
