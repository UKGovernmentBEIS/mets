import { computed, inject, Injectable, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';

import { first, map, Observable, switchMap, tap } from 'rxjs';

import { AuthStore, selectUserRoleType } from '@core/store';
import { catchTaskReassignedBadRequest } from '@error/business-errors';
import { catchNotFoundRequest, ErrorCode } from '@error/not-found-error';
import { requestTaskReassignedError, taskNotFoundError } from '@shared/errors/request-task-error';
import { CapitalizeFirstPipe } from '@shared/pipes/capitalize-first.pipe';
import { AttachedFile } from '@shared/types/attached-file.type';
import { TasksHelperService } from '@tasks/shared/services/tasks-helper.service';
import { CommonTasksState } from '@tasks/store/common-tasks.state';

import {
  InstallationAccountViewService,
  NERApplicationRegulatorReviewSubmitRequestTaskPayload,
  NerApplicationSubmitRequestTaskPayload,
  NERApplicationSubmitToVerifierRequestTaskActionPayload,
  NERApplicationVerificationSubmitRequestTaskPayload,
  NerSaveApplicationRequestTaskActionPayload,
  RequestTaskActionPayload,
  RequestTaskActionProcessDTO,
} from 'pmrv-api';

import { NerPayload } from '../utils';

@Injectable({ providedIn: 'root' })
export class NerService extends TasksHelperService {
  private readonly installationAccountViewService = inject(InstallationAccountViewService);
  private readonly authStore = inject(AuthStore);
  private readonly capitalizeFirstPipe = inject(CapitalizeFirstPipe);

  private readonly installationAccountId$ = this.requestTaskItem$.pipe(
    switchMap((requestTaskItem) =>
      this.installationAccountViewService.getInstallationAccountById(requestTaskItem.requestInfo.accountId),
    ),
  );

  private readonly userRoleType = toSignal(this.authStore.pipe(selectUserRoleType), { initialValue: null });
  private readonly installationAccount = toSignal(this.installationAccountId$, { initialValue: null });

  private readonly installationName = computed(() =>
    this.installationAccount()?.accountPermitDto?.account.name
      ? this.stripSpecialChars(this.installationAccount()?.accountPermitDto?.account.name)
      : 'Unknown',
  );

  get payload(): Signal<NerPayload> {
    return toSignal(this.store.payload$ as Observable<NerPayload>);
  }

  get requestTaskType() {
    return toSignal(this.store.requestTaskType$);
  }

  get requestTaskItem() {
    return toSignal(this.store.requestTaskItem$);
  }

  get daysRemaining() {
    return toSignal(this.daysRemaining$);
  }

  get isEditable() {
    return toSignal(this.isEditable$);
  }

  get requestId() {
    return this.store.requestId;
  }

  get requestAccountId$() {
    return this.store.requestInfo$.pipe(map((info) => info.accountId));
  }

  get requestAccountId() {
    return toSignal(this.requestAccountId$);
  }

  get requestTaskId() {
    return this.store.requestTaskId;
  }

  get allowedRequestTaskActions() {
    return this.store.getState().requestTaskItem?.allowedRequestTaskActions ?? [];
  }

  getOperatorDownloadUrlFile(file: string): AttachedFile {
    const attachments: { [key: string]: string } =
      (this.store.getValue().requestTaskItem.requestTask.payload as NerApplicationSubmitRequestTaskPayload)
        ?.nerAttachments || {};
    const url = this.getBaseFileDownloadUrl();

    return file
      ? {
          downloadUrl: url + `${file}`,
          fileName: attachments[file],
        }
      : null;
  }

  getOperatorDownloadUrlFiles(files: string[]): AttachedFile[] {
    const attachments: { [key: string]: string } =
      (this.store.getValue().requestTaskItem.requestTask.payload as NerApplicationSubmitRequestTaskPayload)
        ?.nerAttachments || {};
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
      this.store.getValue().requestTaskItem.requestTask.payload as NERApplicationVerificationSubmitRequestTaskPayload
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
      this.store.getValue().requestTaskItem.requestTask.payload as NERApplicationVerificationSubmitRequestTaskPayload
    )?.verificationAttachments;
    const url = this.getBaseFileDownloadUrl();

    return (
      files?.map((id) => ({
        downloadUrl: url + `${id}`,
        fileName: attachments[id],
      })) ?? []
    );
  }

  getRegulatorDownloadUrlFile(file: string): AttachedFile {
    const attachments: { [key: string]: string } = (
      this.store.getValue().requestTaskItem.requestTask.payload as NERApplicationRegulatorReviewSubmitRequestTaskPayload
    )?.regulatorReviewAttachments;
    const url = this.getBaseFileDownloadUrl();

    return file
      ? {
          downloadUrl: url + `${file}`,
          fileName: attachments[file],
        }
      : null;
  }

  getRegulatorDownloadUrlFiles(files: string[]): AttachedFile[] {
    const attachments: { [key: string]: string } = (
      this.store.getValue().requestTaskItem.requestTask.payload as NERApplicationRegulatorReviewSubmitRequestTaskPayload
    )?.regulatorReviewAttachments;
    const url = this.getBaseFileDownloadUrl();

    return (
      files?.map((id) => ({
        downloadUrl: url + `${id}`,
        fileName: attachments[id],
      })) ?? []
    );
  }

  stripSpecialChars(str: string): string {
    return str.replace(/[^a-zA-Z0-9 _-]/g, '');
  }

  fileName(fileVersion: number, suffix: string): string {
    const alrId = this.requestId.replace('NER', 'NER-');
    const shortInstallationName = this.installationName().substring(0, 10);
    const roleType = this.capitalizeFirstPipe.transform(this.userRoleType());
    return `${alrId}-v${fileVersion}-uploaded by ${roleType}-${shortInstallationName}${suffix}`;
  }

  isDecisionComponentEditable() {
    return this.allowedRequestTaskActions.includes('NER_SAVE_REGULATOR_REVIEW_GROUP_DECISION');
  }

  postTaskSave(value: any, attachments?: { [key: string]: string }, statusValue?: boolean, statusKey?: string) {
    let actionType: RequestTaskActionProcessDTO['requestTaskActionType'];

    return this.store.pipe(
      first(),
      switchMap((state) => {
        const newPayload = state.requestTaskItem.requestTask.payload as NerApplicationSubmitRequestTaskPayload;

        const postNerState = {
          ...state,
          requestTaskItem: {
            ...state.requestTaskItem,
            requestTask: {
              ...state.requestTaskItem.requestTask,
              payload: {
                ...state.requestTaskItem.requestTask.payload,
                ner: {
                  ...newPayload.ner,
                  ...value,
                },
                nerAttachments: {
                  ...newPayload?.nerAttachments,
                  ...attachments,
                },
                nerSectionsCompleted: {
                  ...newPayload?.nerSectionsCompleted,
                  ...(statusKey ? { [statusKey]: statusValue } : undefined),
                },
                verificationPerformed: false,
              } as NerApplicationSubmitRequestTaskPayload,
            },
          },
        };

        switch (state.requestTaskItem.requestTask.type) {
          case 'NER_APPLICATION_SUBMIT':
            actionType = 'NER_SAVE_APPLICATION';
            break;
          case 'NER_APPLICATION_AMENDS_SUBMIT':
            actionType = 'NER_APPLICATION_AMENDS_SAVE';
            break;
        }

        return this.tasksService
          .processRequestTaskAction({
            requestTaskActionType: actionType,
            requestTaskId: state.requestTaskItem.requestTask.id,
            requestTaskActionPayload: this.createRequestTaskActionPayload(
              actionType,
              postNerState.requestTaskItem.requestTask.payload,
            ),
          })
          .pipe(
            catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
              this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
            ),
            catchTaskReassignedBadRequest(() =>
              this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
            ),
            tap(() => this.store.setState(postNerState)),
          );
      }),
    );
  }

  postNerSubmit(isReturnForAmends = false, regulatorVerificationRequired = false) {
    const state = this.store.getState();
    const requestTaskType = state.requestTaskItem.requestTask.type;
    const payload = state.requestTaskItem.requestTask.payload;
    const { verificationPerformed } = payload as NerApplicationSubmitRequestTaskPayload;

    let actionType: RequestTaskActionProcessDTO['requestTaskActionType'];

    switch (requestTaskType) {
      case 'NER_APPLICATION_VERIFICATION_SUBMIT':
      case 'NER_AMEND_APPLICATION_VERIFICATION_SUBMIT':
        actionType = 'NER_SUBMIT_VERIFICATION';
        break;

      case 'NER_APPLICATION_REVIEW':
        actionType = isReturnForAmends ? 'NER_REGULATOR_REVIEW_RETURN_FOR_AMENDS' : 'NER_COMPLETE_REVIEW';
        break;

      case 'NER_APPLICATION_AMENDS_SUBMIT':
        actionType =
          regulatorVerificationRequired && !verificationPerformed
            ? 'NER_APPLICATION_AMENDS_SUBMIT_TO_VERIFIER'
            : 'NER_APPLICATION_AMENDS_SUBMIT_TO_REGULATOR';

        break;

      default:
        actionType = verificationPerformed ? 'NER_SUBMIT_APPLICATION' : 'NER_APPLICATION_SUBMIT_TO_VERIFIER';
        break;
    }

    return this.store.pipe(
      first(),
      switchMap((state) =>
        this.tasksService.processRequestTaskAction({
          requestTaskActionType: actionType,
          requestTaskId: state.requestTaskItem.requestTask.id,
          requestTaskActionPayload: this.createRequestTaskActionPayload(actionType, payload),
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

  postVerificationTaskSave(
    value: any,
    statusValue?: boolean | boolean[],
    statusKey?: string | 'sendReport',
    attachments?: { [key: string]: string },
  ) {
    return this.store.pipe(
      first(),
      switchMap((state) =>
        this.postNer(
          {
            ...state,
            requestTaskItem: {
              ...state.requestTaskItem,
              requestTask: {
                ...state.requestTaskItem.requestTask,
                payload: {
                  ...state.requestTaskItem.requestTask.payload,
                  verificationReport: {
                    ...(state.requestTaskItem.requestTask.payload as NERApplicationVerificationSubmitRequestTaskPayload)
                      .verificationReport,
                    ...value,
                  },
                  verificationSectionsCompleted: {
                    ...(state.requestTaskItem.requestTask.payload as NERApplicationVerificationSubmitRequestTaskPayload)
                      ?.verificationSectionsCompleted,
                    ...(statusKey
                      ? { [statusKey]: Array.isArray(statusValue) ? statusValue : [statusValue] }
                      : undefined),
                  },
                  verificationAttachments: {
                    ...(state.requestTaskItem.requestTask.payload as NERApplicationVerificationSubmitRequestTaskPayload)
                      ?.verificationAttachments,
                    ...attachments,
                  },
                } as NERApplicationVerificationSubmitRequestTaskPayload,
              },
            },
          },
          'NER_APPLICATION_SAVE_VERIFICATION',
        ),
      ),
    );
  }

  postNer(state: CommonTasksState, actionType: RequestTaskActionProcessDTO['requestTaskActionType']) {
    const payload = state.requestTaskItem.requestTask.payload as NerApplicationSubmitRequestTaskPayload;
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

  postRegulatorTaskSave(
    value: any,
    statusValue?: boolean | boolean[],
    statusKey?: string | 'sendReport',
    attachments?: { [key: string]: string },
  ) {
    return this.store.pipe(
      first(),
      switchMap((state) =>
        this.postNer(
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
                      state.requestTaskItem.requestTask.payload as NERApplicationRegulatorReviewSubmitRequestTaskPayload
                    ).regulatorReviewOutcome,
                    ...value,
                  },
                  regulatorReviewAttachments: {
                    ...(
                      state.requestTaskItem.requestTask.payload as NERApplicationRegulatorReviewSubmitRequestTaskPayload
                    ).regulatorReviewAttachments,
                    ...attachments,
                  },
                  regulatorReviewSectionsCompleted: {
                    ...(
                      state.requestTaskItem.requestTask.payload as NERApplicationRegulatorReviewSubmitRequestTaskPayload
                    )?.regulatorReviewSectionsCompleted,
                    ...{ [statusKey]: statusValue },
                  },
                } as NERApplicationRegulatorReviewSubmitRequestTaskPayload,
              },
            },
          },
          'NER_SAVE_APPLICATION_REVIEW',
        ),
      ),
    );
  }

  postRegulatorTaskSubmit() {
    const state = this.store.getState();
    const payload = state.requestTaskItem.requestTask.payload as NERApplicationRegulatorReviewSubmitRequestTaskPayload;
    const actionType =
      payload.regulatorReviewOutcome.opinion === 'PROCEED_TO_AUTHORITY'
        ? 'NER_COMPLETE_REVIEW'
        : 'NER_WITHDRAW_APPLICATION';

    return this.postNer(state, actionType);
  }

  private createRequestTaskActionPayload(
    actionType: RequestTaskActionProcessDTO['requestTaskActionType'],
    payload?: any,
  ) {
    switch (actionType) {
      case 'NER_SAVE_APPLICATION':
        return {
          payloadType: 'NER_SAVE_APPLICATION_PAYLOAD',
          ner: payload.ner,
          nerSectionsCompleted: payload.nerSectionsCompleted,
        } as NerSaveApplicationRequestTaskActionPayload;
      case 'NER_APPLICATION_SUBMIT_TO_VERIFIER':
        return {
          payloadType: 'NER_APPLICATION_SUBMIT_TO_VERIFIER_PAYLOAD',
        } as NERApplicationSubmitToVerifierRequestTaskActionPayload;
      case 'NER_APPLICATION_SAVE_VERIFICATION':
        return {
          ...(payload as NERApplicationVerificationSubmitRequestTaskPayload).verificationReport,
          payloadType: 'NER_APPLICATION_SAVE_VERIFICATION_PAYLOAD',
          verificationSectionsCompleted: payload.verificationSectionsCompleted,
        } as NERApplicationSubmitToVerifierRequestTaskActionPayload;
      case 'NER_SAVE_APPLICATION_REVIEW':
        return {
          payloadType: 'NER_SAVE_APPLICATION_REVIEW_PAYLOAD',
          regulatorReviewOutcome: payload.regulatorReviewOutcome,
          regulatorReviewSectionsCompleted: payload.regulatorReviewSectionsCompleted,
        } as RequestTaskActionPayload;

      case 'NER_APPLICATION_AMENDS_SAVE':
        return {
          payloadType: 'NER_APPLICATION_AMENDS_SAVE_PAYLOAD',
          ner: payload.ner,
          nerSectionsCompleted: payload.nerSectionsCompleted,
          regulatorReviewSectionsCompleted: payload.regulatorReviewSectionsCompleted,
        } as RequestTaskActionPayload;

      case 'NER_APPLICATION_AMENDS_SUBMIT_TO_REGULATOR': {
        const nerSectionsCompleted = (() => {
          const { changesRequested, ...rest } = payload.nerSectionsCompleted;
          return rest;
        })();
        return {
          payloadType: 'NER_APPLICATION_AMENDS_SUBMIT_TO_REGULATOR_PAYLOAD',
          nerSectionsCompleted: nerSectionsCompleted,
          regulatorReviewSectionsCompleted: {
            ...payload.regulatorReviewSectionsCompleted,
            ...(payload.regulatorReviewSectionsCompleted?.OUTCOME ? { OUTCOME: false } : {}),
          },
        } as RequestTaskActionPayload;
      }
      case 'NER_APPLICATION_AMENDS_SUBMIT_TO_VERIFIER': {
        return {
          payloadType: 'NER_APPLICATION_AMENDS_SUBMIT_TO_VERIFIER_PAYLOAD',
          verificationSectionsCompleted: payload.verificationSectionsCompleted,
        } as RequestTaskActionPayload;
      }
      case 'NER_REGULATOR_REVIEW_RETURN_FOR_AMENDS':
        return {
          payloadType: 'NER_REGULATOR_REVIEW_RETURN_FOR_AMENDS_PAYLOAD',
          nerSectionsCompleted: { ...payload.nerSectionsCompleted, NER: false },
        } as RequestTaskActionPayload;
      default:
        return {
          payloadType: 'EMPTY_PAYLOAD',
        } as RequestTaskActionPayload;
    }
  }
}
