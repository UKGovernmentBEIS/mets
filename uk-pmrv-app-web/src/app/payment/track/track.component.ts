import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, filter, first, map, pluck, switchMap, withLatestFrom } from 'rxjs';

import { BreadcrumbService } from '@shared/breadcrumbs/breadcrumb.service';
import { hasRequestTaskAllowedActions } from '@shared/components/related-actions/request-task-allowed-actions.map';

import { RequestActionInfoDTO, RequestActionsService, RequestItemsService } from 'pmrv-api';

import { getHeadingMap, mapTrackPaymentToPaymentDetails, paymentHintInfo } from '../core/payment.map';
import { shouldHidePaymentAmount } from '../core/utils';
import { PaymentStore } from '../store/payment.store';

@Component({
  selector: 'app-track',
  templateUrl: './track.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TrackComponent implements OnInit {
  isAssignableAndCapableToAssign$ = this.store.pipe(
    filter((state) => !!state.requestTaskItem),
    map((state) => state.requestTaskItem.requestTask.assignable && state.requestTaskItem.userAssignCapable),
  );
  allowedRequestTaskActions$ = this.store.pipe(
    filter((state) => !!state.requestTaskItem),
    map((state) => state.requestTaskItem.allowedRequestTaskActions),
  );
  hasRelatedActions$ = combineLatest([this.isAssignableAndCapableToAssign$, this.allowedRequestTaskActions$]).pipe(
    map(
      ([isAssignableAndCapableToAssign, allowedRequestTaskActions]) =>
        isAssignableAndCapableToAssign || hasRequestTaskAllowedActions(allowedRequestTaskActions),
    ),
  );
  taskId$ = this.store.pipe(
    filter((state) => !!state.requestTaskItem),
    map((state) => state.requestTaskItem.requestTask.id),
  );
  details$ = this.store.pipe(
    filter((state) => !!state.requestTaskItem),
    map((state) => mapTrackPaymentToPaymentDetails(state)),
  );
  type$ = this.store.pipe(
    filter((state) => !!state.requestTaskItem),
    map((state) => state.requestTaskItem.requestTask.type),
  );

  readonly shouldDisplayAmount$ = this.store.pipe(map((state) => !shouldHidePaymentAmount(state)));

  readonly requestTaskItem$ = this.store.pipe(pluck('requestTaskItem'));

  readonly relatedTasks$ = this.store.pipe(
    filter((state) => !!state.requestTaskItem),
    switchMap((state) => this.requestItemsService.getItemsByRequest(state.requestTaskItem.requestInfo.id)),
    withLatestFrom(this.store),
    map(([items, state]) => items?.items.filter((item) => item.taskId !== state?.requestTaskId)),
  );
  actions$ = this.store.pipe(
    filter((state) => !!state.requestTaskItem),
    switchMap((state) => this.requestActionsService.getRequestActionsByRequestId(state.requestTaskItem.requestInfo.id)),
    first(),
    map((res) => this.sortTimeline(res)),
  );

  readonly headingMap$ = this.store.pipe(
    map((state) => getHeadingMap((state.requestTaskItem?.requestInfo?.requestMetadata as any)?.year)),
  );

  readonly paymentHintInfo = paymentHintInfo;
  navigationState = { returnUrl: this.router.url };

  constructor(
    readonly store: PaymentStore,
    private readonly requestItemsService: RequestItemsService,
    private readonly requestActionsService: RequestActionsService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly breadcrumbService: BreadcrumbService,
  ) {}

  ngOnInit(): void {
    this.breadcrumbService.clear();
  }

  received(): void {
    this.router.navigate(['mark-paid'], { relativeTo: this.route });
  }

  cancel(): void {
    this.router.navigate(['cancel'], { relativeTo: this.route });
  }

  private sortTimeline(res: RequestActionInfoDTO[]): RequestActionInfoDTO[] {
    return res.slice().sort((a, b) => new Date(b.creationDate).getTime() - new Date(a.creationDate).getTime());
  }
}
