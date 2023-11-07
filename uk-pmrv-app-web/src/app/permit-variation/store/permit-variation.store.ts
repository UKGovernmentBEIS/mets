/* eslint-disable @typescript-eslint/no-unused-vars */
import { Injectable } from '@angular/core';

import { combineLatest, distinctUntilChanged, first, map, Observable, of, switchMap, tap } from 'rxjs';

import { initialState } from '@permit-application/store/permit-application.state';
import { requestTaskReassignedError, taskNotFoundError } from '@shared/errors/request-task-error';
import { UrlRequestType } from '@shared/types/url-request-type';

import {
  Permit,
  PermitAcceptedVariationDecisionDetails,
  PermitContainer,
  PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload,
  PermitVariationDeemedWithdrawnDetermination,
  PermitVariationDetails,
  PermitVariationRejectDetermination,
  PermitVariationReviewDecision,
  RequestTaskActionPayload,
  RequestTaskActionProcessDTO,
  TasksService,
} from 'pmrv-api';

import { BusinessErrorService } from '../../error/business-error/business-error.service';
import { catchTaskReassignedBadRequest } from '../../error/business-errors';
import { catchNotFoundRequest, ErrorCode } from '../../error/not-found-error';
import { constructAmendTaskStatusKey } from '../../permit-application/amend/amend';
import { isHSEAnnualEmissionTargetsCompleted } from '../../permit-application/review/determination/determination-wizard';
import {
  ReviewDeterminationStatus,
  ReviewGroupDecisionStatus,
  ReviewGroupTasksAggregatorStatus,
} from '../../permit-application/review/types/review.permit.type';
import { applicableReviewGroupTasks, findReviewGroupByTaskKey } from '../../permit-application/review/utils/review';
import { mandatoryReviewGroups } from '../../permit-application/review/utils/review.permit';
import { AmendGroup, GroupKeyAndAmendDecision } from '../../permit-application/shared/types/amend.global.type';
import { PermitAmendGroup } from '../../permit-application/shared/types/amend.permit.type';
import { Path, TaskKey } from '../../permit-application/shared/types/permit-task.type';
import {
  isVariationRegulatorLedRequest,
  isVariationRegulatorLedRequestTask,
  reviewRequestTaskTypes,
} from '../../permit-application/shared/utils/permit';
import { PermitApplicationStore } from '../../permit-application/store/permit-application.store';
import { isVariationDetailsReviewDecisionOfType, isVariationReasonTemplateCompleted } from '../review/review';
import { resolveReviewGroupStatusRegulatorLed } from '../review/review-status';
import { getVariationRequestTaskTypes } from '../variation';
import { variationDetailsStatus } from '../variation-status';
import { PermitVariationState } from './permit-variation.state';

@Injectable({ providedIn: 'root' })
export class PermitVariationStore extends PermitApplicationStore<PermitVariationState> {
  constructor(
    protected readonly tasksService: TasksService,
    protected readonly businessErrorService: BusinessErrorService,
  ) {
    super(initialState, tasksService, businessErrorService);
  }

  override get isPaymentRequired(): boolean {
    return this.payload.requestTaskType === 'PERMIT_VARIATION_REGULATOR_LED_APPLICATION_SUBMIT' ? false : true;
  }

  override get urlRequestType(): UrlRequestType {
    return 'permit-variation';
  }

  override get originalPermitContainer$(): Observable<PermitContainer> {
    return this.pipe(map((state) => state.originalPermitContainer));
  }

  override get showDiff$(): Observable<boolean> {
    return this.originalPermitContainer$.pipe(map((originalPermitContainer) => !!originalPermitContainer));
  }

  get isVariationRegulatorLedRequest(): boolean {
    return isVariationRegulatorLedRequest(this.getState());
  }

  override reviewGroupDecisions(groupKey: string): { [key: string]: any } {
    return groupKey === 'ABOUT_VARIATION'
      ? this.getValue().permitVariationDetailsReviewDecision
      : this.getValue().reviewGroupDecisions?.[groupKey];
  }

  override isPermitTypeEditable(): Observable<boolean> {
    return of(this.isVariationRegulatorLedRequest);
  }

  amendsIsNotNeeded(groupKey: string) {
    return false;
  }

  override getFileUploadSectionAttachmentActionContext(): RequestTaskActionProcessDTO['requestTaskActionType'] {
    return 'PERMIT_VARIATION_UPLOAD_SECTION_ATTACHMENT';
  }

