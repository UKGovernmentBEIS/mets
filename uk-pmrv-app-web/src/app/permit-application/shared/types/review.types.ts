import { PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload } from 'pmrv-api';

export type permitApplicationReviewGroupDecision =
  | PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload['group']
  | 'CONFIRM_TRANSFER_DETAILS';
