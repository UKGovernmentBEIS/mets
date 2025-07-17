import { Pipe, PipeTransform } from '@angular/core';

import {
  AerDataReviewDecision,
  AerVerificationReportDataReviewDecision,
  PermitIssuanceReviewDecision,
  PermitNotificationReviewDecision,
  PermitSurrenderReviewDecision,
  PermitVariationReviewDecision,
} from 'pmrv-api';

@Pipe({ name: 'reviewGroupDecision' })
export class ReviewGroupDecisionPipe implements PipeTransform {
  transform(
    value:
      | PermitIssuanceReviewDecision['type']
      | PermitVariationReviewDecision['type']
      | PermitNotificationReviewDecision['type']
      | PermitSurrenderReviewDecision['type']
      | AerDataReviewDecision['type']
      | AerVerificationReportDataReviewDecision['type'],
  ): string {
    switch (value) {
      case 'ACCEPTED':
        return 'Accepted';
      case 'REJECTED':
        return 'Rejected';
      case 'PERMANENT_CESSATION':
        return 'Permanent cessation';
      case 'TEMPORARY_CESSATION':
        return 'Temporary cessation';
      case 'CESSATION_TREATED_AS_PERMANENT':
        return 'Cessation treated as permanent';
      case 'NOT_CESSATION':
        return 'Not cessation';
      case 'OPERATOR_AMENDS_NEEDED':
        return 'Operator amends needed';
    }
  }
}
