import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map } from 'rxjs';

import { BreadcrumbService } from '@shared/breadcrumbs/breadcrumb.service';

import { PaymentMakeRequestTaskPayload } from 'pmrv-api';

import { PaymentStore } from '../../store/payment.store';

@Component({
  selector: 'app-bank-transfer',
  standalone: false,
  templateUrl: './bank-transfer.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BankTransferComponent {
  readonly competentAuthority$ = this.store.pipe(map((state) => state.competentAuthority));

  readonly makePaymentDetails$ = this.store.pipe(
    first(),
    map((state) => state.paymentDetails as PaymentMakeRequestTaskPayload),
  );

  constructor(
    readonly store: PaymentStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly breadcrumbService: BreadcrumbService,
  ) {}

  onMarkAsPaid(): void {
    this.store.setState({
      ...this.store.getState(),
      markedAsPaid: true,
    });

    this.router.navigate(['../mark-paid'], { relativeTo: this.route });
    this.breadcrumbService.showDashboardBreadcrumb(this.router.url);
  }
}
