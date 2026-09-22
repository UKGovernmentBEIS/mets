import { ChangeDetectionStrategy, Component } from '@angular/core';

import { map, Observable } from 'rxjs';

import { PermitVariationRequestPaymentDetails } from 'pmrv-api';

import { PermitVariationStore } from '../../../store/permit-variation.store';

@Component({
  selector: 'app-permit-variation-request-payment-details',
  standalone: false,
  templateUrl: './details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DetailsComponent {
  readonly paymentDetails$: Observable<PermitVariationRequestPaymentDetails> = this.store.pipe(
    map((state) => (state as any).paymentDetails),
  );

  readonly creationDate$: Observable<string> = this.store.pipe(map((state) => state.requestActionCreationDate));

  constructor(readonly store: PermitVariationStore) {}
}
