/* eslint-disable @typescript-eslint/no-unused-vars */
import { Injectable } from '@angular/core';

import { EMPTY, first, map, Observable, of, switchMap, tap } from 'rxjs';

import { initialState } from '@permit-application/store/permit-application.state';
import { requestTaskReassignedError, taskNotFoundError } from '@shared/errors/request-task-error';
import { UrlRequestType } from '@shared/types/url-request-type';

import {
  Permit,
  PermitContainer,
  PermitIssuanceDeemedWithdrawnDetermination,
  PermitIssuanceRejectDetermination,
  PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload,
  RequestTaskActionPayload,
  RequestTaskActionProcessDTO,
  TasksService,
} from 'pmrv-api';

import { BusinessErrorService } from '../../error/business-error/business-error.service';
import { catchTaskReassignedBadRequest } from '../../error/business-errors';
import { catchNotFoundRequest, ErrorCode } from '../../error/not-found-error';
import { constructAmendTaskStatusKey } from '../../permit-application/amend/amend';
import { ReviewDeterminationStatus } from '../../permit-application/review/types/review.permit.type';
import { findReviewGroupByTaskKey, resolveDeterminationStatus } from '../../permit-application/review/utils/review';
import { mandatoryReviewGroups } from '../../permit-application/review/utils/review.permit';
import { AmendGroup } from '../../permit-application/shared/types/amend.global.type';
import { PermitAmendGroup } from '../../permit-application/shared/types/amend.permit.type';
import { Path, TaskKey } from '../../permit-application/shared/types/permit-task.type';
import { reviewRequestTaskTypes } from '../../permit-application/shared/utils/permit';
import { PermitApplicationStore } from '../../permit-application/store/permit-application.store';
import { PermitIssuanceState } from './permit-issuance.state';

@Injectable({ providedIn: 'root' })
export class PermitIssuanceStore<
  T extends PermitIssuanceState = PermitIssuanceState,
