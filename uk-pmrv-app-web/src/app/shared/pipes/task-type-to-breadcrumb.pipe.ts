import { Pipe, PipeTransform } from '@angular/core';

import { RequestTaskDTO } from 'pmrv-api';

@Pipe({
  name: 'taskTypeToBreadcrumb',
  standalone: true,
  pure: true,
})
export class TaskTypeToBreadcrumbPipe implements PipeTransform {
  transform(type: RequestTaskDTO['type']): string {
    switch (type) {
      // common
      case 'PERMIT_ISSUANCE_WAIT_FOR_RDE_RESPONSE':
      case 'PERMIT_SURRENDER_WAIT_FOR_RDE_RESPONSE':
      case 'PERMIT_VARIATION_WAIT_FOR_RDE_RESPONSE':
      case 'PERMIT_TRANSFER_B_WAIT_FOR_RDE_RESPONSE':
        return 'Extension request sent to operator';
      case 'PERMIT_ISSUANCE_RDE_RESPONSE_SUBMIT':
      case 'PERMIT_SURRENDER_RDE_RESPONSE_SUBMIT':
      case 'PERMIT_VARIATION_RDE_RESPONSE_SUBMIT':
      case 'PERMIT_TRANSFER_B_RDE_RESPONSE_SUBMIT':
        return `Respond to regulator's extension request`;
      case 'PERMIT_ISSUANCE_WAIT_FOR_RFI_RESPONSE':
      case 'PERMIT_SURRENDER_WAIT_FOR_RFI_RESPONSE':
      case 'PERMIT_NOTIFICATION_WAIT_FOR_RFI_RESPONSE':
      case 'PERMIT_VARIATION_WAIT_FOR_RFI_RESPONSE':
      case 'PERMIT_TRANSFER_B_WAIT_FOR_RFI_RESPONSE':
      case 'VIR_WAIT_FOR_RFI_RESPONSE':
      case 'AIR_WAIT_FOR_RFI_RESPONSE':
      case 'AVIATION_VIR_WAIT_FOR_RFI_RESPONSE':
        return 'Request for information sent to operator';
      case 'PERMIT_ISSUANCE_RFI_RESPONSE_SUBMIT':
      case 'PERMIT_SURRENDER_RFI_RESPONSE_SUBMIT':
      case 'PERMIT_NOTIFICATION_RFI_RESPONSE_SUBMIT':
      case 'PERMIT_VARIATION_RFI_RESPONSE_SUBMIT':
      case 'PERMIT_TRANSFER_B_RFI_RESPONSE_SUBMIT':
      case 'VIR_RFI_RESPONSE_SUBMIT':
      case 'AIR_RFI_RESPONSE_SUBMIT':
      case 'AVIATION_VIR_RFI_RESPONSE_SUBMIT':
        return `Respond to regulator's request for information`;

      // account
      case 'ACCOUNT_USERS_SETUP':
        return 'Manage account users';
      case 'INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW':
        return 'Review installation account application';
      case 'INSTALLATION_ACCOUNT_OPENING_ARCHIVE':
        return 'Archive installation account application';
      case 'INSTALLATION_ACCOUNT_TRANSFERRING_ARCHIVE':
        return 'Share transfer code';

      // permit issuance
      case 'PERMIT_ISSUANCE_APPLICATION_SUBMIT':
        return 'Apply for a permit';
      case 'PERMIT_ISSUANCE_WAIT_FOR_REVIEW':
        return 'Permit application sent to regulator';
      case 'PERMIT_ISSUANCE_APPLICATION_REVIEW':
        return 'Review permit application';
      case 'PERMIT_ISSUANCE_WAIT_FOR_AMENDS':
        return 'Permit application returned to operator';
      case 'PERMIT_ISSUANCE_WAIT_FOR_PEER_REVIEW':
        return 'Permit application sent to peer reviewer';
      case 'PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW':
        return 'Peer review permit application';
      case 'PERMIT_ISSUANCE_APPLICATION_AMENDS_SUBMIT':
        return 'Amend your permit application';
      case 'PERMIT_ISSUANCE_MAKE_PAYMENT':
        return 'Pay for permit application';
      case 'PERMIT_ISSUANCE_TRACK_PAYMENT':
      case 'PERMIT_ISSUANCE_CONFIRM_PAYMENT':
        return 'Track payment for permit application';

      // revocation
      case 'PERMIT_REVOCATION_APPLICATION_SUBMIT':
        return 'Complete permit revocation';
      case 'PERMIT_REVOCATION_WAIT_FOR_PEER_REVIEW':
        return 'Permit revocation sent to peer reviewer';
      case 'PERMIT_REVOCATION_WAIT_FOR_APPEAL':
        return 'Revocation sent to regulator';
      case 'PERMIT_REVOCATION_APPLICATION_PEER_REVIEW':
        return 'Peer review permit revocation';
      case 'PERMIT_REVOCATION_CESSATION_SUBMIT':
        return 'Confirm end of regulated activities';
      case 'PERMIT_REVOCATION_MAKE_PAYMENT':
        return 'Pay for permit revocation';
      case 'PERMIT_REVOCATION_TRACK_PAYMENT':
      case 'PERMIT_REVOCATION_CONFIRM_PAYMENT':
        return 'Track payment for permit revocation';

      // surrender
      case 'PERMIT_SURRENDER_APPLICATION_SUBMIT':
        return 'Complete permit surrender';
      case 'PERMIT_SURRENDER_WAIT_FOR_REVIEW':
        return 'Permit surrender sent to regulator';
      case 'PERMIT_SURRENDER_WAIT_FOR_PEER_REVIEW':
        return 'Permit surrender sent to peer reviewer';
      case 'PERMIT_SURRENDER_APPLICATION_REVIEW':
        return 'Review permit surrender';
      case 'PERMIT_SURRENDER_CESSATION_SUBMIT':
        return 'Confirm permit surrender';
      case 'PERMIT_SURRENDER_APPLICATION_PEER_REVIEW':
        return 'Peer review permit surrender';
      case 'PERMIT_SURRENDER_MAKE_PAYMENT':
        return 'Pay for permit surrender';
      case 'PERMIT_SURRENDER_TRACK_PAYMENT':
      case 'PERMIT_SURRENDER_CONFIRM_PAYMENT':
        return 'Track payment for permit surrender';

      // notification
      case 'PERMIT_NOTIFICATION_APPLICATION_SUBMIT':
        return 'Notify regulator about a change';
      case 'PERMIT_NOTIFICATION_WAIT_FOR_REVIEW':
        return 'Notification sent to regulator';
      case 'PERMIT_NOTIFICATION_APPLICATION_REVIEW':
        return 'Review notification';
      case 'PERMIT_NOTIFICATION_WAIT_FOR_PEER_REVIEW':
        return 'Notification sent to peer reviewer';
      case 'PERMIT_NOTIFICATION_APPLICATION_PEER_REVIEW':
        return 'Peer review notification';
      case 'PERMIT_NOTIFICATION_FOLLOW_UP':
        return 'Respond to notification follow up';
      case 'PERMIT_NOTIFICATION_WAIT_FOR_FOLLOW_UP':
        return 'Notification follow up request sent to operator';
      case 'PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_REVIEW':
        return 'Review notification follow up';
      case 'PERMIT_NOTIFICATION_FOLLOW_UP_WAIT_FOR_REVIEW':
        return 'Notification follow up sent to regulator';
      case 'PERMIT_NOTIFICATION_FOLLOW_UP_WAIT_FOR_AMENDS':
        return 'Notification follow up returned to operator';
      case 'PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_AMENDS_SUBMIT':
        return 'Amend your notification follow up';

      // variation
      case 'PERMIT_VARIATION_APPLICATION_SUBMIT':
        return 'Apply for a permit variation';
      case 'PERMIT_VARIATION_REGULATOR_LED_APPLICATION_SUBMIT':
        return 'Permit variation';
      case 'PERMIT_VARIATION_APPLICATION_REVIEW':
        return 'Variation determination';
      case 'PERMIT_VARIATION_WAIT_FOR_REVIEW':
        return 'Permit variation sent to regulator';
      case 'PERMIT_VARIATION_APPLICATION_PEER_REVIEW':
        return 'Variation determination';
      case 'PERMIT_VARIATION_WAIT_FOR_PEER_REVIEW':
        return 'Permit variation sent to peer reviewer';
      case 'PERMIT_VARIATION_REGULATOR_LED_APPLICATION_PEER_REVIEW':
        return 'Peer review permit variation';
      case 'PERMIT_VARIATION_REGULATOR_LED_WAIT_FOR_PEER_REVIEW':
        return 'Permit variation sent to peer reviewer';
      case 'PERMIT_VARIATION_WAIT_FOR_AMENDS':
        return 'Permit variation returned to operator';
      case 'PERMIT_VARIATION_APPLICATION_AMENDS_SUBMIT':
        return 'Amend your permit variation';
      case 'PERMIT_VARIATION_MAKE_PAYMENT':
      case 'PERMIT_VARIATION_REGULATOR_LED_MAKE_PAYMENT':
        return 'Pay for permit variation';
      case 'PERMIT_VARIATION_TRACK_PAYMENT':
      case 'PERMIT_VARIATION_CONFIRM_PAYMENT':
      case 'PERMIT_VARIATION_REGULATOR_LED_TRACK_PAYMENT':
      case 'PERMIT_VARIATION_REGULATOR_LED_CONFIRM_PAYMENT':
        return 'Track payment for permit variation';

      //transfer
      case 'PERMIT_TRANSFER_A_APPLICATION_SUBMIT':
        return 'Apply for a permit transfer';
      case 'PERMIT_TRANSFER_A_WAIT_FOR_TRANSFER':
        return 'Permit transfer sent to new operator';
      case 'PERMIT_TRANSFER_B_APPLICATION_SUBMIT':
        return 'Transfer application';
      case 'PERMIT_TRANSFER_A_MAKE_PAYMENT':
      case 'PERMIT_TRANSFER_B_MAKE_PAYMENT':
        return 'Pay for permit transfer';
      case 'PERMIT_TRANSFER_A_TRACK_PAYMENT':
      case 'PERMIT_TRANSFER_A_CONFIRM_PAYMENT':
      case 'PERMIT_TRANSFER_B_TRACK_PAYMENT':
      case 'PERMIT_TRANSFER_B_CONFIRM_PAYMENT':
        return 'Track payment for permit transfer application';
      case 'PERMIT_TRANSFER_B_APPLICATION_REVIEW':
        return 'Review permit transfer';
      case 'PERMIT_TRANSFER_B_WAIT_FOR_REVIEW':
        return 'Permit transfer sent to regulator';
      case 'PERMIT_TRANSFER_B_WAIT_FOR_AMENDS':
        return 'Permit transfer returned to operator';
      case 'PERMIT_TRANSFER_B_APPLICATION_AMENDS_SUBMIT':
        return 'Amend your permit transfer';
      case 'PERMIT_TRANSFER_B_WAIT_FOR_PEER_REVIEW':
        return 'Permit transfer sent to peer reviewer';
      case 'PERMIT_TRANSFER_B_APPLICATION_PEER_REVIEW':
        return 'Peer review permit transfer';

      // AER
      case 'AER_APPLICATION_SUBMIT':
        return `Emission report`;
      case 'AER_APPLICATION_REVIEW':
        return `Review emission report`;
      case 'AER_WAIT_FOR_REVIEW':
        return `Emission report sent to regulator`;
      case 'AER_APPLICATION_AMENDS_SUBMIT':
        return `Amend emission report`;
      case 'AER_WAIT_FOR_AMENDS':
        return `Emission report returned to operator`;
      case 'AER_APPLICATION_VERIFICATION_SUBMIT':
      case 'AER_AMEND_APPLICATION_VERIFICATION_SUBMIT':
        return `Verify emission report`;
      case 'AER_WAIT_FOR_VERIFICATION':
      case 'AER_AMEND_WAIT_FOR_VERIFICATION':
        return `Emission report sent to verifier`;

      // VIR
      case 'VIR_APPLICATION_SUBMIT':
        return `Verifier improvement report`;
      case 'VIR_APPLICATION_REVIEW':
        return `Review verifier improvement report`;
      case 'VIR_WAIT_FOR_REVIEW':
        return `Verifier improvement report sent to regulator`;
      case 'VIR_RESPOND_TO_REGULATOR_COMMENTS':
        return `Respond to regulator comments`;

      // AIR
      case 'AIR_APPLICATION_SUBMIT':
        return `Annual improvement report`;
      case 'AIR_APPLICATION_REVIEW':
        return `Annual improvement report review`;
      case 'AIR_WAIT_FOR_REVIEW':
        return `Annual improvement report sent to regulator`;
      case 'AIR_RESPOND_TO_REGULATOR_COMMENTS':
        return `Annual improvement report follow up`;

      // DRE
      case 'DRE_APPLICATION_SUBMIT':
        return `Determine reportable emissions`;
      case 'DRE_MAKE_PAYMENT':
        return `Pay reportable emissions fee`;
      case 'DRE_TRACK_PAYMENT':
      case 'DRE_CONFIRM_PAYMENT':
        return `Track payment for reportable emissions`;
      case 'DRE_WAIT_FOR_PEER_REVIEW':
        return 'Reportable emissions sent for peer review';
      case 'DRE_APPLICATION_PEER_REVIEW':
        return 'Peer review reportable emissions';

      // verification
      case 'NEW_VERIFICATION_BODY_EMITTER':
        return 'New appointment from emitter';
      case 'VERIFIER_NO_LONGER_AVAILABLE':
        return 'Verification body is no longer available';
      case 'AVIATION_AER_UKETS_APPLICATION_VERIFICATION_SUBMIT':
      case 'AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMIT':
      case 'AVIATION_AER_UKETS_AMEND_APPLICATION_VERIFICATION_SUBMIT':
      case 'AVIATION_AER_CORSIA_AMEND_APPLICATION_VERIFICATION_SUBMIT':
        return `Verify emissions report`;

      // audit report
      case 'INSTALLATION_AUDIT_APPLICATION_SUBMIT':
        return 'Create audit report';
      case 'INSTALLATION_AUDIT_WAIT_FOR_PEER_REVIEW':
        return 'Audit report sent to peer reviewer';
      case 'INSTALLATION_AUDIT_APPLICATION_PEER_REVIEW':
        return 'Peer review audit report';
      case 'INSTALLATION_AUDIT_OPERATOR_RESPOND_TO_FOLLOWUP_ACTIONS':
        return 'Audit report';

      // ON SITE INSPECTION
      case 'INSTALLATION_ONSITE_INSPECTION_APPLICATION_SUBMIT':
        return 'Create on-site inspection';
      case 'INSTALLATION_ONSITE_INSPECTION_WAIT_FOR_PEER_REVIEW':
        return 'On-site inspection sent to peer reviewer';
      case 'INSTALLATION_ONSITE_INSPECTION_APPLICATION_PEER_REVIEW':
        return 'Peer review on-site inspection';
      case 'INSTALLATION_ONSITE_INSPECTION_OPERATOR_RESPOND_TO_FOLLOWUP_ACTIONS':
        return 'On-site inspection';

      // non compliance
      case 'NON_COMPLIANCE_APPLICATION_SUBMIT':
        return 'Provide details of breach: non-compliance';
      case 'NON_COMPLIANCE_DAILY_PENALTY_NOTICE':
        return 'Upload penalty notice: non-compliance';
      case 'NON_COMPLIANCE_DAILY_PENALTY_NOTICE_WAIT_FOR_PEER_REVIEW':
        return 'Initial penalty notice sent to peer reviewer: non-compliance';
      case 'NON_COMPLIANCE_DAILY_PENALTY_NOTICE_APPLICATION_PEER_REVIEW':
        return 'Peer review initial penalty: non-compliance';
      case 'NON_COMPLIANCE_NOTICE_OF_INTENT':
        return 'Upload notice of intent: non-compliance';
      case 'NON_COMPLIANCE_CIVIL_PENALTY':
        return 'Upload penalty notice: non-compliance';
      case 'NON_COMPLIANCE_FINAL_DETERMINATION':
        return 'Provide conclusion of non-compliance';
      case 'NON_COMPLIANCE_NOTICE_OF_INTENT_WAIT_FOR_PEER_REVIEW':
        return 'Notice of intent sent to peer reviewer: non-compliance';
      case 'NON_COMPLIANCE_NOTICE_OF_INTENT_APPLICATION_PEER_REVIEW':
        return 'Peer review notice of intent: non-compliance';
      case 'NON_COMPLIANCE_CIVIL_PENALTY_WAIT_FOR_PEER_REVIEW':
        return 'Penalty sent to peer reviewer: non-compliance';
      case 'NON_COMPLIANCE_CIVIL_PENALTY_APPLICATION_PEER_REVIEW':
        return 'Peer review penalty: non-compliance';

      //doal
      case 'DOAL_APPLICATION_SUBMIT':
        return 'Determination of activity level change';
      case 'DOAL_AUTHORITY_RESPONSE':
        return 'Provide UK ETS Authority response for activity Level Change';
      case 'DOAL_WAIT_FOR_PEER_REVIEW':
        return 'Activity level determination sent to peer reviewer';
      case 'DOAL_APPLICATION_PEER_REVIEW':
        return 'Activity level determination peer review';

      // withholding of allowances
      case 'WITHHOLDING_OF_ALLOWANCES_APPLICATION_SUBMIT':
        return 'Withholding of allowances';
      case 'WITHHOLDING_OF_ALLOWANCES_APPLICATION_PEER_REVIEW':
        return 'Peer review withholding of allowances';
      case 'WITHHOLDING_OF_ALLOWANCES_WAIT_FOR_PEER_REVIEW':
        return 'Withholding of allowances sent to peer reviewer';
      case 'WITHHOLDING_OF_ALLOWANCES_WITHDRAWAL_APPLICATION_SUBMIT':
        return 'Withdraw withholding of allowances notice';

      // AVIATION
      // Common
      case 'EMP_ISSUANCE_UKETS_APPLICATION_SUBMIT':
      case 'EMP_ISSUANCE_CORSIA_APPLICATION_SUBMIT':
        return 'Apply for an emissions monitoring plan';
      case 'EMP_ISSUANCE_UKETS_APPLICATION_REVIEW':
      case 'EMP_ISSUANCE_CORSIA_APPLICATION_REVIEW':
        return 'Review emissions monitoring plan application';
      case 'EMP_ISSUANCE_UKETS_WAIT_FOR_REVIEW':
      case 'EMP_ISSUANCE_CORSIA_WAIT_FOR_REVIEW':
        return 'Emissions monitoring plan application sent to regulator';
      case 'EMP_ISSUANCE_UKETS_APPLICATION_PEER_REVIEW':
      case 'EMP_ISSUANCE_CORSIA_APPLICATION_PEER_REVIEW':
        return 'Peer review emissions monitoring plan application';
      case 'EMP_ISSUANCE_UKETS_WAIT_FOR_PEER_REVIEW':
      case 'EMP_ISSUANCE_CORSIA_WAIT_FOR_PEER_REVIEW':
        return 'Emissions monitoring plan application sent to peer reviewer';
      case 'EMP_ISSUANCE_UKETS_WAIT_FOR_AMENDS':
      case 'EMP_ISSUANCE_CORSIA_WAIT_FOR_AMENDS':
        return 'Emissions monitoring plan application returned to operator';
      case 'EMP_ISSUANCE_UKETS_APPLICATION_AMENDS_SUBMIT':
      case 'EMP_ISSUANCE_CORSIA_APPLICATION_AMENDS_SUBMIT':
        return 'Amend your emissions monitoring plan';

      case 'EMP_ISSUANCE_UKETS_MAKE_PAYMENT':
      case 'EMP_ISSUANCE_CORSIA_MAKE_PAYMENT':
        return 'Pay emissions monitoring plan application fee';
      case 'EMP_ISSUANCE_UKETS_TRACK_PAYMENT':
      case 'EMP_ISSUANCE_UKETS_CONFIRM_PAYMENT':
      case 'EMP_ISSUANCE_CORSIA_TRACK_PAYMENT':
      case 'EMP_ISSUANCE_CORSIA_CONFIRM_PAYMENT':
        return 'Track payment for emissions monitoring plan application';

      case 'AVIATION_AER_UKETS_WAIT_FOR_REVIEW':
      case 'AVIATION_AER_CORSIA_WAIT_FOR_REVIEW':
        return 'Emissions report sent to regulator';
      case 'AVIATION_AER_UKETS_APPLICATION_SUBMIT':
      case 'AVIATION_AER_CORSIA_APPLICATION_SUBMIT':
        return `Emissions report`;
      case 'AVIATION_AER_UKETS_APPLICATION_REVIEW':
      case 'AVIATION_AER_CORSIA_APPLICATION_REVIEW':
        return `Review emissions report`;
      case 'AVIATION_AER_UKETS_AMEND_WAIT_FOR_VERIFICATION':
      case 'AVIATION_AER_CORSIA_AMEND_WAIT_FOR_VERIFICATION':
      case 'AVIATION_AER_UKETS_WAIT_FOR_VERIFICATION':
      case 'AVIATION_AER_CORSIA_WAIT_FOR_VERIFICATION':
        return `Emissions report sent to verifier`;
      case 'AVIATION_AER_UKETS_WAIT_FOR_AMENDS':
      case 'AVIATION_AER_CORSIA_WAIT_FOR_AMENDS':
        return `Emission report returned to operator`;
      case 'AVIATION_AER_UKETS_APPLICATION_AMENDS_SUBMIT':
      case 'AVIATION_AER_CORSIA_APPLICATION_AMENDS_SUBMIT':
        return 'Amend your emissions report';
      case 'AVIATION_DRE_UKETS_MAKE_PAYMENT':
        return `Pay emissions determination fee`;
      case 'AVIATION_DRE_UKETS_TRACK_PAYMENT':
      case 'AVIATION_DRE_UKETS_CONFIRM_PAYMENT':
        return `Track payment for emissions determination`;
      case 'AVIATION_DRE_UKETS_APPLICATION_PEER_REVIEW':
        return 'Peer review aviation emissions report determination';
      case 'AVIATION_DRE_UKETS_WAIT_FOR_PEER_REVIEW':
        return 'Aviation emissions report determination sent to peer reviewer';

      case 'AVIATION_DRE_UKETS_APPLICATION_SUBMIT':
        return `Determine emissions`;

      case 'AVIATION_ACCOUNT_CLOSURE_SUBMIT':
        return 'Close account';

      case 'EMP_VARIATION_UKETS_APPLICATION_SUBMIT':
      case 'EMP_VARIATION_CORSIA_APPLICATION_SUBMIT':
        return 'Apply to vary your emissions monitoring plan';
      case 'EMP_VARIATION_UKETS_APPLICATION_REVIEW':
      case 'EMP_VARIATION_CORSIA_APPLICATION_REVIEW':
        return 'Review emissions monitoring plan variation';
      case 'EMP_VARIATION_UKETS_WAIT_FOR_REVIEW':
      case 'EMP_VARIATION_CORSIA_WAIT_FOR_REVIEW':
        return 'Emissions monitoring plan variation sent to regulator';
      case 'EMP_VARIATION_UKETS_WAIT_FOR_AMENDS':
      case 'EMP_VARIATION_CORSIA_WAIT_FOR_AMENDS':
        return 'Emissions monitoring plan variation returned to operator';
      case 'EMP_VARIATION_UKETS_WAIT_FOR_PEER_REVIEW':
      case 'EMP_VARIATION_CORSIA_WAIT_FOR_PEER_REVIEW':
        return 'Emissions monitoring plan variation sent to peer reviewer';
      case 'EMP_VARIATION_UKETS_APPLICATION_PEER_REVIEW':
      case 'EMP_VARIATION_CORSIA_APPLICATION_PEER_REVIEW':
        return 'Peer review emissions monitoring plan variation';
      case 'EMP_VARIATION_UKETS_REGULATOR_LED_APPLICATION_SUBMIT':
      case 'EMP_VARIATION_CORSIA_REGULATOR_LED_APPLICATION_SUBMIT':
        return 'Vary the emissions monitoring plan';
      case 'EMP_VARIATION_UKETS_REGULATOR_LED_WAIT_FOR_PEER_REVIEW':
      case 'EMP_VARIATION_CORSIA_REGULATOR_LED_WAIT_FOR_PEER_REVIEW':
        return 'Vary the emissions monitoring plan sent to peer reviewer';
      case 'EMP_VARIATION_UKETS_REGULATOR_LED_APPLICATION_PEER_REVIEW':
      case 'EMP_VARIATION_CORSIA_REGULATOR_LED_APPLICATION_PEER_REVIEW':
        return 'Peer review vary the emissions monitoring plan';
      case 'EMP_VARIATION_UKETS_APPLICATION_AMENDS_SUBMIT':
      case 'EMP_VARIATION_CORSIA_APPLICATION_AMENDS_SUBMIT':
        return 'Amend your emissions monitoring plan variation';

      case 'AVIATION_VIR_APPLICATION_SUBMIT':
      case 'AVIATION_VIR_RESPOND_TO_REGULATOR_COMMENTS':
        return `Verifier improvement report`;
      case 'AVIATION_VIR_WAIT_FOR_REVIEW':
        return `Verifier improvement report sent to regulator`;
      case 'AVIATION_VIR_APPLICATION_REVIEW':
        return `Review verifier improvement report`;

      case 'RETURN_OF_ALLOWANCES_APPLICATION_SUBMIT':
      case 'RETURN_OF_ALLOWANCES_WAIT_FOR_PEER_REVIEW':
        return 'Return of allowances';
      case 'RETURN_OF_ALLOWANCES_APPLICATION_PEER_REVIEW':
        return 'Peer review return of allowances';
      case 'RETURN_OF_ALLOWANCES_RETURNED_APPLICATION_SUBMIT':
        return 'Returned allowances';

      case 'AVIATION_NON_COMPLIANCE_APPLICATION_SUBMIT':
        return 'Provide details of breach: non-compliance';
      case 'AVIATION_NON_COMPLIANCE_DAILY_PENALTY_NOTICE':
        return 'Upload daily penalty notice';
      case 'AVIATION_NON_COMPLIANCE_DAILY_PENALTY_NOTICE_WAIT_FOR_PEER_REVIEW':
        return 'Initial penalty notice sent to peer reviewer: non-compliance';
      case 'AVIATION_NON_COMPLIANCE_DAILY_PENALTY_NOTICE_APPLICATION_PEER_REVIEW':
        return 'Peer review initial penalty: non-compliance';
      case 'AVIATION_NON_COMPLIANCE_NOTICE_OF_INTENT':
        return 'Upload notice of intent: non-compliance';
      case 'AVIATION_NON_COMPLIANCE_NOTICE_OF_INTENT_WAIT_FOR_PEER_REVIEW':
        return 'Notice of intent sent to peer reviewer: non-compliance';
      case 'AVIATION_NON_COMPLIANCE_NOTICE_OF_INTENT_APPLICATION_PEER_REVIEW':
        return 'Peer review notice of intent: non-compliance';
      case 'AVIATION_NON_COMPLIANCE_CIVIL_PENALTY':
        return 'Upload penalty notice: non-compliance';
      case 'AVIATION_NON_COMPLIANCE_CIVIL_PENALTY_WAIT_FOR_PEER_REVIEW':
        return 'Penalty sent to peer reviewer: non-compliance';
      case 'AVIATION_NON_COMPLIANCE_CIVIL_PENALTY_APPLICATION_PEER_REVIEW':
        return 'Peer review upload penalty: non-compliance';
      case 'AVIATION_NON_COMPLIANCE_FINAL_DETERMINATION':
        return 'Provide conclusion of non-compliance';

      case 'AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_APPLICATION_SUBMIT':
        return 'Calculate annual offsetting requirements';
      case 'AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_APPLICATION_PEER_REVIEW':
        return 'Peer review annual offsetting requirements';
      case 'AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_WAIT_FOR_PEER_REVIEW':
        return 'Annual offsetting requirements sent to peer reviewer';

      case 'AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_APPLICATION_SUBMIT':
        return 'Calculate 3-year period offsetting requirements';
      case 'AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_APPLICATION_PEER_REVIEW':
        return 'Peer review 3-year period offsetting requirements';
      case 'AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_WAIT_FOR_PEER_REVIEW':
        return '3-year period offsetting requirements sent to peer reviewer';

      case 'BDR_APPLICATION_SUBMIT':
        return 'Complete baseline data report';
      case 'BDR_WAIT_FOR_VERIFICATION':
      case 'BDR_AMEND_WAIT_FOR_VERIFICATION':
        return 'Baseline data report sent to verifier';
      case 'BDR_WAIT_FOR_REGULATOR_REVIEW':
        return 'Baseline data report sent to regulator';
      case 'BDR_APPLICATION_VERIFICATION_SUBMIT':
      case 'BDR_AMEND_APPLICATION_VERIFICATION_SUBMIT':
        return `Verify baseline data report`;
      case 'BDR_APPLICATION_REGULATOR_REVIEW_SUBMIT':
        return `Review baseline data report`;
      case 'BDR_WAIT_FOR_AMENDS':
        return `Baseline data report`;
      case 'BDR_APPLICATION_AMENDS_SUBMIT':
        return 'Amend baseline data report';
      case 'BDR_WAIT_FOR_PEER_REVIEW':
        return `Peer review baseline data report`;
      case 'BDR_APPLICATION_PEER_REVIEW':
        return `Peer review baseline data report`;

      case 'PERMANENT_CESSATION_APPLICATION_SUBMIT':
        return 'Complete permanent cessation';
      case 'PERMANENT_CESSATION_WAIT_FOR_PEER_REVIEW':
        return 'Permanent cessation sent to peer reviewer';
      case 'PERMANENT_CESSATION_APPLICATION_PEER_REVIEW':
        return 'Peer review permanent cessation';

      case 'AVIATION_DOE_CORSIA_APPLICATION_SUBMIT':
        return 'Estimate emissions';
      case 'AVIATION_DOE_CORSIA_MAKE_PAYMENT':
        return `Pay emissions estimation fee`;
      case 'AVIATION_DOE_CORSIA_TRACK_PAYMENT':
      case 'AVIATION_DOE_CORSIA_CONFIRM_PAYMENT':
        return `Track payment for emissions estimation`;
      case 'AVIATION_DOE_CORSIA_WAIT_FOR_PEER_REVIEW':
        return 'Aviation emissions report estimation sent to peer reviewer';
      case 'AVIATION_DOE_CORSIA_APPLICATION_PEER_REVIEW':
        return 'Peer review emissions estimation';

      case 'ALR_APPLICATION_SUBMIT':
        return 'Complete activity level report';
      case 'ALR_WAIT_FOR_VERIFICATION':
        return 'Activity level report sent to verifier';
      case 'ALR_APPLICATION_VERIFICATION_SUBMIT':
        return `Verify activity level report`;

      default:
        return null;
    }
  }
}
