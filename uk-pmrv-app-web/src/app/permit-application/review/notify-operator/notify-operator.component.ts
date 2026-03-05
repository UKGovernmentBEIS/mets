import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { BehaviorSubject, combineLatest, first, map, Subject, switchMap, take, tap, withLatestFrom } from 'rxjs';

import { BREADCRUMB_ITEMS, BreadcrumbItem } from '@core/navigation/breadcrumbs';
import { PermitRequestTypePipe } from '@permit-application/shared/pipes/permit-request-type.pipe';
import { permitTypeMap } from '@permit-application/shared/utils/permit';
import { getPreviewDocumentsInfo } from '@permit-application/shared/utils/previewDocuments.utils';

import { RequestItemsService } from 'pmrv-api';

import { PendingRequestService } from '../../../core/guards/pending-request.service';
import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';

@Component({
  selector: 'app-permit-issuance-notify-operator',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <app-notify-operator
          [taskId]="taskId$ | async"
          [accountId]="accountId$ | async"
          [requestTaskActionType]="requestTaskActionType$ | async"
          [isRegistryToBeNotified]="isRegistryToBeNotified$ | async"
          [pendingRfi]="pendingRfi$ | async"
          [pendingRde]="pendingRde$ | async"
          [confirmationMessage]="confirmationMessage$ | async"
          [previewDocuments]="previewDocuments$ | async"></app-notify-operator>
      </div>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [PermitRequestTypePipe],
})
export class NotifyOperatorComponent implements OnInit {
  requestTaskType$ = this.store.pipe(map((state) => state.requestTaskType));
  requestTaskActionType$ = this.requestTaskType$.pipe(
    map((requestTaskType) => {
      switch (requestTaskType) {
        case 'PERMIT_ISSUANCE_APPLICATION_REVIEW':
          return 'PERMIT_ISSUANCE_NOTIFY_OPERATOR_FOR_DECISION';
        case 'PERMIT_TRANSFER_B_APPLICATION_REVIEW':
          return 'PERMIT_TRANSFER_B_NOTIFY_OPERATOR_FOR_DECISION';
        case 'PERMIT_VARIATION_APPLICATION_REVIEW':
          return 'PERMIT_VARIATION_NOTIFY_OPERATOR_FOR_DECISION';
        case 'PERMIT_VARIATION_REGULATOR_LED_APPLICATION_SUBMIT':
          return 'PERMIT_VARIATION_NOTIFY_OPERATOR_FOR_DECISION_REGULATOR_LED';
      }
    }),
  );
  permitTypeMap = permitTypeMap;
  isRegistryToBeNotified$ = this.requestTaskType$.pipe(
    map((requestTaskType) => requestTaskType === 'PERMIT_ISSUANCE_APPLICATION_REVIEW'),
  );

  readonly taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));
  readonly accountId$ = this.store.pipe(map((state) => state.accountId));
  readonly determinationStatus$ = this.store.getDeterminationStatus$();
  readonly permitType$ = this.store.pipe(
    map((state) => {
      return state.permitType;
    }),
  );

  readonly confirmationMessage$ = this.store.pipe(
    map((state) => {
      const permitType = this.permitRequestTypePipe.transform(this.store.getValue().requestType);
      return `${permitTypeMap?.[state.permitType]} ${permitType}
        `;
    }),
    withLatestFrom(this.determinationStatus$),
    map(([confirm, determinationStatus]) => `${confirm} ${determinationStatus}`),
  );

  previewDocuments$ = combineLatest([this.requestTaskActionType$, this.determinationStatus$, this.permitType$]).pipe(
    map(([requestTaskActionType, determinationStatus, permitType]) => {
      return getPreviewDocumentsInfo(requestTaskActionType, determinationStatus, permitType);
    }),
  );

  pendingRfi$: Subject<boolean> = new Subject<boolean>();
  pendingRde$: Subject<boolean> = new Subject<boolean>();

  constructor(
    private readonly store: PermitApplicationStore<PermitApplicationState>,
    private readonly route: ActivatedRoute,
    @Inject(BREADCRUMB_ITEMS) private readonly breadcrumbs$: BehaviorSubject<BreadcrumbItem[]>,
    readonly pendingRequest: PendingRequestService,
    private readonly requestItemsService: RequestItemsService,
    private readonly permitRequestTypePipe: PermitRequestTypePipe,
  ) {}

  ngOnInit(): void {
    this.requestTaskType$
      .pipe(
        take(1),
        withLatestFrom(this.breadcrumbs$),
        map(([requestTaskType, breadcrumbs]) => {
          if (
            ['PERMIT_VARIATION_APPLICATION_REVIEW', 'PERMIT_VARIATION_REGULATOR_LED_APPLICATION_SUBMIT'].includes(
              requestTaskType,
            )
          ) {
            const lastBreadcrumb = breadcrumbs[breadcrumbs.length - 1];
            lastBreadcrumb.link = [...lastBreadcrumb.link, 'review'];
          }
          return breadcrumbs;
        }),
      )
      .subscribe((breadcrumbs) => this.breadcrumbs$.next(breadcrumbs));

    this.store
      .pipe(
        first(),
        map((state) => state.requestId),
        switchMap((requestId) => this.requestItemsService.getItemsByRequest(requestId)),
        first(),
        tap((res) =>
          this.pendingRfi$.next(
            res.items.some((i) =>
              [
                'PERMIT_ISSUANCE_WAIT_FOR_RFI_RESPONSE',
                'PERMIT_VARIATION_WAIT_FOR_RFI_RESPONSE',
                'PERMIT_TRANSFER_B_WAIT_FOR_RFI_RESPONSE',
              ].includes(i.taskType),
            ),
          ),
        ),
        tap((res) =>
          this.pendingRde$.next(
            res.items.some((i) =>
              [
                'PERMIT_ISSUANCE_WAIT_FOR_RDE_RESPONSE',
                'PERMIT_VARIATION_WAIT_FOR_RDE_RESPONSE',
                'PERMIT_TRANSFER_B_WAIT_FOR_RDE_RESPONSE',
              ].includes(i.taskType),
            ),
          ),
        ),
      )
      .subscribe();
  }
}
