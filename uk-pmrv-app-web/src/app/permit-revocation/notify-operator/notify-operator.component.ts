import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { map } from 'rxjs';

import { BackLinkService } from '@shared/back-link/back-link.service';
import { BreadcrumbService } from '@shared/breadcrumbs/breadcrumb.service';

import { PermitRevocationStore } from '../store/permit-revocation-store';

@Component({
  selector: 'app-permit-revocation-notify-operator',
  template: `
    <div class="govuk-grid-row">
      <div class="govuk-grid-column-two-thirds">
        <app-notify-operator
          [taskId]="taskId$ | async"
          [accountId]="accountId$ | async"
          [requestTaskActionType]="(route.data | async)?.requestTaskActionType"
          [confirmationMessage]="confirmationMessage$ | async"></app-notify-operator>
      </div>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class NotifyOperatorComponent implements OnInit {
  constructor(
    readonly route: ActivatedRoute,
    private store: PermitRevocationStore,
    private readonly backLinkService: BackLinkService,
    private readonly breadcrumbService: BreadcrumbService,
  ) {}

  readonly taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));
  readonly accountId$ = this.store.pipe(map(({ accountId }) => accountId));
  readonly confirmationMessage$ = this.route.data.pipe(
    map((state) => state.requestTaskActionType),
    map((requestTaskActionType) => {
      switch (requestTaskActionType) {
        case 'PERMIT_REVOCATION_NOTIFY_OPERATOR_FOR_SUBMISSION':
          return 'Permit revocation notice sent to operator';
        case 'PERMIT_REVOCATION_NOTIFY_OPERATOR_FOR_WITHDRAWAL':
          this.breadcrumbService.addToLastBreadcrumbAndShow('wait-for-appeal');
          return 'Permit revocation withdrawn successfully';
        case 'PERMIT_REVOCATION_NOTIFY_OPERATOR_FOR_CESSATION':
          this.breadcrumbService.addToLastBreadcrumbAndShow('cessation');
          return 'Cessation complete';
      }
    }),
  );

  ngOnInit(): void {
    this.backLinkService.show();
  }
}
