import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { filter, map, Observable, of, switchMap, takeUntil, withLatestFrom } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { notInNeedsForReview } from '@permit-revocation/core/section-status';
import { PermitRevocationStore } from '@permit-revocation/store/permit-revocation-store';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { hasRequestTaskAllowedActions } from '@shared/components/related-actions/request-task-allowed-actions.map';

import {
  ItemDTOResponse,
  RequestActionInfoDTO,
  RequestActionsService,
  RequestItemsService,
  RequestTaskItemDTO,
} from 'pmrv-api';

@Component({
  selector: 'app-permit-revocation-tasklist',
  templateUrl: './permit-revocation-tasklist.component.html',
  providers: [DestroySubject],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PermitRevocationTasklistComponent implements OnInit {
  readonly navigationState = { returnUrl: this.router.url };
  assignForm: UntypedFormGroup = this.fb.group({
    assignee: [],
  });

  actionsForm: UntypedFormGroup = this.fb.group({
    action: [],
  });

  hasRelatedActions$ = this.store.pipe(
    map(
      (state) =>
        (state.assignable && state.userAssignCapable) || hasRequestTaskAllowedActions(state.allowedRequestTaskActions),
    ),
  );
  readonly isEditable$: Observable<boolean> = this.store.isEditable$;

  taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));
  isTask$ = this.store.pipe(map((state) => state.isRequestTask));

  readonly relatedTasks$ = this.store.pipe(
    switchMap((state) =>
      state.requestId
        ? this.requestItemsService.getItemsByRequest(state.requestId)
        : of({ items: [], totalItems: 0 } as ItemDTOResponse),
    ),
    withLatestFrom(this.store),
    map(([items, state]) => items?.items.filter((item) => item.taskId !== state?.requestTaskId)),
  );

  readonly timelineActions$: Observable<RequestActionInfoDTO[]> = this.store.pipe(
    filter((state) => !!state.requestTaskId),
    switchMap((state) => this.requestActionsService.getRequestActionsByRequestId(state.requestId)),
    map((res) => this.sortTimeline(res, 'creationDate')),
  );

  readonly requestTaskItem$: Observable<RequestTaskItemDTO> = this.store.pipe(
    filter((state) => !!state.requestTaskId),
    map((state) => ({
      requestTask: {
        id: state.requestTaskId,
        type: state.requestTaskType,
        assigneeUserId: state.assignee.assigneeUserId,
        assigneeFullName: state.assignee.assigneeFullName,
        assignable: state.assignable,
      },
      requestInfo: { id: state.requestId },
      userAssignCapable: state.userAssignCapable,
      allowedRequestTaskActions: state.allowedRequestTaskActions,
    })),
  );

  readonly isDeterminationCompleted$: Observable<boolean> = this.store.pipe(
    map(
      (state) =>
        state?.sectionsCompleted?.REVOCATION_APPLY &&
        (state?.requestTaskType === 'PERMIT_REVOCATION_APPLICATION_PEER_REVIEW' || notInNeedsForReview(state)),
    ),
  );

  constructor(
    readonly store: PermitRevocationStore,
    private router: Router,
    private requestActionsService: RequestActionsService,
    private fb: UntypedFormBuilder,
    private route: ActivatedRoute,
    private readonly destroy$: DestroySubject,
    private readonly backLinkService: BackLinkService,
    private readonly requestItemsService: RequestItemsService,
  ) {}

  ngOnInit(): void {
    this.isEditable$.pipe(takeUntil(this.destroy$)).subscribe((editable) => {
      if (!editable) {
        this.backLinkService.show();
      }
    });
  }

  private sortTimeline(res: RequestActionInfoDTO[], key: string): RequestActionInfoDTO[] {
    return res.slice().sort((a, b) => new Date(b[key]).getTime() - new Date(a[key]).getTime());
  }

  notifyOperator(): void {
    this.router.navigate(['notify-operator'], { relativeTo: this.route });
  }

  sendPeerReview(): void {
    this.router.navigate(['peer-review'], { relativeTo: this.route });
  }

  peerReviewDecision(): void {
    this.router.navigate(['peer-review-decision'], { relativeTo: this.route });
  }
}
