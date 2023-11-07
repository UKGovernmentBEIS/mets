import {
  PermitAcceptedVariationDecisionDetails,
  PermitContainer,
  PermitVariationDetails,
  PermitVariationReviewDecision,
} from 'pmrv-api';

import { PermitApplicationState } from '../../permit-application/store/permit-application.state';

export interface PermitVariationState extends PermitApplicationState {
  originalPermitContainer?: PermitContainer;
  permitVariationDetails?: PermitVariationDetails;
  permitVariationDetailsCompleted?: boolean;
  permitVariationDetailsReviewDecision?: PermitVariationReviewDecision | PermitAcceptedVariationDecisionDetails;
  permitVariationDetailsReviewCompleted?: boolean;
  permitVariationDetailsAmendCompleted?: boolean;
}
