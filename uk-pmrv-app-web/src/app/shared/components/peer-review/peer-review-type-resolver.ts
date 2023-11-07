import { requestTaskTypeToPeerReviewRequestTaskType } from '@shared/components/peer-review/peer-review.utils';

import { ItemDTO, RequestTaskActionPayload, RequestTaskActionProcessDTO, RequestTaskDTO } from 'pmrv-api';

import { UrlRequestType } from '../../types/url-request-type';

export function resolveRequestTaskActionType(
  requestTaskType: RequestTaskDTO['type'],
): RequestTaskActionProcessDTO['requestTaskActionType'] {
  switch (requestTaskType) {
    case 'PERMIT_ISSUANCE_APPLICATION_REVIEW':
      return 'PERMIT_ISSUANCE_REQUEST_PEER_REVIEW';
    case 'PERMIT_VARIATION_APPLICATION_REVIEW':
      return 'PERMIT_VARIATION_REQUEST_PEER_REVIEW';
    case 'PERMIT_VARIATION_REGULATOR_LED_APPLICATION_SUBMIT':
      return 'PERMIT_VARIATION_REQUEST_PEER_REVIEW_REGULATOR_LED';
    case 'PERMIT_SURRENDER_APPLICATION_REVIEW':
      return 'PERMIT_SURRENDER_REQUEST_PEER_REVIEW';
    case 'PERMIT_REVOCATION_APPLICATION_SUBMIT':
      return 'PERMIT_REVOCATION_REQUEST_PEER_REVIEW';
    case 'PERMIT_NOTIFICATION_APPLICATION_REVIEW':
      return 'PERMIT_NOTIFICATION_REQUEST_PEER_REVIEW';
    case 'PERMIT_TRANSFER_B_APPLICATION_REVIEW':
      return 'PERMIT_TRANSFER_B_REQUEST_PEER_REVIEW';
    case 'DRE_APPLICATION_SUBMIT':
      return 'DRE_REQUEST_PEER_REVIEW';
    case 'DOAL_APPLICATION_SUBMIT':
      return 'DOAL_REQUEST_PEER_REVIEW';
    case 'EMP_ISSUANCE_UKETS_APPLICATION_REVIEW':
      return 'EMP_ISSUANCE_UKETS_REQUEST_PEER_REVIEW';
    case 'EMP_ISSUANCE_CORSIA_APPLICATION_REVIEW':
      return 'EMP_ISSUANCE_CORSIA_REQUEST_PEER_REVIEW';
    case 'EMP_VARIATION_UKETS_APPLICATION_REVIEW':
      return 'EMP_VARIATION_UKETS_REQUEST_PEER_REVIEW';
    case 'EMP_VARIATION_UKETS_REGULATOR_LED_APPLICATION_SUBMIT':
      return 'EMP_VARIATION_UKETS_REQUEST_PEER_REVIEW_REGULATOR_LED';
    case 'EMP_VARIATION_CORSIA_REGULATOR_LED_APPLICATION_SUBMIT':
      return 'EMP_VARIATION_CORSIA_REQUEST_PEER_REVIEW_REGULATOR_LED';
    case 'EMP_VARIATION_CORSIA_APPLICATION_REVIEW':
      return 'EMP_VARIATION_CORSIA_REQUEST_PEER_REVIEW';
    case 'NON_COMPLIANCE_DAILY_PENALTY_NOTICE':
      return 'NON_COMPLIANCE_DAILY_PENALTY_NOTICE_REQUEST_PEER_REVIEW';
    case 'NON_COMPLIANCE_NOTICE_OF_INTENT':
      return 'NON_COMPLIANCE_NOTICE_OF_INTENT_REQUEST_PEER_REVIEW';
    case 'NON_COMPLIANCE_CIVIL_PENALTY':
      return 'NON_COMPLIANCE_CIVIL_PENALTY_REQUEST_PEER_REVIEW';
    case 'AVIATION_DRE_UKETS_APPLICATION_SUBMIT':
      return 'AVIATION_DRE_UKETS_REQUEST_PEER_REVIEW';
    case 'WITHHOLDING_OF_ALLOWANCES_APPLICATION_SUBMIT':
      return 'WITHHOLDING_OF_ALLOWANCES_REQUEST_PEER_REVIEW';
    case 'RETURN_OF_ALLOWANCES_APPLICATION_SUBMIT':
      return 'RETURN_OF_ALLOWANCES_REQUEST_PEER_REVIEW';
    case 'AVIATION_NON_COMPLIANCE_DAILY_PENALTY_NOTICE':
      return 'NON_COMPLIANCE_DAILY_PENALTY_NOTICE_REQUEST_PEER_REVIEW';
    case 'AVIATION_NON_COMPLIANCE_NOTICE_OF_INTENT':
      return 'NON_COMPLIANCE_NOTICE_OF_INTENT_REQUEST_PEER_REVIEW';
    case 'AVIATION_NON_COMPLIANCE_CIVIL_PENALTY':
      return 'NON_COMPLIANCE_CIVIL_PENALTY_REQUEST_PEER_REVIEW';

    default:
      return null;
  }
}

