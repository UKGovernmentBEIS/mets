import { Pipe, PipeTransform } from '@angular/core';

import { RequestActionInfoDTO } from 'pmrv-api';

@Pipe({ name: 'itemActionType' })
export class ItemActionTypePipe implements PipeTransform {
  transform(type: RequestActionInfoDTO['type']): string {
    switch (type) {
      case 'INSTALLATION_ACCOUNT_OPENING_ACCEPTED':
        return `The regulator accepted the installation account application`;
      case 'INSTALLATION_ACCOUNT_OPENING_ACCOUNT_APPROVED':
        return 'Installation application approved';
      case 'INSTALLATION_ACCOUNT_OPENING_REJECTED':
        return 'The regulator rejected the installation account application';
      case 'INSTALLATION_ACCOUNT_OPENING_APPLICATION_SUBMITTED':
        return 'Original application';

      case 'PERMIT_ISSUANCE_APPLICATION_AMENDS_SUBMITTED':
        return 'Amended permit application submitted';
      case 'PERMIT_ISSUANCE_APPLICATION_DEEMED_WITHDRAWN':
        return 'Permit application deemed withdrawn';
      case 'PERMIT_ISSUANCE_APPLICATION_GRANTED':
        return 'Permit application approved';
      case 'PERMIT_ISSUANCE_APPLICATION_PEER_REVIEWER_ACCEPTED':
        return 'Peer review agreement submitted';
      case 'PERMIT_ISSUANCE_APPLICATION_PEER_REVIEWER_REJECTED':
        return 'Peer review disagreement submitted';
      case 'PERMIT_ISSUANCE_APPLICATION_REJECTED':
        return 'Permit application rejected';
      case 'PERMIT_ISSUANCE_APPLICATION_RETURNED_FOR_AMENDS':
        return 'Permit application returned for amends';
      case 'PERMIT_ISSUANCE_APPLICATION_SUBMITTED':
        return 'New permit submitted';
      case 'PERMIT_ISSUANCE_PEER_REVIEW_REQUESTED':
        return 'Peer review requested';
      case 'PERMIT_ISSUANCE_RECALLED_FROM_AMENDS':
        return 'Permit application recalled';

      case 'PERMIT_SURRENDER_APPLICATION_CANCELLED':
        return 'Surrender request cancelled';
      case 'PERMIT_SURRENDER_APPLICATION_PEER_REVIEWER_ACCEPTED':
        return 'Peer review agreement submitted';
      case 'PERMIT_SURRENDER_APPLICATION_PEER_REVIEWER_REJECTED':
        return 'Peer review disagreement submitted';
      case 'PERMIT_SURRENDER_APPLICATION_SUBMITTED':
        return 'Surrender request submitted';
      case 'PERMIT_SURRENDER_PEER_REVIEW_REQUESTED':
        return 'Peer review requested';
      case 'PERMIT_SURRENDER_CESSATION_COMPLETED':
        return 'Cessation completed';
      case 'PERMIT_SURRENDER_APPLICATION_DEEMED_WITHDRAWN':
        return 'Surrender request deemed withdrawn';
      case 'PERMIT_SURRENDER_APPLICATION_GRANTED':
        return 'Surrender request approved';
      case 'PERMIT_SURRENDER_APPLICATION_REJECTED':
        return 'Surrender request rejected';

      case 'PERMIT_REVOCATION_APPLICATION_CANCELLED':
        return 'Revocation request cancelled';
      case 'PERMIT_REVOCATION_PEER_REVIEW_REQUESTED':
        return 'Peer review requested';
      case 'PERMIT_REVOCATION_APPLICATION_PEER_REVIEWER_ACCEPTED':
        return 'Peer review agreement submitted';
      case 'PERMIT_REVOCATION_APPLICATION_PEER_REVIEWER_REJECTED':
        return 'Peer review disagreement submitted';
      case 'PERMIT_REVOCATION_CESSATION_COMPLETED':
        return 'Cessation completed';
      case 'PERMIT_REVOCATION_APPLICATION_SUBMITTED':
        return 'Revocation request submitted';
      case 'PERMIT_REVOCATION_APPLICATION_WITHDRAWN':
        return 'Revocation request withdrawn';

      case 'PERMIT_NOTIFICATION_APPLICATION_SUBMITTED':
        return 'Notification submitted';
      case 'PERMIT_NOTIFICATION_APPLICATION_CANCELLED':
        return 'Notification cancelled';
      case 'PERMIT_NOTIFICATION_FOLLOW_UP_RESPONSE_SUBMITTED':
        return 'Follow up response submitted';
      case 'PERMIT_NOTIFICATION_APPLICATION_GRANTED':
        return 'Notification approved';
      case 'PERMIT_NOTIFICATION_APPLICATION_REJECTED':
        return 'Notification rejected';
      case 'PERMIT_NOTIFICATION_FOLLOW_UP_DATE_EXTENDED':
        return 'Follow up response due date updated';
      case 'PERMIT_NOTIFICATION_PEER_REVIEW_REQUESTED':
        return 'Peer review requested';
      case 'PERMIT_NOTIFICATION_APPLICATION_PEER_REVIEWER_ACCEPTED':
        return 'Peer review agreement submitted';
      case 'PERMIT_NOTIFICATION_APPLICATION_PEER_REVIEWER_REJECTED':
        return 'Peer review disagreement submitted';
      case 'PERMIT_NOTIFICATION_APPLICATION_COMPLETED':
        return 'Notification completed';
      case 'PERMIT_NOTIFICATION_FOLLOW_UP_RETURNED_FOR_AMENDS':
        return 'Follow up response returned for amends';
      case 'PERMIT_NOTIFICATION_FOLLOW_UP_RECALLED_FROM_AMENDS':
        return 'Follow up response recalled';
      case 'PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_AMENDS_SUBMITTED':
        return 'Amended follow up response submitted';
      case 'PERMIT_NOTIFICATION_APPLICATION_CESSATION_COMPLETED':
        return 'Regulator review completed';

      case 'PERMIT_VARIATION_APPLICATION_SUBMITTED':
        return 'Permit variation submitted';
      case 'PERMIT_VARIATION_APPLICATION_GRANTED':
      case 'PERMIT_VARIATION_APPLICATION_REGULATOR_LED_APPROVED':
        return 'Variation application approved';
      case 'PERMIT_VARIATION_APPLICATION_REJECTED':
        return 'Permit variation rejected';
      case 'PERMIT_VARIATION_APPLICATION_DEEMED_WITHDRAWN':
        return 'Permit variation deemed withdrawn';
      case 'PERMIT_VARIATION_APPLICATION_CANCELLED':
        return 'Variation application cancelled';
      case 'PERMIT_VARIATION_PEER_REVIEW_REQUESTED':
        return 'Peer review requested';
      case 'PERMIT_VARIATION_APPLICATION_PEER_REVIEWER_ACCEPTED':
        return 'Peer review agreement submitted';
      case 'PERMIT_VARIATION_APPLICATION_PEER_REVIEWER_REJECTED':
        return 'Peer review disagreement submitted';
      case 'PERMIT_VARIATION_APPLICATION_RETURNED_FOR_AMENDS':
        return 'Variation application returned for amends';
      case 'PERMIT_VARIATION_APPLICATION_AMENDS_SUBMITTED':
        return 'Amended variation application submitted';
      case 'PERMIT_VARIATION_RECALLED_FROM_AMENDS':
        return 'Variation application recalled';

      case 'AER_APPLICATION_SUBMITTED':
        return 'Emissions report submitted to regulator';
      case 'AER_APPLICATION_SENT_TO_VERIFIER':
        return 'Emissions report submitted to verifier';
      case 'AER_RECALLED_FROM_VERIFICATION':
        return 'Emissions report recalled';
      case 'AER_APPLICATION_COMPLETED':
        return 'Emissions report reviewed';
      case 'AER_APPLICATION_NOT_REQUIRED':
        return 'Marked as not required';
      case 'AER_APPLICATION_VERIFICATION_SUBMITTED':
        return 'Verification statement submitted to operator';
      case 'AER_APPLICATION_RE_INITIATED':
        return 'Emissions report returned for amends';
      case 'AER_APPLICATION_RETURNED_FOR_AMENDS':
        return 'Emissions report returned for amends';
      case 'AER_APPLICATION_AMENDS_SUBMITTED':
      case 'AER_APPLICATION_AMENDS_SENT_TO_VERIFIER':
        return 'Amended emissions report submitted';
      case 'AER_APPLICATION_REVIEW_SKIPPED':
        return 'Completed without review';
      case 'AER_VERIFICATION_RETURNED_TO_OPERATOR':
        return 'Verifier returned to operator for changes';

      case 'DRE_APPLICATION_CANCELLED':
        return 'Reportable emissions cancelled';
      case 'DRE_APPLICATION_PEER_REVIEW_REQUESTED':
        return 'Peer review requested';
      case 'DRE_APPLICATION_PEER_REVIEWER_ACCEPTED':
        return 'Peer review agreement submitted';
      case 'DRE_APPLICATION_PEER_REVIEWER_REJECTED':
        return 'Peer review disagreement submitted';

      case 'DOAL_APPLICATION_PROCEEDED_TO_AUTHORITY':
        return 'Activity level determination sent to UK Authority';
      case 'DOAL_APPLICATION_CLOSED':
        return 'Activity level determination closed';
      case 'DOAL_APPLICATION_PEER_REVIEW_REQUESTED':
        return 'Peer review requested';
      case 'DOAL_APPLICATION_PEER_REVIEWER_ACCEPTED':
        return 'Peer review agreement submitted';
      case 'DOAL_APPLICATION_PEER_REVIEWER_REJECTED':
        return 'Peer review disagreement submitted';
      case 'DOAL_APPLICATION_ACCEPTED':
        return 'Activity level determination accepted as approved';
      case 'DOAL_APPLICATION_ACCEPTED_WITH_CORRECTIONS':
        return 'Activity level determination accepted as approved with corrections';
      case 'DOAL_APPLICATION_REJECTED':
        return 'Activity level determination  not approved';
      case 'DOAL_APPLICATION_CANCELLED':
        return 'Activity level determination cancelled';

      case 'VIR_APPLICATION_SUBMITTED':
        return 'Verifier improvement report submitted';
      case 'VIR_APPLICATION_REVIEWED':
        return 'Verifier improvement report decision submitted';
      case 'VIR_APPLICATION_RESPONDED_TO_REGULATOR_COMMENTS':
        return 'Follow up response submitted';

      case 'AIR_APPLICATION_SUBMITTED':
        return 'Annual improvement report submitted';
      case 'AIR_APPLICATION_REVIEWED':
        return 'Annual improvement report decision submitted';
      case 'AIR_APPLICATION_RESPONDED_TO_REGULATOR_COMMENTS':
        return 'Follow up response submitted';

      case 'VERIFICATION_STATEMENT_CANCELLED':
        return 'Verification statement cancelled';

      case 'PAYMENT_MARKED_AS_PAID':
        return 'Payment marked as paid (BACS)';
      case 'PAYMENT_CANCELLED':
        return 'Payment task cancelled';
      case 'PAYMENT_MARKED_AS_RECEIVED':
        return 'Payment marked as received';
      case 'PAYMENT_COMPLETED':
        return 'Payment confirmed via GOV.UK pay';

      case 'RDE_ACCEPTED':
        return 'Deadline extension date accepted';
      case 'RDE_CANCELLED':
      case 'RDE_REJECTED':
      case 'RDE_FORCE_REJECTED':
        return 'Deadline extension date rejected';
      case 'RDE_EXPIRED':
        return 'Deadline extension date expired';
      case 'RDE_FORCE_ACCEPTED':
        return 'Deadline extension date approved';
      case 'RDE_SUBMITTED':
        return 'Deadline extension date requested';

      case 'RFI_CANCELLED':
        return 'Request for information withdrawn';
      case 'RFI_EXPIRED':
        return 'Request for information expired';
      case 'RFI_RESPONSE_SUBMITTED':
        return 'Request for information responded';
      case 'RFI_SUBMITTED':
        return 'Request for information sent';
      case 'REQUEST_TERMINATED':
        return 'Workflow terminated by the system';

      case 'PERMIT_TRANSFER_APPLICATION_CANCELLED':
        return 'Transfer application cancelled';
      case 'PERMIT_TRANSFER_A_APPLICATION_SUBMITTED':
        return 'Permit transfer started';
      case 'PERMIT_TRANSFER_B_APPLICATION_SUBMITTED':
        return 'Permit transfer submitted';
      case 'PERMIT_TRANSFER_B_APPLICATION_RETURNED_FOR_AMENDS':
        return 'Permit transfer returned for amends';
      case 'PERMIT_TRANSFER_B_RECALLED_FROM_AMENDS':
        return 'Permit transfer recalled';
      case 'PERMIT_TRANSFER_B_APPLICATION_AMENDS_SUBMITTED':
        return 'Amended transfer application submitted';
      case 'PERMIT_TRANSFER_B_PEER_REVIEW_REQUESTED':
        return 'Peer review requested';
      case 'PERMIT_TRANSFER_B_APPLICATION_PEER_REVIEWER_ACCEPTED':
        return 'Peer review agreement submitted';
      case 'PERMIT_TRANSFER_B_APPLICATION_PEER_REVIEWER_REJECTED':
        return 'Peer review disagreement submitted';
      case 'PERMIT_TRANSFER_A_APPLICATION_DEEMED_WITHDRAWN':
      case 'PERMIT_TRANSFER_B_APPLICATION_DEEMED_WITHDRAWN':
        return 'Permit transfer deemed withdrawn';
      case 'PERMIT_TRANSFER_A_APPLICATION_GRANTED':
      case 'PERMIT_TRANSFER_B_APPLICATION_GRANTED':
        return 'Permit transfer approved';
      case 'PERMIT_TRANSFER_A_APPLICATION_REJECTED':
      case 'PERMIT_TRANSFER_B_APPLICATION_REJECTED':
        return 'Permit transfer rejected';

      case 'BATCH_REISSUE_SUBMITTED':
        return 'Batch variation submitted';
      case 'BATCH_REISSUE_COMPLETED':
        return 'Batch variation completed';
      case 'REISSUE_COMPLETED':
        return 'Batch variation completed';

      case 'EMP_ISSUANCE_UKETS_APPLICATION_SUBMITTED':
      case 'EMP_ISSUANCE_CORSIA_APPLICATION_SUBMITTED':
        return 'Submitted';
      case 'EMP_ISSUANCE_UKETS_APPLICATION_APPROVED':
      case 'EMP_ISSUANCE_CORSIA_APPLICATION_APPROVED':
        return 'Approved';
      case 'EMP_ISSUANCE_UKETS_APPLICATION_DEEMED_WITHDRAWN':
      case 'EMP_ISSUANCE_CORSIA_APPLICATION_DEEMED_WITHDRAWN':
        return 'Withdrawn';
      case 'EMP_ISSUANCE_UKETS_PEER_REVIEW_REQUESTED':
      case 'EMP_ISSUANCE_CORSIA_PEER_REVIEW_REQUESTED':
        return 'Peer review requested';
      case 'EMP_ISSUANCE_UKETS_APPLICATION_RETURNED_FOR_AMENDS':
      case 'EMP_ISSUANCE_CORSIA_APPLICATION_RETURNED_FOR_AMENDS':
        return 'Returned to operator for changes';
      case 'EMP_ISSUANCE_UKETS_APPLICATION_AMENDS_SUBMITTED':
      case 'EMP_ISSUANCE_CORSIA_APPLICATION_AMENDS_SUBMITTED':
        return 'Changes submitted';
      case 'EMP_ISSUANCE_UKETS_APPLICATION_PEER_REVIEWER_ACCEPTED':
      case 'EMP_ISSUANCE_CORSIA_APPLICATION_PEER_REVIEWER_ACCEPTED':
        return 'Peer review agreement submitted';
      case 'EMP_ISSUANCE_UKETS_APPLICATION_PEER_REVIEWER_REJECTED':
      case 'EMP_ISSUANCE_CORSIA_APPLICATION_PEER_REVIEWER_REJECTED':
        return 'Peer review disagreement submitted';
      case 'EMP_ISSUANCE_UKETS_RECALLED_FROM_AMENDS':
      case 'EMP_ISSUANCE_CORSIA_RECALLED_FROM_AMENDS':
        return 'Recalled';

      case 'EMP_VARIATION_UKETS_APPLICATION_SUBMITTED':
      case 'EMP_VARIATION_CORSIA_APPLICATION_SUBMITTED':
        return 'Submitted';
      case 'EMP_VARIATION_APPLICATION_CANCELLED':
        return 'Cancelled';
      case 'EMP_VARIATION_UKETS_PEER_REVIEW_REQUESTED':
      case 'EMP_VARIATION_CORSIA_PEER_REVIEW_REQUESTED':
        return 'Peer review requested';
      case 'EMP_VARIATION_UKETS_APPLICATION_PEER_REVIEWER_ACCEPTED':
      case 'EMP_VARIATION_CORSIA_APPLICATION_PEER_REVIEWER_ACCEPTED':
        return 'Peer review agreement submitted';
      case 'EMP_VARIATION_UKETS_APPLICATION_PEER_REVIEWER_REJECTED':
      case 'EMP_VARIATION_CORSIA_APPLICATION_PEER_REVIEWER_REJECTED':
        return 'Peer review disagreement submitted';
      case 'EMP_VARIATION_UKETS_APPLICATION_REGULATOR_LED_APPROVED':
      case 'EMP_VARIATION_CORSIA_APPLICATION_REGULATOR_LED_APPROVED':
      case 'EMP_VARIATION_UKETS_APPLICATION_APPROVED':
        return 'Approved';
      case 'EMP_VARIATION_UKETS_APPLICATION_DEEMED_WITHDRAWN':
        return 'Deemed withdrawn';
      case 'EMP_VARIATION_UKETS_APPLICATION_REJECTED':
        return 'Rejected';
      case 'EMP_VARIATION_CORSIA_APPLICATION_APPROVED':
        return 'Approved';
      case 'EMP_VARIATION_CORSIA_APPLICATION_DEEMED_WITHDRAWN':
        return 'Deemed withdrawn';
      case 'EMP_VARIATION_CORSIA_APPLICATION_REJECTED':
        return 'Rejected';
      case 'EMP_VARIATION_UKETS_APPLICATION_AMENDS_SUBMITTED':
      case 'EMP_VARIATION_CORSIA_APPLICATION_AMENDS_SUBMITTED':
        return 'Changes submitted';
      case 'EMP_VARIATION_UKETS_RECALLED_FROM_AMENDS':
      case 'EMP_VARIATION_CORSIA_RECALLED_FROM_AMENDS':
        return 'Recalled';
      case 'EMP_VARIATION_UKETS_APPLICATION_RETURNED_FOR_AMENDS':
      case 'EMP_VARIATION_CORSIA_APPLICATION_RETURNED_FOR_AMENDS':
        return 'Returned to operator for changes';

      case 'NON_COMPLIANCE_APPLICATION_CLOSED':
        return 'Non Compliance closed';
      case 'NON_COMPLIANCE_APPLICATION_SUBMITTED':
        return 'Non-compliance details provided';
      case 'NON_COMPLIANCE_DAILY_PENALTY_NOTICE_APPLICATION_SUBMITTED':
        return `Initial penalty notice sent to operator`;
      case 'NON_COMPLIANCE_DAILY_PENALTY_NOTICE_PEER_REVIEW_REQUESTED':
        return 'Peer review of initial penalty requested';
      case 'NON_COMPLIANCE_DAILY_PENALTY_NOTICE_PEER_REVIEWER_ACCEPTED':
        return `Peer review agreement for initial penalty submitted`;
      case 'NON_COMPLIANCE_DAILY_PENALTY_NOTICE_PEER_REVIEWER_REJECTED':
        return `Peer review disagreement for initial penalty submitted`;

      case 'NON_COMPLIANCE_NOTICE_OF_INTENT_APPLICATION_SUBMITTED':
        return `Notice of intent sent to operator`;
      case 'NON_COMPLIANCE_NOTICE_OF_INTENT_PEER_REVIEW_REQUESTED':
        return 'Peer review of notice of intent requested';
      case 'NON_COMPLIANCE_NOTICE_OF_INTENT_PEER_REVIEWER_ACCEPTED':
        return `Peer review agreement for notice of intent submitted`;
      case 'NON_COMPLIANCE_NOTICE_OF_INTENT_PEER_REVIEWER_REJECTED':
        return `Peer review disagreement for notice of intent submitted`;

      case 'NON_COMPLIANCE_CIVIL_PENALTY_APPLICATION_SUBMITTED':
        return `Penalty notice sent to operator`;
      case 'NON_COMPLIANCE_CIVIL_PENALTY_PEER_REVIEW_REQUESTED':
        return 'Peer review of penalty requested';
      case 'NON_COMPLIANCE_CIVIL_PENALTY_PEER_REVIEWER_ACCEPTED':
        return `Peer review agreement for penalty submitted`;
      case 'NON_COMPLIANCE_CIVIL_PENALTY_PEER_REVIEWER_REJECTED':
        return `Peer review disagreement for penalty submitted`;

      case 'NON_COMPLIANCE_FINAL_DETERMINATION_APPLICATION_SUBMITTED':
        return `Conclusion provided`;

      case 'WITHHOLDING_OF_ALLOWANCES_APPLICATION_SUBMITTED':
        return 'Withholding of allowances submitted';
      case 'WITHHOLDING_OF_ALLOWANCES_APPLICATION_WITHDRAWN':
        return 'Withholding of allowances notice withdrawn';
      case 'WITHHOLDING_OF_ALLOWANCES_APPLICATION_CLOSED':
        return 'Withholding of allowances closed';
      case 'WITHHOLDING_OF_ALLOWANCES_PEER_REVIEW_REQUESTED':
        return 'Peer review requested';
      case 'WITHHOLDING_OF_ALLOWANCES_APPLICATION_PEER_REVIEWER_ACCEPTED':
        return `Peer review agreement submitted`;
      case 'WITHHOLDING_OF_ALLOWANCES_APPLICATION_PEER_REVIEWER_REJECTED':
        return `Peer review disagreement submitted`;
      case 'WITHHOLDING_OF_ALLOWANCES_APPLICATION_CANCELLED':
        return `Withholding of allowances cancelled`;

      case 'AVIATION_ACCOUNT_CLOSURE_CANCELLED':
        return 'Account closure cancelled';
      case 'AVIATION_ACCOUNT_CLOSURE_SUBMITTED':
        return 'Account closed';

      case 'AVIATION_DRE_APPLICATION_CANCELLED':
        return 'Reportable emissions cancelled';
      case 'AVIATION_AER_UKETS_APPLICATION_CANCELLED_DUE_TO_DRE':
        return 'Cancelled due to a determination of emissions workflow';
      case 'AVIATION_DRE_UKETS_PEER_REVIEW_REQUESTED':
        return 'Peer review requested';
      case 'AVIATION_DRE_UKETS_PEER_REVIEWER_ACCEPTED':
        return 'Peer review agreement submitted';
      case 'AVIATION_DRE_UKETS_PEER_REVIEWER_REJECTED':
        return 'Peer review disagreement submitted';
      case 'AVIATION_DRE_UKETS_APPLICATION_SUBMITTED':
        return 'Aviation emissions updated';

      case 'AVIATION_AER_UKETS_APPLICATION_SENT_TO_VERIFIER':
      case 'AVIATION_AER_CORSIA_APPLICATION_SENT_TO_VERIFIER':
        return 'Emissions report submitted to verifier';
      case 'AVIATION_AER_UKETS_APPLICATION_VERIFICATION_SUBMITTED':
      case 'AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMITTED':
        return 'Verification statement submitted to operator';
      case 'AVIATION_AER_UKETS_APPLICATION_COMPLETED':
      case 'AVIATION_AER_CORSIA_APPLICATION_COMPLETED':
        return 'Emissions report reviewed';
      case 'AVIATION_AER_UKETS_APPLICATION_REVIEW_SKIPPED':
      case 'AVIATION_AER_CORSIA_APPLICATION_REVIEW_SKIPPED':
        return 'Completed without review';

      case 'AVIATION_AER_UKETS_APPLICATION_SUBMITTED':
      case 'AVIATION_AER_CORSIA_APPLICATION_SUBMITTED':
        return 'Emissions report submitted to regulator';
      case 'AVIATION_AER_RECALLED_FROM_VERIFICATION':
        return 'Recalled from verifier';
      case 'AVIATION_AER_APPLICATION_CANCELLED_DUE_TO_EXEPMT':
        return 'Cancelled due to exempt reporting status';
      case 'AVIATION_AER_APPLICATION_RE_INITIATED':
        return 'Re-initiate after exemption';
      case 'AVIATION_AER_UKETS_APPLICATION_RETURNED_FOR_AMENDS':
      case 'AVIATION_AER_CORSIA_APPLICATION_RETURNED_FOR_AMENDS':
        return 'Returned to operator for changes';

      case 'AVIATION_AER_UKETS_APPLICATION_AMENDS_SENT_TO_VERIFIER':
      case 'AVIATION_AER_CORSIA_APPLICATION_AMENDS_SENT_TO_VERIFIER':
      case 'AVIATION_AER_UKETS_APPLICATION_AMENDS_SUBMITTED':
      case 'AVIATION_AER_CORSIA_APPLICATION_AMENDS_SUBMITTED':
        return 'Changes submitted';

      case 'AVIATION_AER_CORSIA_VERIFICATION_RETURNED_TO_OPERATOR':
      case 'AVIATION_AER_UKETS_VERIFICATION_RETURNED_TO_OPERATOR':
        return 'Verifier returned report to operator for changes';

      case 'AVIATION_VIR_APPLICATION_SUBMITTED':
        return 'Verifier improvement report submitted';
      case 'AVIATION_VIR_APPLICATION_REVIEWED':
        return 'Verifier improvement report decision submitted';
      case 'AVIATION_VIR_APPLICATION_RESPONDED_TO_REGULATOR_COMMENTS':
        return 'Follow up response submitted';

      case 'AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_APPLICATION_SUBMITTED':
        return 'Annual offsetting requirements submitted to Operator';
      case 'AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_APPLICATION_PEER_REVIEW_REQUESTED':
        return 'Peer review requested';
      case 'AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_APPLICATION_PEER_REVIEW_ACCEPTED':
        return 'Peer review agreement submitted';
      case 'AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_APPLICATION_PEER_REVIEW_REJECTED':
        return 'Peer review disagreement submitted';
      case 'AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_APPLICATION_CANCELLED':
        return 'Annual offsetting requirements cancelled';

      case 'AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_APPLICATION_PEER_REVIEW_REQUESTED':
        return 'Peer review requested';
      case 'AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_APPLICATION_PEER_REVIEW_ACCEPTED':
        return 'Peer review agreement submitted';
      case 'AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_APPLICATION_PEER_REVIEW_REJECTED':
        return 'Peer review disagreement submitted';
      case 'AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_APPLICATION_CANCELLED':
        return '3-year offsetting requirements cancelled';
      case 'AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_APPLICATION_SUBMITTED':
        return '3-year offsetting requirements submitted to Operator';

      case 'RETURN_OF_ALLOWANCES_APPLICATION_SUBMITTED':
        return 'Return of allowances submitted';
      case 'RETURN_OF_ALLOWANCES_PEER_REVIEW_REQUESTED':
        return 'Peer review of return of allowances requested';
      case 'RETURN_OF_ALLOWANCES_APPLICATION_PEER_REVIEWER_ACCEPTED':
        return `Peer review agreement for return of allowances submitted`;
      case 'RETURN_OF_ALLOWANCES_APPLICATION_PEER_REVIEWER_REJECTED':
        return `Peer review disagreement for return of allowances submitted`;
      case 'RETURN_OF_ALLOWANCES_APPLICATION_CANCELLED':
        return `Return of allowances cancelled`;
      case 'RETURN_OF_ALLOWANCES_RETURNED_APPLICATION_COMPLETED':
        return `Returned allowances submitted`;

      case 'INSTALLATION_ONSITE_INSPECTION_APPLICATION_PEER_REVIEW_REQUESTED':
      case 'INSTALLATION_AUDIT_APPLICATION_PEER_REVIEW_REQUESTED':
        return `Peer review requested`;
      case 'INSTALLATION_ONSITE_INSPECTION_APPLICATION_PEER_REVIEWER_ACCEPTED':
      case 'INSTALLATION_AUDIT_APPLICATION_PEER_REVIEWER_ACCEPTED':
        return 'Peer review agreement submitted';
      case 'INSTALLATION_ONSITE_INSPECTION_APPLICATION_PEER_REVIEWER_REJECTED':
      case 'INSTALLATION_AUDIT_APPLICATION_PEER_REVIEWER_REJECTED':
        return 'Peer review disagreement submitted';

      case 'INSTALLATION_ONSITE_INSPECTION_OPERATOR_RESPONDED':
        return 'On-site inspection submitted to Regulator';
      case 'INSTALLATION_ONSITE_INSPECTION_APPLICATION_CANCELLED':
        return 'On-site inspection cancelled';
      case 'INSTALLATION_ONSITE_INSPECTION_APPLICATION_SUBMITTED':
        return 'On-site inspection submitted to Operator';

      case 'INSTALLATION_AUDIT_APPLICATION_CANCELLED':
        return 'Audit report cancelled';
      case 'INSTALLATION_AUDIT_OPERATOR_RESPONDED':
        return 'Audit report submitted to Regulator';
      case 'INSTALLATION_AUDIT_APPLICATION_SUBMITTED':
        return 'Audit report submitted to Operator';

      case 'BDR_APPLICATION_SENT_TO_VERIFIER':
      case 'BDR_APPLICATION_AMENDS_SENT_TO_VERIFIER':
        return 'Baseline data report submitted to verifier';
      case 'BDR_APPLICATION_SENT_TO_REGULATOR':
        return 'Baseline data report submitted to regulator';
      case 'BDR_RECALLED_FROM_VERIFICATION':
        return 'Baseline data report recalled';
      case 'BDR_APPLICATION_VERIFICATION_SUBMITTED':
        return 'Baseline data report verification statement submitted to operator';
      case 'BDR_VERIFICATION_RETURNED_TO_OPERATOR':
        return 'Baseline data report returned to operator for changes';
      case 'BDR_REGULATOR_REVIEW_RETURNED_FOR_AMENDS':
        return 'Baseline data report returned to operator';
      case 'BDR_APPLICATION_PEER_REVIEW_REQUESTED':
        return 'Peer review requested';
      case 'BDR_APPLICATION_COMPLETED':
        return 'Baseline data report reviewed';
      case 'BDR_APPLICATION_RE_INITIATED':
        return 'Baseline data report reopened';
      case 'BDR_APPLICATION_PEER_REVIEW_ACCEPTED':
        return 'Peer review agreement submitted';
      case 'BDR_APPLICATION_PEER_REVIEW_REJECTED':
        return 'Peer review disagreement submitted';

      case 'PERMANENT_CESSATION_SUBMITTED':
        return 'Permanent cessation started';
      case 'PERMANENT_CESSATION_APPLICATION_CANCELLED':
        return 'Permanent cessation cancelled';
      case 'PERMANENT_CESSATION_APPLICATION_PEER_REVIEW_REQUESTED':
        return 'Peer review requested';
      case 'PERMANENT_CESSATION_APPLICATION_PEER_REVIEW_REJECTED':
        return 'Peer review disagreement submitted';
      case 'PERMANENT_CESSATION_APPLICATION_PEER_REVIEW_ACCEPTED':
        return 'Peer review agreement submitted';
      case 'PERMANENT_CESSATION_APPLICATION_SUBMITTED':
        return 'Permanent cessation notice sent to operator';

      case 'AVIATION_DOE_CORSIA_SUBMIT_CANCELLED':
        return 'Estimation of emissions cancelled';
      case 'AVIATION_DOE_CORSIA_SUBMITTED':
        return 'Aviation emissions updated';
      case 'AVIATION_AER_CORSIA_APPLICATION_CANCELLED_DUE_TO_DOE':
        return 'Cancelled due to an estimation of emissions workflow';
      case 'AVIATION_DOE_CORSIA_PEER_REVIEW_REQUESTED':
        return 'Peer review requested';
      case 'AVIATION_DOE_CORSIA_PEER_REVIEWER_ACCEPTED':
        return 'Peer review agreement submitted';
      case 'AVIATION_DOE_CORSIA_PEER_REVIEWER_REJECTED':
        return 'Peer review disagreement submitted';

      case 'ALR_APPLICATION_SENT_TO_VERIFIER':
        return 'Activity level report submitted to verifier';
      case 'ALR_APPLICATION_VERIFICATION_SUBMITTED':
        return 'Activity level report submitted to operator';
      case 'ALR_VERIFICATION_RETURNED_TO_OPERATOR':
        return 'Activity level report returned to operator for changes';
      case 'ALR_RECALLED_FROM_VERIFICATION':
        return 'Activity level report recalled';

      default:
        return 'Approved Application';
    }
  }
}
