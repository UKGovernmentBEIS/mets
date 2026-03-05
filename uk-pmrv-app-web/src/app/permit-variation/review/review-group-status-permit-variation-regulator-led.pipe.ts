import { Pipe, PipeTransform } from '@angular/core';

import { map, Observable } from 'rxjs';

import { PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload } from 'pmrv-api';

import { ReviewGroupTasksAggregatorStatus } from '../../permit-application/review/types/review.permit.type';
import { PermitVariationStore } from '../store/permit-variation.store';
import { resolveReviewGroupStatusRegulatorLed } from './review-status';

@Pipe({
  name: 'reviewGroupStatusPermitVariationRegulatorLed',
})
export class ReviewGroupStatusPermitVariationRegulatorLedPipe implements PipeTransform {
  constructor(private readonly store: PermitVariationStore) {}

  transform(
    key: PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload['group'],
  ): Observable<ReviewGroupTasksAggregatorStatus> {
    return this.store.pipe(
      map((state) => {
        return resolveReviewGroupStatusRegulatorLed(state, key);
      }),
    );
  }
}