export function resolveRequestTaskActionPayloadType(
  requestTaskType: RequestTaskDTO['type'],
): RequestTaskActionPayload['payloadType'] {
  switch (requestTaskType) {
    case 'PERMIT_ISSUANCE_APPLICATION_REVIEW':
      return 'PERMIT_ISSUANCE_REQUEST_PEER_REVIEW_PAYLOAD';
    case 'PERMIT_VARIATION_APPLICATION_REVIEW':
      return 'PERMIT_VARIATION_REQUEST_PEER_REVIEW_PAYLOAD';
    case 'PERMIT_VARIATION_REGULATOR_LED_APPLICATION_SUBMIT':
      return 'PERMIT_VARIATION_REQUEST_PEER_REVIEW_REGULATOR_LED_PAYLOAD';
    case 'PERMIT_SURRENDER_APPLICATION_REVIEW':
      return 'PERMIT_SURRENDER_REQUEST_PEER_REVIEW_PAYLOAD';
    case 'PERMIT_REVOCATION_APPLICATION_SUBMIT':
      return 'PERMIT_REVOCATION_REQUEST_PEER_REVIEW_PAYLOAD';
    case 'PERMIT_NOTIFICATION_APPLICATION_REVIEW':
      return 'PERMIT_NOTIFICATION_REQUEST_PEER_REVIEW_PAYLOAD';
    case 'PERMIT_TRANSFER_B_APPLICATION_REVIEW':
      return 'PERMIT_TRANSFER_B_REQUEST_PEER_REVIEW_PAYLOAD';
    case 'DRE_APPLICATION_SUBMIT':
      return 'DRE_REQUEST_PEER_REVIEW_PAYLOAD';
    case 'DOAL_APPLICATION_SUBMIT':
      return 'DOAL_REQUEST_PEER_REVIEW_PAYLOAD';
    case 'EMP_ISSUANCE_UKETS_APPLICATION_REVIEW':
      return 'EMP_ISSUANCE_UKETS_REQUEST_PEER_REVIEW_PAYLOAD';
    case 'EMP_ISSUANCE_CORSIA_APPLICATION_REVIEW':
      return 'EMP_ISSUANCE_CORSIA_REQUEST_PEER_REVIEW_PAYLOAD';
    case 'EMP_VARIATION_UKETS_APPLICATION_REVIEW':
      return 'EMP_VARIATION_UKETS_REQUEST_PEER_REVIEW_PAYLOAD';
    case 'EMP_VARIATION_UKETS_REGULATOR_LED_APPLICATION_SUBMIT':
      return 'EMP_VARIATION_UKETS_REQUEST_PEER_REVIEW_REGULATOR_LED_PAYLOAD';
    case 'EMP_VARIATION_CORSIA_REGULATOR_LED_APPLICATION_SUBMIT':
      return 'EMP_VARIATION_CORSIA_REQUEST_PEER_REVIEW_REGULATOR_LED_PAYLOAD';
    case 'EMP_VARIATION_CORSIA_APPLICATION_REVIEW':
      return 'EMP_VARIATION_CORSIA_REQUEST_PEER_REVIEW_PAYLOAD';
    case 'NON_COMPLIANCE_DAILY_PENALTY_NOTICE':
      return 'NON_COMPLIANCE_DAILY_PENALTY_NOTICE_REQUEST_PEER_REVIEW_PAYLOAD';
    case 'NON_COMPLIANCE_NOTICE_OF_INTENT':
      return 'NON_COMPLIANCE_NOTICE_OF_INTENT_REQUEST_PEER_REVIEW_PAYLOAD';
    case 'NON_COMPLIANCE_CIVIL_PENALTY':
      return 'NON_COMPLIANCE_CIVIL_PENALTY_REQUEST_PEER_REVIEW_PAYLOAD';
    case 'AVIATION_DRE_UKETS_APPLICATION_SUBMIT':
      return 'AVIATION_DRE_UKETS_REQUEST_PEER_REVIEW_PAYLOAD';
    case 'WITHHOLDING_OF_ALLOWANCES_APPLICATION_SUBMIT':
      return 'WITHHOLDING_OF_ALLOWANCES_REQUEST_PEER_REVIEW_PAYLOAD';
    case 'RETURN_OF_ALLOWANCES_APPLICATION_SUBMIT':
      return 'RETURN_OF_ALLOWANCES_REQUEST_PEER_REVIEW_PAYLOAD';
    case 'AVIATION_NON_COMPLIANCE_DAILY_PENALTY_NOTICE':
      return 'NON_COMPLIANCE_DAILY_PENALTY_NOTICE_REQUEST_PEER_REVIEW_PAYLOAD';
    case 'AVIATION_NON_COMPLIANCE_NOTICE_OF_INTENT':
      return 'NON_COMPLIANCE_NOTICE_OF_INTENT_REQUEST_PEER_REVIEW_PAYLOAD';
    case 'AVIATION_NON_COMPLIANCE_CIVIL_PENALTY':
      return 'NON_COMPLIANCE_CIVIL_PENALTY_REQUEST_PEER_REVIEW_PAYLOAD';

    default:
      return null;
  }
}

