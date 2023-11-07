import { Injectable } from '@angular/core';

import { distinctUntilChanged, map, Observable, pluck, tap } from 'rxjs';

import { Store } from '@core/store/store';

import {
  PermitSaveCessationRequestTaskActionPayload,
  PermitSurrender,
  PermitSurrenderApplicationSubmitRequestTaskPayload,
  PermitSurrenderReviewDecision,
  PermitSurrenderSaveApplicationRequestTaskActionPayload,
  PermitSurrenderSaveReviewDeterminationRequestTaskActionPayload,
  PermitSurrenderSaveReviewGroupDecisionRequestTaskActionPayload,
  RequestTaskActionEmptyPayload,
  TasksService,
} from 'pmrv-api';

import { BusinessErrorService } from '../../error/business-error/business-error.service';
import { catchTaskReassignedBadRequest } from '../../error/business-errors';
import { catchNotFoundRequest, ErrorCode } from '../../error/not-found-error';
import { requestTaskReassignedError, taskNotFoundError } from '../../shared/errors/request-task-error';
import { initialState, PermitSurrenderState } from './permit-surrender.state';

@Injectable({ providedIn: 'root' })
export class PermitSurrenderStore extends Store<PermitSurrenderState> {
  constructor(
    private readonly tasksService: TasksService,
    private readonly businessErrorService: BusinessErrorService,
  ) {
    super(initialState);
  }

  setState(state: PermitSurrenderState): void {
    super.setState(state);
  }

  get payload(): PermitSurrenderApplicationSubmitRequestTaskPayload {
    return this.getValue() as PermitSurrenderApplicationSubmitRequestTaskPayload;
  }

  get permitSurrender(): PermitSurrender {
    return this.payload.permitSurrender;
  }

  get isEditable$(): Observable<boolean> {
    return this.pipe(pluck('isEditable'));
  }

  get isPaymentRequired(): boolean {
    return true;
  }

  get isAssignableAndCapable$(): Observable<boolean> {
    return this.pipe(map((state) => state?.userAssignCapable && state?.assignable));
  }

  getDownloadUrlFiles(files: string[]): { downloadUrl: string; fileName: string }[] {
    const url = this.createBaseFileDownloadUrl();

    return (
      files?.map((id) => ({
        downloadUrl: url + `${id}`,
        fileName: this.getState().permitSurrenderAttachments[id],
      })) ?? []
    );
  }

  createBaseFileDownloadUrl() {
    return this.getState().requestActionId
      ? `/permit-surrender/action/${this.getState().requestActionId}/file-download/attachment/`
      : `/permit-surrender/${this.getState().requestTaskId}/file-download/`;
  }

  getPermitSurrender(): Observable<PermitSurrender> {
    return this.pipe(pluck('permitSurrender'), distinctUntilChanged());
  }

  postApplyPermitSurrender(state: PermitSurrenderState) {
    return this.tasksService
      .processRequestTaskAction({
        requestTaskActionType: 'PERMIT_SURRENDER_SAVE_APPLICATION',
        requestTaskId: state.requestTaskId,
        requestTaskActionPayload: {
          payloadType: 'PERMIT_SURRENDER_SAVE_APPLICATION_PAYLOAD',
          permitSurrender: state.permitSurrender,
          sectionsCompleted: state.sectionsCompleted,
        } as PermitSurrenderSaveApplicationRequestTaskActionPayload,
      })
      .pipe(
        catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
          this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
        ),
        catchTaskReassignedBadRequest(() =>
          this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
        ),
        tap(() => this.setState(state)),
      );
  }

  postSubmitPermitSurrender() {
    return this.tasksService
      .processRequestTaskAction({
        requestTaskActionType: 'PERMIT_SURRENDER_SUBMIT_APPLICATION',
        requestTaskId: this.getState().requestTaskId,
        requestTaskActionPayload: {
          payloadType: 'EMPTY_PAYLOAD',
        } as RequestTaskActionEmptyPayload,
      })
      .pipe(
        catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
          this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
        ),
        catchTaskReassignedBadRequest(() =>
          this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
        ),
      );
  }

  postReviewDecision(reviewDecision: PermitSurrenderReviewDecision, reviewDeterminationCompleted: boolean) {
    return this.tasksService
      .processRequestTaskAction({
        requestTaskActionType: 'PERMIT_SURRENDER_SAVE_REVIEW_GROUP_DECISION',
        requestTaskId: this.getState().requestTaskId,
        requestTaskActionPayload: {
          payloadType: 'PERMIT_SURRENDER_SAVE_REVIEW_GROUP_DECISION_PAYLOAD',
          reviewDecision: reviewDecision,
          reviewDeterminationCompleted: reviewDeterminationCompleted,
        } as PermitSurrenderSaveReviewGroupDecisionRequestTaskActionPayload,
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
            reviewDecision,
            reviewDeterminationCompleted,
            ...(!reviewDeterminationCompleted ? { reviewDetermination: null } : null),
          }),
        ),
      );
  }

  postReviewDetermination(value: any, status: boolean) {
    return this.tasksService
      .processRequestTaskAction({
        requestTaskActionType: 'PERMIT_SURRENDER_SAVE_REVIEW_DETERMINATION',
        requestTaskId: this.getState().requestTaskId,
        requestTaskActionPayload: {
          payloadType: 'PERMIT_SURRENDER_SAVE_REVIEW_DETERMINATION_PAYLOAD',
          reviewDetermination: value,
          reviewDeterminationCompleted: status,
        } as PermitSurrenderSaveReviewDeterminationRequestTaskActionPayload,
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
            reviewDetermination: value,
            reviewDeterminationCompleted: status,
          }),
        ),
      );
  }

  postSaveCessation(value: any, status: boolean) {
    return this.tasksService
      .processRequestTaskAction({
        requestTaskActionType: 'PERMIT_SURRENDER_SAVE_CESSATION',
        requestTaskId: this.getState().requestTaskId,
        requestTaskActionPayload: {
          payloadType: 'PERMIT_SURRENDER_SAVE_CESSATION_PAYLOAD',
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
}
