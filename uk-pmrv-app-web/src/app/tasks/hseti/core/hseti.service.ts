import { computed, Injectable, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';

import { first, map, Observable, switchMap, tap } from 'rxjs';

import { AuthStore, selectUserRoleType } from '@core/store';
import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchTaskReassignedBadRequest } from '@error/business-errors';
import { catchNotFoundRequest, ErrorCode } from '@error/not-found-error';
import { requestTaskReassignedError, taskNotFoundError } from '@shared/errors/request-task-error';
import { AttachedFile } from '@shared/types/attached-file.type';
import { TasksHelperService } from '@tasks/shared/services/tasks-helper.service';
import { CommonTasksState } from '@tasks/store/common-tasks.state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import {
  HSETIApplicationRegulatorReviewSaveTaskActionPayload,
  HSETIApplicationRegulatorReviewSubmitRequestTaskPayload,
  HSETIApplicationSaveRequestTaskActionPayload,
  HSETIApplicationSubmitRequestTaskPayload,
  HSETIRequestMetadata,
  InstallationAccountViewService,
  RequestMetadata,
  RequestTaskActionPayload,
  RequestTaskActionProcessDTO,
  RequestTaskDTO,
  RequestTaskItemDTO,
  TasksService,
} from 'pmrv-api';

@Injectable()
export class HseTiService extends TasksHelperService {
  constructor(
    store: CommonTasksStore,
    tasksService: TasksService,
    businessErrorService: BusinessErrorService,
    private readonly installationAccountViewService: InstallationAccountViewService,
    private readonly authStore: AuthStore,
  ) {
    super(store, tasksService, businessErrorService);
  }

  get payload(): Signal<HSETIApplicationSubmitRequestTaskPayload> {
    return toSignal(this.store.payload$);
  }

  get requestTaskType(): Signal<RequestTaskDTO['type']> {
    return toSignal(this.store.requestTaskType$);
  }

  get requestMetadata(): Signal<RequestMetadata> {
    return toSignal(this.requestMetadata$);
  }

  get daysRemaining(): Signal<number> {
    return toSignal(this.daysRemaining$);
  }

  get allocationPeriod$(): Observable<string> {
    return this.requestMetadata$.pipe(
      map((requestTaskItem) => {
        const metadata = requestTaskItem as HSETIRequestMetadata;
        const period = metadata.allocationPeriod.split('_');
        return `${period[1]}-${period[2]}`;
      }),
    );
  }

  get allocationPeriod(): Signal<string> {
    return toSignal(this.allocationPeriod$);
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

  installationAccountId$ = this.requestTaskItem$.pipe(
    switchMap((requestTaskItem) =>
      this.installationAccountViewService.getInstallationAccountById(requestTaskItem.requestInfo.accountId),
    ),
  );

  userRoleType = toSignal(this.authStore.pipe(selectUserRoleType), { initialValue: null });

  installationAccount = toSignal(this.installationAccountId$, { initialValue: null });
  installationName = computed(() => this.installationAccount()?.accountPermitDto?.account.name ?? 'Unknown');

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
      case 'HSE_TI_APPLICATION_SUBMIT':
        actionType = 'HSE_TI_SAVE_APPLICATION';
        break;
      case 'HSE_TI_APPLICATION_AMENDS_SUBMIT':
        actionType = 'HSE_TI_APPLICATION_AMENDS_SAVE';
        break;
    }