export function resolveWaitActionTypes(requestTaskType: RequestTaskDTO['type']): ItemDTO['taskType'][] {
  switch (requestTaskType) {
    case 'PERMIT_ISSUANCE_APPLICATION_REVIEW':
      return ['PERMIT_ISSUANCE_WAIT_FOR_RFI_RESPONSE'];
    case 'PERMIT_VARIATION_APPLICATION_REVIEW':
      return ['PERMIT_VARIATION_WAIT_FOR_RFI_RESPONSE'];
    case 'PERMIT_SURRENDER_APPLICATION_REVIEW':
      return ['PERMIT_SURRENDER_WAIT_FOR_RFI_RESPONSE'];
    case 'PERMIT_NOTIFICATION_APPLICATION_REVIEW':
      return ['PERMIT_NOTIFICATION_WAIT_FOR_RFI_RESPONSE'];
    case 'PERMIT_TRANSFER_B_APPLICATION_REVIEW':
      return ['PERMIT_TRANSFER_B_WAIT_FOR_RFI_RESPONSE'];
    case 'EMP_ISSUANCE_UKETS_APPLICATION_REVIEW':
      return ['EMP_ISSUANCE_UKETS_WAIT_FOR_RFI_RESPONSE'];
    case 'EMP_VARIATION_UKETS_APPLICATION_REVIEW':
      return ['EMP_VARIATION_UKETS_WAIT_FOR_RFI_RESPONSE'];
    case 'EMP_VARIATION_CORSIA_APPLICATION_REVIEW':
      return ['EMP_VARIATION_CORSIA_WAIT_FOR_RFI_RESPONSE'];
    default:
      return [];
  }
}

export function resolveReturnToText(
  requestType: UrlRequestType,
  requestTaskType: RequestTaskDTO['type'],
): { text: string; link: any } {
  switch (requestType) {
    case 'permit-issuance':
      return { text: 'Permit Determination', link: '..' };
    case 'permit-variation':
      return { text: 'Permit Variation', link: '..' };
    case 'permit-surrender':
      return { text: 'Permit Surrender', link: '..' };
    case 'permit-revocation':
      return { text: 'Permit Revocation', link: '..' };
    case 'permit-notification':
      return { text: 'Permit Notification', link: '..' };
    case 'permit-transfer':
      return { text: 'Permit Transfer', link: '..' };
    case 'dre':
      return { text: 'Reportable emissions', link: '..' };
    case 'doal':
      return { text: 'Determination of activity level change', link: '..' };
    case 'aviation': {
      switch (requestTaskType) {
        case 'EMP_VARIATION_UKETS_REGULATOR_LED_APPLICATION_SUBMIT':
        case 'EMP_VARIATION_CORSIA_REGULATOR_LED_APPLICATION_SUBMIT':
          return { text: 'Vary the emissions monitoring plan', link: '../../..' };
        case 'EMP_VARIATION_UKETS_APPLICATION_REVIEW':
          return { text: 'Review emissions monitoring plan variation', link: '../../..' };
        case 'EMP_VARIATION_CORSIA_APPLICATION_REVIEW':
          return { text: 'Review emissions monitoring plan variation', link: '../../../..' };
        case 'AVIATION_NON_COMPLIANCE_DAILY_PENALTY_NOTICE':
          return { text: 'Initial penalty notice', link: '../../..' };
        case 'AVIATION_NON_COMPLIANCE_NOTICE_OF_INTENT':
          return { text: 'Notice of intent', link: '../../..' };
        case 'AVIATION_NON_COMPLIANCE_CIVIL_PENALTY':
          return { text: 'Penalty notice', link: '../../..' };

        default:
          return { text: 'Review emissions monitoring plan application', link: '../../..' };
      }
    }
    case 'non-compliance':
      return { text: 'Non compliance', link: '..' };
    case 'withholding-allowances':
      return { text: 'Withholding of allowances', link: '..' };
    case 'return-of-allowances':
      return { text: 'Return of allowances', link: '..' };
    default:
      return null;
  }
}

export function resolvePeerReviewTaskType(taskType: RequestTaskDTO['type']): RequestTaskDTO['type'] {
  return requestTaskTypeToPeerReviewRequestTaskType[taskType];
}
