import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { first, map, Subject, switchMap, tap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';

import { RequestItemsService } from 'pmrv-api';

import { PermitSurrenderStore } from '../../store/permit-surrender.store';

@Component({
  selector: 'app-permit-surrender-cessation-notify-operator',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <app-notify-operator
          [taskId]="taskId$ | async"
          [accountId]="accountId$ | async"
          requestTaskActionType="PERMIT_SURRENDER_CESSATION_NOTIFY_OPERATOR_FOR_DECISION"
          [pendingRfi]="pendingRfi$ | async"
          [pendingRde]="pendingRde$ | async"
          [confirmationMessage]="'Cessation complete'"></app-notify-operator>
      </div>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NotifyOperatorComponent implements OnInit {
  constructor(
    private readonly store: PermitSurrenderStore,
    private readonly route: ActivatedRoute,
    readonly pendingRequest: PendingRequestService,
    private readonly requestItemsService: RequestItemsService,
  ) {}

  readonly taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));
  readonly accountId$ = this.store.pipe(map((state) => state?.accountId));

  pendingRfi$: Subject<boolean> = new Subject<boolean>();
  pendingRde$: Subject<boolean> = new Subject<boolean>();

  ngOnInit(): void {
    this.store
      .pipe(
        first(),
        map((state) => state?.requestId),
        switchMap((requestId) => this.requestItemsService.getItemsByRequest(requestId)),
        first(),
        tap((res) =>
          this.pendingRfi$.next(res.items.some((i) => 'PERMIT_SURRENDER_WAIT_FOR_RFI_RESPONSE' === i.taskType)),
        ),
        tap((res) =>
          this.pendingRde$.next(res.items.some((i) => 'PERMIT_SURRENDER_WAIT_FOR_RDE_RESPONSE' === i.taskType)),
        ),
      )
      .subscribe();
  }
}
