/* eslint-disable @typescript-eslint/no-unused-vars */
import { Injectable } from '@angular/core';

import { first, map, Observable, switchMap, tap } from 'rxjs';

import { BusinessErrorService } from '@error/business-error/business-error.service';
import { catchTaskReassignedBadRequest } from '@error/business-errors';
import { catchNotFoundRequest, ErrorCode } from '@error/not-found-error';
import { constructAmendTaskStatusKey } from '@permit-application/amend/amend';
import {
  ReviewDeterminationStatus,
  ReviewGroupDecisionStatus,
  ReviewGroupTasksAggregatorStatus,
} from '@permit-application/review/types/review.permit.type';
import { resolveDeterminationStatus } from '@permit-application/review/utils/review';
import { mandatoryReviewGroups } from '@permit-application/review/utils/review.permit';
import { AmendGroup } from '@permit-application/shared/types/amend.global.type';
import { PermitAmendGroup } from '@permit-application/shared/types/amend.permit.type';
import { permitApplicationReviewGroupDecision } from '@permit-application/shared/types/review.types';
import { reviewRequestTaskTypes } from '@permit-application/shared/utils/permit';
import { PermitIssuanceStore } from '@permit-issuance/store/permit-issuance.store';
import { requestTaskReassignedError, taskNotFoundError } from '@shared/errors/request-task-error';
import { UrlRequestType } from '@shared/types/url-request-type';

import {
  PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload,
  PermitTransferBDetailsConfirmationReviewDecision,
  PermitTransferBSaveDetailsConfirmationReviewGroupDecisionRequestTaskActionPayload,
  PermitTransferDetailsConfirmation,
  RequestTaskActionPayload,
  RequestTaskActionProcessDTO,
  TasksService,
} from 'pmrv-api';

import { PermitTransferState } from './permit-transfer.state';

@Injectable({ providedIn: 'root' })
export class PermitTransferStore extends PermitIssuanceStore<PermitTransferState> {
  constructor(
    protected readonly tasksService: TasksService,
    protected readonly businessErrorService: BusinessErrorService,
  ) {
    super(tasksService, businessErrorService);
  }

  override get urlRequestType(): UrlRequestType {
    return 'permit-transfer';
  }

  override getRequestTaskActionTypeReturnForAmend(): RequestTaskActionProcessDTO['requestTaskActionType'] {
    return 'PERMIT_TRANSFER_B_REVIEW_RETURN_FOR_AMENDS';
  }

  override getRequestTaskActionTypeRecall(): RequestTaskActionProcessDTO['requestTaskActionType'] {
    return 'PERMIT_TRANSFER_B_RECALL_FROM_AMENDS';
  }

  override getApplyForHeader(): string {
    return 'Full transfer of permit';
  }

  override getReturnForAmendHeader(): string {
    return 'Transfer application returned for amends';
  }

  override isDeterminationGrantButtonDisplayed$(): Observable<boolean> {
    return this.pipe(
      map((state) => {
        const groups = mandatoryReviewGroups.concat(
          Object.keys(state.permit.monitoringApproaches) as Array<
            PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload['group']
          >,
        );
        return (
          groups.every((mg) => state.reviewSectionsCompleted[mg]) &&
          groups.every((mg) => state.reviewGroupDecisions[mg]?.type === 'ACCEPTED') &&
          state.permitTransferDetailsConfirmationDecision?.type === 'ACCEPTED' &&
          state.reviewSectionsCompleted['CONFIRM_TRANSFER_DETAILS']
        );
      }),
    );
  }

  override isDeterminationRejectButtonDisplayed$(): Observable<boolean> {
    return this.pipe(
      map((state) => {
        const groups = mandatoryReviewGroups.concat(
          Object.keys(state.permit.monitoringApproaches) as Array<
            PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload['group']
          >,
        );

        return (
          groups.every((mg) => state.reviewSectionsCompleted[mg]) &&
          state.reviewSectionsCompleted['CONFIRM_TRANSFER_DETAILS'] &&
          groups.every((mg) => !!state.reviewGroupDecisions[mg]?.type) &&
          (state.permitTransferDetailsConfirmationDecision?.type === 'REJECTED' ||
            groups.some((mg) => state.reviewGroupDecisions[mg]?.type === 'REJECTED')) &&
          !groups.some((mg) => state.reviewGroupDecisions[mg]?.type === 'OPERATOR_AMENDS_NEEDED')
        );
      }),
    );
  }

  override getDeterminationHeader(): string {
    return 'Transfer determination';
  }

  override getDeterminationHeaderHint(): string {
    return 'determination';
  }

  override getDeterminationGrantText(): string {
    return 'Grant';
  }

  override amendsIsNotNeeded(groupKey: permitApplicationReviewGroupDecision) {
    return groupKey === 'CONFIRM_TRANSFER_DETAILS' ? true : false;
  }

  override reviewGroupDecisions(groupKey: permitApplicationReviewGroupDecision): { [key: string]: any } {
    return this.amendsIsNotNeeded(groupKey)
      ? this.getValue().permitTransferDetailsConfirmationDecision
      : this.getValue().reviewGroupDecisions?.[groupKey];
  }

