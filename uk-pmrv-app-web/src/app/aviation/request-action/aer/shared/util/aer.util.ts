import { AerCorsiaRequestActionPayload, AerUkEtsRequestActionPayload } from '@aviation/request-action/store';
import { aerCorsiaReviewGroupMap } from '@aviation/request-task/aer/corsia/shared/aer-review-corsia.types';
import { aerReviewGroupMap } from '@aviation/request-task/aer/shared/util/aer.util';
import { aerVerifyReviewGroupMap } from '@aviation/request-task/aer/shared/util/aer-verify-tasks.util';

import { AerDataReviewDecision, AerVerificationReportDataReviewDecision, RequestActionDTO } from 'pmrv-api';

export interface AerDecisionViewModel {
  showDecision?: boolean;
  reviewDecision?: AerDataReviewDecision;
  reviewVerifyDecision?: AerVerificationReportDataReviewDecision;
  reviewAttachments?: { [key: string]: string };
}

const allRequestActionsWithDecision: RequestActionDTO['type'][] = [
  'AVIATION_AER_CORSIA_APPLICATION_COMPLETED',
  'AVIATION_AER_UKETS_APPLICATION_COMPLETED',
];

export function getAerDecisionReview(
  payload: AerUkEtsRequestActionPayload | AerCorsiaRequestActionPayload,
  requestActionType: RequestActionDTO['type'],
  regulatorViewer: boolean,
  key: string,
  isAer: boolean,
): AerDecisionViewModel {
  return allRequestActionsWithDecision.includes(requestActionType) && regulatorViewer
    ? {
        showDecision: true,
        ...(isAer
          ? {
              reviewDecision: payload.reviewGroupDecisions[
                aerReviewGroupMap[key] ?? aerCorsiaReviewGroupMap[key] ?? key
              ] as AerDataReviewDecision,
              reviewAttachments: payload.reviewAttachments,
            }
          : {
              reviewVerifyDecision: payload.reviewGroupDecisions[
                aerVerifyReviewGroupMap[key] ?? aerCorsiaReviewGroupMap[key] ?? key
              ] as AerVerificationReportDataReviewDecision,
            }),
      }
    : {};
}
