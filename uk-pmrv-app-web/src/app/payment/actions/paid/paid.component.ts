import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map } from 'rxjs';

import { shouldHidePaymentAmount } from '../../core/utils';
import { PaymentStore } from '../../store/payment.store';

@Component({
  selector: 'app-paid',
  template: `
    <ng-container *ngIf="store | async as state">
      <app-request-action-heading
        headerText="Payment marked as paid"
        [timelineCreationDate]="state.requestActionCreationDate"></app-request-action-heading>

      <app-payment-summary [details]="details$ | async" [shouldDisplayAmount]="shouldDisplayAmount$ | async">
        <h2 app-summary-header class="govuk-heading-m">Details</h2>
      </app-payment-summary>
    </ng-container>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PaidComponent {
  readonly shouldDisplayAmount$ = this.store.pipe(map((state) => !shouldHidePaymentAmount(state)));

  details$ = this.store.pipe(
    map((state: any) => {
      return { ...state.actionPayload, amount: +state.actionPayload?.amount };
    }),
  );

  constructor(readonly store: PaymentStore) {}
}
