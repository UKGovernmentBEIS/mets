import { Pipe, PipeTransform } from '@angular/core';

import { RequestActionInfoDTO } from 'pmrv-api';

@Pipe({ name: 'timelineItemLink' })
export class TimelineItemLinkPipe implements PipeTransform {
  transform(value: RequestActionInfoDTO, isWorkflow?: boolean, isAviation?: boolean): any[] {
    const routerLooks = isWorkflow ? './' : '/';
    switch (value.type) {
      case 'INSTALLATION_ACCOUNT_OPENING_ACCEPTED':
      case 'INSTALLATION_ACCOUNT_OPENING_REJECTED':
        return [routerLooks + 'installation-account', 'submitted-decision', value.id];
      case 'INSTALLATION_ACCOUNT_OPENING_ACCOUNT_APPROVED':
      case 'INSTALLATION_ACCOUNT_OPENING_APPLICATION_SUBMITTED':
        return [routerLooks + 'installation-account', 'summary', value.id];

      case 'PERMIT_ISSUANCE_APPLICATION_SUBMITTED':
        return [routerLooks + 'permit-issuance', 'action', value.id];
      case 'PERMIT_ISSUANCE_APPLICATION_AMENDS_SUBMITTED':
        return null;
      case 'PERMIT_ISSUANCE_APPLICATION_DEEMED_WITHDRAWN':
      case 'PERMIT_ISSUANCE_APPLICATION_GRANTED':
      case 'PERMIT_ISSUANCE_APPLICATION_REJECTED':
        return [routerLooks + 'permit-issuance', 'action', value.id, 'review', 'decision-summary'];
      case 'PERMIT_ISSUANCE_APPLICATION_PEER_REVIEWER_ACCEPTED':
      case 'PERMIT_ISSUANCE_APPLICATION_PEER_REVIEWER_REJECTED':
        return [routerLooks + 'permit-issuance', 'action', value.id, 'review', 'peer-reviewer-submitted'];
      case 'PERMIT_ISSUANCE_APPLICATION_RETURNED_FOR_AMENDS':
        return [routerLooks + 'permit-issuance', 'action', value.id, 'review', 'return-for-amends'];
      case 'PERMIT_ISSUANCE_PEER_REVIEW_REQUESTED':
      case 'PERMIT_ISSUANCE_RECALLED_FROM_AMENDS':
        return null;
      case 'PERMIT_SURRENDER_APPLICATION_CANCELLED':
        return null;
      case 'PERMIT_SURRENDER_APPLICATION_PEER_REVIEWER_ACCEPTED':
      case 'PERMIT_SURRENDER_APPLICATION_PEER_REVIEWER_REJECTED':
        return [routerLooks + 'permit-surrender', 'action', value.id, 'review', 'peer-reviewer-submitted'];
      case 'PERMIT_SURRENDER_APPLICATION_SUBMITTED':
        return [routerLooks + 'permit-surrender', 'action', value.id, 'surrender-submitted'];
      case 'PERMIT_SURRENDER_PEER_REVIEW_REQUESTED':
        return null;
      case 'PERMIT_SURRENDER_APPLICATION_REJECTED':
      case 'PERMIT_SURRENDER_APPLICATION_GRANTED':
      case 'PERMIT_SURRENDER_APPLICATION_DEEMED_WITHDRAWN':
        return [routerLooks + 'permit-surrender', 'action', value.id, 'determination-submitted'];
      case 'PERMIT_SURRENDER_CESSATION_COMPLETED':
        return [routerLooks + 'permit-surrender', 'action', value.id, 'cessation', 'completed'];
      case 'PERMIT_REVOCATION_APPLICATION_CANCELLED':
      case 'PERMIT_REVOCATION_PEER_REVIEW_REQUESTED':
        return null;
      case 'PERMIT_REVOCATION_APPLICATION_PEER_REVIEWER_ACCEPTED':
      case 'PERMIT_REVOCATION_APPLICATION_PEER_REVIEWER_REJECTED':
        return [routerLooks + 'permit-revocation', 'action', value.id, 'peer-reviewer-submitted'];
      case 'PERMIT_REVOCATION_APPLICATION_SUBMITTED':
        return [routerLooks + 'permit-revocation', 'action', value.id, 'revocation-submitted'];
      case 'PERMIT_REVOCATION_APPLICATION_WITHDRAWN':
        return [routerLooks + 'permit-revocation', 'action', value.id, 'withdraw-completed'];
      case 'PERMIT_REVOCATION_CESSATION_COMPLETED':
        return [routerLooks + 'permit-revocation', 'action', value.id, 'cessation', 'completed'];

      case 'PERMIT_NOTIFICATION_APPLICATION_CANCELLED':
        return null;
      case 'PERMIT_NOTIFICATION_APPLICATION_SUBMITTED':
        return [routerLooks + 'actions', value.id, 'permit-notification', 'submitted'];
      case 'PERMIT_NOTIFICATION_APPLICATION_GRANTED':
      case 'PERMIT_NOTIFICATION_APPLICATION_REJECTED':
        return [routerLooks + 'actions', value.id, 'permit-notification', 'decision'];
      case 'PERMIT_NOTIFICATION_PEER_REVIEW_REQUESTED':
        return null;
      case 'PERMIT_NOTIFICATION_APPLICATION_PEER_REVIEWER_ACCEPTED':
      case 'PERMIT_NOTIFICATION_APPLICATION_PEER_REVIEWER_REJECTED':
        return [routerLooks + 'actions', value.id, 'permit-notification', 'peer-review-decision'];
      case 'PERMIT_NOTIFICATION_FOLLOW_UP_RESPONSE_SUBMITTED':
        return [routerLooks + 'actions', value.id, 'permit-notification', 'follow-up-response'];
      case 'PERMIT_NOTIFICATION_FOLLOW_UP_RETURNED_FOR_AMENDS':
        return [routerLooks + 'actions', value.id, 'permit-notification', 'follow-up-return-for-amends'];
      case 'PERMIT_NOTIFICATION_FOLLOW_UP_DATE_EXTENDED':
      case 'PERMIT_NOTIFICATION_FOLLOW_UP_RECALLED_FROM_AMENDS':
      case 'PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_AMENDS_SUBMITTED':
        return null;
      case 'PERMIT_NOTIFICATION_APPLICATION_COMPLETED':
        return [routerLooks + 'actions', value.id, 'permit-notification', 'completed'];

      case 'PERMIT_VARIATION_APPLICATION_SUBMITTED':
        return [routerLooks + 'permit-variation', 'action', value.id];
      case 'PERMIT_VARIATION_APPLICATION_DEEMED_WITHDRAWN':
      case 'PERMIT_VARIATION_APPLICATION_GRANTED':
      case 'PERMIT_VARIATION_APPLICATION_REJECTED':
      case 'PERMIT_VARIATION_APPLICATION_REGULATOR_LED_APPROVED':
        return [routerLooks + 'permit-variation', 'action', value.id, 'review', 'decision-summary'];
      case 'PERMIT_VARIATION_APPLICATION_PEER_REVIEWER_ACCEPTED':
      case 'PERMIT_VARIATION_APPLICATION_PEER_REVIEWER_REJECTED':
        return [routerLooks + 'permit-variation', 'action', value.id, 'review', 'peer-reviewer-submitted'];
      case 'PERMIT_VARIATION_PEER_REVIEW_REQUESTED':
      case 'PERMIT_VARIATION_APPLICATION_CANCELLED':
        return null;
      case 'PERMIT_VARIATION_APPLICATION_RETURNED_FOR_AMENDS':
        return [routerLooks + 'permit-variation', 'action', value.id, 'review', 'return-for-amends'];
      case 'PERMIT_VARIATION_APPLICATION_AMENDS_SUBMITTED':
      case 'PERMIT_VARIATION_RECALLED_FROM_AMENDS':
        return null;

      case 'PERMIT_TRANSFER_B_RECALLED_FROM_AMENDS':
      case 'PERMIT_TRANSFER_APPLICATION_CANCELLED':
      case 'PERMIT_TRANSFER_B_APPLICATION_AMENDS_SUBMITTED':
      case 'PERMIT_TRANSFER_B_PEER_REVIEW_REQUESTED':
        return null;
      case 'PERMIT_TRANSFER_A_APPLICATION_SUBMITTED':
        return [routerLooks + 'actions', value.id, 'permit-transfer-a', 'submitted'];
      case 'PERMIT_TRANSFER_B_APPLICATION_SUBMITTED':
        return [routerLooks + 'permit-transfer', 'action', value.id];
      case 'PERMIT_TRANSFER_B_APPLICATION_RETURNED_FOR_AMENDS':
        return [routerLooks + 'permit-transfer', 'action', value.id, 'review', 'return-for-amends'];
      case 'PERMIT_TRANSFER_B_APPLICATION_PEER_REVIEWER_ACCEPTED':
      case 'PERMIT_TRANSFER_B_APPLICATION_PEER_REVIEWER_REJECTED':
        return [routerLooks + 'permit-transfer', 'action', value.id, 'review', 'peer-reviewer-submitted'];
      case 'PERMIT_TRANSFER_A_APPLICATION_DEEMED_WITHDRAWN':
      case 'PERMIT_TRANSFER_A_APPLICATION_GRANTED':
      case 'PERMIT_TRANSFER_A_APPLICATION_REJECTED':
      case 'PERMIT_TRANSFER_B_APPLICATION_DEEMED_WITHDRAWN':
      case 'PERMIT_TRANSFER_B_APPLICATION_GRANTED':
      case 'PERMIT_TRANSFER_B_APPLICATION_REJECTED':
        return [routerLooks + 'permit-transfer', 'action', value.id, 'review', 'decision-summary'];

      case 'PAYMENT_MARKED_AS_PAID':
        return isAviation
          ? [routerLooks + 'aviation/payment', 'actions', value.id, 'paid']
          : [routerLooks + 'payment', 'actions', value.id, 'paid'];
      case 'PAYMENT_CANCELLED':
        return isAviation
          ? [routerLooks + 'aviation/payment', 'actions', value.id, 'cancelled']
          : [routerLooks + 'payment', 'actions', value.id, 'cancelled'];
      case 'PAYMENT_MARKED_AS_RECEIVED':
        return isAviation
          ? [routerLooks + 'aviation/payment', 'actions', value.id, 'received']
          : [routerLooks + 'payment', 'actions', value.id, 'received'];
      case 'PAYMENT_COMPLETED':
        return isAviation
          ? [routerLooks + 'aviation/payment', 'actions', value.id, 'completed']
          : [routerLooks + 'payment', 'actions', value.id, 'completed'];

      case 'RDE_ACCEPTED':
      case 'RDE_CANCELLED':
      case 'RDE_EXPIRED':
        return null;
      case 'RDE_FORCE_ACCEPTED':
      case 'RDE_FORCE_REJECTED':
        return isAviation
          ? [routerLooks + 'aviation/rde', 'action', value.id, 'rde-manual-approval-submitted']
          : [routerLooks + 'rde', 'action', value.id, 'rde-manual-approval-submitted'];
      case 'RDE_REJECTED':
        return isAviation
          ? [routerLooks + 'aviation/rde', 'action', value.id, 'rde-response-submitted']
          : [routerLooks + 'rde', 'action', value.id, 'rde-response-submitted'];
      case 'RDE_SUBMITTED':
        return isAviation
          ? [routerLooks + 'aviation/rde', 'action', value.id, 'rde-submitted']
          : [routerLooks + 'rde', 'action', value.id, 'rde-submitted'];
      case 'RFI_CANCELLED':
      case 'RFI_EXPIRED':
        return null;
      case 'RFI_RESPONSE_SUBMITTED':
        return isAviation
          ? [routerLooks + 'aviation/rfi', 'action', value.id, 'rfi-response-submitted']
          : [routerLooks + 'rfi', 'action', value.id, 'rfi-response-submitted'];
      case 'RFI_SUBMITTED':
        return isAviation
          ? [routerLooks + 'aviation/rfi', 'action', value.id, 'rfi-submitted']
          : [routerLooks + 'rfi', 'action', value.id, 'rfi-submitted'];
      case 'REQUEST_TERMINATED':
        return null;

      case 'AER_RECALLED_FROM_VERIFICATION':
      case 'AER_APPLICATION_RE_INITIATED':
        return null;
      case 'AER_APPLICATION_RETURNED_FOR_AMENDS':
        return [routerLooks + 'actions', value.id, 'aer', 'return-for-amends'];
      case 'AER_APPLICATION_VERIFICATION_SUBMITTED':
      case 'AER_APPLICATION_SUBMITTED':
      case 'AER_APPLICATION_SENT_TO_VERIFIER':
      case 'AER_APPLICATION_AMENDS_SUBMITTED':
      case 'AER_APPLICATION_AMENDS_SENT_TO_VERIFIER':
        return [routerLooks + 'actions', value.id, 'aer', 'submitted'];
      case 'AER_APPLICATION_COMPLETED':
        return [routerLooks + 'actions', value.id, 'aer', 'completed'];

      case 'VIR_APPLICATION_SUBMITTED':
        return [routerLooks + 'actions', value.id, 'vir', 'submitted'];
      case 'VIR_APPLICATION_REVIEWED':
        return [routerLooks + 'actions', value.id, 'vir', 'reviewed'];
      case 'VIR_APPLICATION_RESPONDED_TO_REGULATOR_COMMENTS':
        return [routerLooks + 'actions', value.id, 'vir', 'responded'];

      case 'AIR_APPLICATION_SUBMITTED':
        return [routerLooks + 'actions', value.id, 'air', 'submitted'];
      case 'AIR_APPLICATION_REVIEWED':
        return [routerLooks + 'actions', value.id, 'air', 'reviewed'];
      case 'AIR_APPLICATION_RESPONDED_TO_REGULATOR_COMMENTS':
        return [routerLooks + 'actions', value.id, 'air', 'responded'];

      case 'DRE_APPLICATION_SUBMITTED':
        return [routerLooks + 'actions', value.id, 'dre', 'submitted'];
      case 'DRE_APPLICATION_PEER_REVIEW_REQUESTED':
        return null;
      case 'DRE_APPLICATION_PEER_REVIEWER_ACCEPTED':
      case 'DRE_APPLICATION_PEER_REVIEWER_REJECTED':
        return [routerLooks + 'actions', value.id, 'dre', 'peer-review-decision'];
      case 'DRE_APPLICATION_CANCELLED':
        return null;

      case 'DOAL_APPLICATION_PROCEEDED_TO_AUTHORITY':
        return [routerLooks + 'actions', value.id, 'doal', 'proceeded'];
      case 'DOAL_APPLICATION_CLOSED':
        return [routerLooks + 'actions', value.id, 'doal', 'closed'];
      case 'DOAL_APPLICATION_PEER_REVIEWER_ACCEPTED':
      case 'DOAL_APPLICATION_PEER_REVIEWER_REJECTED':
        return [routerLooks + 'actions', value.id, 'doal', 'peer-review-decision'];
      case 'DOAL_APPLICATION_ACCEPTED':
      case 'DOAL_APPLICATION_ACCEPTED_WITH_CORRECTIONS':
      case 'DOAL_APPLICATION_REJECTED':
        return [routerLooks + 'actions', value.id, 'doal', 'completed'];
      case 'DOAL_APPLICATION_PEER_REVIEW_REQUESTED':
      case 'DOAL_APPLICATION_CANCELLED':
        return null;

      case 'VERIFICATION_STATEMENT_CANCELLED':
        return null;

      case 'BATCH_REISSUE_SUBMITTED':
        return isAviation
          ? [routerLooks + 'actions', value.id, 'emp-batch-variation', 'batch-reissue', 'submitted']
          : [routerLooks + 'actions', value.id, 'permit-batch-variation', 'submitted'];
      case 'BATCH_REISSUE_COMPLETED':
        return isAviation
          ? [routerLooks + 'actions', value.id, 'emp-batch-variation', 'batch-reissue', 'completed']
          : [routerLooks + 'actions', value.id, 'permit-batch-variation', 'completed'];

      case 'REISSUE_COMPLETED':
        return isAviation
          ? [routerLooks + 'actions', value.id, 'emp-batch-variation', 'reissue', 'completed']
          : [routerLooks + 'actions', value.id, 'variation', 'completed'];

      case 'EMP_ISSUANCE_UKETS_APPLICATION_SUBMITTED':
      case 'EMP_ISSUANCE_UKETS_APPLICATION_AMENDS_SUBMITTED':
      case 'EMP_ISSUANCE_CORSIA_APPLICATION_AMENDS_SUBMITTED':
      case 'EMP_VARIATION_UKETS_APPLICATION_AMENDS_SUBMITTED':
      case 'EMP_VARIATION_CORSIA_APPLICATION_AMENDS_SUBMITTED':
      case 'EMP_ISSUANCE_CORSIA_APPLICATION_SUBMITTED':
      case 'EMP_VARIATION_CORSIA_APPLICATION_SUBMITTED':
        return isWorkflow ? [routerLooks, 'actions', value.id] : [routerLooks + 'aviation', 'actions', value.id];
      case 'EMP_ISSUANCE_UKETS_APPLICATION_APPROVED':
      case 'EMP_ISSUANCE_UKETS_APPLICATION_DEEMED_WITHDRAWN':
      case 'EMP_ISSUANCE_CORSIA_APPLICATION_APPROVED':
      case 'EMP_ISSUANCE_CORSIA_APPLICATION_DEEMED_WITHDRAWN':
      case 'EMP_VARIATION_UKETS_APPLICATION_REGULATOR_LED_APPROVED':
      case 'EMP_VARIATION_CORSIA_APPLICATION_REGULATOR_LED_APPROVED':
      case 'EMP_VARIATION_UKETS_APPLICATION_APPROVED':
      case 'EMP_VARIATION_UKETS_APPLICATION_DEEMED_WITHDRAWN':
      case 'EMP_VARIATION_UKETS_APPLICATION_REJECTED':
      case 'EMP_VARIATION_CORSIA_APPLICATION_APPROVED':
      case 'EMP_VARIATION_CORSIA_APPLICATION_DEEMED_WITHDRAWN':
      case 'EMP_VARIATION_CORSIA_APPLICATION_REJECTED':
        return isWorkflow
          ? [routerLooks + 'actions', value.id, 'decision-summary']
          : [routerLooks + 'aviation', 'actions', value.id, 'decision-summary'];
      case 'EMP_ISSUANCE_UKETS_PEER_REVIEW_REQUESTED':
      case 'EMP_ISSUANCE_CORSIA_PEER_REVIEW_REQUESTED':
      case 'EMP_ISSUANCE_UKETS_RECALLED_FROM_AMENDS':
      case 'EMP_ISSUANCE_CORSIA_RECALLED_FROM_AMENDS':
      case 'EMP_VARIATION_UKETS_RECALLED_FROM_AMENDS':
      case 'EMP_VARIATION_CORSIA_RECALLED_FROM_AMENDS':
      case 'EMP_VARIATION_UKETS_PEER_REVIEW_REQUESTED':
      case 'EMP_VARIATION_CORSIA_PEER_REVIEW_REQUESTED':
        return null;
      case 'EMP_ISSUANCE_UKETS_APPLICATION_RETURNED_FOR_AMENDS':
      case 'EMP_ISSUANCE_CORSIA_APPLICATION_RETURNED_FOR_AMENDS':
      case 'EMP_VARIATION_UKETS_APPLICATION_RETURNED_FOR_AMENDS':
      case 'EMP_VARIATION_CORSIA_APPLICATION_RETURNED_FOR_AMENDS':
        return isWorkflow
          ? [routerLooks, 'actions', value.id, 'return-for-amends']
          : [routerLooks + 'aviation', 'actions', value.id, 'return-for-amends'];
      case 'EMP_ISSUANCE_UKETS_APPLICATION_PEER_REVIEWER_ACCEPTED':
      case 'EMP_ISSUANCE_UKETS_APPLICATION_PEER_REVIEWER_REJECTED':
      case 'EMP_ISSUANCE_CORSIA_APPLICATION_PEER_REVIEWER_ACCEPTED':
      case 'EMP_ISSUANCE_CORSIA_APPLICATION_PEER_REVIEWER_REJECTED':
      case 'EMP_VARIATION_UKETS_APPLICATION_PEER_REVIEWER_ACCEPTED':
      case 'EMP_VARIATION_UKETS_APPLICATION_PEER_REVIEWER_REJECTED':
      case 'EMP_VARIATION_CORSIA_APPLICATION_PEER_REVIEWER_ACCEPTED':
      case 'EMP_VARIATION_CORSIA_APPLICATION_PEER_REVIEWER_REJECTED':
        return isWorkflow
          ? [routerLooks + 'actions', value.id, 'peer-reviewer-submitted']
          : [routerLooks + 'aviation', 'actions', value.id, 'peer-reviewer-submitted'];

      case 'NON_COMPLIANCE_APPLICATION_CLOSED':
        return [routerLooks + 'actions', value.id, 'non-compliance', 'closed'];
      case 'NON_COMPLIANCE_APPLICATION_SUBMITTED':
        return isAviation && !isWorkflow
          ? [routerLooks + 'aviation', 'actions', value.id, 'non-compliance', 'submitted']
          : [routerLooks + 'actions', value.id, 'non-compliance', 'submitted'];
      case 'NON_COMPLIANCE_DAILY_PENALTY_NOTICE_APPLICATION_SUBMITTED':
        return isAviation && !isWorkflow
          ? [routerLooks + 'aviation', 'actions', value.id, 'non-compliance', 'daily-penalty-notice-submitted']
          : [routerLooks + 'actions', value.id, 'non-compliance', 'daily-penalty-notice-submitted'];
      case 'NON_COMPLIANCE_DAILY_PENALTY_NOTICE_PEER_REVIEWER_ACCEPTED':
      case 'NON_COMPLIANCE_DAILY_PENALTY_NOTICE_PEER_REVIEWER_REJECTED':
        return isAviation && !isWorkflow
          ? [routerLooks + 'aviation', 'actions', value.id, 'non-compliance', 'dpn-peer-review-decision']
          : [routerLooks + 'actions', value.id, 'non-compliance', 'dpn-peer-review-decision'];
      case 'NON_COMPLIANCE_DAILY_PENALTY_NOTICE_PEER_REVIEW_REQUESTED':
        return null;
      case 'NON_COMPLIANCE_NOTICE_OF_INTENT_APPLICATION_SUBMITTED':
        return isAviation && !isWorkflow
          ? [routerLooks + 'aviation', 'actions', value.id, 'non-compliance', 'notice-of-intent-submitted']
          : [routerLooks + 'actions', value.id, 'non-compliance', 'notice-of-intent-submitted'];
      case 'NON_COMPLIANCE_NOTICE_OF_INTENT_PEER_REVIEWER_ACCEPTED':
      case 'NON_COMPLIANCE_NOTICE_OF_INTENT_PEER_REVIEWER_REJECTED':
        return isAviation && !isWorkflow
          ? [routerLooks + 'aviation', 'actions', value.id, 'non-compliance', 'noi-peer-review-decision']
          : [routerLooks + 'actions', value.id, 'non-compliance', 'noi-peer-review-decision'];
      case 'NON_COMPLIANCE_NOTICE_OF_INTENT_PEER_REVIEW_REQUESTED':
        return null;
      case 'NON_COMPLIANCE_CIVIL_PENALTY_APPLICATION_SUBMITTED':
        return isAviation && !isWorkflow
          ? [routerLooks + 'aviation', 'actions', value.id, 'non-compliance', 'civil-penalty-notice-submitted']
          : [routerLooks + 'actions', value.id, 'non-compliance', 'civil-penalty-notice-submitted'];
      case 'NON_COMPLIANCE_CIVIL_PENALTY_PEER_REVIEWER_ACCEPTED':
      case 'NON_COMPLIANCE_CIVIL_PENALTY_PEER_REVIEWER_REJECTED':
        return isAviation && !isWorkflow
          ? [routerLooks + 'aviation', 'actions', value.id, 'non-compliance', 'cpn-peer-review-decision']
          : [routerLooks + 'actions', value.id, 'non-compliance', 'cpn-peer-review-decision'];
      case 'NON_COMPLIANCE_CIVIL_PENALTY_PEER_REVIEW_REQUESTED':
        return null;
      case 'NON_COMPLIANCE_FINAL_DETERMINATION_APPLICATION_SUBMITTED':
        return isAviation && !isWorkflow
          ? [routerLooks + 'aviation', 'actions', value.id, 'non-compliance', 'conclusion-submitted']
          : [routerLooks + 'actions', value.id, 'non-compliance', 'conclusion-submitted'];

      case 'WITHHOLDING_OF_ALLOWANCES_APPLICATION_SUBMITTED':
        return [routerLooks + 'actions', value.id, 'withholding-allowances', 'submitted'];
      case 'WITHHOLDING_OF_ALLOWANCES_APPLICATION_WITHDRAWN':
        return [routerLooks + 'actions', value.id, 'withholding-allowances', 'withdrawn'];
      case 'WITHHOLDING_OF_ALLOWANCES_APPLICATION_CLOSED':
        return [routerLooks + 'actions', value.id, 'withholding-allowances', 'closed'];
      case 'WITHHOLDING_OF_ALLOWANCES_PEER_REVIEW_REQUESTED':
      case 'WITHHOLDING_OF_ALLOWANCES_APPLICATION_CANCELLED':
        return null;
      case 'WITHHOLDING_OF_ALLOWANCES_APPLICATION_PEER_REVIEWER_ACCEPTED':
      case 'WITHHOLDING_OF_ALLOWANCES_APPLICATION_PEER_REVIEWER_REJECTED':
        return [routerLooks + 'actions', value.id, 'withholding-allowances', 'peer-review-decision'];

      case 'EMP_VARIATION_UKETS_APPLICATION_SUBMITTED':
      case 'AVIATION_AER_UKETS_APPLICATION_SUBMITTED':
      case 'AVIATION_AER_UKETS_APPLICATION_AMENDS_SUBMITTED':
      case 'AVIATION_AER_UKETS_APPLICATION_VERIFICATION_SUBMITTED':
      case 'AVIATION_AER_UKETS_APPLICATION_SENT_TO_VERIFIER':
      case 'AVIATION_AER_UKETS_APPLICATION_AMENDS_SENT_TO_VERIFIER':
      case 'AVIATION_AER_UKETS_APPLICATION_REVIEW_SKIPPED':
      case 'AVIATION_AER_CORSIA_APPLICATION_SENT_TO_VERIFIER':
      case 'AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMITTED':
      case 'AVIATION_AER_CORSIA_APPLICATION_SUBMITTED':
        return isWorkflow ? [routerLooks, 'actions', value.id] : [routerLooks + 'aviation', 'actions', value.id];
      case 'AVIATION_AER_UKETS_APPLICATION_COMPLETED':
        return isWorkflow
          ? [routerLooks, 'actions', value.id, 'aer', 'aer-completed']
          : [routerLooks + 'aviation', 'actions', value.id, 'aer', 'aer-completed'];
      case 'AVIATION_AER_CORSIA_APPLICATION_COMPLETED':
        return isWorkflow
          ? [routerLooks, 'actions', value.id, 'aer-corsia', 'aer-completed']
          : [routerLooks + 'aviation', 'actions', value.id, 'aer-corsia', 'aer-completed'];
      case 'AVIATION_AER_UKETS_APPLICATION_RETURNED_FOR_AMENDS':
        return isWorkflow
          ? [routerLooks, 'actions', value.id, 'aer', 'return-for-amends']
          : [routerLooks + 'aviation', 'actions', value.id, 'aer', 'return-for-amends'];

      case 'AVIATION_AER_APPLICATION_CANCELLED_DUE_TO_EXEPMT':
      case 'AVIATION_AER_RECALLED_FROM_VERIFICATION':
      case 'AVIATION_AER_APPLICATION_RE_INITIATED':
        return null;

      case 'EMP_VARIATION_APPLICATION_CANCELLED':
        return null;

      case 'AVIATION_ACCOUNT_CLOSURE_CANCELLED':
        return null;
      case 'AVIATION_ACCOUNT_CLOSURE_SUBMITTED':
        return [routerLooks + 'actions', value.id, 'account-closure-submitted'];

      case 'AVIATION_DRE_APPLICATION_CANCELLED':
      case 'AVIATION_DRE_UKETS_PEER_REVIEW_REQUESTED':
      case 'AVIATION_AER_UKETS_APPLICATION_CANCELLED_DUE_TO_DRE':
        return null;
      case 'AVIATION_DRE_UKETS_PEER_REVIEWER_ACCEPTED':
      case 'AVIATION_DRE_UKETS_PEER_REVIEWER_REJECTED':
        return isWorkflow
          ? [routerLooks + 'actions', value.id, 'peer-reviewer-submitted']
          : [routerLooks + 'aviation', 'actions', value.id, 'peer-reviewer-submitted'];
      case 'AVIATION_DRE_UKETS_APPLICATION_SUBMITTED':
        return isWorkflow
          ? [routerLooks + 'actions', value.id, 'aviation-emissions-updated']
          : [routerLooks + 'aviation', 'actions', value.id, 'aviation-emissions-updated'];

      case 'AVIATION_VIR_APPLICATION_SUBMITTED':
        return isWorkflow ? [routerLooks, 'actions', value.id] : [routerLooks + 'aviation', 'actions', value.id];
      case 'AVIATION_VIR_APPLICATION_REVIEWED':
        return isWorkflow
          ? [routerLooks, 'actions', value.id, 'vir', 'decision-summary']
          : [routerLooks + 'aviation', 'actions', value.id, 'vir', 'decision-summary'];
      case 'AVIATION_VIR_APPLICATION_RESPONDED_TO_REGULATOR_COMMENTS':
        return isWorkflow
          ? [routerLooks, 'actions', value.id, 'vir', 'responded']
          : [routerLooks + 'aviation', 'actions', value.id, 'vir', 'responded'];

      case 'RETURN_OF_ALLOWANCES_APPLICATION_SUBMITTED':
        return [routerLooks + 'actions', value.id, 'return-of-allowances', 'submitted'];
      case 'RETURN_OF_ALLOWANCES_APPLICATION_PEER_REVIEWER_ACCEPTED':
      case 'RETURN_OF_ALLOWANCES_APPLICATION_PEER_REVIEWER_REJECTED':
        return ['/actions', value.id, 'return-of-allowances', 'roa-peer-review-decision'];
      case 'RETURN_OF_ALLOWANCES_PEER_REVIEW_REQUESTED':
      case 'RETURN_OF_ALLOWANCES_APPLICATION_CANCELLED':
        return null;
      case 'RETURN_OF_ALLOWANCES_RETURNED_APPLICATION_COMPLETED':
        return [routerLooks + 'actions', value.id, 'return-of-allowances', 'returned'];

      default:
        throw new Error('Provide an action url');
    }
  }
}