  override getFileUploadReviewGroupDecisionAttachmentActionContext(): RequestTaskActionProcessDTO['requestTaskActionType'] {
    return 'PERMIT_VARIATION_UPLOAD_REVIEW_GROUP_DECISION_ATTACHMENT';
  }

  override getRequestTaskActionTypeReturnForAmend(): RequestTaskActionProcessDTO['requestTaskActionType'] {
    return 'PERMIT_VARIATION_REVIEW_RETURN_FOR_AMENDS';
  }

  override getRequestTaskActionTypeRecall(): RequestTaskActionProcessDTO['requestTaskActionType'] {
    return 'PERMIT_VARIATION_RECALL_FROM_AMENDS';
  }

  override getApplyForHeader(): string {
    return 'Make a change to your permit';
  }

  override getReturnForAmendHeader(): string {
    return 'Variation application returned for amends';
  }

  override isAnySectionForAmend$(): Observable<boolean> {
    return combineLatest([super.isAnySectionForAmend$(), this.isVariationDetailsForAmend$()]).pipe(
      map(([isPermitForAmend, isVariationDetailsForAmend]) => isPermitForAmend || isVariationDetailsForAmend),
    );
  }

  override isSectionForAmendWithStatus$(section: AmendGroup, completed: boolean): Observable<boolean> {
    return combineLatest([
      super.isSectionForAmendWithStatus$(section, completed),
      this.isVariationDetailsSectionForAmendWithStatus$(section, completed),
    ]).pipe(map(([isPermitForAmend, isVariationDetailsForAmend]) => isPermitForAmend || isVariationDetailsForAmend));
  }

  override getSectionGroupsWithAmendDecision$(): Observable<GroupKeyAndAmendDecision[]> {
    const aboutVariationAmendDecision$ = this.pipe(
      map((state) =>
        state.requestTaskType !== 'PERMIT_VARIATION_REGULATOR_LED_APPLICATION_SUBMIT' &&
        (state.permitVariationDetailsReviewDecision as PermitVariationReviewDecision)?.type === 'OPERATOR_AMENDS_NEEDED'
          ? ([
              {
                groupKey: 'ABOUT_VARIATION',
                data: state.permitVariationDetailsReviewDecision,
              },
            ] as GroupKeyAndAmendDecision[])
          : [],
      ),
    );
    return combineLatest([super.getSectionGroupsWithAmendDecision$(), aboutVariationAmendDecision$]).pipe(
      map(([sectionGroupsWithAmendDecision, aboutVariationAmendDecision]) => [
        ...aboutVariationAmendDecision,
        ...sectionGroupsWithAmendDecision,
      ]),
    );
  }

  override getSectionGroupsWithAmendDecisionByGroup$(section: AmendGroup): Observable<GroupKeyAndAmendDecision[]> {
    if (section === 'about-variation') {
      return this.pipe(
        map((state) => [
          {
            groupKey: 'ABOUT_VARIATION',
            data: state.permitVariationDetailsReviewDecision,
          } as GroupKeyAndAmendDecision,
        ]),
      );
    } else {
      return super.getSectionGroupsWithAmendDecisionByGroup$(section);
    }
  }

  override isDeterminationTypeApplicable(): boolean {
    return !this.isVariationRegulatorLedRequest;
  }

  override isDeterminationWizardComplete(): boolean {
    const state = this.getState();
    if (state.determination?.type === 'GRANTED' || !this.isDeterminationTypeApplicable()) {
      return this.isGrantWizardComplete(state);
    } else if (state.determination?.type === 'REJECTED') {
      return this.isRejectWizardComplete(state.determination);
    } else if (state.determination?.type === 'DEEMED_WITHDRAWN') {
      return this.isDeemWithdrawnWizardComplete(state.determination);
    }
    return false;
  }

  private isGrantWizardComplete(state: PermitVariationState): boolean {
    return (
      !!state?.determination?.reason &&
      !!state?.determination?.activationDate &&
      (state.permitType === 'GHGE' || isHSEAnnualEmissionTargetsCompleted(state)) &&
      (!getVariationRequestTaskTypes().includes(state.requestTaskType) || !!state?.determination?.logChanges) &&
      (!isVariationRegulatorLedRequestTask(this.getState().requestTaskType) ||
        isVariationReasonTemplateCompleted(state.determination))
    );
  }

  private isRejectWizardComplete(rejectDetermination: PermitVariationRejectDetermination): boolean {
    return !!rejectDetermination.reason && !!rejectDetermination.officialNotice;
  }

