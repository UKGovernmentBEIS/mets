import { Pipe, PipeTransform } from '@angular/core';

import { map, Observable } from 'rxjs';

import {
  PermitNotificationApplicationReviewRequestTaskPayload,
  PermitNotificationFollowUpApplicationReviewRequestTaskPayload,
} from 'pmrv-api';

import { PermitNotificationService } from '../../core/permit-notification.service';
import {
  DecisionStatus,
  FollowUpDecisionStatus,
  resolveFollowUpReviewDecisionStatus,
  resolveReviewDecisionStatus,
  ReviewSectionKey,
} from '../../core/section-status';

@Pipe({
  name: 'reviewNotificationStatus',
})
export class ReviewNotificationStatusPipe implements PipeTransform {
  constructor(private readonly permitNotificationService: PermitNotificationService) {}

  transform(key: ReviewSectionKey): Observable<DecisionStatus | FollowUpDecisionStatus> {
    return this.permitNotificationService.getPayload().pipe(
      map(
        (
          payload:
            | PermitNotificationApplicationReviewRequestTaskPayload
            | PermitNotificationFollowUpApplicationReviewRequestTaskPayload,
        ) => {
          switch (key) {
            case 'DETAILS_CHANGE':
              return resolveReviewDecisionStatus(
                (payload as PermitNotificationApplicationReviewRequestTaskPayload)?.reviewDecision,
              );
            case 'FOLLOW_UP':
              return resolveFollowUpReviewDecisionStatus(
                payload as PermitNotificationFollowUpApplicationReviewRequestTaskPayload,
              );
          }
        },
      ),
    );
  }
}
