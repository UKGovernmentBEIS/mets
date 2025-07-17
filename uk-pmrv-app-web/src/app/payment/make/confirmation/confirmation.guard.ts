import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { getPaymentBaseLink } from '../../core/utils';
import { PaymentStore } from '../../store/payment.store';

@Injectable({ providedIn: 'root' })
export class ConfirmationGuard {
  constructor(
    private readonly store: PaymentStore,
    private readonly router: Router,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.store.pipe(
      map((state) => {
        const paymentMethod = route.queryParams?.method;
        const returnUrl = `/${getPaymentBaseLink(state.requestType)}payment/${route.paramMap.get('taskId')}/make`;

        switch (paymentMethod) {
          case 'BANK_TRANSFER':
            return (
              (state.selectedPaymentMethod === 'BANK_TRANSFER' && state.markedAsPaid && state.completed) ||
              this.router.parseUrl(returnUrl)
            );
          case 'CREDIT_OR_DEBIT_CARD':
            return true;
          default:
            return this.router.parseUrl(returnUrl);
        }
      }),
    );
  }
}
