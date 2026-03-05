import { Pipe, PipeTransform } from '@angular/core';

import { map, Observable } from 'rxjs';

import { PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload } from 'pmrv-api';

import { PermitVariationStore } from '../../../store/permit-variation.store';
import { AboutVariationGroupKey } from '../../../variation-types';

@Pipe({ name: 'reviewGroupVariation' })
export class ReviewGroupVariationPipe implements PipeTransform {
  constructor(private readonly store: PermitVariationStore) {}

  transform(
    key: PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload['group'] | AboutVariationGroupKey,
  ): Observable<any> {
    return this.store.pipe(
      map((state) =>
        key === 'ABOUT_VARIATION'
          ? state.permitVariationDetailsReviewDecision
          : (state.reviewGroupDecisions[key] ?? null),
      ),
    );
  }
}
