/* eslint-disable @typescript-eslint/no-unused-vars */
import { distinctUntilChanged, first, map, Observable, switchMap, tap } from 'rxjs';

import { Store } from '@core/store/store';
import { requestTaskReassignedError, taskNotFoundError } from '@shared/errors/request-task-error';
import { UrlRequestType } from '@shared/types/url-request-type';

import {
  Permit,
  PermitContainer,
  PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload,
  RequestTaskActionPayload,
  RequestTaskActionProcessDTO,
  TasksService,
} from 'pmrv-api';

import { BusinessErrorService } from '../../error/business-error/business-error.service';
import { catchTaskReassignedBadRequest } from '../../error/business-errors';
import { catchNotFoundRequest, ErrorCode } from '../../error/not-found-error';
import { ReviewDeterminationStatus } from '../review/types/review.permit.type';
import { findReviewGroupByTaskKey } from '../review/utils/review';
import { AmendGroup, GroupKeyAndAmendDecision } from '../shared/types/amend.global.type';
import { PERMIT_AMEND_STATUS_PREFIX, permitAmendGroupReviewGroupsMap } from '../shared/types/amend.permit.type';
import { Path, StatusKey, TaskKey } from '../shared/types/permit-task.type';
import { PermitApplicationState } from './permit-application.state';

export abstract class PermitApplicationStore<T extends PermitApplicationState> extends Store<T> {
  protected constructor(
    protected initialState: T,
    protected readonly tasksService: TasksService,
    protected readonly businessErrorService: BusinessErrorService,
  ) {
    super(initialState);
  }

  override setState(state: T): void {
    super.setState(state);
  }

  get isEditable$(): Observable<boolean> {
    return this.pipe(map((state) => state.isEditable));
  }

  get payload() {
    return this.getValue();
  }

  get permit(): Permit {
    return this.payload.permit;
  }

  get permitType(): PermitContainer['permitType'] {
    return this.payload.permitType;
  }

  get isAssignableAndCapable$(): Observable<boolean> {
    return this.pipe(map((state) => state?.userAssignCapable && state?.assignable));
  }

  createBaseFileAttachmentDownloadUrl() {
    return this.getState().actionId
      ? `/${this.urlRequestType}/action/${this.getState().actionId}/file-download/attachment/`
      : `/${this.urlRequestType}/${this.getState().requestTaskId}/file-download/attachment/`;
  }

  abstract get isPaymentRequired(): boolean;
  abstract get urlRequestType(): UrlRequestType;

  abstract getApplyForHeader(): string;
  abstract getReturnForAmendHeader(): string;
  abstract get originalPermitContainer$(): Observable<PermitContainer>;
  abstract get showDiff$(): Observable<boolean>;

  abstract getFileUploadSectionAttachmentActionContext(): RequestTaskActionProcessDTO['requestTaskActionType'];
  abstract getFileUploadReviewGroupDecisionAttachmentActionContext(): RequestTaskActionProcessDTO['requestTaskActionType'];
  abstract getRequestTaskActionTypeReturnForAmend(): RequestTaskActionProcessDTO['requestTaskActionType'];
  abstract getRequestTaskActionTypeRecall(): RequestTaskActionProcessDTO['requestTaskActionType'];
  abstract reviewGroupDecisions(groupKey: string): { [key: string]: any };
  abstract getDownloadUrlFiles(
    files: string[],
    filesCategory?: 'permitAttachments' | 'reviewAttachments',
    fromOriginal?: boolean,
  ): {
    downloadUrl: string;
    fileName: string;
  }[];
  abstract isPermitTypeEditable(): Observable<boolean>;
  abstract amendsIsNotNeeded(groupKey: string): boolean;

  isAnySectionForAmend$(): Observable<boolean> {
    return this.pipe(
      map((state) =>
        Object.keys(state?.reviewGroupDecisions ?? []).some(
          (key) =>
            state.reviewGroupDecisions[key].type === 'OPERATOR_AMENDS_NEEDED' && state.reviewSectionsCompleted[key],
        ),
      ),
    );
  }

  isSectionForAmendWithStatus$(section: AmendGroup, completed: boolean): Observable<boolean> {
    return this.pipe(
      map((state) => {
        const reviewGroupKeys = Object.keys(
          state.reviewGroupDecisions ?? [],
        ) as PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload['group'][];

        return (
          reviewGroupKeys.some((reviewGroupKey) =>
            permitAmendGroupReviewGroupsMap[section]?.includes(reviewGroupKey),
          ) && (state.permitSectionsCompleted?.[PERMIT_AMEND_STATUS_PREFIX.concat(section)]?.[0] ?? false) === completed
        );
      }),
    );
  }

