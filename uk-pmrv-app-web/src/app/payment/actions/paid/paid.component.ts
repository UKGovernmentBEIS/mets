import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map } from 'rxjs';

import { PaymentStore } from '../../store/payment.store';

@Component({
  selector: 'app-paid',
  standalone: false,
  template: `
    <ng-container *ngIf="store | async as state">
      <app-request-action-heading
        headerText="Payment marked as paid"
        [timelineCreationDate]="state.requestActionCreationDate"></app-request-action-heading>

      <app-payment-summary [details]="details$ | async">
        <app-summary-header class="govuk-heading-m">Details</app-summary-header>
      </app-payment-summary>
    </ng-container>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PaidComponent {
  details$ = this.store.pipe(
    map((state: any) => {
      return { ...state.actionPayload, amount: +state.actionPayload?.amount };
    }),
  );

  constructor(readonly store: PaymentStore) {}
}
