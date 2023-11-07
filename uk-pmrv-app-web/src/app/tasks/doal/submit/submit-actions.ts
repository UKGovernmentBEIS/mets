import {
  DoalApplicationSubmitRequestTaskPayload,
  DoalProceedToAuthorityDetermination,
  RequestTaskItemDTO,
} from 'pmrv-api';

import { areAllSubmitSectionCompleted } from './section-status';

export function canNotifyOperator(
  payload: DoalApplicationSubmitRequestTaskPayload,
  allowedActions: RequestTaskItemDTO['allowedRequestTaskActions'],
): boolean {
  return (
    areAllSubmitSectionCompleted(payload) &&
    payload.doal.determination.type === 'PROCEED_TO_AUTHORITY' &&
    (payload.doal.determination as DoalProceedToAuthorityDetermination).needsOfficialNotice &&
    allowedActions.includes('DOAL_PROCEED_TO_AUTHORITY_AND_NOTIFY_OPERATOR_FOR_DECISION')
  );
}

export function canComplete(
  payload: DoalApplicationSubmitRequestTaskPayload,
  allowedActions: RequestTaskItemDTO['allowedRequestTaskActions'],
): boolean {
  return (
    areAllSubmitSectionCompleted(payload) &&
    ((payload.doal.determination.type === 'CLOSED' && allowedActions.includes('DOAL_CLOSE_APPLICATION')) ||
      (payload.doal.determination.type === 'PROCEED_TO_AUTHORITY' &&
        !(payload.doal.determination as DoalProceedToAuthorityDetermination).needsOfficialNotice &&
        allowedActions.includes('DOAL_PROCEED_TO_AUTHORITY_APPLICATION')))
  );
}

export function canSendPeerReview(
  payload: DoalApplicationSubmitRequestTaskPayload,
  allowedActions: RequestTaskItemDTO['allowedRequestTaskActions'],
): boolean {
  return areAllSubmitSectionCompleted(payload) && allowedActions.includes('DOAL_REQUEST_PEER_REVIEW');
}
