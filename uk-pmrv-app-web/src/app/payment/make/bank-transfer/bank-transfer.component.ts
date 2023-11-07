import { ChangeDetectionStrategy, Component, OnDestroy, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map } from 'rxjs';

import { BackLinkService } from '@shared/back-link/back-link.service';
import { BreadcrumbService } from '@shared/breadcrumbs/breadcrumb.service';

import { PaymentMakeRequestTaskPayload } from 'pmrv-api';

import { getHeadingMap } from '../../core/payment.map';
import { getPaymentBaseLink, shouldHidePaymentAmount } from '../../core/utils';
import { PaymentStore } from '../../store/payment.store';

@Component({
  selector: 'app-bank-transfer',
  templateUrl: './bank-transfer.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class BankTransferComponent implements OnInit, OnDestroy {
  readonly competentAuthority$ = this.store.pipe(map((state) => state.competentAuthority));
  readonly makePaymentDetails$ = this.store.pipe(
    first(),
    map((state) => state.paymentDetails as PaymentMakeRequestTaskPayload),
  );

  requestType$ = this.store.pipe(map((state) => state.requestType));

  requestTaskType$ = this.store.pipe(map((state) => state.requestTaskItem.requestTask.type));

  readonly shouldDisplayAmount$ = this.store.pipe(map((state) => !shouldHidePaymentAmount(state)));

  constructor(
    readonly store: PaymentStore,
    private readonly backLinkService: BackLinkService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly breadcrumbService: BreadcrumbService,
  ) {}

  ngOnInit(): void {
    this.backLinkService.show();

    this.store.subscribe((state) => {
      this.breadcrumbService.show([
        {
          text: getHeadingMap((state.requestTaskItem?.requestInfo?.requestMetadata as any)?.year)[
            state.requestTaskItem?.requestTask.type
          ],
          link: [getPaymentBaseLink(state.requestType) + 'payment', state.requestTaskId, 'make', 'details'],
        },
      ]);
    });
  }

  onMarkAsPaid(): void {
    this.store.setState({
      ...this.store.getState(),
      markedAsPaid: true,
    });

    this.router.navigate(['../mark-paid'], { relativeTo: this.route });
  }

  ngOnDestroy(): void {
    this.backLinkService.hide();
    this.breadcrumbService.clear();
  }
}
