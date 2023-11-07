import { PipeTransform } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, filter, map, Observable, of, shareReplay, switchMap, takeUntil, withLatestFrom } from 'rxjs';

import {
  ItemDTOResponse,
  RequestActionInfoDTO,
  RequestActionsService,
  RequestItemsService,
  RequestTaskItemDTO,
} from 'pmrv-api';

import { DestroySubject } from '../../../core/services/destroy-subject.service';
import { BackLinkService } from '../../../shared/back-link/back-link.service';
import { hasRequestTaskAllowedActions } from '../../../shared/components/related-actions/request-task-allowed-actions.map';
import { ReviewDeterminationStatus } from '../../review/types/review.permit.type';
import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';
import {
  notifyOperatorRequestTaskActionTypes,
  peerReviewRequestTaskActionTypes,
  peerReviewSubmitRequestTaskActionTypes,
} from '../utils/permit';

export abstract class ReviewSectionsContainerAbstractComponent {
  statusResolverPipe: PipeTransform;

  navigationState = { returnUrl: this.router.url };

  isTask$ = this.store.pipe(map((state) => state.isRequestTask));
  taskId$ = this.store.pipe(map((state) => state.requestTaskId));

  requestTaskType$ = this.store.pipe(
    filter((state) => state.isRequestTask),
    map((state) => state.requestTaskType),
  );

  readonly relatedTasks$ = this.store.pipe(
    switchMap((state) =>
      state.requestId
        ? this.requestItemsService.getItemsByRequest(state.requestId)
        : of({ items: [], totalItems: 0 } as ItemDTOResponse),
    ),
    withLatestFrom(this.store),
    map(([items, state]) => items?.items.filter((item) => item.taskId !== state?.requestTaskId)),
    shareReplay({ bufferSize: 1, refCount: true }),
  );

  readonly requestActions$ = this.store.pipe(
    filter((state) => state.isRequestTask),
    switchMap((state) => this.requestActionsService.getRequestActionsByRequestId(state.requestId)),
    map((res) => this.sortTimeline(res)),
    shareReplay({ bufferSize: 1, refCount: true }),
  );

  allowedRequestTaskActions$ = this.store.pipe(
    filter((state) => state.isRequestTask),
    map((state) => state.allowedRequestTaskActions),
  );

  hasRelatedActions$ = this.store.pipe(
    map((state) => state.assignable || hasRequestTaskAllowedActions(state.allowedRequestTaskActions)),
  );

  isRelatedActionsSectionVisible$ = combineLatest([this.isTask$, this.hasRelatedActions$]).pipe(
    map(([isTask, hasRelatedActions]) => isTask && hasRelatedActions),
  );

  readonly info$: Observable<RequestTaskItemDTO> = this.store.pipe(
    map((state) => ({
      requestTask: {
        id: state.requestTaskId,
        assigneeUserId: state.assignee?.assigneeUserId,
        assigneeFullName: state.assignee?.assigneeFullName,
        assignable: state.assignable,
        type: state.requestTaskType as any,
      },
      userAssignCapable: state.userAssignCapable,
    })),
  );

  readonly determinationStatus$: Observable<ReviewDeterminationStatus> = this.store.getDeterminationStatus$();

  isDeterminationCompleted$: Observable<boolean> = this.store.pipe(
    map((state) => state?.reviewSectionsCompleted?.determination === true),
  );

  isNotifyOperatorActionAllowed$ = this.allowedRequestTaskActions$.pipe(
    map((actions) => actions.some((action) => notifyOperatorRequestTaskActionTypes.includes(action))),
  );

  isPeerReviewActionAllowed$ = this.allowedRequestTaskActions$.pipe(
    map((actions) => actions.some((action) => peerReviewRequestTaskActionTypes.includes(action))),
  );

  isAnyForAmends$: Observable<boolean> = this.store.isAnySectionForAmend$();

  isPeerReviewSubmitActionAllowed$ = this.allowedRequestTaskActions$.pipe(
    map((actions) => actions.some((action) => peerReviewSubmitRequestTaskActionTypes.includes(action))),
  );

  constructor(
    protected readonly store: PermitApplicationStore<PermitApplicationState>,
    protected readonly router: Router,
    protected readonly route: ActivatedRoute,
    protected readonly requestItemsService: RequestItemsService,
    protected readonly requestActionsService: RequestActionsService,
    protected readonly destroy$: DestroySubject,
    protected readonly backLinkService: BackLinkService,
  ) {
    if (this.router.getCurrentNavigation()?.extras.state) {
      this.store
        .pipe(takeUntil(this.destroy$))
        .subscribe((state) => this.backLinkService.show(`/accounts/${state.accountId}/workflows/${state.requestId}`));
    }
  }

  notifyOperator(): void {
    this.router.navigate(['notify-operator'], { relativeTo: this.route });
  }

  sendPeerReview(): void {
    this.router.navigate(['peer-review'], { relativeTo: this.route });
  }

  sendReturnForAmends(): void {
    this.router.navigate(['return-for-amends'], { relativeTo: this.route });
  }

  peerReviewDecision(): void {
    this.router.navigate(['peer-review-decision'], { relativeTo: this.route });
  }

  private sortTimeline(res: RequestActionInfoDTO[]): RequestActionInfoDTO[] {
    return res.slice().sort((a, b) => new Date(b.creationDate).getTime() - new Date(a.creationDate).getTime());
  }
}
