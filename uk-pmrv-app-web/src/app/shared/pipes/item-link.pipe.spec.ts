import { ItemDTO } from 'pmrv-api';

import { ItemLinkPipe } from './item-link.pipe';

type DatasetDTO = Pick<ItemDTO, 'requestType' | 'taskType'> & { expectedPath: (string | number)[] };

describe('ItemLinkPipe', () => {
  const pipe = new ItemLinkPipe();

  const taskId = 1;

  const dataSet: DatasetDTO[] = [
    // INSTALLATION_ACCOUNT_OPENING
    {
      requestType: 'INSTALLATION_ACCOUNT_OPENING',
      taskType: null,
      expectedPath: ['/installation-account', taskId],
    },

    // SYSTEM_MESSAGE_NOTIFICATION
    { requestType: 'SYSTEM_MESSAGE_NOTIFICATION', taskType: null, expectedPath: ['/message', taskId] },

    // PERMIT_ISSUANCE
    {
      requestType: 'PERMIT_ISSUANCE',
      taskType: 'PERMIT_ISSUANCE_WAIT_FOR_REVIEW',
      expectedPath: ['/permit-issuance', taskId, 'review'],
    },
    {
      requestType: 'PERMIT_ISSUANCE',
      taskType: 'PERMIT_ISSUANCE_APPLICATION_REVIEW',
      expectedPath: ['/permit-issuance', taskId, 'review'],
    },
    {
      requestType: 'PERMIT_ISSUANCE',
      taskType: 'PERMIT_ISSUANCE_WAIT_FOR_PEER_REVIEW',
      expectedPath: ['/permit-issuance', taskId, 'review'],
    },
    {
      requestType: 'PERMIT_ISSUANCE',
      taskType: 'PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW',
      expectedPath: ['/permit-issuance', taskId, 'review'],
    },
    {
      requestType: 'PERMIT_ISSUANCE',
      taskType: 'PERMIT_ISSUANCE_WAIT_FOR_AMENDS',
      expectedPath: ['/permit-issuance', taskId, 'review'],
    },
    {
      requestType: 'PERMIT_ISSUANCE',
      taskType: null,
      expectedPath: ['/permit-issuance', taskId],
    },
    {
      requestType: 'PERMIT_ISSUANCE',
      taskType: 'PERMIT_ISSUANCE_WAIT_FOR_RDE_RESPONSE',
      expectedPath: ['/rde', taskId, 'manual-approval'],
    },
    {
      requestType: 'PERMIT_ISSUANCE',
      taskType: 'PERMIT_ISSUANCE_MAKE_PAYMENT',
      expectedPath: ['/payment', taskId, 'make', 'details'],
    },
    {
      requestType: 'PERMIT_ISSUANCE',
      taskType: 'PERMIT_ISSUANCE_TRACK_PAYMENT',
      expectedPath: ['/payment', taskId, 'track'],
    },
    {
      requestType: 'PERMIT_ISSUANCE',
      taskType: 'PERMIT_ISSUANCE_CONFIRM_PAYMENT',
      expectedPath: ['/payment', taskId, 'track'],
    },

    // PERMIT_REVOCATION
    {
      requestType: 'PERMIT_REVOCATION',
      taskType: 'PERMIT_REVOCATION_MAKE_PAYMENT',
      expectedPath: ['/payment', taskId, 'make', 'details'],
    },
    {
      requestType: 'PERMIT_REVOCATION',
      taskType: 'PERMIT_REVOCATION_TRACK_PAYMENT',
      expectedPath: ['/payment', taskId, 'track'],
    },
    {
      requestType: 'PERMIT_REVOCATION',
      taskType: 'PERMIT_REVOCATION_CONFIRM_PAYMENT',
      expectedPath: ['/payment', taskId, 'track'],
    },

    // PERMIT SURRENDER
    {
      requestType: 'PERMIT_SURRENDER',
      taskType: 'PERMIT_SURRENDER_APPLICATION_SUBMIT',
      expectedPath: ['/permit-surrender', taskId],
    },
    {
      requestType: 'PERMIT_SURRENDER',
      taskType: 'PERMIT_SURRENDER_APPLICATION_REVIEW',
      expectedPath: ['/permit-surrender', taskId, 'review'],
    },
    {
      requestType: 'PERMIT_SURRENDER',
      taskType: 'PERMIT_SURRENDER_WAIT_FOR_REVIEW',
      expectedPath: ['/permit-surrender', taskId, 'review', 'wait'],
    },
    {
      requestType: 'PERMIT_SURRENDER',
      taskType: 'PERMIT_SURRENDER_CESSATION_SUBMIT',
      expectedPath: ['/permit-surrender', taskId, 'cessation'],
    },
    {
      requestType: 'PERMIT_SURRENDER',
      taskType: 'PERMIT_SURRENDER_RFI_RESPONSE_SUBMIT',
      expectedPath: ['/rfi', taskId, 'responses'],
    },
    {
      requestType: 'PERMIT_SURRENDER',
      taskType: 'PERMIT_SURRENDER_RFI_RESPONSE_SUBMIT',
      expectedPath: ['/rfi', taskId, 'responses'],
    },
    {
      requestType: 'PERMIT_SURRENDER',
      taskType: 'PERMIT_SURRENDER_RDE_RESPONSE_SUBMIT',
      expectedPath: ['/rde', taskId, 'responses'],
    },
    {
      requestType: 'PERMIT_SURRENDER',
      taskType: 'PERMIT_SURRENDER_WAIT_FOR_RDE_RESPONSE',
      expectedPath: ['/rde', taskId, 'manual-approval'],
    },
    {
      requestType: 'PERMIT_SURRENDER',
      taskType: 'PERMIT_SURRENDER_MAKE_PAYMENT',
      expectedPath: ['/payment', taskId, 'make', 'details'],
    },
    {
      requestType: 'PERMIT_SURRENDER',
      taskType: 'PERMIT_SURRENDER_TRACK_PAYMENT',
      expectedPath: ['/payment', taskId, 'track'],
    },
    {
      requestType: 'PERMIT_SURRENDER',
      taskType: 'PERMIT_SURRENDER_CONFIRM_PAYMENT',
      expectedPath: ['/payment', taskId, 'track'],
    },

    // PERMIT_NOTIFICATION
    {
      requestType: 'PERMIT_NOTIFICATION',
      taskType: 'PERMIT_NOTIFICATION_APPLICATION_SUBMIT',
      expectedPath: ['/tasks', taskId, 'permit-notification', 'submit'],
    },
    {
      requestType: 'PERMIT_NOTIFICATION',
      taskType: 'PERMIT_NOTIFICATION_APPLICATION_REVIEW',
      expectedPath: ['/tasks', taskId, 'permit-notification', 'review'],
    },
    {
      requestType: 'PERMIT_NOTIFICATION',
      taskType: 'PERMIT_NOTIFICATION_WAIT_FOR_REVIEW',
      expectedPath: ['/tasks', taskId, 'permit-notification', 'review-wait'],
    },
    {
      requestType: 'PERMIT_NOTIFICATION',
      taskType: 'PERMIT_NOTIFICATION_WAIT_FOR_PEER_REVIEW',
      expectedPath: ['/tasks', taskId, 'permit-notification', 'peer-review-wait'],
    },
    {
      requestType: 'PERMIT_NOTIFICATION',
      taskType: 'PERMIT_NOTIFICATION_APPLICATION_PEER_REVIEW',
      expectedPath: ['/tasks', taskId, 'permit-notification', 'peer-review'],
    },
    {
      requestType: 'PERMIT_NOTIFICATION',
      taskType: 'PERMIT_NOTIFICATION_FOLLOW_UP',
      expectedPath: ['/tasks', taskId, 'permit-notification', 'follow-up'],
    },
    {
      requestType: 'PERMIT_NOTIFICATION',
      taskType: 'PERMIT_NOTIFICATION_WAIT_FOR_FOLLOW_UP',
      expectedPath: ['/tasks', taskId, 'permit-notification', 'follow-up', 'wait'],
    },
    {
      requestType: 'PERMIT_NOTIFICATION',
      taskType: 'PERMIT_NOTIFICATION_RFI_RESPONSE_SUBMIT',
      expectedPath: ['/rfi', taskId, 'responses'],
    },
    {
      requestType: 'PERMIT_NOTIFICATION',
      taskType: 'PERMIT_NOTIFICATION_WAIT_FOR_RFI_RESPONSE',
      expectedPath: ['/rfi', taskId, 'wait'],
    },
    {
      requestType: 'PERMIT_NOTIFICATION',
      taskType: 'PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_REVIEW',
      expectedPath: ['/tasks', taskId, 'permit-notification', 'follow-up', 'review'],
    },
    {
      requestType: 'PERMIT_NOTIFICATION',
      taskType: 'PERMIT_NOTIFICATION_FOLLOW_UP_WAIT_FOR_REVIEW',
      expectedPath: ['/tasks', taskId, 'permit-notification', 'follow-up', 'review-wait'],
    },
    {
      requestType: 'PERMIT_NOTIFICATION',
      taskType: 'PERMIT_NOTIFICATION_FOLLOW_UP_WAIT_FOR_AMENDS',
      expectedPath: ['/tasks', taskId, 'permit-notification', 'follow-up', 'wait'],
    },

    // PERMIT_VARIATION
    {
      requestType: 'PERMIT_VARIATION',
      taskType: 'PERMIT_VARIATION_REGULATOR_LED_APPLICATION_SUBMIT',
      expectedPath: ['/permit-variation', taskId, 'review'],
    },
    {
      requestType: 'PERMIT_VARIATION',
      taskType: 'PERMIT_VARIATION_APPLICATION_SUBMIT',
      expectedPath: ['/permit-variation', taskId],
    },
    {
      requestType: 'PERMIT_VARIATION',
      taskType: 'PERMIT_VARIATION_APPLICATION_REVIEW',
      expectedPath: ['/permit-variation', taskId, 'review'],
    },
    {
      requestType: 'PERMIT_VARIATION',
      taskType: 'PERMIT_VARIATION_WAIT_FOR_REVIEW',
      expectedPath: ['/permit-variation', taskId, 'review'],
    },
    {
      requestType: 'PERMIT_VARIATION',
      taskType: 'PERMIT_VARIATION_MAKE_PAYMENT',
      expectedPath: ['/payment', taskId, 'make', 'details'],
    },
    {
      requestType: 'PERMIT_VARIATION',
      taskType: 'PERMIT_VARIATION_TRACK_PAYMENT',
      expectedPath: ['/payment', taskId, 'track'],
    },
    {
      requestType: 'PERMIT_VARIATION',
      taskType: 'PERMIT_VARIATION_CONFIRM_PAYMENT',
      expectedPath: ['/payment', taskId, 'track'],
    },
    {
      requestType: 'PERMIT_VARIATION',
      taskType: 'PERMIT_VARIATION_APPLICATION_PEER_REVIEW',
      expectedPath: ['/permit-variation', taskId, 'review'],
    },
    {
      requestType: 'PERMIT_VARIATION',
      taskType: 'PERMIT_VARIATION_WAIT_FOR_PEER_REVIEW',
      expectedPath: ['/permit-variation', taskId, 'review'],
    },
    {
      requestType: 'PERMIT_VARIATION',
      taskType: 'PERMIT_VARIATION_REGULATOR_LED_APPLICATION_PEER_REVIEW',
      expectedPath: ['/permit-variation', taskId, 'review'],
    },
    {
      requestType: 'PERMIT_VARIATION',
      taskType: 'PERMIT_VARIATION_REGULATOR_LED_WAIT_FOR_PEER_REVIEW',
      expectedPath: ['/permit-variation', taskId, 'review'],
    },
    {
      requestType: 'PERMIT_VARIATION',
      taskType: 'PERMIT_VARIATION_REGULATOR_LED_MAKE_PAYMENT',
      expectedPath: ['/payment', taskId, 'make', 'details'],
    },
    {
      requestType: 'PERMIT_VARIATION',
      taskType: 'PERMIT_VARIATION_REGULATOR_LED_TRACK_PAYMENT',
      expectedPath: ['/payment', taskId, 'track'],
    },
    {
      requestType: 'PERMIT_VARIATION',
      taskType: 'PERMIT_VARIATION_REGULATOR_LED_CONFIRM_PAYMENT',
      expectedPath: ['/payment', taskId, 'track'],
    },

    // PERMIT_TRANSFER_A
    {
      requestType: 'PERMIT_TRANSFER_A',
      taskType: 'PERMIT_TRANSFER_A_APPLICATION_SUBMIT',
      expectedPath: ['/tasks', taskId, 'permit-transfer-a', 'submit'],
    },
    {
      requestType: 'PERMIT_TRANSFER_A',
      taskType: 'PERMIT_TRANSFER_A_WAIT_FOR_TRANSFER',
      expectedPath: ['/tasks', taskId, 'permit-transfer-a', 'wait'],
    },
    {
      requestType: 'PERMIT_TRANSFER_A',
      taskType: 'PERMIT_TRANSFER_A_MAKE_PAYMENT',
      expectedPath: ['/payment', taskId, 'make', 'details'],
    },
    {
      requestType: 'PERMIT_TRANSFER_A',
      taskType: 'PERMIT_TRANSFER_A_TRACK_PAYMENT',
      expectedPath: ['/payment', taskId, 'track'],
    },
    {
      requestType: 'PERMIT_TRANSFER_A',
      taskType: 'PERMIT_TRANSFER_A_CONFIRM_PAYMENT',
      expectedPath: ['/payment', taskId, 'track'],
    },

    // PERMIT_TRANSFER_B
    {
      requestType: 'PERMIT_TRANSFER_B',
      taskType: 'PERMIT_TRANSFER_B_APPLICATION_SUBMIT',
      expectedPath: ['/permit-transfer', taskId],
    },
    {
      requestType: 'PERMIT_TRANSFER_B',
      taskType: 'PERMIT_TRANSFER_B_APPLICATION_AMENDS_SUBMIT',
      expectedPath: ['/permit-transfer', taskId],
    },
    {
      requestType: 'PERMIT_TRANSFER_B',
      taskType: 'PERMIT_TRANSFER_B_MAKE_PAYMENT',
      expectedPath: ['/payment', taskId, 'make', 'details'],
    },
    {
      requestType: 'PERMIT_TRANSFER_B',
      taskType: 'PERMIT_TRANSFER_B_TRACK_PAYMENT',
      expectedPath: ['/payment', taskId, 'track'],
    },
    {
      requestType: 'PERMIT_TRANSFER_B',
      taskType: 'PERMIT_TRANSFER_B_CONFIRM_PAYMENT',
      expectedPath: ['/payment', taskId, 'track'],
    },
    {
      requestType: 'PERMIT_TRANSFER_B',
      taskType: 'PERMIT_TRANSFER_B_APPLICATION_REVIEW',
      expectedPath: ['/permit-transfer', taskId, 'review'],
    },
    {
      requestType: 'PERMIT_TRANSFER_B',
      taskType: 'PERMIT_TRANSFER_B_WAIT_FOR_REVIEW',
      expectedPath: ['/permit-transfer', taskId, 'review'],
    },
    {
      requestType: 'PERMIT_TRANSFER_B',
      taskType: 'PERMIT_TRANSFER_B_RFI_RESPONSE_SUBMIT',
      expectedPath: ['/rfi', taskId, 'responses'],
    },
    {
      requestType: 'PERMIT_TRANSFER_B',
      taskType: 'PERMIT_TRANSFER_B_WAIT_FOR_RFI_RESPONSE',
      expectedPath: ['/rfi', taskId, 'wait'],
    },
    {
      requestType: 'PERMIT_TRANSFER_B',
      taskType: 'PERMIT_TRANSFER_B_RDE_RESPONSE_SUBMIT',
      expectedPath: ['/rde', taskId, 'responses'],
    },
    {
      requestType: 'PERMIT_TRANSFER_B',
      taskType: 'PERMIT_TRANSFER_B_WAIT_FOR_RDE_RESPONSE',
      expectedPath: ['/rde', taskId, 'manual-approval'],
    },
    {
      requestType: 'PERMIT_TRANSFER_B',
      taskType: 'PERMIT_TRANSFER_B_WAIT_FOR_AMENDS',
      expectedPath: ['/permit-transfer', taskId, 'review'],
    },
    {
      requestType: 'PERMIT_TRANSFER_B',
      taskType: 'PERMIT_TRANSFER_B_WAIT_FOR_PEER_REVIEW',
      expectedPath: ['/permit-transfer', taskId, 'review'],
    },
    {
      requestType: 'PERMIT_TRANSFER_B',
      taskType: 'PERMIT_TRANSFER_B_APPLICATION_PEER_REVIEW',
      expectedPath: ['/permit-transfer', taskId, 'review'],
    },

    // AER
    {
      requestType: 'AER',
      taskType: 'AER_APPLICATION_SUBMIT',
      expectedPath: ['/tasks', taskId, 'aer', 'submit'],
    },
    {
      requestType: 'AER',
      taskType: 'AER_WAIT_FOR_VERIFICATION',
      expectedPath: ['/tasks', taskId, 'aer', 'verification-wait'],
    },
    {
      requestType: 'AER',
      taskType: 'AER_WAIT_FOR_REVIEW',
      expectedPath: ['/tasks', taskId, 'aer', 'review-wait'],
    },
    {
      requestType: 'AER',
      taskType: 'AER_APPLICATION_VERIFICATION_SUBMIT',
      expectedPath: ['/tasks', taskId, 'aer', 'verification-submit'],
    },
    {
      requestType: 'AER',
      taskType: 'AER_APPLICATION_REVIEW',
      expectedPath: ['/tasks', taskId, 'aer', 'review'],
    },

    // VIR
    {
      requestType: 'VIR',
      taskType: 'VIR_APPLICATION_SUBMIT',
      expectedPath: ['/tasks', taskId, 'vir', 'submit'],
    },
    {
      requestType: 'VIR',
      taskType: 'VIR_APPLICATION_REVIEW',
      expectedPath: ['/tasks', taskId, 'vir', 'review'],
    },
    {
      requestType: 'VIR',
      taskType: 'VIR_WAIT_FOR_REVIEW',
      expectedPath: ['/tasks', taskId, 'vir', 'review-wait'],
    },
    {
      requestType: 'VIR',
      taskType: 'VIR_RESPOND_TO_REGULATOR_COMMENTS',
      expectedPath: ['/tasks', taskId, 'vir', 'comments-response'],
    },
    {
      requestType: 'VIR',
      taskType: 'VIR_WAIT_FOR_RFI_RESPONSE',
      expectedPath: ['/rfi', taskId, 'wait'],
    },
    {
      requestType: 'VIR',
      taskType: 'VIR_RFI_RESPONSE_SUBMIT',
      expectedPath: ['/rfi', taskId, 'responses'],
    },

    // AIR
    {
      requestType: 'AIR',
      taskType: 'AIR_APPLICATION_SUBMIT',
      expectedPath: ['/tasks', taskId, 'air', 'submit'],
    },
    {
      requestType: 'AIR',
      taskType: 'AIR_APPLICATION_REVIEW',
      expectedPath: ['/tasks', taskId, 'air', 'review'],
    },
    {
      requestType: 'AIR',
      taskType: 'AIR_WAIT_FOR_REVIEW',
      expectedPath: ['/tasks', taskId, 'air', 'review-wait'],
    },
    {
      requestType: 'AIR',
      taskType: 'AIR_RESPOND_TO_REGULATOR_COMMENTS',
      expectedPath: ['/tasks', taskId, 'air', 'comments-response'],
    },
    {
      requestType: 'AIR',
      taskType: 'AIR_WAIT_FOR_RFI_RESPONSE',
      expectedPath: ['/rfi', taskId, 'wait'],
    },
    {
      requestType: 'AIR',
      taskType: 'AIR_RFI_RESPONSE_SUBMIT',
      expectedPath: ['/rfi', taskId, 'responses'],
    },

    // DRE
    {
      requestType: 'DRE',
      taskType: 'DRE_APPLICATION_SUBMIT',
      expectedPath: ['/tasks', taskId, 'dre', 'submit'],
    },
    {
      requestType: 'DRE',
      taskType: 'DRE_MAKE_PAYMENT',
      expectedPath: ['/payment', taskId, 'make', 'details'],
    },
    {
      requestType: 'DRE',
      taskType: 'DRE_TRACK_PAYMENT',
      expectedPath: ['/payment', taskId, 'track'],
    },
    {
      requestType: 'DRE',
      taskType: 'DRE_CONFIRM_PAYMENT',
      expectedPath: ['/payment', taskId, 'track'],
    },
    {
      requestType: 'DRE',
      taskType: 'DRE_WAIT_FOR_PEER_REVIEW',
      expectedPath: ['/tasks', taskId, 'dre', 'peer-review-wait'],
    },
    {
      requestType: 'DRE',
      taskType: 'DRE_APPLICATION_PEER_REVIEW',
      expectedPath: ['/tasks', taskId, 'dre', 'peer-review'],
    },

    // NON_COMPLIANCE
    {
      requestType: 'NON_COMPLIANCE',
      taskType: 'NON_COMPLIANCE_APPLICATION_SUBMIT',
      expectedPath: ['/tasks', taskId, 'non-compliance', 'submit'],
    },
    {
      requestType: 'NON_COMPLIANCE',
      taskType: 'NON_COMPLIANCE_DAILY_PENALTY_NOTICE',
      expectedPath: ['/tasks', taskId, 'non-compliance', 'daily-penalty-notice'],
    },
    {
      requestType: 'NON_COMPLIANCE',
      taskType: 'NON_COMPLIANCE_DAILY_PENALTY_NOTICE_WAIT_FOR_PEER_REVIEW',
      expectedPath: ['/tasks', taskId, 'non-compliance', 'daily-penalty-notice', 'peer-review-wait'],
    },
    {
      requestType: 'NON_COMPLIANCE',
      taskType: 'NON_COMPLIANCE_DAILY_PENALTY_NOTICE_APPLICATION_PEER_REVIEW',
      expectedPath: ['/tasks', taskId, 'non-compliance', 'daily-penalty-notice', 'dpn-peer-review'],
    },
    {
      requestType: 'NON_COMPLIANCE',
      taskType: 'NON_COMPLIANCE_NOTICE_OF_INTENT',
      expectedPath: ['/tasks', taskId, 'non-compliance', 'notice-of-intent'],
    },
    {
      requestType: 'NON_COMPLIANCE',
      taskType: 'NON_COMPLIANCE_NOTICE_OF_INTENT_WAIT_FOR_PEER_REVIEW',
      expectedPath: ['/tasks', taskId, 'non-compliance', 'notice-of-intent', 'peer-review-wait'],
    },
    {
      requestType: 'NON_COMPLIANCE',
      taskType: 'NON_COMPLIANCE_NOTICE_OF_INTENT_APPLICATION_PEER_REVIEW',
      expectedPath: ['/tasks', taskId, 'non-compliance', 'notice-of-intent', 'noi-peer-review'],
    },
    {
      requestType: 'NON_COMPLIANCE',
      taskType: 'NON_COMPLIANCE_CIVIL_PENALTY',
      expectedPath: ['/tasks', taskId, 'non-compliance', 'civil-penalty-notice'],
    },
    {
      requestType: 'NON_COMPLIANCE',
      taskType: 'NON_COMPLIANCE_CIVIL_PENALTY_WAIT_FOR_PEER_REVIEW',
      expectedPath: ['/tasks', taskId, 'non-compliance', 'civil-penalty-notice', 'peer-review-wait'],
    },
    {
      requestType: 'NON_COMPLIANCE',
      taskType: 'NON_COMPLIANCE_CIVIL_PENALTY_APPLICATION_PEER_REVIEW',
      expectedPath: ['/tasks', taskId, 'non-compliance', 'civil-penalty-notice', 'cpn-peer-review'],
    },

    {
      requestType: 'DOAL',
      taskType: 'DOAL_APPLICATION_SUBMIT',
      expectedPath: ['/tasks', taskId, 'doal', 'submit'],
    },
    {
      requestType: 'DOAL',
      taskType: 'DOAL_AUTHORITY_RESPONSE',
      expectedPath: ['/tasks', taskId, 'doal', 'authority-response'],
    },
    {
      requestType: 'DOAL',
      taskType: 'DOAL_WAIT_FOR_PEER_REVIEW',
      expectedPath: ['/tasks', taskId, 'doal', 'peer-review-wait'],
    },
    {
      requestType: 'DOAL',
      taskType: 'DOAL_APPLICATION_PEER_REVIEW',
      expectedPath: ['/tasks', taskId, 'doal', 'peer-review'],
    },

    // EMP_ISSUANCE_CORSIA
    {
      requestType: 'EMP_ISSUANCE_CORSIA',
      taskType: 'EMP_ISSUANCE_CORSIA_APPLICATION_SUBMIT',
      expectedPath: ['/aviation/tasks', taskId],
    },
    {
      requestType: 'EMP_ISSUANCE_CORSIA',
      taskType: 'EMP_ISSUANCE_CORSIA_APPLICATION_REVIEW',
      expectedPath: ['/aviation/tasks', taskId],
    },
    {
      requestType: 'EMP_VARIATION_CORSIA',
      taskType: 'EMP_VARIATION_CORSIA_WAIT_FOR_REVIEW',
      expectedPath: ['/aviation/tasks', taskId],
    },
    {
      requestType: 'EMP_ISSUANCE_CORSIA',
      taskType: 'EMP_ISSUANCE_CORSIA_MAKE_PAYMENT',
      expectedPath: ['/aviation/', 'payment', taskId, 'make', 'details'],
    },
    {
      requestType: 'EMP_ISSUANCE_CORSIA',
      taskType: 'EMP_ISSUANCE_CORSIA_TRACK_PAYMENT',
      expectedPath: ['/aviation/', 'payment', taskId, 'track'],
    },
    {
      requestType: 'EMP_ISSUANCE_CORSIA',
      taskType: 'EMP_ISSUANCE_CORSIA_CONFIRM_PAYMENT',
      expectedPath: ['/aviation/', 'payment', taskId, 'track'],
    },
    {
      requestType: 'EMP_ISSUANCE_CORSIA',
      taskType: 'EMP_ISSUANCE_CORSIA_APPLICATION_AMENDS_SUBMIT',
      expectedPath: ['/aviation/tasks', taskId],
    },
    {
      requestType: 'EMP_ISSUANCE_CORSIA',
      taskType: 'EMP_ISSUANCE_CORSIA_WAIT_FOR_AMENDS',
      expectedPath: ['/aviation/tasks', taskId],
    },
    {
      requestType: 'EMP_ISSUANCE_CORSIA',
      taskType: 'EMP_ISSUANCE_CORSIA_APPLICATION_PEER_REVIEW',
      expectedPath: ['/aviation/tasks', taskId],
    },
    {
      requestType: 'EMP_ISSUANCE_CORSIA',
      taskType: 'EMP_ISSUANCE_CORSIA_WAIT_FOR_PEER_REVIEW',
      expectedPath: ['/aviation/tasks', taskId],
    },
    {
      requestType: 'EMP_ISSUANCE_CORSIA',
      taskType: 'EMP_ISSUANCE_CORSIA_RFI_RESPONSE_SUBMIT',
      expectedPath: ['/aviation/rfi', taskId, 'responses'],
    },
    {
      requestType: 'EMP_ISSUANCE_CORSIA',
      taskType: 'EMP_ISSUANCE_CORSIA_WAIT_FOR_RFI_RESPONSE',
      expectedPath: ['/aviation/rfi', taskId, 'wait'],
    },
    {
      requestType: 'EMP_ISSUANCE_CORSIA',
      taskType: 'EMP_ISSUANCE_CORSIA_WAIT_FOR_RDE_RESPONSE',
      expectedPath: ['/aviation/rde', taskId, 'manual-approval'],
    },
    {
      requestType: 'EMP_ISSUANCE_CORSIA',
      taskType: 'EMP_ISSUANCE_CORSIA_RDE_RESPONSE_SUBMIT',
      expectedPath: ['/aviation/rde', taskId, 'responses'],
    },

    // EMP_VARIATION_CORSIA
    {
      requestType: 'EMP_VARIATION_CORSIA',
      taskType: 'EMP_VARIATION_CORSIA_APPLICATION_SUBMIT',
      expectedPath: ['/aviation/tasks', taskId],
    },
    {
      requestType: 'EMP_VARIATION_CORSIA',
      taskType: 'EMP_VARIATION_CORSIA_APPLICATION_REVIEW',
      expectedPath: ['/aviation/tasks', taskId],
    },
    {
      requestType: 'EMP_VARIATION_CORSIA',
      taskType: 'EMP_VARIATION_CORSIA_RFI_RESPONSE_SUBMIT',
      expectedPath: ['/aviation/rfi', taskId, 'responses'],
    },
    {
      requestType: 'EMP_VARIATION_CORSIA',
      taskType: 'EMP_VARIATION_CORSIA_WAIT_FOR_RFI_RESPONSE',
      expectedPath: ['/aviation/rfi', taskId, 'wait'],
    },
    {
      requestType: 'EMP_VARIATION_CORSIA',
      taskType: 'EMP_VARIATION_CORSIA_RDE_RESPONSE_SUBMIT',
      expectedPath: ['/aviation/rde', taskId, 'responses'],
    },
    {
      requestType: 'EMP_VARIATION_CORSIA',
      taskType: 'EMP_VARIATION_CORSIA_WAIT_FOR_RDE_RESPONSE',
      expectedPath: ['/aviation/rde', taskId, 'manual-approval'],
    },
    {
      requestType: 'EMP_VARIATION_CORSIA',
      taskType: 'EMP_VARIATION_CORSIA_WAIT_FOR_PEER_REVIEW',
      expectedPath: ['/aviation/tasks', taskId],
    },
    {
      requestType: 'EMP_VARIATION_CORSIA',
      taskType: 'EMP_VARIATION_CORSIA_APPLICATION_PEER_REVIEW',
      expectedPath: ['/aviation/tasks', taskId],
    },

    // EMP_ISSUANCE_UKETS
    {
      requestType: 'EMP_ISSUANCE_UKETS',
      taskType: 'EMP_ISSUANCE_UKETS_APPLICATION_SUBMIT',
      expectedPath: ['/aviation/tasks', taskId],
    },
    {
      requestType: 'EMP_ISSUANCE_UKETS',
      taskType: 'EMP_ISSUANCE_UKETS_APPLICATION_REVIEW',
      expectedPath: ['/aviation/tasks', taskId],
    },
    {
      requestType: 'EMP_ISSUANCE_UKETS',
      taskType: 'EMP_ISSUANCE_UKETS_WAIT_FOR_REVIEW',
      expectedPath: ['/aviation/tasks', taskId],
    },
    {
      requestType: 'EMP_ISSUANCE_UKETS',
      taskType: 'EMP_ISSUANCE_UKETS_WAIT_FOR_AMENDS',
      expectedPath: ['/aviation/tasks', taskId],
    },
    {
      requestType: 'EMP_ISSUANCE_UKETS',
      taskType: 'EMP_ISSUANCE_UKETS_APPLICATION_AMENDS_SUBMIT',
      expectedPath: ['/aviation/tasks', taskId],
    },
    {
      requestType: 'EMP_ISSUANCE_UKETS',
      taskType: 'EMP_ISSUANCE_UKETS_MAKE_PAYMENT',
      expectedPath: ['/aviation/', 'payment', taskId, 'make', 'details'],
    },
    {
      requestType: 'EMP_ISSUANCE_UKETS',
      taskType: 'EMP_ISSUANCE_UKETS_TRACK_PAYMENT',
      expectedPath: ['/aviation/', 'payment', taskId, 'track'],
    },
    {
      requestType: 'EMP_ISSUANCE_UKETS',
      taskType: 'EMP_ISSUANCE_UKETS_CONFIRM_PAYMENT',
      expectedPath: ['/aviation/', 'payment', taskId, 'track'],
    },
    {
      requestType: 'EMP_ISSUANCE_UKETS',
      taskType: 'EMP_ISSUANCE_UKETS_WAIT_FOR_PEER_REVIEW',
      expectedPath: ['/aviation/tasks', taskId],
    },
    {
      requestType: 'EMP_ISSUANCE_UKETS',
      taskType: 'EMP_ISSUANCE_UKETS_APPLICATION_PEER_REVIEW',
      expectedPath: ['/aviation/tasks', taskId],
    },
    {
      requestType: 'EMP_ISSUANCE_UKETS',
      taskType: 'EMP_ISSUANCE_UKETS_RFI_RESPONSE_SUBMIT',
      expectedPath: ['/aviation/rfi', taskId, 'responses'],
    },
    {
      requestType: 'EMP_ISSUANCE_UKETS',
      taskType: 'EMP_ISSUANCE_UKETS_WAIT_FOR_RFI_RESPONSE',
      expectedPath: ['/aviation/rfi', taskId, 'wait'],
    },
    {
      requestType: 'EMP_ISSUANCE_UKETS',
      taskType: 'EMP_ISSUANCE_UKETS_RDE_RESPONSE_SUBMIT',
      expectedPath: ['/aviation/rde', taskId, 'responses'],
    },
    {
      requestType: 'EMP_ISSUANCE_UKETS',
      taskType: 'EMP_ISSUANCE_UKETS_WAIT_FOR_RDE_RESPONSE',
      expectedPath: ['/aviation/rde', taskId, 'manual-approval'],
    },

    // EMP_VARIATION_UKETS
    {
      requestType: 'EMP_VARIATION_UKETS',
      taskType: 'EMP_VARIATION_UKETS_APPLICATION_SUBMIT',
      expectedPath: ['/aviation/tasks', taskId],
    },
    {
      requestType: 'EMP_VARIATION_UKETS',
      taskType: 'EMP_VARIATION_UKETS_APPLICATION_REVIEW',
      expectedPath: ['/aviation/tasks', taskId],
    },
    {
      requestType: 'EMP_VARIATION_UKETS',
      taskType: 'EMP_VARIATION_UKETS_WAIT_FOR_REVIEW',
      expectedPath: ['/aviation/tasks', taskId],
    },
    {
      requestType: 'EMP_VARIATION_UKETS',
      taskType: 'EMP_VARIATION_UKETS_WAIT_FOR_PEER_REVIEW',
      expectedPath: ['/aviation/tasks', taskId],
    },
    {
      requestType: 'EMP_VARIATION_UKETS',
      taskType: 'EMP_VARIATION_UKETS_APPLICATION_PEER_REVIEW',
      expectedPath: ['/aviation/tasks', taskId],
    },
    {
      requestType: 'EMP_VARIATION_UKETS',
      taskType: 'EMP_VARIATION_UKETS_RFI_RESPONSE_SUBMIT',
      expectedPath: ['/aviation/rfi', taskId, 'responses'],
    },
    {
      requestType: 'EMP_VARIATION_UKETS',
      taskType: 'EMP_VARIATION_UKETS_WAIT_FOR_RFI_RESPONSE',
      expectedPath: ['/aviation/rfi', taskId, 'wait'],
    },
    {
      requestType: 'EMP_VARIATION_UKETS',
      taskType: 'EMP_VARIATION_UKETS_RDE_RESPONSE_SUBMIT',
      expectedPath: ['/aviation/rde', taskId, 'responses'],
    },
    {
      requestType: 'EMP_VARIATION_UKETS',
      taskType: 'EMP_VARIATION_UKETS_WAIT_FOR_RDE_RESPONSE',
      expectedPath: ['/aviation/rde', taskId, 'manual-approval'],
    },
    {
      requestType: 'EMP_VARIATION_UKETS',
      taskType: 'EMP_VARIATION_UKETS_WAIT_FOR_AMENDS',
      expectedPath: ['/aviation/tasks', taskId],
    },
    {
      requestType: 'EMP_VARIATION_UKETS',
      taskType: 'EMP_VARIATION_UKETS_MAKE_PAYMENT',
      expectedPath: ['/aviation/', 'payment', taskId, 'make', 'details'],
    },
    {
      requestType: 'EMP_VARIATION_UKETS',
      taskType: 'EMP_VARIATION_UKETS_TRACK_PAYMENT',
      expectedPath: ['/aviation/', 'payment', taskId, 'track'],
    },
    {
      requestType: 'EMP_VARIATION_UKETS',
      taskType: 'EMP_VARIATION_UKETS_CONFIRM_PAYMENT',
      expectedPath: ['/aviation/', 'payment', taskId, 'track'],
    },
    {
      requestType: 'EMP_VARIATION_UKETS',
      taskType: 'EMP_VARIATION_UKETS_APPLICATION_AMENDS_SUBMIT',
      expectedPath: ['/aviation/tasks', taskId],
    },
    {
      requestType: 'EMP_VARIATION_CORSIA',
      taskType: 'EMP_VARIATION_CORSIA_APPLICATION_AMENDS_SUBMIT',
      expectedPath: ['/aviation/tasks', taskId],
    },
    {
      requestType: 'EMP_VARIATION_UKETS',
      taskType: 'EMP_VARIATION_UKETS_REGULATOR_LED_APPLICATION_SUBMIT',
      expectedPath: ['/aviation/tasks', taskId],
    },
    {
      requestType: 'EMP_VARIATION_CORSIA',
      taskType: 'EMP_VARIATION_CORSIA_REGULATOR_LED_APPLICATION_SUBMIT',
      expectedPath: ['/aviation/tasks', taskId],
    },
    {
      requestType: 'EMP_VARIATION_CORSIA',
      taskType: 'EMP_VARIATION_CORSIA_WAIT_FOR_AMENDS',
      expectedPath: ['/aviation/tasks', taskId],
    },
    {
      requestType: 'EMP_VARIATION_UKETS',
      taskType: 'EMP_VARIATION_UKETS_REGULATOR_LED_WAIT_FOR_PEER_REVIEW',
      expectedPath: ['/aviation/tasks', taskId],
    },
    {
      requestType: 'EMP_VARIATION_UKETS',
      taskType: 'EMP_VARIATION_UKETS_REGULATOR_LED_APPLICATION_PEER_REVIEW',
      expectedPath: ['/aviation/tasks', taskId],
    },
    {
      requestType: 'EMP_VARIATION_CORSIA',
      taskType: 'EMP_VARIATION_CORSIA_REGULATOR_LED_WAIT_FOR_PEER_REVIEW',
      expectedPath: ['/aviation/tasks', taskId],
    },
    {
      requestType: 'EMP_VARIATION_CORSIA',
      taskType: 'EMP_VARIATION_CORSIA_REGULATOR_LED_APPLICATION_PEER_REVIEW',
      expectedPath: ['/aviation/tasks', taskId],
    },
    {
      requestType: 'EMP_VARIATION_UKETS',
      taskType: 'EMP_VARIATION_UKETS_REGULATOR_LED_MAKE_PAYMENT',
      expectedPath: ['/aviation/', 'payment', taskId, 'make', 'details'],
    },
    {
      requestType: 'EMP_VARIATION_UKETS',
      taskType: 'EMP_VARIATION_UKETS_REGULATOR_LED_TRACK_PAYMENT',
      expectedPath: ['/aviation/', 'payment', taskId, 'track'],
    },
    {
      requestType: 'EMP_VARIATION_UKETS',
      taskType: 'EMP_VARIATION_UKETS_REGULATOR_LED_CONFIRM_PAYMENT',
      expectedPath: ['/aviation/', 'payment', taskId, 'track'],
    },
    {
      requestType: 'EMP_VARIATION_CORSIA',
      taskType: 'EMP_VARIATION_CORSIA_REGULATOR_LED_MAKE_PAYMENT',
      expectedPath: ['/aviation/', 'payment', taskId, 'make', 'details'],
    },
    {
      requestType: 'EMP_VARIATION_CORSIA',
      taskType: 'EMP_VARIATION_UKETS_REGULATOR_LED_TRACK_PAYMENT',
      expectedPath: ['/aviation/', 'payment', taskId, 'track'],
    },
    {
      requestType: 'EMP_VARIATION_CORSIA',
      taskType: 'EMP_VARIATION_CORSIA_REGULATOR_LED_CONFIRM_PAYMENT',
      expectedPath: ['/aviation/', 'payment', taskId, 'track'],
    },
    {
      requestType: 'EMP_VARIATION_CORSIA',
      taskType: 'EMP_VARIATION_CORSIA_MAKE_PAYMENT',
      expectedPath: ['/aviation/', 'payment', taskId, 'make', 'details'],
    },
    {
      requestType: 'EMP_VARIATION_CORSIA',
      taskType: 'EMP_VARIATION_CORSIA_TRACK_PAYMENT',
      expectedPath: ['/aviation/', 'payment', taskId, 'track'],
    },
    {
      requestType: 'EMP_VARIATION_CORSIA',
      taskType: 'EMP_VARIATION_CORSIA_CONFIRM_PAYMENT',
      expectedPath: ['/aviation/', 'payment', taskId, 'track'],
    },
    // AVIATION_AER_UKETS
    {
      requestType: 'AVIATION_AER_UKETS',
      taskType: 'AVIATION_AER_UKETS_APPLICATION_SUBMIT',
      expectedPath: ['/aviation/tasks', taskId],
    },
    {
      requestType: 'AVIATION_AER_UKETS',
      taskType: 'AVIATION_AER_UKETS_APPLICATION_VERIFICATION_SUBMIT',
      expectedPath: ['/aviation/tasks', taskId],
    },
    {
      requestType: 'AVIATION_AER_UKETS',
      taskType: 'AVIATION_AER_UKETS_AMEND_APPLICATION_VERIFICATION_SUBMIT',
      expectedPath: ['/aviation/tasks', taskId],
    },
    {
      requestType: 'AVIATION_AER_UKETS',
      taskType: 'AVIATION_AER_UKETS_WAIT_FOR_VERIFICATION',
      expectedPath: ['/aviation/tasks', taskId],
    },
    {
      requestType: 'AVIATION_AER_UKETS',
      taskType: 'AVIATION_AER_UKETS_AMEND_WAIT_FOR_VERIFICATION',
      expectedPath: ['/aviation/tasks', taskId],
    },
    {
      requestType: 'AVIATION_AER_UKETS',
      taskType: 'AVIATION_AER_UKETS_APPLICATION_REVIEW',
      expectedPath: ['/aviation/tasks', taskId],
    },
    {
      requestType: 'AVIATION_AER_UKETS',
      taskType: 'AVIATION_AER_UKETS_WAIT_FOR_REVIEW',
      expectedPath: ['/aviation/tasks', taskId],
    },
    {
      requestType: 'AVIATION_AER_UKETS',
      taskType: 'AVIATION_AER_UKETS_WAIT_FOR_AMENDS',
      expectedPath: ['/aviation/tasks', taskId],
    },
    {
      requestType: 'AVIATION_AER_UKETS',
      taskType: 'AVIATION_AER_UKETS_APPLICATION_AMENDS_SUBMIT',
      expectedPath: ['/aviation/tasks', taskId],
    },

    // AVIATION_DRE_UKETS
    {
      requestType: 'AVIATION_DRE_UKETS',
      taskType: 'AVIATION_DRE_UKETS_APPLICATION_SUBMIT',
      expectedPath: ['/aviation/tasks', taskId],
    },
    {
      requestType: 'AVIATION_DRE_UKETS',
      taskType: 'AVIATION_DRE_UKETS_APPLICATION_PEER_REVIEW',
      expectedPath: ['/aviation/tasks', taskId],
    },
    {
      requestType: 'AVIATION_DRE_UKETS',
      taskType: 'AVIATION_DRE_UKETS_WAIT_FOR_PEER_REVIEW',
      expectedPath: ['/aviation/tasks', taskId],
    },
    {
      requestType: 'AVIATION_DRE_UKETS',
      taskType: 'AVIATION_DRE_UKETS_MAKE_PAYMENT',
      expectedPath: ['/aviation/', 'payment', taskId, 'make', 'details'],
    },
    {
      requestType: 'AVIATION_DRE_UKETS',
      taskType: 'AVIATION_DRE_UKETS_TRACK_PAYMENT',
      expectedPath: ['/aviation/', 'payment', taskId, 'track'],
    },
    {
      requestType: 'AVIATION_DRE_UKETS',
      taskType: 'AVIATION_DRE_UKETS_CONFIRM_PAYMENT',
      expectedPath: ['/aviation/', 'payment', taskId, 'track'],
    },

    // AVIATION_ACCOUNT_CLOSURE
    {
      requestType: 'AVIATION_ACCOUNT_CLOSURE',
      taskType: 'AVIATION_ACCOUNT_CLOSURE_SUBMIT',
      expectedPath: ['/aviation/tasks', taskId],
    },

    // AVIATION_AER_CORSIA
    {
      requestType: 'AVIATION_AER_CORSIA',
      taskType: 'AVIATION_AER_CORSIA_APPLICATION_SUBMIT',
      expectedPath: ['/aviation/tasks', taskId],
    },
    {
      requestType: 'AVIATION_AER_CORSIA',
      taskType: 'AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMIT',
      expectedPath: ['/aviation/tasks', taskId],
    },
    {
      requestType: 'AVIATION_AER_CORSIA',
      taskType: 'AVIATION_AER_CORSIA_WAIT_FOR_VERIFICATION',
      expectedPath: ['/aviation/tasks', taskId],
    },

    // AVIATION_VIR
    {
      requestType: 'AVIATION_VIR',
      taskType: 'AVIATION_VIR_APPLICATION_SUBMIT',
      expectedPath: ['/aviation/tasks', taskId],
    },
    {
      requestType: 'AVIATION_VIR',
      taskType: 'AVIATION_VIR_WAIT_FOR_REVIEW',
      expectedPath: ['/aviation/tasks', taskId],
    },
    {
      requestType: 'AVIATION_VIR',
      taskType: 'AVIATION_VIR_APPLICATION_REVIEW',
      expectedPath: ['/aviation/tasks', taskId],
    },
    {
      requestType: 'AVIATION_VIR',
      taskType: 'AVIATION_VIR_RFI_RESPONSE_SUBMIT',
      expectedPath: ['/aviation/rfi', taskId, 'responses'],
    },
    {
      requestType: 'AVIATION_VIR',
      taskType: 'AVIATION_VIR_WAIT_FOR_RFI_RESPONSE',
      expectedPath: ['/aviation/rfi', taskId, 'wait'],
    },
    {
      requestType: 'AVIATION_VIR',
      taskType: 'AVIATION_VIR_RESPOND_TO_REGULATOR_COMMENTS',
      expectedPath: ['/aviation/tasks', taskId],
    },

    // RETURN OF ALLOWANCES
    {
      requestType: 'RETURN_OF_ALLOWANCES',
      taskType: 'RETURN_OF_ALLOWANCES_APPLICATION_PEER_REVIEW',
      expectedPath: ['/tasks', taskId, 'return-of-allowances', 'roa-peer-review'],
    },
    {
      requestType: 'RETURN_OF_ALLOWANCES',
      taskType: 'RETURN_OF_ALLOWANCES_WAIT_FOR_PEER_REVIEW',
      expectedPath: ['/tasks', taskId, 'return-of-allowances', 'peer-review-wait'],
    },

    //AVIATION NON COMPLIANCE
    {
      requestType: 'AVIATION_NON_COMPLIANCE',
      taskType: 'AVIATION_NON_COMPLIANCE_APPLICATION_SUBMIT',
      expectedPath: ['/aviation' + '/tasks', taskId],
    },
    {
      requestType: 'AVIATION_NON_COMPLIANCE',
      taskType: 'AVIATION_NON_COMPLIANCE_DAILY_PENALTY_NOTICE',
      expectedPath: ['/aviation' + '/tasks', taskId],
    },
    {
      requestType: 'AVIATION_NON_COMPLIANCE',
      taskType: 'AVIATION_NON_COMPLIANCE_DAILY_PENALTY_NOTICE_WAIT_FOR_PEER_REVIEW',
      expectedPath: ['/aviation' + '/tasks', taskId],
    },
    {
      requestType: 'AVIATION_NON_COMPLIANCE',
      taskType: 'AVIATION_NON_COMPLIANCE_DAILY_PENALTY_NOTICE_APPLICATION_PEER_REVIEW',
      expectedPath: ['/aviation' + '/tasks', taskId],
    },
    {
      requestType: 'AVIATION_NON_COMPLIANCE',
      taskType: 'AVIATION_NON_COMPLIANCE_NOTICE_OF_INTENT',
      expectedPath: ['/aviation' + '/tasks', taskId],
    },
    {
      requestType: 'AVIATION_NON_COMPLIANCE',
      taskType: 'AVIATION_NON_COMPLIANCE_NOTICE_OF_INTENT_APPLICATION_PEER_REVIEW',
      expectedPath: ['/aviation' + '/tasks', taskId],
    },
    {
      requestType: 'AVIATION_NON_COMPLIANCE',
      taskType: 'AVIATION_NON_COMPLIANCE_NOTICE_OF_INTENT_WAIT_FOR_PEER_REVIEW',
      expectedPath: ['/aviation' + '/tasks', taskId],
    },
    {
      requestType: 'AVIATION_NON_COMPLIANCE',
      taskType: 'AVIATION_NON_COMPLIANCE_CIVIL_PENALTY',
      expectedPath: ['/aviation' + '/tasks', taskId],
    },
    {
      requestType: 'AVIATION_NON_COMPLIANCE',
      taskType: 'AVIATION_NON_COMPLIANCE_CIVIL_PENALTY_WAIT_FOR_PEER_REVIEW',
      expectedPath: ['/aviation' + '/tasks', taskId],
    },
    {
      requestType: 'AVIATION_NON_COMPLIANCE',
      taskType: 'AVIATION_NON_COMPLIANCE_CIVIL_PENALTY_APPLICATION_PEER_REVIEW',
      expectedPath: ['/aviation' + '/tasks', taskId],
    },
    {
      requestType: 'AVIATION_NON_COMPLIANCE',
      taskType: 'AVIATION_NON_COMPLIANCE_FINAL_DETERMINATION',
      expectedPath: ['/aviation' + '/tasks', taskId],
    },

    // NULL
    {
      requestType: null,
      taskType: null,
      expectedPath: ['.'],
    },
  ];

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it.each<DatasetDTO>(dataSet)(
    'should map $requestType . $taskType => $expectedPath',
    ({ requestType, taskType, expectedPath }) => {
      expect(
        pipe.transform({
          requestType: requestType,
          taskType: taskType,
          taskId: taskId,
        }),
      ).toEqual(expectedPath);
    },
  );
});
