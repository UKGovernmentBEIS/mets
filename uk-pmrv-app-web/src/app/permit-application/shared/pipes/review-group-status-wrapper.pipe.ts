import { Pipe, PipeTransform } from '@angular/core';

import { Observable } from 'rxjs';

import { PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload } from 'pmrv-api';

import { ReviewGroupDecisionStatus } from '../../review/types/review.permit.type';

@Pipe({
  name: 'reviewGroupStatusWrapper',
})
export class ReviewGroupStatusWrapperPipe implements PipeTransform {
  transform(
    key: PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload['group'],
    statusResolverPipe: PipeTransform,
  ): Observable<ReviewGroupDecisionStatus> {
    return statusResolverPipe.transform(key);
  }
}
