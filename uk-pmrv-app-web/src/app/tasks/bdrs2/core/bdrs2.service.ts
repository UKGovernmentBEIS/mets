import { computed, Injectable, Signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';

import { first, map, Observable, switchMap, tap } from 'rxjs';

import { AuthStore, selectUserRoleType } from '@core/store';
import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchTaskReassignedBadRequest } from '@error/business-errors';
import { catchNotFoundRequest, ErrorCode } from '@error/not-found-error';
import { requestTaskReassignedError, taskNotFoundError } from '@shared/errors/request-task-error';
import { CapitalizeFirstPipe } from '@shared/pipes/capitalize-first.pipe';
import { ItemNamePipe } from '@shared/pipes/item-name.pipe';
import { TasksHelperService } from '@tasks/shared/services/tasks-helper.service';
import { CommonTasksState } from '@tasks/store/common-tasks.state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import {
  BDRS2ApplicationSubmitRequestTaskPayload,
  BDRS2RequestMetadata,
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
    }

    return this.store.pipe(
      first(),
      switchMap((state) => {
        const bdrs2FileVersion = (state.requestTaskItem.requestTask.payload as BDRS2ApplicationSubmitRequestTaskPayload)
          ?.bdrs2FileVersion;

        const postBdrState = {
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
                bdrs2FileVersion: bdrs2FileVersion !== undefined ? bdrs2FileVersion : undefined,
              } as BDRS2ApplicationSubmitRequestTaskPayload,
            },
          },
        };

        return this.postBdr(postBdrState, actionType);
      }),
    );
  }

  postBdr(state: CommonTasksState, actionType: RequestTaskActionProcessDTO['requestTaskActionType']) {
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
    payload?: BDRS2ApplicationSubmitRequestTaskPayload,
  ) {
    switch (actionType) {
      case 'BDRS2_SAVE_APPLICATION':
        return {
          payloadType: 'BDRS2_APPLICATION_SAVE_PAYLOAD',
          bdrs2: payload.bdrs2,
          bdrs2SectionsCompleted: payload.bdrs2SectionsCompleted,
          bdrs2FileVersion: payload.bdrs2FileVersion,
        } as RequestTaskActionPayload;

      default:
        return {
          payloadType: 'EMPTY_PAYLOAD',
        } as RequestTaskActionPayload;
    }
  }
}