  private isDeemWithdrawnWizardComplete(
    deemWithdrawnDetermination: PermitVariationDeemedWithdrawnDetermination,
  ): boolean {
    return !!deemWithdrawnDetermination.reason;
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
          !!(state as PermitVariationState).permitVariationDetailsReviewCompleted &&
          isVariationDetailsReviewDecisionOfType(state, 'ACCEPTED')
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
          !!(state as PermitVariationState).permitVariationDetailsReviewCompleted &&
          groups.every((mg) => !!state.reviewGroupDecisions[mg]?.type) &&
          (groups.some((mg) => state.reviewGroupDecisions[mg]?.type === 'REJECTED') ||
            isVariationDetailsReviewDecisionOfType(state, 'REJECTED')) &&
          !groups.some((mg) => state.reviewGroupDecisions[mg]?.type === 'OPERATOR_AMENDS_NEEDED') &&
          !isVariationDetailsReviewDecisionOfType(state, 'OPERATOR_AMENDS_NEEDED')
        );
      }),
    );
  }

  override getDeterminationHeader(): string {
    return 'Permit variation determination';
  }

  override getDeterminationHeaderHint(): string {
    return 'review';
  }

  override getDeterminationGrantText(): string {
    return 'Approve';
  }

  override getDeterminationType$(): Observable<string> {
    if (this.isVariationRegulatorLedRequest) {
      return of(this.getDeterminationGrantText());
    } else {
      return super.getDeterminationType$();
    }
  }

  override getDeterminationStatus$(): Observable<ReviewDeterminationStatus> {
    return this.pipe(
      map((state) => {
        if (!state.isRequestTask) {
          return state.requestActionType === 'PERMIT_VARIATION_APPLICATION_GRANTED'
            ? ('approved' as ReviewDeterminationStatus)
            : state.requestActionType === 'PERMIT_VARIATION_APPLICATION_REGULATOR_LED_APPROVED'
            ? ('approved' as ReviewDeterminationStatus)
            : ('undecided' as ReviewDeterminationStatus); //fallback, should not happen
        } else {
          if (isVariationRegulatorLedRequestTask(this.getState().requestTaskType)) {
            return this.resolveDeterminationStatusRegulatorLed(state);
          } else {
            return this.resolveDeterminationStatusOperatorLed(state);
          }
        }
      }),
    );
  }

  getVariationDetailsReviewStatus$(): Observable<ReviewGroupDecisionStatus | ReviewGroupTasksAggregatorStatus> {
    return this.pipe(map((state) => this.variationDetailsReviewStatus(state)));
  }

  override getDownloadUrlFiles(
    files: string[],
    filesCategory: 'permitAttachments' | 'reviewAttachments' = 'permitAttachments',
    fromOriginal = false,
  ): { downloadUrl: string; fileName: string }[] {
    const attachments = (fromOriginal ? this.getState()?.originalPermitContainer : this.getState())?.[filesCategory];
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

  override getOriginalTask<K extends keyof Permit>(permitTask: K): Observable<Permit[K]> {
    return this.pipe(
      map((state) => state.originalPermitContainer?.permit?.[permitTask]),
      distinctUntilChanged(),
    );
  }

  override findOriginalTask<T = any, P extends Path<Permit> = any>(path: P): Observable<T> {
    return this.pipe(
      map((state) => {
        return path.split('.').reduce((pp, cp) => pp?.[cp] ?? null, state.originalPermitContainer?.permit);
      }),
    ) as Observable<T>;
  }

  override postAmendStatus(section: AmendGroup) {
    if (section === 'about-variation') {
      return this.postVariationDetailsAmendStatus(true);
    } else {
      return this.postStatus(constructAmendTaskStatusKey(section as PermitAmendGroup), true);
    }
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

        const areAllOtherReviewGroupsAccepted =
          reviewGroups
            .filter((mg) => mg !== groupKey)
            .every((mg) => state.reviewGroupDecisions[mg]?.type === 'ACCEPTED') &&
          (state.permitVariationDetailsReviewDecision as PermitVariationReviewDecision)?.type === 'ACCEPTED';

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
    const state = this.getState();
    const requestTaskActionType =
      state.requestTaskType === 'PERMIT_VARIATION_REGULATOR_LED_APPLICATION_SUBMIT'
        ? 'PERMIT_VARIATION_SAVE_REVIEW_DETERMINATION_REGULATOR_LED'
        : 'PERMIT_VARIATION_SAVE_REVIEW_DETERMINATION';

    const requestTaskActionPayloadType =
      state.requestTaskType === 'PERMIT_VARIATION_REGULATOR_LED_APPLICATION_SUBMIT'
        ? 'PERMIT_VARIATION_SAVE_REVIEW_DETERMINATION_REGULATOR_LED_PAYLOAD'
        : 'PERMIT_VARIATION_SAVE_REVIEW_DETERMINATION_PAYLOAD';
    return super.postDeterminationInner(requestTaskActionType, requestTaskActionPayloadType, value, status);
  }

  override postReviewGroupDecision(
    state: PermitVariationState,
    groupKey: PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload['group'],
  ) {
    return super.postReviewGroupDecisionInner(
      'PERMIT_VARIATION_SAVE_REVIEW_GROUP_DECISION',
      'PERMIT_VARIATION_SAVE_REVIEW_GROUP_DECISION_PAYLOAD',
      state,
      groupKey,
    );
  }

  protected override shouldResetReviewSectionsCompletedStatus(state: PermitVariationState, taskKey: TaskKey): boolean {
    return !this.isVariationRegulatorLedRequest && !!findReviewGroupByTaskKey(taskKey);
  }

  protected override shouldResetDetermination(state: PermitVariationState): boolean {
    return state?.determination?.type !== 'DEEMED_WITHDRAWN' || this.isVariationRegulatorLedRequest;
  }

  postVariationDetails(permitVariationDetails: PermitVariationDetails, permitVariationDetailsCompleted = false) {
    return this.pipe(
      first(),
      switchMap((state) =>
        this.postState({
          ...state,
          permitVariationDetails,
          permitVariationDetailsCompleted,
          permitVariationDetailsReviewCompleted: false,
          reviewSectionsCompleted: {
            ...state.reviewSectionsCompleted,
            ...(this.shouldResetDetermination(state) ? { determination: false } : null),
          },
        }),
      ),
    );
  }

  postVariationDetailsAmendStatus(status: boolean) {
    return this.pipe(
      first(),
      switchMap((state) =>
        this.postState({
          ...state,
          permitVariationDetailsAmendCompleted: status,
        }),
      ),
    );
  }

  postVariationDetailsReviewDecision(decision, files?: { uuid: string; file: File }[]) {
    const state = this.getState();

    const reviewGroups = mandatoryReviewGroups.concat(
      Object.keys(state.permit.monitoringApproaches) as Array<
        PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload['group']
      >,
    );

    const areAllOtherReviewGroupsAccepted = reviewGroups.every(
      (mg) => state.reviewGroupDecisions[mg]?.type === 'ACCEPTED',
    );

    const shouldResetDetermination = (state) =>
      (state.determination?.type === 'GRANTED' && decision.type !== 'ACCEPTED') ||
      (state.determination?.type === 'REJECTED' && decision.type === 'ACCEPTED' && areAllOtherReviewGroupsAccepted) ||
      (state.determination?.type === 'REJECTED' && decision.type === 'OPERATOR_AMENDS_NEEDED');

    return this.tasksService
      .processRequestTaskAction({
        requestTaskActionType: 'PERMIT_VARIATION_SAVE_DETAILS_REVIEW_GROUP_DECISION',
        requestTaskId: state.requestTaskId,
        requestTaskActionPayload: {
          payloadType: 'PERMIT_VARIATION_SAVE_DETAILS_REVIEW_GROUP_DECISION_PAYLOAD',
          decision: decision,
          reviewSectionsCompleted: {
            ...state.reviewSectionsCompleted,
            ...(shouldResetDetermination(state) ? { determination: false } : null),
          },
          permitVariationDetailsReviewCompleted: true,
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
            ...{
              permitVariationDetailsReviewDecision: decision,
              permitVariationDetailsReviewCompleted: true,
            },
            reviewSectionsCompleted: {
              ...state.reviewSectionsCompleted,
              ...(shouldResetDetermination(state) ? { determination: false } : null),
            },
            reviewAttachments: {
              ...state.reviewAttachments,
              ...files?.reduce((result, item) => ({ ...result, [item.uuid]: item.file.name }), {}),
            },
            determination: shouldResetDetermination(state) ? {} : state.determination,
          }),
        ),
      );
  }

  postReviewGroupDecisionVariationRegulatorLed(
    groupKey: PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload['group'],
    decision: PermitAcceptedVariationDecisionDetails,
  ) {
    const state = this.getState();
    return this.tasksService
      .processRequestTaskAction({
        requestTaskActionType: 'PERMIT_VARIATION_SAVE_REVIEW_GROUP_DECISION_REGULATOR_LED',
        requestTaskId: state.requestTaskId,
        requestTaskActionPayload: {
          payloadType: 'PERMIT_VARIATION_SAVE_REVIEW_GROUP_DECISION_REGULATOR_LED_PAYLOAD',
          group: groupKey,
          decision: decision,
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
            reviewGroupDecisions: {
              ...state.reviewGroupDecisions,
              [groupKey]: decision,
            },
          }),
        ),
      );
  }

  postReviewGroupDecisionAboutVariationRegulatorLed(decision: PermitAcceptedVariationDecisionDetails) {
    const state = this.getState();
    return this.tasksService
      .processRequestTaskAction({
        requestTaskActionType: 'PERMIT_VARIATION_SAVE_DETAILS_REVIEW_GROUP_DECISION_REGULATOR_LED',
        requestTaskId: state.requestTaskId,
        requestTaskActionPayload: {
          payloadType: 'PERMIT_VARIATION_SAVE_DETAILS_REVIEW_GROUP_DECISION_REGULATOR_LED_PAYLOAD',
          decision,
        } as RequestTaskActionPayload,
      })
      .pipe(
        catchNotFoundRequest(ErrorCode.NOTFOUND1001, () =>
          this.businessErrorService.showErrorForceNavigation(taskNotFoundError),
        ),
        catchTaskReassignedBadRequest(() =>
          this.businessErrorService.showErrorForceNavigation(requestTaskReassignedError()),
        ),
        tap(() => this.setState({ ...state, permitVariationDetailsReviewDecision: decision })),
      );
  }

  protected override postState(state: PermitVariationState) {
    return this.tasksService
      .processRequestTaskAction({
        requestTaskActionType:
          state.requestTaskType === 'PERMIT_VARIATION_REGULATOR_LED_APPLICATION_SUBMIT'
            ? 'PERMIT_VARIATION_SAVE_APPLICATION_REGULATOR_LED'
            : state.requestTaskType === 'PERMIT_VARIATION_APPLICATION_SUBMIT'
            ? 'PERMIT_VARIATION_SAVE_APPLICATION'
            : state.requestTaskType === 'PERMIT_VARIATION_APPLICATION_REVIEW'
            ? 'PERMIT_VARIATION_SAVE_APPLICATION_REVIEW'
            : state.requestTaskType === 'PERMIT_VARIATION_APPLICATION_AMENDS_SUBMIT'
            ? 'PERMIT_VARIATION_SAVE_APPLICATION_AMEND'
            : null,
        requestTaskId: state.requestTaskId,
        requestTaskActionPayload: {
          payloadType:
            state.requestTaskType === 'PERMIT_VARIATION_REGULATOR_LED_APPLICATION_SUBMIT'
              ? 'PERMIT_VARIATION_SAVE_APPLICATION_REGULATOR_LED_PAYLOAD'
              : state.requestTaskType === 'PERMIT_VARIATION_APPLICATION_SUBMIT'
              ? 'PERMIT_VARIATION_SAVE_APPLICATION_PAYLOAD'
              : state.requestTaskType === 'PERMIT_VARIATION_APPLICATION_REVIEW'
              ? 'PERMIT_VARIATION_SAVE_APPLICATION_REVIEW_PAYLOAD'
              : state.requestTaskType === 'PERMIT_VARIATION_APPLICATION_AMENDS_SUBMIT'
              ? 'PERMIT_VARIATION_SAVE_APPLICATION_AMEND_PAYLOAD'
              : null,
          permit: state.permit,
          permitSectionsCompleted: state.permitSectionsCompleted,
          ...([
            'PERMIT_VARIATION_APPLICATION_SUBMIT',
            'PERMIT_VARIATION_REGULATOR_LED_APPLICATION_SUBMIT',
            'PERMIT_VARIATION_APPLICATION_REVIEW',
            'PERMIT_VARIATION_APPLICATION_AMENDS_SUBMIT',
          ].includes(state.requestTaskType)
            ? { reviewSectionsCompleted: state.reviewSectionsCompleted }
            : null),
          ...(![
            'PERMIT_VARIATION_APPLICATION_SUBMIT',
            'PERMIT_VARIATION_APPLICATION_REVIEW',
            'PERMIT_VARIATION_APPLICATION_AMENDS_SUBMIT',
          ].includes(state.requestTaskType) && {
            permitType: state.permitType,
          }),
          permitVariationDetails: state.permitVariationDetails ?? {},
          permitVariationDetailsCompleted: state.permitVariationDetailsCompleted ?? false,

          ...(['PERMIT_VARIATION_APPLICATION_REVIEW', 'PERMIT_VARIATION_APPLICATION_AMENDS_SUBMIT'].includes(
            state.requestTaskType,
          ) && {
            permitVariationDetailsReviewCompleted: state.permitVariationDetailsReviewCompleted,
          }),
          ...(state.requestTaskType === 'PERMIT_VARIATION_APPLICATION_AMENDS_SUBMIT' && {
            permitVariationDetailsAmendCompleted: state.permitVariationDetailsAmendCompleted,
          }),
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

  private isVariationDetailsForAmend$(): Observable<boolean> {
    return this.pipe(
      map(
        (state) =>
          state.requestTaskType !== 'PERMIT_VARIATION_REGULATOR_LED_APPLICATION_SUBMIT' &&
          (state.permitVariationDetailsReviewDecision as PermitVariationReviewDecision)?.type ===
            'OPERATOR_AMENDS_NEEDED' &&
          state.permitVariationDetailsReviewCompleted,
      ),
    );
  }

  private isVariationDetailsSectionForAmendWithStatus$(section: AmendGroup, completed: boolean): Observable<boolean> {
    return this.pipe(
      map((state) => {
        const variationDetailsDecision = state?.permitVariationDetailsReviewDecision as PermitVariationReviewDecision;
        return (
          section === 'about-variation' &&
          variationDetailsDecision?.type === 'OPERATOR_AMENDS_NEEDED' &&
          (state.permitVariationDetailsAmendCompleted ?? false) === completed
        );
      }),
    );
  }

  private resolveDeterminationStatusOperatorLed(state: PermitVariationState): ReviewDeterminationStatus {
    const reviewGroups = mandatoryReviewGroups.concat(
      Object.keys(state.permit.monitoringApproaches ?? {}) as Array<
        PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload['group']
      >,
    );

    const areAllReviewGroupsCompleted =
      reviewGroups.every((mg) => state.reviewSectionsCompleted[mg]) && state.permitVariationDetailsReviewCompleted;
    return state?.reviewSectionsCompleted?.determination
      ? state?.determination?.type !== 'DEEMED_WITHDRAWN'
        ? areAllReviewGroupsCompleted
          ? state?.determination?.type === 'GRANTED'
            ? 'approved'
            : 'rejected'
          : 'undecided'
        : 'deemed withdrawn'
      : 'undecided';
  }

  private resolveDeterminationStatusRegulatorLed(state: PermitVariationState): ReviewDeterminationStatus {
    const reviewGroupAggregatorStatuses: ReviewGroupTasksAggregatorStatus[] = [];

    const reviewGroupTasks = applicableReviewGroupTasks(state);
    Object.keys(reviewGroupTasks).forEach((key) => {
      reviewGroupAggregatorStatuses.push(
        resolveReviewGroupStatusRegulatorLed(
          state,
          key as PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload['group'],
        ),
      );
    });

    const aboutVariationReviewStatus = this.variationDetailsReviewStatus(state) as ReviewGroupTasksAggregatorStatus;

    return !(
      reviewGroupAggregatorStatuses.every((status) => status === 'complete') &&
      aboutVariationReviewStatus === 'complete'
    )
      ? 'cannot start yet'
      : !!state.determination && state.reviewSectionsCompleted?.determination
      ? 'approved'
      : 'undecided';
  }

  private variationDetailsReviewStatus(state: PermitVariationState) {
    const permitVariationDetailsReviewDecision =
      state?.permitVariationDetailsReviewDecision as PermitVariationReviewDecision;
    if (!state.isRequestTask) {
      return this.isVariationRegulatorLedRequest
        ? 'complete'
        : permitVariationDetailsReviewDecision?.type === 'ACCEPTED'
        ? 'accepted'
        : permitVariationDetailsReviewDecision?.type === 'REJECTED'
        ? 'rejected'
        : 'operator to amend';
    } else {
      if (isVariationRegulatorLedRequestTask(this.getState().requestTaskType)) {
        return variationDetailsStatus(state) as ReviewGroupTasksAggregatorStatus;
      } else {
        return state?.permitVariationDetailsReviewCompleted === true
          ? permitVariationDetailsReviewDecision?.type === 'ACCEPTED'
            ? 'accepted'
            : permitVariationDetailsReviewDecision?.type === 'REJECTED'
            ? 'rejected'
            : 'operator to amend'
          : 'undecided';
      }
    }
  }
}
