import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';

import { map } from 'rxjs';

import { BackLinkService } from '@shared/back-link/back-link.service';

import { shouldHidePaymentAmount } from '../../core/utils';
import { PaymentStore } from '../../store/payment.store';

@Component({
  selector: 'app-received',
  template: `
    <ng-container *ngIf="store | async as state">
      <app-request-action-heading
        headerText="Payment marked as received"
        [timelineCreationDate]="state.requestActionCreationDate"
      >
      </app-request-action-heading>
      <app-payment-summary [details]="details$ | async" [shouldDisplayAmount]="shouldDisplayAmount$ | async">
        <h2 app-summary-header class="govuk-heading-m">Details</h2>
      </app-payment-summary>
    </ng-container>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ReceivedComponent implements OnInit {
  readonly shouldDisplayAmount$ = this.store.pipe(map((state) => !shouldHidePaymentAmount(state)));

  constructor(readonly store: PaymentStore, private readonly backLinkService: BackLinkService) {}
  details$ = this.store.pipe(
    map((state: any) => {
      return { ...state.actionPayload, amount: +state.actionPayload.amount };
    }),
  );

  ngOnInit(): void {
    this.backLinkService.show();
  }
}