    return this.store.pipe(
      first(),
      switchMap((state) => {
        const postHseTiState = {
          ...state,
          requestTaskItem: {
            ...state.requestTaskItem,
            requestTask: {
              ...state.requestTaskItem.requestTask,
              payload: {
                ...state.requestTaskItem.requestTask.payload,
                hseti: {
                  ...(state.requestTaskItem.requestTask.payload as HSETIApplicationSubmitRequestTaskPayload).hseti,
                  ...value,
                },
                hsetiAttachments: {
                  ...(state.requestTaskItem.requestTask.payload as HSETIApplicationSubmitRequestTaskPayload)
                    ?.hsetiAttachments,
                  ...attachments,
                },
                hsetiSectionsCompleted: {
                  ...(state.requestTaskItem.requestTask.payload as HSETIApplicationSubmitRequestTaskPayload)
                    ?.hsetiSectionsCompleted,
                  ...(statusKey ? { [statusKey]: statusValue } : undefined),
                },
              } as HSETIApplicationSubmitRequestTaskPayload,
            },
          },
        };

        return this.postHseTi(postHseTiState, actionType);
      }),
    );
  }

  postGroupDecisionReview(value: any, groupKey?: string, attachments?: { uuid: string; file: File }[]) {
    return this.store.pipe(
      first(),
      switchMap((state) =>
        this.tasksService.processRequestTaskAction({
          requestTaskActionType: 'HSE_TI_SAVE_REGULATOR_REVIEW_GROUP_DECISION',
          requestTaskId: state.requestTaskItem.requestTask.id,
          requestTaskActionPayload: this.createRequestTaskActionPayload('HSE_TI_SAVE_REGULATOR_REVIEW_GROUP_DECISION', {
            groupKey,
            value,
            state,
          }),
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
                    state.requestTaskItem.requestTask.payload as HSETIApplicationRegulatorReviewSubmitRequestTaskPayload
                  ).regulatorReviewGroupDecisions,
                  [groupKey]: {
                    ...value,
                  },
                },
                regulatorReviewAttachments: {
                  ...(
                    state.requestTaskItem.requestTask.payload as HSETIApplicationRegulatorReviewSubmitRequestTaskPayload
                  ).regulatorReviewAttachments,
                  ...attachments?.reduce((result, item) => ({ ...result, [item.uuid]: item.file.name }), {}),
                },
                regulatorReviewSectionsCompleted: {
                  ...(
                    state.requestTaskItem.requestTask.payload as HSETIApplicationRegulatorReviewSubmitRequestTaskPayload
                  )?.regulatorReviewSectionsCompleted,
                  ...{ [groupKey]: true },
                },
              } as HSETIApplicationRegulatorReviewSubmitRequestTaskPayload,
            },
          },
        });
      }),
    );
  }

  postOverallDecisionReview(value: any, groupKeyValue: boolean = false) {
    return this.store.pipe(
      first(),
      switchMap((state) =>
        this.tasksService.processRequestTaskAction({
          requestTaskActionType: 'HSE_TI_REGULATOR_REVIEW_SAVE',
          requestTaskId: state.requestTaskItem.requestTask.id,
          requestTaskActionPayload: this.createRequestTaskActionPayload('HSE_TI_REGULATOR_REVIEW_SAVE', {
            state,
            groupKeyValue,
            value,
          }),
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
                overallDecision: {
                  ...(
                    state.requestTaskItem.requestTask.payload as HSETIApplicationRegulatorReviewSubmitRequestTaskPayload
                  ).overallDecision,
                  ...value,
                },
                regulatorReviewSectionsCompleted: {
                  ...(
                    state.requestTaskItem.requestTask.payload as HSETIApplicationRegulatorReviewSubmitRequestTaskPayload
                  )?.regulatorReviewSectionsCompleted,
                  ...{ OVERALL_DECISION: groupKeyValue },
                },
              } as HSETIApplicationRegulatorReviewSubmitRequestTaskPayload,
            },
          },
        });
      }),
    );
  }

  postHseTi(state: CommonTasksState, actionType: RequestTaskActionProcessDTO['requestTaskActionType']) {
    const payload = state.requestTaskItem.requestTask.payload as HSETIApplicationSubmitRequestTaskPayload;
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

  postHseTiSubmit(actionType: RequestTaskActionProcessDTO['requestTaskActionType'], payload?: any) {
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

  private createRequestTaskActionPayload(
    actionType: RequestTaskActionProcessDTO['requestTaskActionType'],
    payload?: any,
  ) {
    switch (actionType) {
      case 'HSE_TI_SAVE_APPLICATION':
        return {
          payloadType: 'HSE_TI_APPLICATION_SAVE_PAYLOAD',
          hseti: payload.hseti,
          hsetiSectionsCompleted: payload.hsetiSectionsCompleted,
        } as HSETIApplicationSaveRequestTaskActionPayload;
      case 'HSE_TI_APPLICATION_AMENDS_SAVE':
        return {
          payloadType: 'HSE_TI_APPLICATION_AMENDS_SAVE_PAYLOAD',
          hseti: payload.hseti,
          hsetiSectionsCompleted: payload.hsetiSectionsCompleted,
          regulatorReviewSectionsCompleted: payload.regulatorReviewSectionsCompleted,
        } as RequestTaskActionPayload;
      case 'HSE_TI_APPLICATION_AMENDS_SUBMIT_TO_REGULATOR': {
        const hsetiSectionsCompleted = (() => {
          const { changesRequested, ...rest } = payload.hsetiSectionsCompleted;
          return rest;
        })();
        return {
          payloadType: 'HSE_TI_APPLICATION_AMENDS_SUBMIT_TO_REGULATOR_PAYLOAD',
          hsetiSectionsCompleted: hsetiSectionsCompleted,
        } as RequestTaskActionPayload;
      }
      case 'HSE_TI_SAVE_REGULATOR_REVIEW_GROUP_DECISION':
        return {
          payloadType: 'HSE_TI_SAVE_REGULATOR_REVIEW_GROUP_DECISION_PAYLOAD',
          group: payload.groupKey,
          decision: {
            ...payload.value,
          },
          regulatorReviewSectionsCompleted: {
            ...(
              payload.state.requestTaskItem.requestTask
                .payload as HSETIApplicationRegulatorReviewSubmitRequestTaskPayload
            )?.regulatorReviewSectionsCompleted,
            ...{ [payload.groupKey]: true },
          },
        } as RequestTaskActionPayload;
      case 'HSE_TI_REGULATOR_REVIEW_SAVE': {
        const hsetiPayload = payload.state.requestTaskItem.requestTask
          .payload as HSETIApplicationRegulatorReviewSubmitRequestTaskPayload;
        return {
          payloadType: 'HSE_TI_REGULATOR_REVIEW_SAVE_PAYLOAD',
          overallDecision: {
            type: payload?.value?.type ?? hsetiPayload.overallDecision?.type,
            reason: payload?.value?.reason ?? hsetiPayload.overallDecision?.reason,
          },
          regulatorReviewSectionsCompleted: {
            ...hsetiPayload?.regulatorReviewSectionsCompleted,
            ...{ OVERALL_DECISION: payload.groupKeyValue },
          },
        } as HSETIApplicationRegulatorReviewSaveTaskActionPayload;
      }
      default:
        return {
          payloadType: 'EMPTY_PAYLOAD',
        } as RequestTaskActionPayload;
    }
  }

  getOperatorDownloadUrlHsetiFile(hsetiFile: string): AttachedFile {
    const attachments: { [key: string]: string } = (
      this.store.getValue().requestTaskItem.requestTask.payload as HSETIApplicationSubmitRequestTaskPayload
    )?.hsetiAttachments;
    const url = this.getBaseFileDownloadUrl();

    return hsetiFile
      ? {
          downloadUrl: url + `${hsetiFile}`,
          fileName: attachments[hsetiFile],
        }
      : null;
  }

  getOperatorDownloadUrlFiles(files: string[]): AttachedFile[] {
    const attachments: { [key: string]: string } = (
      this.store.getValue().requestTaskItem.requestTask.payload as HSETIApplicationSubmitRequestTaskPayload
    )?.hsetiAttachments;
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
      this.store.getValue().requestTaskItem.requestTask
        .payload as HSETIApplicationRegulatorReviewSubmitRequestTaskPayload
    )?.regulatorReviewAttachments;
    const url = this.getBaseFileDownloadUrl();
    return (
      files?.map((id) => ({
        downloadUrl: url + `${id}`,
        fileName: attachments[id],
      })) ?? []
    );
  }
}
