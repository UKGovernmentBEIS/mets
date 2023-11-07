import { Injectable } from '@angular/core';

import { distinctUntilChanged, map, Observable, tap } from 'rxjs';

import { Store } from '@core/store/store';
import { requestTaskReassignedError, taskNotFoundError } from '@shared/errors/request-task-error';

import {
  PermitCessation,
  PermitRevocation,
  PermitRevocationApplicationSubmitRequestTaskPayload,
  PermitRevocationApplicationWithdrawRequestTaskActionPayload,
  PermitRevocationSaveApplicationRequestTaskActionPayload,
  PermitSaveCessationRequestTaskActionPayload,
  TasksService,
} from 'pmrv-api';

import { BusinessErrorService } from '../../error/business-error/business-error.service';
import { catchTaskReassignedBadRequest } from '../../error/business-errors';
import { catchNotFoundRequest, ErrorCode } from '../../error/not-found-error';
import { initialState, PermitRevocationState } from './permit-revocation.state';

@Injectable({ providedIn: 'root' })
export class PermitRevocationStore extends Store<PermitRevocationState> {
  constructor(
    private readonly tasksService: TasksService,
    private readonly businessErrorService: BusinessErrorService,
  ) {
    super(initialState);
  }

  setState(state: PermitRevocationState): void {
    super.setState(state);
  }

  get payload(): PermitRevocationApplicationSubmitRequestTaskPayload {
    return {
      ...this.getValue(),
      feeAmount: this.getValue().feeAmount?.toString(),
    } as PermitRevocationApplicationSubmitRequestTaskPayload;
  }

  get permitRevocation(): PermitRevocation {
    return this.payload.permitRevocation;
  }

  get isEditable$(): Observable<boolean> {
    return this.pipe(map((state) => state.isEditable));
  }

  get isPaymentRequired(): boolean {
    return true;
  }

  get isAssignableAndCapable$(): Observable<boolean> {
    return this.pipe(map((state) => state?.userAssignCapable && state?.assignable));
  }

  getPermitRevocation(): Observable<PermitRevocation> {
    return this.pipe(
      map((state) => state.permitRevocation),
      distinctUntilChanged(),
    );
  }

  postApplyPermitRevocation(state: PermitRevocationState) {
    const payload: PermitRevocationSaveApplicationRequestTaskActionPayload = {
      payloadType: 'PERMIT_REVOCATION_SAVE_APPLICATION_PAYLOAD',
      permitRevocation: state.permitRevocation,
      sectionsCompleted: state.sectionsCompleted,
    };

    return this.tasksService
      .processRequestTaskAction({
        requestTaskActionType: 'PERMIT_REVOCATION_SAVE_APPLICATION',
        requestTaskId: state.requestTaskId,
        requestTaskActionPayload: payload,
      })
      .pipe(
        catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
          this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
        ),
        catchTaskReassignedBadRequest(() =>
          this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
        ),
        tap(() => {
          this.setState(state);
        }),
      );
  }

  getDownloadUrlFiles(files: string[]): { downloadUrl: string; fileName: string }[] {
    const url = this.createBaseFileDownloadUrl();

    return (
      files?.map((id) => ({
        downloadUrl: url + `${id}`,
        fileName: this.getState().revocationAttachments[id],
      })) ?? []
    );
  }

  postPermitRevocationWithdraw(state: PermitRevocationState) {
    const payload: PermitRevocationApplicationWithdrawRequestTaskActionPayload = {
      payloadType: 'PERMIT_REVOCATION_WITHDRAW_APPLICATION_PAYLOAD',
      reason: state.reason,
      files: state.withdrawFiles,
    };

    return this.tasksService
      .processRequestTaskAction({
        requestTaskActionType: 'PERMIT_REVOCATION_WITHDRAW_APPLICATION',
        requestTaskId: state.requestTaskId,
        requestTaskActionPayload: payload,
      })
      .pipe(
        catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
          this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
        ),
        catchTaskReassignedBadRequest(() =>
          this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
        ),
        tap(() => {
          this.setState(state);
        }),
      );
  }

  postSaveCessation(value: PermitCessation, status: boolean) {
    return this.tasksService
      .processRequestTaskAction({
        requestTaskActionType: 'PERMIT_REVOCATION_SAVE_CESSATION',
        requestTaskId: this.getState().requestTaskId,
        requestTaskActionPayload: {
          payloadType: 'PERMIT_REVOCATION_SAVE_CESSATION_PAYLOAD',
          cessation: value,
          cessationCompleted: status,
        } as PermitSaveCessationRequestTaskActionPayload,
      })
      .pipe(
        catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
          this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
        ),
        catchTaskReassignedBadRequest(() =>
          this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
        ),
        tap(() =>
          this.setState({
            ...this.getState(),
            cessation: value,
            cessationCompleted: status,
          }),
        ),
      );
  }

  createBaseFileDownloadUrl() {
    return this.getState().requestActionId
      ? `/permit-revocation/action/${this.getState().requestActionId}/file-download/attachment/`
      : `/permit-revocation/${this.getState().requestTaskId}/file-download/attachment/`;
  }
}