  override getDeterminationStatus$(): Observable<ReviewDeterminationStatus> {
    return this.pipe(
      map((state) => {
        if (!state.isRequestTask) {
          return state.requestActionType === 'PERMIT_TRANSFER_B_APPLICATION_GRANTED'
            ? ('granted' as ReviewDeterminationStatus)
            : ('undecided' as ReviewDeterminationStatus); //fallback, should not happen
        } else {
          return resolveDeterminationStatus(state);
        }
      }),
    );
  }

  override postAmendStatus(section: AmendGroup) {
    return this.postStatus(constructAmendTaskStatusKey(section as PermitAmendGroup), true);
  }

  override postReview(
    value: any,
    groupKey: permitApplicationReviewGroupDecision,
    files?: { uuid: string; file: File }[],
  ) {
    return this.pipe(
      first(),
      switchMap((state) => {
        const reviewGroups = mandatoryReviewGroups.concat(
          Object.keys(state.permit.monitoringApproaches) as Array<
            PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload['group']
          >,
        );

        const areAllOtherReviewGroupsAccepted =
          reviewGroups
            .filter((mg) => mg !== groupKey)
            .every((mg) => state.reviewGroupDecisions[mg]?.type === 'ACCEPTED') &&
          (groupKey === 'CONFIRM_TRANSFER_DETAILS'
            ? value
            : (state.permitTransferDetailsConfirmationDecision as PermitTransferBDetailsConfirmationReviewDecision)
          )?.type === 'ACCEPTED';

        const shouldResetDetermination = (state) =>
          (state.determination?.type === 'GRANTED' && value.type !== 'ACCEPTED') ||
          (state.determination?.type === 'REJECTED' && value.type === 'ACCEPTED' && areAllOtherReviewGroupsAccepted) ||
          (state.determination?.type === 'REJECTED' && value.type === 'OPERATOR_AMENDS_NEEDED');

        if (this.amendsIsNotNeeded(groupKey)) {
          return this.postReviewGroupDecisionTransfer({
            ...state,
            reviewGroupDecisions: {
              ...state.reviewGroupDecisions,
            },
            reviewSectionsCompleted: {
              ...state.reviewSectionsCompleted,
              [groupKey]: true,
              ...(shouldResetDetermination(state) ? { determination: false } : null),
            },
            reviewAttachments: {
              ...state.reviewAttachments,
              ...files?.reduce((result, item) => ({ ...result, [item.uuid]: item.file.name }), {}),
            },
            determination: shouldResetDetermination(state) ? {} : state.determination,
            permitTransferDetailsConfirmationDecision: value,
          });
        } else {
          return super.postReviewGroupDecision(
            {
              ...state,
              reviewGroupDecisions: {
                ...state.reviewGroupDecisions,
                [groupKey]: value,
              },
              reviewSectionsCompleted: {
                ...state.reviewSectionsCompleted,
                [groupKey]: true,
                ...(shouldResetDetermination(state) ? { determination: false } : null),
              },
              reviewAttachments: {
                ...state.reviewAttachments,
                ...files?.reduce((result, item) => ({ ...result, [item.uuid]: item.file.name }), {}),
              },
              determination: shouldResetDetermination(state) ? {} : state.determination,
            },
            groupKey,
          );
        }
      }),
    );
  }

  override postDetermination(value: any, status: boolean): Observable<any> {
    const requestTaskActionType = 'PERMIT_ISSUANCE_SAVE_REVIEW_DETERMINATION';
    const requestTaskActionPayloadType = 'PERMIT_ISSUANCE_SAVE_REVIEW_DETERMINATION_PAYLOAD';
    return super.postDeterminationInner(requestTaskActionType, requestTaskActionPayloadType, value, status);
  }

