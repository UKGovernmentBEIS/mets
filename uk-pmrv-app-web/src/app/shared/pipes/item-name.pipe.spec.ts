import { ItemNamePipe } from './item-name.pipe';

describe('ItemNamePipe', () => {
  const pipe = new ItemNamePipe();

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should map task types to item names', () => {
    expect(pipe.transform('ACCOUNT_USERS_SETUP')).toEqual('Manage account users');
    expect(pipe.transform('INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW')).toEqual(
      'Review installation account application',
    );
    expect(pipe.transform('INSTALLATION_ACCOUNT_OPENING_ARCHIVE')).toEqual('Archive installation account application');
    expect(pipe.transform('INSTALLATION_ACCOUNT_TRANSFERRING_ARCHIVE')).toEqual('Share transfer code');
    expect(pipe.transform('PERMIT_ISSUANCE_APPLICATION_SUBMIT')).toEqual('Apply for a permit');
    expect(pipe.transform('PERMIT_ISSUANCE_WAIT_FOR_REVIEW')).toEqual('Permit application sent to regulator');
    expect(pipe.transform('PERMIT_ISSUANCE_APPLICATION_REVIEW')).toEqual('Review permit application');
    expect(pipe.transform('PERMIT_ISSUANCE_WAIT_FOR_PEER_REVIEW')).toEqual('Permit application sent to peer reviewer');
    expect(pipe.transform('PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW')).toEqual('Peer review permit application');
    expect(pipe.transform('NEW_VERIFICATION_BODY_EMITTER')).toEqual('New appointment from emitter');
    expect(pipe.transform('AVIATION_AER_UKETS_AMEND_APPLICATION_VERIFICATION_SUBMIT', '2022')).toEqual(
      'Verify 2022 emissions report',
    );
    expect(pipe.transform('AVIATION_AER_CORSIA_AMEND_APPLICATION_VERIFICATION_SUBMIT', '2022')).toEqual(
      'Verify 2022 emissions report',
    );
    expect(pipe.transform('AVIATION_AER_UKETS_APPLICATION_VERIFICATION_SUBMIT', '2022')).toEqual(
      'Verify 2022 emissions report',
    );
    expect(pipe.transform('AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMIT', '2022')).toEqual(
      'Verify 2022 emissions report',
    );
    expect(pipe.transform('VERIFIER_NO_LONGER_AVAILABLE')).toEqual('Verification body is no longer available');
    expect(pipe.transform('PERMIT_ISSUANCE_APPLICATION_AMENDS_SUBMIT')).toEqual('Amend your permit application');
    expect(pipe.transform('PERMIT_ISSUANCE_WAIT_FOR_AMENDS')).toEqual('Permit application returned to operator');
    expect(pipe.transform('PERMIT_SURRENDER_APPLICATION_SUBMIT')).toEqual('Complete permit surrender');
    expect(pipe.transform('PERMIT_SURRENDER_WAIT_FOR_REVIEW')).toEqual('Permit surrender sent to regulator');
    expect(pipe.transform('PERMIT_SURRENDER_APPLICATION_REVIEW')).toEqual('Review permit surrender');
    expect(pipe.transform('PERMIT_SURRENDER_CESSATION_SUBMIT')).toEqual('Confirm permit surrender');
    expect(pipe.transform('PERMIT_ISSUANCE_WAIT_FOR_RDE_RESPONSE')).toEqual('Extension request sent to operator');
    expect(pipe.transform('PERMIT_ISSUANCE_RDE_RESPONSE_SUBMIT')).toEqual(`Respond to regulator's extension request`);
    expect(pipe.transform('PERMIT_ISSUANCE_WAIT_FOR_RFI_RESPONSE')).toEqual('Request for information sent to operator');
    expect(pipe.transform('PERMIT_ISSUANCE_RFI_RESPONSE_SUBMIT')).toEqual(
      `Respond to regulator's request for information`,
    );
    expect(pipe.transform('PERMIT_SURRENDER_WAIT_FOR_RFI_RESPONSE')).toEqual(
      'Request for information sent to operator',
    );
    expect(pipe.transform('PERMIT_SURRENDER_RFI_RESPONSE_SUBMIT')).toEqual(
      `Respond to regulator's request for information`,
    );
    expect(pipe.transform('PERMIT_SURRENDER_WAIT_FOR_RDE_RESPONSE')).toEqual('Extension request sent to operator');
    expect(pipe.transform('PERMIT_SURRENDER_RDE_RESPONSE_SUBMIT')).toEqual(`Respond to regulator's extension request`);
    expect(pipe.transform('PERMIT_NOTIFICATION_APPLICATION_SUBMIT')).toEqual('Notify regulator about a change');
    expect(pipe.transform('PERMIT_NOTIFICATION_WAIT_FOR_REVIEW')).toEqual('Notification sent to regulator');
    expect(pipe.transform('PERMIT_NOTIFICATION_APPLICATION_REVIEW')).toEqual('Review notification');
    expect(pipe.transform('PERMIT_NOTIFICATION_WAIT_FOR_PEER_REVIEW')).toEqual('Notification sent to peer reviewer');
    expect(pipe.transform('PERMIT_NOTIFICATION_APPLICATION_PEER_REVIEW')).toEqual('Peer review notification');
    expect(pipe.transform('PERMIT_NOTIFICATION_WAIT_FOR_RFI_RESPONSE')).toEqual(
      'Request for information sent to operator',
    );
    expect(pipe.transform('PERMIT_NOTIFICATION_RFI_RESPONSE_SUBMIT')).toEqual(
      `Respond to regulator's request for information`,
    );
    expect(pipe.transform('PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_REVIEW')).toEqual('Review notification follow up');
    expect(pipe.transform('PERMIT_NOTIFICATION_FOLLOW_UP_WAIT_FOR_REVIEW')).toEqual(
      'Notification follow up sent to regulator',
    );
    expect(pipe.transform('PERMIT_NOTIFICATION_FOLLOW_UP_WAIT_FOR_AMENDS')).toEqual(
      'Notification follow up returned to operator',
    );
    expect(pipe.transform('PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_AMENDS_SUBMIT')).toEqual(
      'Amend your notification follow up',
    );

    expect(pipe.transform('PERMIT_ISSUANCE_MAKE_PAYMENT')).toEqual('Pay for permit application');
    expect(pipe.transform('PERMIT_ISSUANCE_TRACK_PAYMENT')).toEqual('Track payment for permit application');
    expect(pipe.transform('PERMIT_ISSUANCE_CONFIRM_PAYMENT')).toEqual('Track payment for permit application');

    expect(pipe.transform('PERMIT_SURRENDER_MAKE_PAYMENT')).toEqual('Pay for permit surrender');
    expect(pipe.transform('PERMIT_SURRENDER_TRACK_PAYMENT')).toEqual('Track payment for permit surrender');
    expect(pipe.transform('PERMIT_SURRENDER_CONFIRM_PAYMENT')).toEqual('Track payment for permit surrender');

    expect(pipe.transform('PERMIT_REVOCATION_MAKE_PAYMENT')).toEqual('Pay for permit revocation');
    expect(pipe.transform('PERMIT_REVOCATION_TRACK_PAYMENT')).toEqual('Track payment for permit revocation');
    expect(pipe.transform('PERMIT_REVOCATION_CONFIRM_PAYMENT')).toEqual('Track payment for permit revocation');

    expect(pipe.transform('PERMIT_VARIATION_REGULATOR_LED_APPLICATION_SUBMIT')).toEqual('Permit variation');
    expect(pipe.transform('PERMIT_VARIATION_APPLICATION_SUBMIT')).toEqual('Apply for a permit variation');
    expect(pipe.transform('PERMIT_VARIATION_WAIT_FOR_REVIEW')).toEqual('Permit variation sent to regulator');
    expect(pipe.transform('PERMIT_VARIATION_APPLICATION_REVIEW')).toEqual('Review permit variation');
    expect(pipe.transform('PERMIT_VARIATION_APPLICATION_AMENDS_SUBMIT')).toEqual('Amend your permit variation');
    expect(pipe.transform('PERMIT_VARIATION_WAIT_FOR_AMENDS')).toEqual('Permit variation returned to operator');
    expect(pipe.transform('PERMIT_VARIATION_MAKE_PAYMENT')).toEqual('Pay for permit variation');
    expect(pipe.transform('PERMIT_VARIATION_TRACK_PAYMENT')).toEqual('Track payment for permit variation');
    expect(pipe.transform('PERMIT_VARIATION_CONFIRM_PAYMENT')).toEqual('Track payment for permit variation');
    expect(pipe.transform('PERMIT_VARIATION_WAIT_FOR_PEER_REVIEW')).toEqual('Permit variation sent to peer reviewer');
    expect(pipe.transform('PERMIT_VARIATION_APPLICATION_PEER_REVIEW')).toEqual('Peer review permit variation');
    expect(pipe.transform('PERMIT_VARIATION_REGULATOR_LED_APPLICATION_PEER_REVIEW')).toEqual(
      'Peer review permit variation',
    );
    expect(pipe.transform('PERMIT_VARIATION_REGULATOR_LED_WAIT_FOR_PEER_REVIEW')).toEqual(
      'Permit variation sent to peer reviewer',
    );
    expect(pipe.transform('PERMIT_VARIATION_REGULATOR_LED_MAKE_PAYMENT')).toEqual('Pay for permit variation');
    expect(pipe.transform('PERMIT_VARIATION_REGULATOR_LED_TRACK_PAYMENT')).toEqual(
      'Track payment for permit variation',
    );
    expect(pipe.transform('PERMIT_VARIATION_REGULATOR_LED_CONFIRM_PAYMENT')).toEqual(
      'Track payment for permit variation',
    );

    expect(pipe.transform('PERMIT_TRANSFER_A_APPLICATION_SUBMIT')).toEqual('Apply for a permit transfer');
    expect(pipe.transform('PERMIT_TRANSFER_A_WAIT_FOR_TRANSFER')).toEqual('Permit transfer sent to new operator');
    expect(pipe.transform('PERMIT_TRANSFER_A_MAKE_PAYMENT')).toEqual('Pay for permit transfer');
    expect(pipe.transform('PERMIT_TRANSFER_A_TRACK_PAYMENT')).toEqual('Track payment for permit transfer application');
    expect(pipe.transform('PERMIT_TRANSFER_A_CONFIRM_PAYMENT')).toEqual(
      'Track payment for permit transfer application',
    );
    expect(pipe.transform('PERMIT_TRANSFER_B_APPLICATION_SUBMIT')).toEqual('Transfer application');
    expect(pipe.transform('PERMIT_TRANSFER_B_MAKE_PAYMENT')).toEqual('Pay for permit transfer');
    expect(pipe.transform('PERMIT_TRANSFER_B_TRACK_PAYMENT')).toEqual('Track payment for permit transfer application');
    expect(pipe.transform('PERMIT_TRANSFER_B_CONFIRM_PAYMENT')).toEqual(
      'Track payment for permit transfer application',
    );
    expect(pipe.transform('PERMIT_TRANSFER_B_APPLICATION_REVIEW')).toEqual('Review permit transfer');
    expect(pipe.transform('PERMIT_TRANSFER_B_WAIT_FOR_REVIEW')).toEqual('Permit transfer sent to regulator');
    expect(pipe.transform('PERMIT_TRANSFER_B_RFI_RESPONSE_SUBMIT')).toEqual(
      `Respond to regulator's request for information`,
    );
    expect(pipe.transform('VIR_RFI_RESPONSE_SUBMIT')).toEqual(`Respond to regulator's request for information`);
    expect(pipe.transform('PERMIT_TRANSFER_B_WAIT_FOR_RFI_RESPONSE')).toEqual(
      'Request for information sent to operator',
    );
    expect(pipe.transform('PERMIT_TRANSFER_B_RDE_RESPONSE_SUBMIT')).toEqual(`Respond to regulator's extension request`);
    expect(pipe.transform('PERMIT_TRANSFER_B_WAIT_FOR_RDE_RESPONSE')).toEqual('Extension request sent to operator');
    expect(pipe.transform('PERMIT_TRANSFER_B_WAIT_FOR_AMENDS')).toEqual('Permit transfer returned to operator');
    expect(pipe.transform('PERMIT_TRANSFER_B_APPLICATION_AMENDS_SUBMIT')).toEqual('Amend your permit transfer');
    expect(pipe.transform('PERMIT_TRANSFER_B_WAIT_FOR_PEER_REVIEW')).toEqual('Permit transfer sent to peer reviewer');
    expect(pipe.transform('PERMIT_TRANSFER_B_APPLICATION_PEER_REVIEW')).toEqual('Peer review permit transfer');

    expect(pipe.transform('AER_APPLICATION_SUBMIT', '2022')).toEqual('Complete 2022 emission report');

    expect(pipe.transform('VIR_APPLICATION_SUBMIT', '2022')).toEqual('Complete 2022 verifier improvement report');
    expect(pipe.transform('VIR_APPLICATION_REVIEW', '2022')).toEqual('Review 2022 verifier improvement report');
    expect(pipe.transform('VIR_WAIT_FOR_REVIEW', '2022')).toEqual('2022 verifier improvement report sent to regulator');
    expect(pipe.transform('VIR_RESPOND_TO_REGULATOR_COMMENTS', '2022')).toEqual('2022 respond to regulator comments');

    expect(pipe.transform('AIR_APPLICATION_SUBMIT', '2022')).toEqual('2022 Annual improvement report');
    expect(pipe.transform('AIR_APPLICATION_REVIEW', '2022')).toEqual('2022 Annual improvement report review');
    expect(pipe.transform('AIR_WAIT_FOR_REVIEW', '2022')).toEqual('2022 Annual improvement report sent to regulator');
    expect(pipe.transform('AIR_RESPOND_TO_REGULATOR_COMMENTS', '2022')).toEqual(
      '2022 Annual improvement report follow up',
    );

    expect(pipe.transform('DRE_APPLICATION_SUBMIT', '2022')).toEqual('Determine 2022 reportable emissions');
    expect(pipe.transform('DRE_MAKE_PAYMENT', '2022')).toEqual('Pay 2022 reportable emissions fee');
    expect(pipe.transform('DRE_TRACK_PAYMENT', '2022')).toEqual('Track payment for 2022 reportable emissions');
    expect(pipe.transform('DRE_CONFIRM_PAYMENT', '2022')).toEqual('Track payment for 2022 reportable emissions');

    expect(pipe.transform('INSTALLATION_AUDIT_APPLICATION_SUBMIT', 2022)).toEqual('Create 2022 audit report');
    expect(pipe.transform('INSTALLATION_AUDIT_WAIT_FOR_PEER_REVIEW', 2022)).toEqual(
      '2022 Audit report sent to peer reviewer',
    );
    expect(pipe.transform('INSTALLATION_AUDIT_APPLICATION_PEER_REVIEW', 2022)).toEqual('Peer review 2022 audit report');
    expect(pipe.transform('INSTALLATION_AUDIT_OPERATOR_RESPOND_TO_FOLLOWUP_ACTIONS', 2022)).toEqual(
      '2022 audit report',
    );

    expect(pipe.transform('INSTALLATION_ONSITE_INSPECTION_APPLICATION_SUBMIT', 2022)).toEqual(
      'Create an on-site inspection',
    );
    expect(pipe.transform('INSTALLATION_ONSITE_INSPECTION_WAIT_FOR_PEER_REVIEW', 2022)).toEqual(
      'On-site inspection sent to peer reviewer',
    );
    expect(pipe.transform('INSTALLATION_ONSITE_INSPECTION_APPLICATION_PEER_REVIEW', 2022)).toEqual(
      'Peer review on-site inspection',
    );
    expect(pipe.transform('INSTALLATION_ONSITE_INSPECTION_OPERATOR_RESPOND_TO_FOLLOWUP_ACTIONS', 2022)).toEqual(
      'On-site inspection',
    );

    expect(pipe.transform('NON_COMPLIANCE_APPLICATION_SUBMIT')).toEqual('Provide details of breach: non-compliance');
    expect(pipe.transform('NON_COMPLIANCE_DAILY_PENALTY_NOTICE')).toEqual('Upload penalty notice: non-compliance');
    expect(pipe.transform('NON_COMPLIANCE_DAILY_PENALTY_NOTICE_WAIT_FOR_PEER_REVIEW')).toEqual(
      'Initial penalty notice sent to peer reviewer: non-compliance',
    );
    expect(pipe.transform('NON_COMPLIANCE_DAILY_PENALTY_NOTICE_APPLICATION_PEER_REVIEW')).toEqual(
      'Peer review initial penalty: non-compliance',
    );
    expect(pipe.transform('NON_COMPLIANCE_NOTICE_OF_INTENT')).toEqual('Upload notice of intent: non-compliance');
    expect(pipe.transform('NON_COMPLIANCE_NOTICE_OF_INTENT_APPLICATION_PEER_REVIEW')).toEqual(
      'Peer review notice of intent: non-compliance',
    );
    expect(pipe.transform('NON_COMPLIANCE_NOTICE_OF_INTENT_WAIT_FOR_PEER_REVIEW')).toEqual(
      'Notice of intent sent to peer reviewer: non-compliance',
    );

    expect(pipe.transform('PERMANENT_CESSATION_APPLICATION_SUBMIT')).toEqual('Complete permanent cessation');
    expect(pipe.transform('PERMANENT_CESSATION_WAIT_FOR_PEER_REVIEW')).toEqual(
      'Permanent cessation sent to peer reviewer',
    );
    expect(pipe.transform('PERMANENT_CESSATION_APPLICATION_PEER_REVIEW')).toEqual('Peer review permanent cessation');

    expect(pipe.transform('DOAL_APPLICATION_SUBMIT')).toEqual('Determination of activity level');
    expect(pipe.transform('DOAL_AUTHORITY_RESPONSE')).toEqual(
      'Provide UK ETS Authority response for activity Level Change',
    );
    expect(pipe.transform('DOAL_WAIT_FOR_PEER_REVIEW')).toEqual('Activity level determination sent to peer reviewer');
    expect(pipe.transform('DOAL_APPLICATION_PEER_REVIEW')).toEqual('Activity level determination peer review');

    expect(pipe.transform('EMP_ISSUANCE_UKETS_APPLICATION_SUBMIT')).toEqual('Apply for an emissions monitoring plan');

    expect(pipe.transform('DRE_WAIT_FOR_PEER_REVIEW')).toEqual('Reportable emissions sent for peer review');
    expect(pipe.transform('DRE_APPLICATION_PEER_REVIEW')).toEqual('Peer review reportable emissions');

    expect(pipe.transform('EMP_ISSUANCE_UKETS_APPLICATION_SUBMIT')).toEqual('Apply for an emissions monitoring plan');
    expect(pipe.transform('EMP_ISSUANCE_CORSIA_APPLICATION_SUBMIT')).toEqual('Apply for an emissions monitoring plan');
    expect(pipe.transform('EMP_ISSUANCE_UKETS_APPLICATION_REVIEW')).toEqual(
      'Review emissions monitoring plan application',
    );
    expect(pipe.transform('EMP_ISSUANCE_CORSIA_APPLICATION_REVIEW')).toEqual(
      'Review emissions monitoring plan application',
    );
    expect(pipe.transform('EMP_ISSUANCE_UKETS_WAIT_FOR_REVIEW')).toEqual(
      'Emissions monitoring plan application sent to regulator',
    );
    expect(pipe.transform('EMP_ISSUANCE_CORSIA_WAIT_FOR_REVIEW')).toEqual(
      'Emissions monitoring plan application sent to regulator',
    );
    expect(pipe.transform('EMP_ISSUANCE_UKETS_WAIT_FOR_PEER_REVIEW')).toEqual(
      'Emissions monitoring plan application sent to peer reviewer',
    );
    expect(pipe.transform('EMP_ISSUANCE_UKETS_WAIT_FOR_AMENDS')).toEqual(
      'Emissions monitoring plan application returned to operator',
    );
    expect(pipe.transform('EMP_ISSUANCE_UKETS_APPLICATION_PEER_REVIEW')).toEqual(
      'Peer review emissions monitoring plan application',
    );

    expect(pipe.transform('EMP_ISSUANCE_UKETS_APPLICATION_AMENDS_SUBMIT')).toEqual(
      'Amend your emissions monitoring plan application',
    );
    expect(pipe.transform('EMP_ISSUANCE_UKETS_MAKE_PAYMENT')).toEqual('Pay emissions monitoring plan application fee');
    expect(pipe.transform('EMP_ISSUANCE_UKETS_TRACK_PAYMENT')).toEqual(
      'Track payment for emissions monitoring plan application',
    );
    expect(pipe.transform('EMP_ISSUANCE_UKETS_CONFIRM_PAYMENT')).toEqual(
      'Track payment for emissions monitoring plan application',
    );

    expect(pipe.transform('EMP_ISSUANCE_CORSIA_MAKE_PAYMENT')).toEqual('Pay emissions monitoring plan application fee');
    expect(pipe.transform('EMP_ISSUANCE_CORSIA_TRACK_PAYMENT')).toEqual(
      'Track payment for emissions monitoring plan application',
    );
    expect(pipe.transform('EMP_ISSUANCE_CORSIA_CONFIRM_PAYMENT')).toEqual(
      'Track payment for emissions monitoring plan application',
    );
    expect(pipe.transform('EMP_ISSUANCE_CORSIA_APPLICATION_AMENDS_SUBMIT')).toEqual(
      'Amend your emissions monitoring plan application',
    );
    expect(pipe.transform('EMP_ISSUANCE_CORSIA_WAIT_FOR_AMENDS')).toEqual(
      'Emissions monitoring plan application returned to operator',
    );
    expect(pipe.transform('EMP_ISSUANCE_CORSIA_APPLICATION_PEER_REVIEW')).toEqual(
      'Peer review emissions monitoring plan application',
    );
    expect(pipe.transform('EMP_ISSUANCE_CORSIA_WAIT_FOR_PEER_REVIEW')).toEqual(
      'Emissions monitoring plan application sent to peer reviewer',
    );

    expect(pipe.transform('EMP_VARIATION_UKETS_APPLICATION_SUBMIT')).toEqual(
      'Apply to vary your emissions monitoring plan',
    );
    expect(pipe.transform('EMP_VARIATION_UKETS_APPLICATION_REVIEW')).toEqual(
      `Review emissions monitoring plan variation`,
    );
    expect(pipe.transform('EMP_VARIATION_CORSIA_APPLICATION_REVIEW')).toEqual(
      `Review emissions monitoring plan variation`,
    );
    expect(pipe.transform('EMP_VARIATION_UKETS_WAIT_FOR_REVIEW')).toEqual(
      'Emissions monitoring plan variation sent to regulator',
    );
    expect(pipe.transform('EMP_VARIATION_CORSIA_WAIT_FOR_REVIEW')).toEqual(
      'Emissions monitoring plan variation sent to regulator',
    );
    expect(pipe.transform('EMP_VARIATION_UKETS_WAIT_FOR_PEER_REVIEW')).toEqual(
      'Emissions monitoring plan variation sent to peer reviewer',
    );
    expect(pipe.transform('EMP_VARIATION_CORSIA_WAIT_FOR_PEER_REVIEW')).toEqual(
      'Emissions monitoring plan variation sent to peer reviewer',
    );
    expect(pipe.transform('EMP_VARIATION_UKETS_APPLICATION_PEER_REVIEW')).toEqual(
      'Peer review emissions monitoring plan variation',
    );
    expect(pipe.transform('EMP_VARIATION_CORSIA_APPLICATION_PEER_REVIEW')).toEqual(
      'Peer review emissions monitoring plan variation',
    );
    expect(pipe.transform('EMP_VARIATION_UKETS_WAIT_FOR_RDE_RESPONSE')).toEqual('Extension request sent to operator');
    expect(pipe.transform('EMP_VARIATION_UKETS_RDE_RESPONSE_SUBMIT')).toEqual(
      `Respond to regulator's extension request`,
    );
    expect(pipe.transform('EMP_VARIATION_UKETS_WAIT_FOR_RFI_RESPONSE')).toEqual(
      'Request for information sent to operator',
    );
    expect(pipe.transform('EMP_VARIATION_UKETS_RFI_RESPONSE_SUBMIT')).toEqual(
      `Respond to regulator's request for information`,
    );
    expect(pipe.transform('EMP_VARIATION_UKETS_APPLICATION_AMENDS_SUBMIT')).toEqual(
      'Amend your emissions monitoring plan variation',
    );
    expect(pipe.transform('EMP_VARIATION_CORSIA_APPLICATION_AMENDS_SUBMIT')).toEqual(
      'Amend your emissions monitoring plan variation',
    );
    expect(pipe.transform('EMP_VARIATION_UKETS_WAIT_FOR_AMENDS')).toEqual(
      'Emissions monitoring plan variation returned to operator',
    );
    expect(pipe.transform('EMP_VARIATION_CORSIA_WAIT_FOR_AMENDS')).toEqual(
      'Emissions monitoring plan variation returned to operator',
    );
    expect(pipe.transform('EMP_VARIATION_UKETS_MAKE_PAYMENT')).toEqual('Pay emissions monitoring plan variation fee');
    expect(pipe.transform('EMP_VARIATION_UKETS_TRACK_PAYMENT')).toEqual(
      'Track payment for emissions monitoring plan variation',
    );
    expect(pipe.transform('EMP_VARIATION_UKETS_CONFIRM_PAYMENT')).toEqual(
      'Track payment for emissions monitoring plan variation',
    );
    expect(pipe.transform('EMP_VARIATION_UKETS_REGULATOR_LED_MAKE_PAYMENT')).toEqual(
      'Pay emissions monitoring plan variation fee',
    );
    expect(pipe.transform('EMP_VARIATION_UKETS_REGULATOR_LED_TRACK_PAYMENT')).toEqual(
      'Track payment for emissions monitoring plan variation',
    );
    expect(pipe.transform('EMP_VARIATION_UKETS_REGULATOR_LED_CONFIRM_PAYMENT')).toEqual(
      'Track payment for emissions monitoring plan variation',
    );

    expect(pipe.transform('EMP_VARIATION_CORSIA_APPLICATION_SUBMIT')).toEqual(
      'Apply to vary your emissions monitoring plan',
    );
    expect(pipe.transform('EMP_VARIATION_CORSIA_WAIT_FOR_RFI_RESPONSE')).toEqual(
      'Request for information sent to operator',
    );
    expect(pipe.transform('EMP_VARIATION_CORSIA_RFI_RESPONSE_SUBMIT')).toEqual(
      `Respond to regulator's request for information`,
    );
    expect(pipe.transform('EMP_VARIATION_CORSIA_WAIT_FOR_RDE_RESPONSE')).toEqual('Extension request sent to operator');
    expect(pipe.transform('EMP_VARIATION_CORSIA_RDE_RESPONSE_SUBMIT')).toEqual(
      `Respond to regulator's extension request`,
    );
    expect(pipe.transform('EMP_VARIATION_CORSIA_REGULATOR_LED_MAKE_PAYMENT')).toEqual(
      'Pay emissions monitoring plan variation fee',
    );
    expect(pipe.transform('EMP_VARIATION_CORSIA_REGULATOR_LED_TRACK_PAYMENT')).toEqual(
      'Track payment for emissions monitoring plan variation',
    );
    expect(pipe.transform('EMP_VARIATION_CORSIA_REGULATOR_LED_CONFIRM_PAYMENT')).toEqual(
      'Track payment for emissions monitoring plan variation',
    );
    expect(pipe.transform('EMP_VARIATION_CORSIA_MAKE_PAYMENT')).toEqual('Pay emissions monitoring plan variation fee');
    expect(pipe.transform('EMP_VARIATION_CORSIA_TRACK_PAYMENT')).toEqual(
      'Track payment for emissions monitoring plan variation',
    );
    expect(pipe.transform('EMP_VARIATION_CORSIA_CONFIRM_PAYMENT')).toEqual(
      'Track payment for emissions monitoring plan variation',
    );

    expect(pipe.transform('AVIATION_AER_UKETS_APPLICATION_SUBMIT', '2022')).toEqual('Complete 2022 emissions report');
    expect(pipe.transform('AVIATION_AER_CORSIA_APPLICATION_SUBMIT', '2022')).toEqual('Complete 2022 emissions report');

    expect(pipe.transform('AVIATION_AER_UKETS_AMEND_WAIT_FOR_VERIFICATION', '2022')).toEqual(
      '2022 emissions report sent to verifier',
    );
    expect(pipe.transform('AVIATION_AER_CORSIA_AMEND_WAIT_FOR_VERIFICATION', '2022')).toEqual(
      '2022 emissions report sent to verifier',
    );
    expect(pipe.transform('AVIATION_AER_UKETS_WAIT_FOR_VERIFICATION', '2022')).toEqual(
      '2022 emissions report sent to verifier',
    );
    expect(pipe.transform('AVIATION_AER_CORSIA_WAIT_FOR_VERIFICATION', '2022')).toEqual(
      '2022 emissions report sent to verifier',
    );
    expect(pipe.transform('AVIATION_AER_UKETS_APPLICATION_REVIEW', '2022')).toEqual('Review 2022 emissions report');
    expect(pipe.transform('AVIATION_AER_CORSIA_APPLICATION_REVIEW', '2022')).toEqual('Review 2022 emissions report');
    expect(pipe.transform('AVIATION_AER_UKETS_WAIT_FOR_REVIEW', '2022')).toEqual(
      '2022 emissions report sent to regulator for review',
    );
    expect(pipe.transform('AVIATION_AER_CORSIA_WAIT_FOR_REVIEW', '2022')).toEqual(
      '2022 emissions report sent to regulator for review',
    );
    expect(pipe.transform('AVIATION_AER_CORSIA_WAIT_FOR_AMENDS', '2022')).toEqual(
      '2022 emissions report returned to operator',
    );
    expect(pipe.transform('AVIATION_AER_UKETS_WAIT_FOR_AMENDS', '2022')).toEqual(
      '2022 emissions report returned to operator',
    );
    expect(pipe.transform('AVIATION_AER_UKETS_APPLICATION_AMENDS_SUBMIT', '2022')).toEqual(
      'Amend 2022 emissions report',
    );
    expect(pipe.transform('AVIATION_AER_CORSIA_APPLICATION_AMENDS_SUBMIT', '2022')).toEqual(
      'Amend 2022 emissions report',
    );

    expect(pipe.transform('AVIATION_DRE_UKETS_APPLICATION_SUBMIT', '2022')).toEqual('Determine 2022 emissions');
    expect(pipe.transform('AVIATION_DRE_UKETS_MAKE_PAYMENT', '2022')).toEqual('Pay 2022 emissions determination fee');
    expect(pipe.transform('AVIATION_DRE_UKETS_TRACK_PAYMENT', '2022')).toEqual(
      'Track payment for 2022 emissions determination',
    );
    expect(pipe.transform('AVIATION_DRE_UKETS_CONFIRM_PAYMENT', '2022')).toEqual(
      'Track payment for 2022 emissions determination',
    );
    expect(pipe.transform('AVIATION_DRE_UKETS_WAIT_FOR_PEER_REVIEW', '2022')).toEqual(
      '2022 emissions determination sent to peer reviewer',
    );
    expect(pipe.transform('AVIATION_DRE_UKETS_APPLICATION_PEER_REVIEW', '2022')).toEqual(
      'Peer review 2022 emissions determination',
    );

    expect(pipe.transform('AVIATION_ACCOUNT_CLOSURE_SUBMIT')).toEqual('Close account');

    expect(pipe.transform('NON_COMPLIANCE_DAILY_PENALTY_NOTICE_WAIT_FOR_PEER_REVIEW')).toEqual(
      'Initial penalty notice sent to peer reviewer: non-compliance',
    );
    expect(pipe.transform('NON_COMPLIANCE_DAILY_PENALTY_NOTICE_APPLICATION_PEER_REVIEW')).toEqual(
      'Peer review initial penalty: non-compliance',
    );

    expect(pipe.transform('NON_COMPLIANCE_CIVIL_PENALTY_WAIT_FOR_PEER_REVIEW')).toEqual(
      'Penalty sent to peer reviewer: non-compliance',
    );
    expect(pipe.transform('NON_COMPLIANCE_CIVIL_PENALTY_APPLICATION_PEER_REVIEW')).toEqual(
      'Peer review upload penalty: non-compliance',
    );

    expect(pipe.transform('EMP_ISSUANCE_UKETS_WAIT_FOR_RDE_RESPONSE')).toEqual('Extension request sent to operator');
    expect(pipe.transform('EMP_ISSUANCE_UKETS_RDE_RESPONSE_SUBMIT')).toEqual(
      `Respond to regulator's extension request`,
    );
    expect(pipe.transform('EMP_ISSUANCE_UKETS_WAIT_FOR_RFI_RESPONSE')).toEqual(
      'Request for information sent to operator',
    );
    expect(pipe.transform('EMP_ISSUANCE_UKETS_RFI_RESPONSE_SUBMIT')).toEqual(
      `Respond to regulator's request for information`,
    );
    expect(pipe.transform('EMP_ISSUANCE_CORSIA_RFI_RESPONSE_SUBMIT')).toEqual(
      `Respond to regulator's request for information`,
    );
    expect(pipe.transform('EMP_ISSUANCE_CORSIA_WAIT_FOR_RFI_RESPONSE')).toEqual(
      'Request for information sent to operator',
    );
    expect(pipe.transform('EMP_ISSUANCE_CORSIA_WAIT_FOR_RDE_RESPONSE')).toEqual('Extension request sent to operator');
    expect(pipe.transform('EMP_ISSUANCE_CORSIA_RDE_RESPONSE_SUBMIT')).toEqual(
      `Respond to regulator's extension request`,
    );

    expect(pipe.transform('EMP_VARIATION_UKETS_REGULATOR_LED_APPLICATION_SUBMIT')).toEqual(
      `Vary the emissions monitoring plan`,
    );
    expect(pipe.transform('EMP_VARIATION_CORSIA_REGULATOR_LED_APPLICATION_SUBMIT')).toEqual(
      `Vary the emissions monitoring plan`,
    );
    expect(pipe.transform('EMP_VARIATION_UKETS_REGULATOR_LED_WAIT_FOR_PEER_REVIEW')).toEqual(
      `Vary the emissions monitoring plan sent to peer reviewer`,
    );
    expect(pipe.transform('EMP_VARIATION_CORSIA_REGULATOR_LED_WAIT_FOR_PEER_REVIEW')).toEqual(
      `Vary the emissions monitoring plan sent to peer reviewer`,
    );
    expect(pipe.transform('EMP_VARIATION_UKETS_REGULATOR_LED_APPLICATION_PEER_REVIEW')).toEqual(
      `Peer review vary the emissions monitoring plan`,
    );
    expect(pipe.transform('EMP_VARIATION_CORSIA_REGULATOR_LED_APPLICATION_PEER_REVIEW')).toEqual(
      `Peer review vary the emissions monitoring plan`,
    );

    expect(pipe.transform('AVIATION_VIR_APPLICATION_SUBMIT', '2022')).toEqual(
      `Complete 2022 verifier improvement report`,
    );
    expect(pipe.transform('AVIATION_VIR_WAIT_FOR_REVIEW', '2022')).toEqual(
      `2022 verifier improvement report sent to regulator`,
    );
    expect(pipe.transform('AVIATION_VIR_APPLICATION_REVIEW', '2022')).toEqual(
      `Review 2022 verifier improvement report`,
    );
    expect(pipe.transform('AVIATION_VIR_WAIT_FOR_RFI_RESPONSE')).toEqual('Request for information sent to operator');
    expect(pipe.transform('AVIATION_VIR_RFI_RESPONSE_SUBMIT')).toEqual(
      `Respond to regulator's request for information`,
    );
    expect(pipe.transform('AVIATION_VIR_RESPOND_TO_REGULATOR_COMMENTS', '2022')).toEqual(
      `2022 verifier improvement report`,
    );

    expect(pipe.transform('RETURN_OF_ALLOWANCES_APPLICATION_SUBMIT')).toEqual('Return of allowances');
    expect(pipe.transform('RETURN_OF_ALLOWANCES_WAIT_FOR_PEER_REVIEW')).toEqual(
      'Return of allowances sent for peer review',
    );
    expect(pipe.transform('RETURN_OF_ALLOWANCES_APPLICATION_PEER_REVIEW')).toEqual('Peer review return of allowances');
    expect(pipe.transform('RETURN_OF_ALLOWANCES_RETURNED_APPLICATION_SUBMIT')).toEqual('Returned allowances');

    expect(pipe.transform('AVIATION_NON_COMPLIANCE_APPLICATION_SUBMIT')).toEqual(
      'Provide details of breach: non-compliance',
    );
    expect(pipe.transform('AVIATION_NON_COMPLIANCE_DAILY_PENALTY_NOTICE')).toEqual(
      'Upload penalty notice: non-compliance',
    );
    expect(pipe.transform('AVIATION_NON_COMPLIANCE_DAILY_PENALTY_NOTICE_WAIT_FOR_PEER_REVIEW')).toEqual(
      'Initial penalty notice sent to peer reviewer: non-compliance',
    );
    expect(pipe.transform('AVIATION_NON_COMPLIANCE_DAILY_PENALTY_NOTICE_APPLICATION_PEER_REVIEW')).toEqual(
      'Peer review initial penalty: non-compliance',
    );
    expect(pipe.transform('AVIATION_NON_COMPLIANCE_NOTICE_OF_INTENT')).toEqual(
      'Upload notice of intent: non-compliance',
    );
    expect(pipe.transform('AVIATION_NON_COMPLIANCE_NOTICE_OF_INTENT_APPLICATION_PEER_REVIEW')).toEqual(
      'Peer review notice of intent: non-compliance',
    );
    expect(pipe.transform('AVIATION_NON_COMPLIANCE_NOTICE_OF_INTENT_WAIT_FOR_PEER_REVIEW')).toEqual(
      'Notice of intent sent to peer reviewer: non-compliance',
    );
    expect(pipe.transform('AVIATION_NON_COMPLIANCE_CIVIL_PENALTY')).toEqual('Upload penalty notice: non-compliance');
    expect(pipe.transform('AVIATION_NON_COMPLIANCE_CIVIL_PENALTY_WAIT_FOR_PEER_REVIEW')).toEqual(
      'Penalty sent to peer reviewer: non-compliance',
    );
    expect(pipe.transform('AVIATION_NON_COMPLIANCE_CIVIL_PENALTY_APPLICATION_PEER_REVIEW')).toEqual(
      'Peer review upload penalty: non-compliance',
    );
    expect(pipe.transform('AVIATION_NON_COMPLIANCE_FINAL_DETERMINATION')).toEqual('Provide conclusion: non-compliance');

    expect(pipe.transform('AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_APPLICATION_SUBMIT')).toEqual(
      'Calculate annual offsetting requirements',
    );
    expect(pipe.transform('AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_WAIT_FOR_PEER_REVIEW')).toEqual(
      'Annual offsetting requirements sent to peer reviewer',
    );
    expect(pipe.transform('AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_APPLICATION_PEER_REVIEW')).toEqual(
      'Peer review annual offsetting requirements',
    );

    expect(pipe.transform('AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_APPLICATION_SUBMIT')).toEqual(
      'Calculate 3-year period offsetting requirements',
    );
    expect(pipe.transform('AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_APPLICATION_PEER_REVIEW')).toEqual(
      'Peer review 3-year period offsetting requirements',
    );
    expect(pipe.transform('AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_WAIT_FOR_PEER_REVIEW')).toEqual(
      '3-year period offsetting requirements sent to peer reviewer',
    );

    expect(pipe.transform('BDR_APPLICATION_SUBMIT', 2022)).toEqual('Complete 2022 baseline data report');
    expect(pipe.transform('BDR_WAIT_FOR_VERIFICATION', 2022)).toEqual('2022 baseline data report sent to verifier');
    expect(pipe.transform('BDR_AMEND_WAIT_FOR_VERIFICATION', 2022)).toEqual(
      '2022 baseline data report sent to verifier',
    );

    expect(pipe.transform('BDR_WAIT_FOR_REGULATOR_REVIEW', 2022)).toEqual(
      '2022 baseline data report sent to regulator',
    );
    expect(pipe.transform('BDR_APPLICATION_VERIFICATION_SUBMIT', 2022)).toEqual('Verify 2022 baseline data report');
    expect(pipe.transform('BDR_AMEND_APPLICATION_VERIFICATION_SUBMIT', 2022)).toEqual(
      'Verify 2022 baseline data report',
    );
    expect(pipe.transform('BDR_APPLICATION_REGULATOR_REVIEW_SUBMIT', 2022)).toEqual('Review 2022 baseline data report');
    expect(pipe.transform('BDR_APPLICATION_REGULATOR_REVIEW_SUBMIT', 2022)).toEqual('Review 2022 baseline data report');
    expect(pipe.transform('BDR_APPLICATION_PEER_REVIEW', 2025)).toEqual('Peer review 2025 baseline data report');
    expect(pipe.transform('BDR_WAIT_FOR_PEER_REVIEW', 2025)).toEqual('2025 baseline data report sent to peer reviewer');
    expect(pipe.transform('BDR_APPLICATION_AMENDS_SUBMIT', 2022)).toEqual('Amend 2022 baseline data report');

    expect(pipe.transform('AVIATION_DOE_CORSIA_APPLICATION_SUBMIT', 2022)).toEqual('Estimate 2022 emissions');
    expect(pipe.transform('AVIATION_DOE_CORSIA_MAKE_PAYMENT', 2022)).toEqual('Pay 2022 emissions estimation fee');
    expect(pipe.transform('AVIATION_DOE_CORSIA_TRACK_PAYMENT', 2022)).toEqual(
      'Track payment for 2022 emissions estimation',
    );
    expect(pipe.transform('AVIATION_DOE_CORSIA_CONFIRM_PAYMENT', 2022)).toEqual(
      'Track payment for 2022 emissions estimation',
    );
    expect(pipe.transform('AVIATION_DOE_CORSIA_WAIT_FOR_PEER_REVIEW', 2022)).toEqual(
      '2022 emissions estimation sent to peer reviewer',
    );
    expect(pipe.transform('AVIATION_DOE_CORSIA_APPLICATION_PEER_REVIEW', 2022)).toEqual(
      'Peer review 2022 emissions estimation',
    );

    expect(pipe.transform('ALR_APPLICATION_SUBMIT', 2022)).toEqual('Complete 2022 activity level report');
    expect(pipe.transform('ALR_WAIT_FOR_VERIFICATION', 2022)).toEqual('2022 activity level report sent to verifier');
    expect(pipe.transform('ALR_APPLICATION_VERIFICATION_SUBMIT', 2022)).toEqual('Verify 2022 activity level report');

    expect(pipe.transform(null)).toBeNull();
  });
});