  getSectionGroupsWithAmendDecision$(): Observable<GroupKeyAndAmendDecision[]> {
    return this.pipe(
      map((state) =>
        Object.keys(state?.reviewGroupDecisions ?? [])
          .filter((key) => state?.reviewGroupDecisions[key].type === 'OPERATOR_AMENDS_NEEDED')
          .map((key) => ({ groupKey: key, data: state.reviewGroupDecisions[key] } as GroupKeyAndAmendDecision)),
      ),
    );
  }

  getSectionGroupsWithAmendDecisionByGroup$(section: AmendGroup): Observable<GroupKeyAndAmendDecision[]> {
    return this.pipe(
      map((state) =>
        (
          Object.keys(
            state.reviewGroupDecisions,
          ) as PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload['group'][]
        )
          .filter((reviewGroupKey) => permitAmendGroupReviewGroupsMap[section]?.includes(reviewGroupKey))
          .map((reviewGroupKey) => ({ groupKey: reviewGroupKey, data: state.reviewGroupDecisions[reviewGroupKey] })),
      ),
    );
  }

  abstract isDeterminationGrantButtonDisplayed$(): Observable<boolean>;
  abstract isDeterminationRejectButtonDisplayed$(): Observable<boolean>;
  abstract getDeterminationHeader(): string;
  abstract getDeterminationHeaderHint(): string;
  abstract getDeterminationGrantText(): string;
  abstract isDeterminationTypeApplicable(): boolean;
  abstract isDeterminationWizardComplete(): boolean;

  getDeterminationType$(): Observable<string> {
    return this.pipe(
      map((state) => {
        switch (state.determination?.type) {
          case 'GRANTED':
            return this.getDeterminationGrantText();
          case 'REJECTED':
            return 'Reject';
          case 'DEEMED_WITHDRAWN':
            return 'Deem withdrawn';
          default:
            return null;
        }
      }),
    );
  }
  abstract getDeterminationStatus$(): Observable<ReviewDeterminationStatus>;

  getTask<K extends keyof Permit>(permitTask: K): Observable<Permit[K]> {
    return this.pipe(
      map((state) => state.permit[permitTask]),
      distinctUntilChanged(),
    );
  }

  abstract getOriginalTask<K extends keyof Permit>(permitTask: K): Observable<Permit[K]>;

  findTask<T = any, P extends Path<Permit> = any>(path: P): Observable<T> {
    return this.pipe(
      map((state) => {
        return path.split('.').reduce((pp, cp) => pp?.[cp] ?? null, state.permit);
      }),
    ) as Observable<T>;
  }

  abstract findOriginalTask<T = any, P extends Path<Permit> = any>(path: P): Observable<T>;

  patchTask(taskKey: TaskKey, value: any, statusValue?: boolean | boolean[], statusKey?: StatusKey) {
    return this.updateTask(taskKey, value, 'PATCH', statusValue, statusKey);
  }

  postTask(taskKey: TaskKey, value: any, statusValue?: boolean | boolean[], statusKey?: StatusKey) {
    return this.updateTask(taskKey, value, 'POST', statusValue, statusKey);
  }

  abstract postAmendStatus(section: AmendGroup);

  abstract postReview(
    value: any,
    groupKey: PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload['group'],
    files?: { uuid: string; file: File }[],
  ): Observable<any>;

  postStatus(statusKey: StatusKey, statusValue: boolean | boolean[], taskKey?: TaskKey) {
    return this.pipe(
      first(),
      switchMap((state) =>
        this.postState({
          ...state,
          permitSectionsCompleted: {
            ...state.permitSectionsCompleted,
            [statusKey]: Array.isArray(statusValue) ? statusValue : [statusValue],
          },
          ...(taskKey &&
          [
            'PERMIT_ISSUANCE_APPLICATION_REVIEW',
            'PERMIT_ISSUANCE_APPLICATION_AMENDS_SUBMIT',
            'PERMIT_VARIATION_APPLICATION_SUBMIT',
            'PERMIT_VARIATION_REGULATOR_LED_APPLICATION_SUBMIT',
            'PERMIT_VARIATION_APPLICATION_REVIEW',
            'PERMIT_VARIATION_APPLICATION_AMENDS_SUBMIT',
            'PERMIT_TRANSFER_B_APPLICATION_REVIEW',
            'PERMIT_TRANSFER_B_APPLICATION_AMENDS_SUBMIT',
          ].includes(state.requestTaskType)
            ? {
                reviewSectionsCompleted: {
                  ...state.reviewSectionsCompleted,
                  ...(this.shouldResetReviewSectionsCompletedStatus(state, taskKey)
                    ? { [findReviewGroupByTaskKey(taskKey)]: false }
                    : {}),
                  ...(this.shouldResetDetermination(state) ? { determination: false } : null),
                },
              }
            : null),
        }),
      ),
    );
  }

