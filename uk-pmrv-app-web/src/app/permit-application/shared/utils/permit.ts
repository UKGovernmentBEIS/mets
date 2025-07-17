import { PermitApplicationState } from '@permit-application/store/permit-application.state';

import { PermitContainer, RequestTaskActionProcessDTO, RequestTaskDTO } from 'pmrv-api';

export const permitTypeMap: Record<PermitContainer['permitType'], string> = {
  GHGE: 'GHGE',
  HSE: 'HSE',
  WASTE: 'Voluntary waste',
};

export const permitTypeMapLowercase: Record<PermitContainer['permitType'], string> = {
  GHGE: 'GHGE',
  HSE: 'HSE',
  WASTE: 'voluntary waste',
};

export const notifyOperatorRequestTaskActionTypes: Array<RequestTaskActionProcessDTO['requestTaskActionType']> = [
  'PERMIT_ISSUANCE_NOTIFY_OPERATOR_FOR_DECISION',
  'PERMIT_VARIATION_NOTIFY_OPERATOR_FOR_DECISION',
  'PERMIT_VARIATION_NOTIFY_OPERATOR_FOR_DECISION_REGULATOR_LED',
  'PERMIT_TRANSFER_B_NOTIFY_OPERATOR_FOR_DECISION',
];

export const peerReviewRequestTaskActionTypes: Array<RequestTaskActionProcessDTO['requestTaskActionType']> = [
  'PERMIT_ISSUANCE_REQUEST_PEER_REVIEW',
  'PERMIT_VARIATION_REQUEST_PEER_REVIEW',
  'PERMIT_VARIATION_REQUEST_PEER_REVIEW_REGULATOR_LED',
  'PERMIT_TRANSFER_B_REQUEST_PEER_REVIEW',
];

export const peerReviewSubmitRequestTaskActionTypes: Array<RequestTaskActionProcessDTO['requestTaskActionType']> = [
  'PERMIT_ISSUANCE_REVIEW_SUBMIT_PEER_REVIEW_DECISION',
  'PERMIT_VARIATION_REVIEW_SUBMIT_PEER_REVIEW_DECISION',
  'PERMIT_VARIATION_REVIEW_SUBMIT_PEER_REVIEW_DECISION_REGULATOR_LED',
  'PERMIT_TRANSFER_B_REVIEW_SUBMIT_PEER_REVIEW_DECISION',
];

export const returnForAmendRequestTaskActionTypes: Array<RequestTaskActionProcessDTO['requestTaskActionType']> = [
  'PERMIT_ISSUANCE_REVIEW_RETURN_FOR_AMENDS',
  'PERMIT_VARIATION_REVIEW_RETURN_FOR_AMENDS',
  'PERMIT_TRANSFER_B_REVIEW_RETURN_FOR_AMENDS',
];

export const reviewRequestTaskTypes: Array<RequestTaskDTO['type']> = [
  'PERMIT_ISSUANCE_APPLICATION_REVIEW',
  'PERMIT_ISSUANCE_WAIT_FOR_PEER_REVIEW',
  'PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW',
  'PERMIT_VARIATION_APPLICATION_REVIEW',
  'PERMIT_VARIATION_REGULATOR_LED_APPLICATION_SUBMIT', //special case. it is considered as review task (uses review related pages)
  'PERMIT_VARIATION_APPLICATION_PEER_REVIEW',
  'PERMIT_VARIATION_WAIT_FOR_PEER_REVIEW',
  'PERMIT_VARIATION_REGULATOR_LED_APPLICATION_PEER_REVIEW',
  'PERMIT_VARIATION_REGULATOR_LED_WAIT_FOR_PEER_REVIEW',
];

export const amendRequestTaskTypes: Array<RequestTaskDTO['type']> = [
  'PERMIT_ISSUANCE_APPLICATION_AMENDS_SUBMIT',
  'PERMIT_VARIATION_APPLICATION_AMENDS_SUBMIT',
  'PERMIT_TRANSFER_B_APPLICATION_AMENDS_SUBMIT',
];

export const recallRequestTaskActionTypes: Array<RequestTaskActionProcessDTO['requestTaskActionType']> = [
  'PERMIT_ISSUANCE_RECALL_FROM_AMENDS',
  'PERMIT_VARIATION_RECALL_FROM_AMENDS',
  'PERMIT_TRANSFER_B_RECALL_FROM_AMENDS',
];

export const isVariationRegulatorLedRequestTask = (requestTaskType: RequestTaskDTO['type']) => {
  return [
    'PERMIT_VARIATION_REGULATOR_LED_APPLICATION_SUBMIT',
    'PERMIT_VARIATION_REGULATOR_LED_APPLICATION_PEER_REVIEW',
    'PERMIT_VARIATION_REGULATOR_LED_WAIT_FOR_PEER_REVIEW',
  ].includes(requestTaskType);
};

export const isVariationRegulatorLedRequest = (state: PermitApplicationState): boolean => {
  return state.isRequestTask
    ? isVariationRegulatorLedRequestTask(state.requestTaskType)
    : state.requestActionType === 'PERMIT_VARIATION_APPLICATION_REGULATOR_LED_APPROVED';
};
