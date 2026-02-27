import { ChangeDetectionStrategy, Component } from '@angular/core';

@Component({
  selector: 'app-payment-not-completed',
  standalone: false,
  template: `
    <app-page-heading>The payment task must be closed before you can proceed</app-page-heading>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PaymentNotCompletedComponent {}
