import { computed, inject, Injectable, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';

import { first, map, switchMap, tap } from 'rxjs';

import { AuthStore, selectUserRoleType } from '@core/store';
import { catchTaskReassignedBadRequest } from '@error/business-errors';
import { catchNotFoundRequest, ErrorCode } from '@error/not-found-error';
import { requestTaskReassignedError, taskNotFoundError } from '@shared/errors/request-task-error';
import { CapitalizeFirstPipe } from '@shared/pipes/capitalize-first.pipe';
import { AttachedFile } from '@shared/types/attached-file.type';
import { TasksHelperService } from '@tasks/shared/services/tasks-helper.service';

import {
  InstallationAccountViewService,
  NerApplicationSubmitRequestTaskPayload,
  NERApplicationSubmitToVerifierRequestTaskActionPayload,
  NerSaveApplicationRequestTaskActionPayload,
  RequestTaskActionPayload,
  RequestTaskActionProcessDTO,
} from 'pmrv-api';

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

  get payload(): Signal<NerApplicationSubmitRequestTaskPayload> {
    return toSignal(this.store.payload$);
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

  stripSpecialChars(str: string): string {
    return str.replace(/[^a-zA-Z0-9 _-]/g, '');
  }

  fileName(fileVersion: number, suffix: string): string {
    const alrId = this.requestId.replace('NER', 'NER-');
    const shortInstallationName = this.installationName().substring(0, 10);
    const roleType = this.capitalizeFirstPipe.transform(this.userRoleType());
    return `${alrId}-v${fileVersion}-uploaded by ${roleType}-${shortInstallationName}${suffix}`;
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
              } as NerApplicationSubmitRequestTaskPayload,
            },
          },
        };

        switch (state.requestTaskItem.requestTask.type) {
          default:
            actionType = 'NER_SAVE_APPLICATION';
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

  postNerSubmit() {
    const state = this.store.getState();
    const requestTaskType = state.requestTaskItem.requestTask.type;
    const payload = state.requestTaskItem.requestTask.payload;

    let actionType: RequestTaskActionProcessDTO['requestTaskActionType'];

    switch (requestTaskType) {
      default:
        actionType = 'NER_APPLICATION_SUBMIT_TO_VERIFIER';
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

      default:
        return {
          payloadType: 'EMPTY_PAYLOAD',
        } as RequestTaskActionPayload;
    }
  }
}
