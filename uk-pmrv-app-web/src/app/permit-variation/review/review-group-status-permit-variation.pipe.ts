import { Pipe, PipeTransform } from '@angular/core';

import { map, Observable } from 'rxjs';

import { PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload } from 'pmrv-api';

import { ReviewGroupDecisionStatus } from '../../permit-application/review/types/review.permit.type';
import { resolveReviewGroupStatus } from '../../permit-application/review/utils/review';
import { PermitVariationStore } from '../store/permit-variation.store';

@Pipe({
  name: 'reviewGroupStatusPermitVariation',
})
export class ReviewGroupStatusPermitVariationPipe implements PipeTransform {
  constructor(private readonly store: PermitVariationStore) {}

  transform(
    key: PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload['group'],
  ): Observable<ReviewGroupDecisionStatus> {
    return this.store.pipe(map((state) => resolveReviewGroupStatus(state, key)));
  }
}