  protected override postState(state: PermitTransferState) {
    return this.tasksService
      .processRequestTaskAction({
        requestTaskActionType:
          state.requestTaskType === 'PERMIT_TRANSFER_B_APPLICATION_REVIEW'
            ? 'PERMIT_ISSUANCE_SAVE_APPLICATION_REVIEW'
            : state.requestTaskType === 'PERMIT_TRANSFER_B_APPLICATION_AMENDS_SUBMIT'
              ? 'PERMIT_ISSUANCE_SAVE_APPLICATION_AMEND'
              : 'PERMIT_TRANSFER_B_SAVE_APPLICATION',
        requestTaskId: state.requestTaskId,
        requestTaskActionPayload: {
          permitType: state.permitType,
          payloadType:
            state.requestTaskType === 'PERMIT_TRANSFER_B_APPLICATION_REVIEW'
              ? 'PERMIT_ISSUANCE_SAVE_APPLICATION_REVIEW_PAYLOAD'
              : state.requestTaskType === 'PERMIT_TRANSFER_B_APPLICATION_AMENDS_SUBMIT'
                ? 'PERMIT_ISSUANCE_SAVE_APPLICATION_AMEND_PAYLOAD'
                : 'PERMIT_TRANSFER_B_SAVE_APPLICATION_PAYLOAD',
          permit: state.permit,
          permitSectionsCompleted: state.permitSectionsCompleted,
          ...(['PERMIT_TRANSFER_B_APPLICATION_REVIEW', 'PERMIT_TRANSFER_B_APPLICATION_AMENDS_SUBMIT'].includes(
            state.requestTaskType,
          )
            ? { reviewSectionsCompleted: state.reviewSectionsCompleted }
            : null),
          ...(!['PERMIT_TRANSFER_B_APPLICATION_SUBMIT', 'PERMIT_TRANSFER_B_APPLICATION_REVIEW'].includes(
            state.requestTaskType,
          ) && {
            permitType: state.permitType,
          }),
          ...(!['PERMIT_TRANSFER_B_APPLICATION_REVIEW', 'PERMIT_TRANSFER_B_APPLICATION_AMENDS_SUBMIT'].includes(
            state.requestTaskType,
          )
            ? { permitTransferDetailsConfirmation: state.permitTransferDetailsConfirmation ?? {} }
            : null),
        } as RequestTaskActionPayload,
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
            ...state,
            ...(reviewRequestTaskTypes.includes(state.requestTaskType) && this.shouldResetDetermination(state)
              ? { determination: {} }
              : null),
          }),
        ),
      );
  }

  getConfirmTransferDetailsReviewStatus$(): Observable<ReviewGroupDecisionStatus | ReviewGroupTasksAggregatorStatus> {
    return this.pipe(map((state) => this.confirmTransferDetailsReviewStatus(state)));
  }

  postTransferDetailsConfirmation(permitTransferDetailsConfirmation: PermitTransferDetailsConfirmation) {
    return this.pipe(
      first(),
      switchMap((state) =>
        this.postState({
          ...state,
          permitTransferDetailsConfirmation,
          permitSectionsCompleted: {
            ...state.permitSectionsCompleted,
            transferDetails: [true],
          },
        }),
      ),
    );
  }

  getRequestId(): Observable<string> {
    return this.pipe(map((state) => state.requestId));
  }

  getCompetentAuthority() {
    return this.pipe(map((state) => state.competentAuthority));
  }

  private confirmTransferDetailsReviewStatus(state: PermitTransferState) {
    const permitTransferDetailsConfirmationDecision =
      state?.permitTransferDetailsConfirmationDecision as PermitTransferBDetailsConfirmationReviewDecision;
    if (!state.isRequestTask) {
      return permitTransferDetailsConfirmationDecision?.type === 'ACCEPTED'
        ? 'accepted'
        : permitTransferDetailsConfirmationDecision?.type === 'REJECTED'
          ? 'rejected'
          : 'operator to amend';
    } else {
      return state?.reviewSectionsCompleted['CONFIRM_TRANSFER_DETAILS']
        ? permitTransferDetailsConfirmationDecision?.type === 'ACCEPTED'
          ? 'accepted'
          : permitTransferDetailsConfirmationDecision?.type === 'REJECTED'
            ? 'rejected'
            : 'operator to amend'
        : 'undecided';
    }
  }

  private postReviewGroupDecisionTransfer(state: PermitTransferState) {
    const decision = state.permitTransferDetailsConfirmationDecision;
    const reviewGroups = mandatoryReviewGroups.concat(
      Object.keys(state.permit.monitoringApproaches) as Array<
        PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload['group']
      >,
    );

    const areAllOtherReviewGroupsAccepted = reviewGroups.every(
      (mg) =>
        state.reviewGroupDecisions[mg]?.type === 'ACCEPTED' &&
        state.permitTransferDetailsConfirmationDecision?.type === 'ACCEPTED',
    );

    const shouldResetDetermination = (state) =>
      (state.determination?.type === 'GRANTED' && decision.type !== 'ACCEPTED') ||
      (state.determination?.type === 'REJECTED' && decision.type === 'ACCEPTED' && areAllOtherReviewGroupsAccepted);

    return this.tasksService
      .processRequestTaskAction({
        requestTaskActionType: 'PERMIT_TRANSFER_B_SAVE_DETAILS_CONFIRMATION_REVIEW_GROUP_DECISION',
        requestTaskId: state.requestTaskId,
        requestTaskActionPayload: {
          payloadType: 'PERMIT_TRANSFER_B_SAVE_DETAILS_CONFIRMATION_REVIEW_GROUP_DECISION_PAYLOAD',
          decision,
          reviewSectionsCompleted: {
            ...state.reviewSectionsCompleted,
            ...(shouldResetDetermination(state) ? { determination: false } : null),
          },
        } as PermitTransferBSaveDetailsConfirmationReviewGroupDecisionRequestTaskActionPayload,
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
            ...state,
            permitTransferDetailsConfirmationDecision: state.permitTransferDetailsConfirmationDecision,
            reviewSectionsCompleted: {
              ...state.reviewSectionsCompleted,
              ...(shouldResetDetermination(state) ? { determination: false } : null),
            },
          }),
        ),
      );
  }
}
