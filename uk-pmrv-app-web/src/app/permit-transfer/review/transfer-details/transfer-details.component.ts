import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { map, Observable } from 'rxjs';

import { ReviewGroupDecisionStatus } from '@permit-application/review/types/review.permit.type';

import { PermitTransferStore } from '../../store/permit-transfer.store';
import { transferDetailsStatus } from '../../transfer-status';

@Component({
  selector: 'app-transfer-details-review',
  templateUrl: './transfer-details.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TransferDetailsReviewComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  transferDetailsStatus$ = this.store.pipe(map((state) => transferDetailsStatus(state)));
  transferDetailsConfirmation$ = this.store.pipe(map((state) => state.permitTransferDetailsConfirmation));
  isPermitTypeEditable$ = this.store.isPermitTypeEditable();
  groupKey$ = this.route.data.pipe(map((data) => data.groupKey));
  permitTransferDetailsConfirmationDecision$ = this.store.pipe(
    map((state) => state.permitTransferDetailsConfirmationDecision),
  );
  confirmTransferDetailsReviewStatus$: Observable<ReviewGroupDecisionStatus> =
    this.store.getConfirmTransferDetailsReviewStatus$() as Observable<ReviewGroupDecisionStatus>;

  constructor(
    private readonly router: Router,
    private readonly route: ActivatedRoute,
    private readonly store: PermitTransferStore,
  ) {}
}
