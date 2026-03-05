import { Pipe, PipeTransform } from '@angular/core';

import { map, Observable } from 'rxjs';

import { PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload } from 'pmrv-api';

import { ReviewGroupDecisionStatus } from '../../permit-application/review/types/review.permit.type';
import { resolveReviewGroupStatus } from '../../permit-application/review/utils/review';
import { PermitTransferStore } from '../store/permit-transfer.store';

@Pipe({
  name: 'reviewGroupStatusPermitTransfer',
})
export class ReviewGroupStatusPermitTransferPipe implements PipeTransform {
  constructor(private readonly store: PermitTransferStore) {}

  transform(
    key: PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload['group'],
  ): Observable<ReviewGroupDecisionStatus> {
    return this.store.pipe(map((state) => resolveReviewGroupStatus(state, key)));
  }
}