  abstract postDetermination(value: any, status: boolean): Observable<any>;

  protected postDeterminationInner(
    requestTaskActionType: RequestTaskActionProcessDTO['requestTaskActionType'],
    requestTaskActionPayloadType: RequestTaskActionPayload['payloadType'],
    value: any,
    status: boolean,
  ): Observable<any> {
    const state = this.getState();

    return this.tasksService
      .processRequestTaskAction({
        requestTaskActionType,
        requestTaskId: state.requestTaskId,
        requestTaskActionPayload: {
          payloadType: requestTaskActionPayloadType,
          determination: value,
          reviewSectionsCompleted: {
            ...state.reviewSectionsCompleted,
            determination: status,
          },
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
            determination: value,
            reviewSectionsCompleted: {
              ...state.reviewSectionsCompleted,
              determination: status,
            },
          }),
        ),
      );
  }

  postCategoryTask(taskKey: TaskKey, state: T, reviewSectionsCompleted?: { [key: string]: boolean }) {
    return this.postState({
      ...state,
      ...(reviewSectionsCompleted
        ? { reviewSectionsCompleted: reviewSectionsCompleted }
        : {
            reviewSectionsCompleted: {
              ...state.reviewSectionsCompleted,
              ...(findReviewGroupByTaskKey(taskKey) ? { [findReviewGroupByTaskKey(taskKey)]: false } : {}),
              ...(this.shouldResetDetermination(state) ? { determination: false } : null),
            },
          }),
    });
  }

  abstract postReviewGroupDecision(state: T, groupKey: string): Observable<any>;

  protected postReviewGroupDecisionInner(
    requestTaskActionType: RequestTaskActionProcessDTO['requestTaskActionType'],
    requestTaskActionPayloadType: RequestTaskActionPayload['payloadType'],
    state: T,
    groupKey: string,
  ) {
    return this.tasksService
      .processRequestTaskAction({
        requestTaskActionType,
        requestTaskId: state.requestTaskId,
        requestTaskActionPayload: {
          payloadType: requestTaskActionPayloadType,
          group: groupKey,
          decision: state.reviewGroupDecisions[groupKey],
          reviewSectionsCompleted: state.reviewSectionsCompleted,
        } as RequestTaskActionPayload,
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

  protected updateTask(
    taskKey: TaskKey,
    value: any,
    updateType: 'PATCH' | 'POST',
    statusValue?: boolean | boolean[],
    statusKey?: StatusKey,
  ) {
    return this.pipe(
      first(),
      switchMap((state) =>
        this.postState({
          ...state,
          permit: {
            ...state.permit,
            ...taskKey
              .split('.')
              .reverse()
              .reduce(
                (taskTree, taskTreeKey, index, array) => ({
                  [taskTreeKey]:
                    updateType === 'POST' && index === 0
                      ? taskTree
                      : {
                          ...taskKey
                            .split('.')
                            .slice(0, array.length - index)
                            .reduce((target, targetKey) => (target = target[targetKey]), state.permit),
                          ...taskTree,
                        },
                }),
                value,
              ),
          },
          permitSectionsCompleted: {
            ...state.permitSectionsCompleted,
            ...(statusKey
              ? { [statusKey]: Array.isArray(statusValue) ? statusValue : [statusValue] }
              : statusValue !== undefined
              ? { [taskKey]: Array.isArray(statusValue) ? statusValue : [statusValue] }
              : null),
          },
          reviewSectionsCompleted: {
            ...state.reviewSectionsCompleted,
            ...(this.shouldResetReviewSectionsCompletedStatus(state, taskKey)
              ? { [findReviewGroupByTaskKey(taskKey)]: false }
              : {}),
            ...(this.shouldResetDetermination(state) ? { determination: false } : null),
          },
        }),
      ),
    );
  }

  protected abstract shouldResetDetermination(state: T): boolean;

  protected abstract shouldResetReviewSectionsCompletedStatus(state: T, taskKey: TaskKey): boolean;

  protected abstract postState(state: T);
}
