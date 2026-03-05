import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map } from 'rxjs';

import { BreadcrumbService } from '@shared/breadcrumbs/breadcrumb.service';

import { PaymentMakeRequestTaskPayload } from 'pmrv-api';

import { shouldHidePaymentAmount } from '../../core/utils';
import { PaymentStore } from '../../store/payment.store';

@Component({
  selector: 'app-bank-transfer',
  templateUrl: './bank-transfer.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BankTransferComponent {
  readonly competentAuthority$ = this.store.pipe(map((state) => state.competentAuthority));

  readonly shouldDisplayAmount$ = this.store.pipe(map((state) => !shouldHidePaymentAmount(state)));

  readonly makePaymentDetails$ = this.store.pipe(
    first(),
    map((state) => state.paymentDetails as PaymentMakeRequestTaskPayload),
  );

  requestType$ = this.store.pipe(map((state) => state.requestType));
  requestTaskType$ = this.store.pipe(map((state) => state.requestTaskItem.requestTask.type));

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
