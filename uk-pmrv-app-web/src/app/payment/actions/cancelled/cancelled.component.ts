import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';

import { first, map, Observable } from 'rxjs';

import { BackLinkService } from '@shared/back-link/back-link.service';

import { PaymentCancelledRequestActionPayload } from 'pmrv-api';

import { PaymentStore } from '../../store/payment.store';

@Component({
  selector: 'app-cancelled',
  template: `
    <ng-container *ngIf="details$ | async as details">
      <app-request-action-heading headerText="Payment task cancelled" [timelineCreationDate]="creationDate$ | async">
      </app-request-action-heading>
      <h2 app-summary-header class="govuk-heading-m">Details</h2>
      <dl govuk-summary-list>
        <div govukSummaryListRow>
          <dt govukSummaryListRowKey>Payment status</dt>
          <dd govukSummaryListRowValue>{{ details.status | paymentStatus }}</dd>
        </div>
        <div govukSummaryListRow>
          <dt govukSummaryListRowKey>Reason</dt>
          <dd govukSummaryListRowValue class="pre-wrap">{{ details.cancellationReason }}</dd>
        </div>
      </dl>
    </ng-container>
    <app-return-link [requestType]="(store | async).requestType" [home]="true"></app-return-link>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class CancelledComponent implements OnInit {
  details$ = this.store.pipe(
    first(),
    map((state) => state.actionPayload as PaymentCancelledRequestActionPayload),
  );
  creationDate$: Observable<string> = this.store.select('requestActionCreationDate');

  constructor(readonly store: PaymentStore, private readonly backLinkService: BackLinkService) {}

  ngOnInit(): void {
    this.backLinkService.show();
  }
}