> extends PermitApplicationStore<T> {
  constructor(
    protected readonly tasksService: TasksService,
    protected readonly businessErrorService: BusinessErrorService,
  ) {
    super(initialState as T, tasksService, businessErrorService);
  }

  override get isPaymentRequired(): boolean {
    return true;
  }

  override get urlRequestType(): UrlRequestType {
    return 'permit-issuance';
  }

  get showDiff$(): Observable<boolean> {
    return of(false);
  }

  get originalPermitContainer$(): Observable<PermitContainer> {
    return EMPTY;
  }

  get isAssignableAndCapable$(): Observable<boolean> {
    return this.pipe(map((state) => state?.userAssignCapable && state?.assignable));
  }

  override getFileUploadReviewGroupDecisionAttachmentActionContext(): RequestTaskActionProcessDTO['requestTaskActionType'] {
    return 'PERMIT_ISSUANCE_UPLOAD_REVIEW_GROUP_DECISION_ATTACHMENT';
  }

  override getFileUploadSectionAttachmentActionContext(): RequestTaskActionProcessDTO['requestTaskActionType'] {
    return 'PERMIT_ISSUANCE_UPLOAD_SECTION_ATTACHMENT';
  }

  override getRequestTaskActionTypeReturnForAmend(): RequestTaskActionProcessDTO['requestTaskActionType'] {
    return 'PERMIT_ISSUANCE_REVIEW_RETURN_FOR_AMENDS';
  }

  override getRequestTaskActionTypeRecall(): RequestTaskActionProcessDTO['requestTaskActionType'] {
    return 'PERMIT_ISSUANCE_RECALL_FROM_AMENDS';
  }

  override getApplyForHeader(): string {
    return 'Apply for a permit';
  }

  override getReturnForAmendHeader(): string {
    return 'Permit application returned for amends';
  }

  override reviewGroupDecisions(groupKey: string): { [key: string]: any } {
    return this.getValue().reviewGroupDecisions?.[groupKey];
  }

  override getDownloadUrlFiles(
    files: string[],
    filesCategory: 'permitAttachments' | 'reviewAttachments' = 'permitAttachments',
    fromOriginal = false, //not applicable
  ): { downloadUrl: string; fileName: string }[] {
    const attachments = this.getState()?.[filesCategory];
    const url = this.createBaseFileAttachmentDownloadUrl();
    return (
      files?.map((id) => {
        return {
          downloadUrl: url + `${id}`,
          fileName: attachments[id],
        };
      }) ?? []
    );
  }

  override isPermitTypeEditable(): Observable<boolean> {
    return this.pipe(map(() => true));
  }

  override amendsIsNotNeeded(groupKey: string) {
    return false;
  }

  override isDeterminationWizardComplete(): boolean {
    const state = this.getState();
    if (state.determination?.type === 'GRANTED') {
      return this.isGrantWizardComplete(state);
    } else if (state.determination?.type === 'REJECTED') {
      return this.isRejectWizardComplete(state.determination);
    } else if (state.determination?.type === 'DEEMED_WITHDRAWN') {
      return this.isDeemWithdrawnWizardComplete(state.determination);
    }
    return false;
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
          groups.every((mg) => state.reviewGroupDecisions[mg]?.type === 'ACCEPTED')
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
          groups.every((mg) => !!state.reviewGroupDecisions[mg]?.type) &&
          groups.some((mg) => state.reviewGroupDecisions[mg]?.type === 'REJECTED') &&
          !groups.some((mg) => state.reviewGroupDecisions[mg]?.type === 'OPERATOR_AMENDS_NEEDED')
        );
      }),
    );
  }

  override getDeterminationHeader(): string {
    return 'Permit determination';
  }

  override getDeterminationHeaderHint(): string {
    return 'determination';
  }

  override getDeterminationGrantText(): string {
    return 'Grant';
  }

  override isDeterminationTypeApplicable(): boolean {
    return true;
  }

  override getDeterminationStatus$(): Observable<ReviewDeterminationStatus> {
    return this.pipe(
      map((state) => {
        if (!state.isRequestTask) {
          return state.requestActionType === 'PERMIT_ISSUANCE_APPLICATION_GRANTED'
            ? ('granted' as ReviewDeterminationStatus)
            : ('undecided' as ReviewDeterminationStatus); //fallback, should not happen
        } else {
          return resolveDeterminationStatus(state);
        }
      }),
    );
  }

  override getOriginalTask<K extends keyof Permit>(permitTask: K): Observable<Permit[K]> {
    return EMPTY;
  }

  override findOriginalTask<T = any, P extends Path<Permit> = any>(path: P): Observable<T> {
    return EMPTY;
  }

  override postAmendStatus(section: AmendGroup) {
    return this.postStatus(constructAmendTaskStatusKey(section as PermitAmendGroup), true);
  }

  override postReview(
    value: any,
    groupKey: PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload['group'],
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

        const areAllOtherReviewGroupsAccepted = reviewGroups
          .filter((mg) => mg !== groupKey)
          .every((mg) => state.reviewGroupDecisions[mg]?.type === 'ACCEPTED');

        const shouldResetDetermination = (state) =>
          (state.determination?.type === 'GRANTED' && value.type !== 'ACCEPTED') ||
          (state.determination?.type === 'REJECTED' && value.type === 'ACCEPTED' && areAllOtherReviewGroupsAccepted) ||
          (state.determination?.type === 'REJECTED' && value.type === 'OPERATOR_AMENDS_NEEDED');

        return this.postReviewGroupDecision(
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
      }),
    );
  }

  override postDetermination(value: any, status: boolean): Observable<any> {
    const requestTaskActionType = 'PERMIT_ISSUANCE_SAVE_REVIEW_DETERMINATION';
    const requestTaskActionPayloadType = 'PERMIT_ISSUANCE_SAVE_REVIEW_DETERMINATION_PAYLOAD';
    return super.postDeterminationInner(requestTaskActionType, requestTaskActionPayloadType, value, status);
  }

  override postReviewGroupDecision(state: T, groupKey: string) {
    return this.postReviewGroupDecisionInner(
      'PERMIT_ISSUANCE_SAVE_REVIEW_GROUP_DECISION',
      'PERMIT_ISSUANCE_SAVE_REVIEW_GROUP_DECISION_PAYLOAD',
      state,
      groupKey,
    );
  }

  protected override shouldResetReviewSectionsCompletedStatus(state: PermitIssuanceState, taskKey: TaskKey): boolean {
    return !!findReviewGroupByTaskKey(taskKey);
  }

  protected override shouldResetDetermination(state: PermitIssuanceState): boolean {
    return state?.determination?.type !== 'DEEMED_WITHDRAWN';
  }

  protected override postState(state: PermitIssuanceState) {
    return this.tasksService
      .processRequestTaskAction({
        requestTaskActionType:
          state.requestTaskType === 'PERMIT_ISSUANCE_APPLICATION_REVIEW'
            ? 'PERMIT_ISSUANCE_SAVE_APPLICATION_REVIEW'
            : state.requestTaskType === 'PERMIT_ISSUANCE_APPLICATION_AMENDS_SUBMIT'
            ? 'PERMIT_ISSUANCE_SAVE_APPLICATION_AMEND'
            : 'PERMIT_ISSUANCE_SAVE_APPLICATION',
        requestTaskId: state.requestTaskId,
        requestTaskActionPayload: {
          payloadType:
            state.requestTaskType === 'PERMIT_ISSUANCE_APPLICATION_REVIEW'
              ? 'PERMIT_ISSUANCE_SAVE_APPLICATION_REVIEW_PAYLOAD'
              : state.requestTaskType === 'PERMIT_ISSUANCE_APPLICATION_AMENDS_SUBMIT'
              ? 'PERMIT_ISSUANCE_SAVE_APPLICATION_AMEND_PAYLOAD'
              : 'PERMIT_ISSUANCE_SAVE_APPLICATION_PAYLOAD',
          permit: state.permit,
          permitSectionsCompleted: state.permitSectionsCompleted,
          ...(['PERMIT_ISSUANCE_APPLICATION_REVIEW', 'PERMIT_ISSUANCE_APPLICATION_AMENDS_SUBMIT'].includes(
            state.requestTaskType,
          )
            ? { reviewSectionsCompleted: state.reviewSectionsCompleted }
            : null),
          permitType: state.permitType,
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
          } as T),
        ),
      );
  }

  isGrantWizardComplete(state: PermitIssuanceState): boolean {
    return (
      !!state?.determination?.reason &&
      !!state?.determination?.activationDate &&
      (state.permitType === 'GHGE' || this.isHSEAnnualEmissionTargetsCompleted(state))
    );
  }

  isRejectWizardComplete(rejectDetermination: PermitIssuanceRejectDetermination): boolean {
    return !!rejectDetermination.reason && !!rejectDetermination.officialNotice;
  }

  isDeemWithdrawnWizardComplete(deemWithdrawnDetermination: PermitIssuanceDeemedWithdrawnDetermination): boolean {
    return !!deemWithdrawnDetermination.reason;
  }

  isHSEAnnualEmissionTargetsCompleted(state: PermitIssuanceState) {
    return (
      !!state?.determination?.annualEmissionsTargets &&
      Object.keys(state.determination.annualEmissionsTargets).length > 0
    );
  }
}
