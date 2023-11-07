import { UrlRequestType } from '@shared/types/url-request-type';

import { RequestTaskActionPayload, RequestTaskActionProcessDTO, RequestTaskDTO } from 'pmrv-api';

export function resolveRequestTaskActionType(
  requestTaskType: RequestTaskDTO['type'],
): RequestTaskActionProcessDTO['requestTaskActionType'] {
  switch (requestTaskType) {
    case 'PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW':
      return 'PERMIT_ISSUANCE_REVIEW_SUBMIT_PEER_REVIEW_DECISION';
    case 'PERMIT_VARIATION_APPLICATION_PEER_REVIEW':
      return 'PERMIT_VARIATION_REVIEW_SUBMIT_PEER_REVIEW_DECISION';
    case 'PERMIT_VARIATION_REGULATOR_LED_APPLICATION_PEER_REVIEW':
      return 'PERMIT_VARIATION_REVIEW_SUBMIT_PEER_REVIEW_DECISION_REGULATOR_LED';
    case 'PERMIT_SURRENDER_APPLICATION_PEER_REVIEW':
      return 'PERMIT_SURRENDER_REVIEW_SUBMIT_PEER_REVIEW_DECISION';
    case 'PERMIT_REVOCATION_APPLICATION_PEER_REVIEW':
      return 'PERMIT_REVOCATION_SUBMIT_PEER_REVIEW_DECISION';
    case 'PERMIT_NOTIFICATION_APPLICATION_PEER_REVIEW':
      return 'PERMIT_NOTIFICATION_REVIEW_SUBMIT_PEER_REVIEW_DECISION';
    case 'PERMIT_TRANSFER_B_APPLICATION_PEER_REVIEW':
      return 'PERMIT_TRANSFER_B_REVIEW_SUBMIT_PEER_REVIEW_DECISION';
    case 'DRE_APPLICATION_PEER_REVIEW':
      return 'DRE_SUBMIT_PEER_REVIEW_DECISION';
    case 'DOAL_APPLICATION_PEER_REVIEW':
      return 'DOAL_SUBMIT_PEER_REVIEW_DECISION';
    case 'NON_COMPLIANCE_DAILY_PENALTY_NOTICE_APPLICATION_PEER_REVIEW':
      return 'NON_COMPLIANCE_DAILY_PENALTY_NOTICE_SUBMIT_PEER_REVIEW_DECISION';
    case 'NON_COMPLIANCE_NOTICE_OF_INTENT_APPLICATION_PEER_REVIEW':
      return 'NON_COMPLIANCE_NOTICE_OF_INTENT_SUBMIT_PEER_REVIEW_DECISION';
    case 'NON_COMPLIANCE_CIVIL_PENALTY_APPLICATION_PEER_REVIEW':
      return 'NON_COMPLIANCE_CIVIL_PENALTY_SUBMIT_PEER_REVIEW_DECISION';
    case 'WITHHOLDING_OF_ALLOWANCES_APPLICATION_PEER_REVIEW':
      return 'WITHHOLDING_OF_ALLOWANCES_SUBMIT_PEER_REVIEW_DECISION';
    case 'EMP_ISSUANCE_UKETS_APPLICATION_PEER_REVIEW':
      return 'EMP_ISSUANCE_UKETS_REVIEW_SUBMIT_PEER_REVIEW_DECISION';
    case 'EMP_ISSUANCE_CORSIA_APPLICATION_PEER_REVIEW':
      return 'EMP_ISSUANCE_CORSIA_REVIEW_SUBMIT_PEER_REVIEW_DECISION';
    case 'EMP_VARIATION_UKETS_REGULATOR_LED_APPLICATION_PEER_REVIEW':
      return 'EMP_VARIATION_UKETS_REVIEW_SUBMIT_PEER_REVIEW_DECISION_REGULATOR_LED';
    case 'EMP_VARIATION_CORSIA_REGULATOR_LED_APPLICATION_PEER_REVIEW':
      return 'EMP_VARIATION_CORSIA_REVIEW_SUBMIT_PEER_REVIEW_DECISION_REGULATOR_LED';
    case 'EMP_VARIATION_UKETS_APPLICATION_PEER_REVIEW':
      return 'EMP_VARIATION_UKETS_REVIEW_SUBMIT_PEER_REVIEW_DECISION';
    case 'EMP_VARIATION_CORSIA_APPLICATION_PEER_REVIEW':
      return 'EMP_VARIATION_CORSIA_REVIEW_SUBMIT_PEER_REVIEW_DECISION';
    case 'RETURN_OF_ALLOWANCES_APPLICATION_PEER_REVIEW':
      return 'RETURN_OF_ALLOWANCES_SUBMIT_PEER_REVIEW_DECISION';
    case 'AVIATION_DRE_UKETS_APPLICATION_PEER_REVIEW':
      return 'AVIATION_DRE_UKETS_SUBMIT_PEER_REVIEW_DECISION';
    case 'AVIATION_NON_COMPLIANCE_DAILY_PENALTY_NOTICE':
      return 'NON_COMPLIANCE_DAILY_PENALTY_NOTICE_REQUEST_PEER_REVIEW';
    case 'AVIATION_NON_COMPLIANCE_DAILY_PENALTY_NOTICE_APPLICATION_PEER_REVIEW':
      return 'NON_COMPLIANCE_DAILY_PENALTY_NOTICE_SUBMIT_PEER_REVIEW_DECISION';
    case 'AVIATION_NON_COMPLIANCE_NOTICE_OF_INTENT':
      return 'NON_COMPLIANCE_NOTICE_OF_INTENT_REQUEST_PEER_REVIEW';
    case 'AVIATION_NON_COMPLIANCE_NOTICE_OF_INTENT_APPLICATION_PEER_REVIEW':
      return 'NON_COMPLIANCE_NOTICE_OF_INTENT_SUBMIT_PEER_REVIEW_DECISION';
    case 'AVIATION_NON_COMPLIANCE_CIVIL_PENALTY':
      return 'NON_COMPLIANCE_CIVIL_PENALTY_REQUEST_PEER_REVIEW';
    case 'AVIATION_NON_COMPLIANCE_CIVIL_PENALTY_APPLICATION_PEER_REVIEW':
      return 'NON_COMPLIANCE_CIVIL_PENALTY_SUBMIT_PEER_REVIEW_DECISION';

    default:
      return null;
  }
}

