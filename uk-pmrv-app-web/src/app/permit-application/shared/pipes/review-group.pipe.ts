import { Pipe, PipeTransform } from '@angular/core';

import { map, Observable } from 'rxjs';

import { PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload } from 'pmrv-api';

import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';

@Pipe({ name: 'reviewGroup' })
export class ReviewGroupPipe implements PipeTransform {
  constructor(private readonly store: PermitApplicationStore<PermitApplicationState>) {}

  transform(key: PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload['group']): Observable<any> {
    return this.store.pipe(map((state) => state.reviewGroupDecisions[key] ?? null));
  }
}
