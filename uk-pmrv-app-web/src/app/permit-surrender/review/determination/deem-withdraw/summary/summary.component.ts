import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { map, Observable } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';
import { PendingRequest } from '@core/interfaces/pending-request.interface';

import { PermitSurrenderReviewDeterminationDeemWithdraw } from 'pmrv-api';

import { PermitSurrenderStore } from '../../../../store/permit-surrender.store';

@Component({
  selector: 'app-summary',
  template: `
    <app-page-heading>Permit surrender deem withdrawn</app-page-heading>

    <govuk-notification-banner *ngIf="notification" type="success">
      <h1 class="govuk-notification-banner__heading">Details updated</h1>
    </govuk-notification-banner>

    <app-deem-withdraw-determination-summary-details
      [deemWithdrawDetermination$]="deemWithdrawDetermination$"></app-deem-withdraw-determination-summary-details>

    <a govukLink routerLink="../../..">Return to: Permit surrender review</a>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryComponent implements PendingRequest {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;

  deemWithdrawDetermination$ = this.store.pipe(
    map((state) => state?.reviewDetermination),
  ) as Observable<PermitSurrenderReviewDeterminationDeemWithdraw>;
  isEditable$ = this.store.pipe(map((state) => state?.isEditable));

  constructor(
    readonly pendingRequest: PendingRequestService,
    private readonly store: PermitSurrenderStore,
    private readonly router: Router,
  ) {}
}