export function resolveRequestTaskActionPayloadType(
  requestTaskType: RequestTaskDTO['type'],
): RequestTaskActionPayload['payloadType'] {
  switch (requestTaskType) {
    case 'PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW':
      return 'PERMIT_ISSUANCE_REVIEW_SUBMIT_PEER_REVIEW_DECISION_PAYLOAD';
    case 'PERMIT_VARIATION_APPLICATION_PEER_REVIEW':
      return 'PERMIT_VARIATION_REVIEW_SUBMIT_PEER_REVIEW_DECISION_PAYLOAD';
    case 'PERMIT_VARIATION_REGULATOR_LED_APPLICATION_PEER_REVIEW':
      return 'PERMIT_VARIATION_REVIEW_SUBMIT_PEER_REVIEW_DECISION_REGULATOR_LED_PAYLOAD';
    case 'PERMIT_SURRENDER_APPLICATION_PEER_REVIEW':
      return 'PERMIT_SURRENDER_REVIEW_SUBMIT_PEER_REVIEW_DECISION_PAYLOAD';
    case 'PERMIT_REVOCATION_APPLICATION_PEER_REVIEW':
      return 'PERMIT_REVOCATION_SUBMIT_PEER_REVIEW_DECISION_PAYLOAD';
    case 'PERMIT_NOTIFICATION_APPLICATION_PEER_REVIEW':
      return 'PERMIT_NOTIFICATION_REVIEW_SUBMIT_PEER_REVIEW_DECISION_PAYLOAD';
    case 'PERMIT_TRANSFER_B_APPLICATION_PEER_REVIEW':
      return 'PERMIT_TRANSFER_B_REVIEW_SUBMIT_PEER_REVIEW_DECISION_PAYLOAD';
    case 'DRE_APPLICATION_PEER_REVIEW':
      return 'DRE_SUBMIT_PEER_REVIEW_DECISION_PAYLOAD';
    case 'DOAL_APPLICATION_PEER_REVIEW':
      return 'DOAL_SUBMIT_PEER_REVIEW_DECISION_PAYLOAD';
    case 'NON_COMPLIANCE_DAILY_PENALTY_NOTICE_APPLICATION_PEER_REVIEW':
      return 'NON_COMPLIANCE_DAILY_PENALTY_NOTICE_SUBMIT_PEER_REVIEW_DECISION_PAYLOAD';
    case 'NON_COMPLIANCE_NOTICE_OF_INTENT_APPLICATION_PEER_REVIEW':
      return 'NON_COMPLIANCE_NOTICE_OF_INTENT_SUBMIT_PEER_REVIEW_DECISION_PAYLOAD';
    case 'NON_COMPLIANCE_CIVIL_PENALTY_APPLICATION_PEER_REVIEW':
      return 'NON_COMPLIANCE_CIVIL_PENALTY_SUBMIT_PEER_REVIEW_DECISION_PAYLOAD';
    case 'WITHHOLDING_OF_ALLOWANCES_APPLICATION_PEER_REVIEW':
      return 'WITHHOLDING_OF_ALLOWANCES_SUBMIT_PEER_REVIEW_DECISION_PAYLOAD';
    case 'EMP_ISSUANCE_UKETS_APPLICATION_PEER_REVIEW':
      return 'EMP_ISSUANCE_UKETS_REVIEW_SUBMIT_PEER_REVIEW_DECISION_PAYLOAD';
    case 'EMP_ISSUANCE_CORSIA_APPLICATION_PEER_REVIEW':
      return 'EMP_ISSUANCE_CORSIA_REVIEW_SUBMIT_PEER_REVIEW_DECISION_PAYLOAD';
    case 'EMP_VARIATION_UKETS_REGULATOR_LED_APPLICATION_PEER_REVIEW':
      return 'EMP_VARIATION_UKETS_REVIEW_SUBMIT_PEER_REVIEW_DECISION_REGULATOR_LED_PAYLOAD';
    case 'EMP_VARIATION_CORSIA_REGULATOR_LED_APPLICATION_PEER_REVIEW':
      return 'EMP_VARIATION_CORSIA_REVIEW_SUBMIT_PEER_REVIEW_DECISION_REGULATOR_LED_PAYLOAD';
    case 'EMP_VARIATION_UKETS_APPLICATION_PEER_REVIEW':
      return 'EMP_VARIATION_UKETS_REVIEW_SUBMIT_PEER_REVIEW_DECISION_PAYLOAD';
    case 'EMP_VARIATION_CORSIA_APPLICATION_PEER_REVIEW':
      return 'EMP_VARIATION_CORSIA_REVIEW_SUBMIT_PEER_REVIEW_DECISION_PAYLOAD';
    case 'RETURN_OF_ALLOWANCES_APPLICATION_PEER_REVIEW':
      return 'RETURN_OF_ALLOWANCES_SUBMIT_PEER_REVIEW_DECISION_PAYLOAD';
    case 'AVIATION_DRE_UKETS_APPLICATION_PEER_REVIEW':
      return 'AVIATION_DRE_UKETS_SUBMIT_PEER_REVIEW_DECISION_PAYLOAD';
    case 'AVIATION_NON_COMPLIANCE_DAILY_PENALTY_NOTICE_APPLICATION_PEER_REVIEW':
      return 'NON_COMPLIANCE_DAILY_PENALTY_NOTICE_SUBMIT_PEER_REVIEW_DECISION_PAYLOAD';
    case 'AVIATION_NON_COMPLIANCE_NOTICE_OF_INTENT_APPLICATION_PEER_REVIEW':
      return 'NON_COMPLIANCE_NOTICE_OF_INTENT_SUBMIT_PEER_REVIEW_DECISION_PAYLOAD';
    case 'AVIATION_NON_COMPLIANCE_CIVIL_PENALTY_APPLICATION_PEER_REVIEW':
      return 'NON_COMPLIANCE_CIVIL_PENALTY_SUBMIT_PEER_REVIEW_DECISION_PAYLOAD';

    default:
      return null;
  }
}
export function resolveReturnToText(requestType: UrlRequestType, requestTaskType: RequestTaskDTO['type']): string {
  switch (requestType) {
    case 'permit-issuance':
      return 'Permit peer review';
    case 'permit-variation':
      return 'Variation peer review';
    case 'permit-surrender':
      return 'Surrender permit determination';
    case 'permit-revocation':
      return 'Permit Revocation';
    case 'permit-notification':
      return 'Permit Notification';
    case 'permit-transfer':
      return 'Transfer peer review';
    case 'dre':
      return 'Reportable emissions peer review';
    case 'doal':
      return 'Activity level determination peer review';
    case 'non-compliance':
      return 'Non compliance peer review';
    case 'withholding-allowances':
      return 'Withholding of allowances peer review';
    case 'aviation':
      switch (requestTaskType) {
        case 'EMP_VARIATION_UKETS_REGULATOR_LED_APPLICATION_PEER_REVIEW':
        case 'EMP_VARIATION_CORSIA_REGULATOR_LED_APPLICATION_PEER_REVIEW':
          return 'Peer review vary the emissions monitoring plan';
        case 'EMP_VARIATION_UKETS_APPLICATION_PEER_REVIEW':
        case 'EMP_VARIATION_CORSIA_APPLICATION_PEER_REVIEW':
          return 'Peer review emissions monitoring plan variation';

        case 'AVIATION_NON_COMPLIANCE_DAILY_PENALTY_NOTICE_APPLICATION_PEER_REVIEW':
          return 'Peer review initial penalty: non-compliance';
        case 'AVIATION_NON_COMPLIANCE_NOTICE_OF_INTENT_APPLICATION_PEER_REVIEW':
          return 'Peer review notice of intent: non-compliance';
        case 'AVIATION_NON_COMPLIANCE_CIVIL_PENALTY_APPLICATION_PEER_REVIEW':
          return 'Peer review upload penalty: non-compliance';

        default:
          return 'Peer review emissions monitoring plan application';
      }
    case 'return-of-allowances':
      return 'Peer review return of allowances';

    default:
      return null;
  }
}

