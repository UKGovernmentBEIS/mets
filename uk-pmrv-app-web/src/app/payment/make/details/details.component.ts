import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, filter, map, Observable, of, switchMap, takeUntil, withLatestFrom } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { AuthStore, selectCurrentDomain } from '@core/store';
import { hasRequestTaskAllowedActions } from '@shared/components/related-actions/request-task-allowed-actions.map';

import {
  ItemDTOResponse,
  PaymentMakeRequestTaskPayload,
  RequestActionInfoDTO,
  RequestActionsService,
  RequestItemsService,
} from 'pmrv-api';

import { getHeadingMap, paymentHintInfo } from '../../core/payment.map';
import { shouldHidePaymentAmount } from '../../core/utils';
import { PaymentStore } from '../../store/payment.store';

@Component({
  selector: 'app-details',
  templateUrl: './details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DetailsComponent {
  readonly requestTaskItem$ = this.store.pipe(map((state) => state?.requestTaskItem));

  readonly relatedTasks$ = this.store.pipe(
    switchMap((state) =>
      state.requestId
        ? this.requestItemsService.getItemsByRequest(state.requestId)
        : of({ items: [], totalItems: 0 } as ItemDTOResponse),
    ),
    withLatestFrom(this.store),
    map(([items, state]) => items?.items.filter((item) => item.taskId !== state?.requestTaskId)),
  );

  readonly actions$ = this.store.pipe(
    filter((state) => !!state.requestTaskId),
    switchMap((state) => this.requestActionsService.getRequestActionsByRequestId(state.requestId)),
    map((res) => this.sortTimeline(res)),
  );

  readonly shouldDisplayAmount$ = this.store.pipe(map((state) => !shouldHidePaymentAmount(state)));

  readonly headingMap$ = this.store.pipe(
    map((state) => getHeadingMap((state.requestTaskItem?.requestInfo?.requestMetadata as any)?.year)),
  );

  readonly paymentHintInfo = paymentHintInfo;

  readonly paymentDetails$ = this.store.pipe(
    filter((state) => !!state.requestTaskId),
    map((state) => state.paymentDetails as PaymentMakeRequestTaskPayload),
  );

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

  requestType$ = this.store.pipe(map((state) => state.requestType));

  competentAuthority$ = this.store.pipe(map((state) => state.competentAuthority));

  requestTaskType$ = this.store.pipe(
    filter((state) => !!state.requestTaskItem),
    map((state) => state.requestTaskItem.requestTask.type),
  );

  isAviation$: Observable<boolean> = this.authStore.pipe(
    selectCurrentDomain,
    takeUntil(this.destroy$),
    map((v) => v === 'AVIATION'),
  );

  constructor(
    readonly store: PaymentStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly requestItemsService: RequestItemsService,
    private readonly requestActionsService: RequestActionsService,
    public readonly authStore: AuthStore,
    private readonly destroy$: DestroySubject,
  ) {}

  makePayment(): void {
    this.router.navigate(['options'], { relativeTo: this.route });
  }

  private sortTimeline(res: RequestActionInfoDTO[]): RequestActionInfoDTO[] {
    return res.slice().sort((a, b) => new Date(b.creationDate).getTime() - new Date(a.creationDate).getTime());
  }
}
