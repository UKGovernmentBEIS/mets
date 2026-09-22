import { Injectable } from '@angular/core';

import { first, map, Observable } from 'rxjs';

import { RequestsService } from 'pmrv-api';

import { PermitVariationStore } from '../../store/permit-variation.store';

@Injectable({ providedIn: 'root' })
export class RequestPaymentGuard {
  constructor(
    private readonly store: PermitVariationStore,
    private readonly requestsService: RequestsService,
  ) {}

  canActivate(): Observable<boolean> {
    return this.requestsService.hasAccessRequestPayment(this.store.getState().requestId).pipe(
      first(),
      map((hasAccess) => !!hasAccess),
    );
  }
}