export function resolvePeerReviewDecisionUrl(requestType: UrlRequestType, taskId: string): string {
  switch (requestType) {
    case 'permit-issuance':
      return `permit-issuance/${taskId}/review/peer-review-decision`;
    case 'permit-variation':
      return `permit-variation/${taskId}/review/peer-review-decision`;
    case 'permit-surrender':
      return `permit-surrender/${taskId}/review/peer-review-decision`;
    case 'permit-revocation':
      return `permit-revocation/${taskId}/peer-review-decision`;
    case 'permit-notification':
      return `tasks/${taskId}/${requestType}/peer-review/decision`;
    case 'permit-transfer':
      return `permit-transfer/${taskId}/review/peer-review-decision`;
    case 'dre':
      return `dre/${taskId}/peer-review/decision`;
    case 'doal':
      return `doal/${taskId}/peer-review/decision`;
    case 'withholding-allowances':
      return `tasks/${taskId}/withholding-allowances/peer-review/decision`;
    case 'aviation':
      return `aviation/tasks/${taskId}/peer-review-decision`;

    default:
      return null;
  }
}

export function resolvePeerReviewBaseUrl(requestType: UrlRequestType, taskId: string): string {
  switch (requestType) {
    case 'permit-issuance':
      return `permit-issuance/${taskId}/review`;
    case 'permit-variation':
      return `permit-variation/${taskId}/review`;
    case 'permit-surrender':
      return `permit-surrender/${taskId}/review`;
    case 'permit-revocation':
      return `permit-revocation/${taskId}`;
    case 'permit-notification':
      return `tasks/${taskId}/${requestType}/peer-review`;
    case 'permit-transfer':
      return `permit-transfer/${taskId}/review`;
    case 'withholding-allowances':
      return `tasks/${taskId}/withholding-allowances/submit`;
    case 'aviation':
      return `aviation/tasks/${taskId}`;

    default:
      return null;
  }
}
