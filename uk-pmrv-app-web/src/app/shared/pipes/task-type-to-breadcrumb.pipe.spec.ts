import { TaskTypeToBreadcrumbPipe } from './task-type-to-breadcrumb.pipe';

describe('TaskTypeToBreadcrumbPipe', () => {
  const pipe = new TaskTypeToBreadcrumbPipe();

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
    expect(pipe.transform('AVIATION_AER_UKETS_APPLICATION_VERIFICATION_SUBMIT')).toEqual('Verify emissions report');
    expect(pipe.transform('AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMIT')).toEqual('Verify emissions report');
    expect(pipe.transform('AVIATION_AER_UKETS_AMEND_APPLICATION_VERIFICATION_SUBMIT')).toEqual(
      'Verify emissions report',
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
    expect(pipe.transform('PERMIT_VARIATION_APPLICATION_REVIEW')).toEqual('Variation determination');
    expect(pipe.transform('PERMIT_VARIATION_APPLICATION_AMENDS_SUBMIT')).toEqual('Amend your permit variation');
    expect(pipe.transform('PERMIT_VARIATION_WAIT_FOR_AMENDS')).toEqual('Permit variation returned to operator');
    expect(pipe.transform('PERMIT_VARIATION_MAKE_PAYMENT')).toEqual('Pay for permit variation');
    expect(pipe.transform('PERMIT_VARIATION_TRACK_PAYMENT')).toEqual('Track payment for permit variation');
    expect(pipe.transform('PERMIT_VARIATION_CONFIRM_PAYMENT')).toEqual('Track payment for permit variation');
    expect(pipe.transform('PERMIT_VARIATION_WAIT_FOR_PEER_REVIEW')).toEqual('Permit variation sent to peer reviewer');
    expect(pipe.transform('PERMIT_VARIATION_APPLICATION_PEER_REVIEW')).toEqual('Variation determination');
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

    expect(pipe.transform('AER_APPLICATION_SUBMIT')).toEqual('Emission report');

    expect(pipe.transform('VIR_APPLICATION_SUBMIT')).toEqual('Verifier improvement report');
    expect(pipe.transform('VIR_APPLICATION_REVIEW')).toEqual('Review verifier improvement report');
    expect(pipe.transform('VIR_WAIT_FOR_REVIEW')).toEqual('Verifier improvement report sent to regulator');
    expect(pipe.transform('VIR_RESPOND_TO_REGULATOR_COMMENTS')).toEqual('Respond to regulator comments');

    expect(pipe.transform('AIR_APPLICATION_SUBMIT')).toEqual('Annual improvement report');
    expect(pipe.transform('AIR_APPLICATION_REVIEW')).toEqual('Annual improvement report review');
    expect(pipe.transform('AIR_WAIT_FOR_REVIEW')).toEqual('Annual improvement report sent to regulator');
    expect(pipe.transform('AIR_RESPOND_TO_REGULATOR_COMMENTS')).toEqual('Annual improvement report follow up');

    expect(pipe.transform('DRE_APPLICATION_SUBMIT')).toEqual('Determine reportable emissions');
    expect(pipe.transform('DRE_MAKE_PAYMENT')).toEqual('Pay reportable emissions fee');
    expect(pipe.transform('DRE_TRACK_PAYMENT')).toEqual('Track payment for reportable emissions');
    expect(pipe.transform('DRE_CONFIRM_PAYMENT')).toEqual('Track payment for reportable emissions');

    expect(pipe.transform('NON_COMPLIANCE_APPLICATION_SUBMIT')).toEqual('Provide details of breach: non-compliance');

    expect(pipe.transform('DOAL_APPLICATION_SUBMIT')).toEqual('Determination of activity level change');
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
    expect(pipe.transform('EMP_ISSUANCE_UKETS_WAIT_FOR_PEER_REVIEW')).toEqual(
      'Emissions monitoring plan application sent to peer reviewer',
    );
    expect(pipe.transform('EMP_ISSUANCE_UKETS_APPLICATION_PEER_REVIEW')).toEqual(
      'Peer review emissions monitoring plan application',
    );
    expect(pipe.transform('EMP_ISSUANCE_UKETS_WAIT_FOR_AMENDS')).toEqual(
      'Emissions monitoring plan application returned to operator',
    );
    expect(pipe.transform('EMP_ISSUANCE_UKETS_APPLICATION_AMENDS_SUBMIT')).toEqual(
      'Amend your emissions monitoring plan',
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
      'Amend your emissions monitoring plan',
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

    expect(pipe.transform('AVIATION_AER_UKETS_WAIT_FOR_REVIEW')).toEqual('Emissions report sent to regulator');
    expect(pipe.transform('AVIATION_AER_UKETS_APPLICATION_SUBMIT')).toEqual('Emissions report');
    expect(pipe.transform('AVIATION_AER_CORSIA_APPLICATION_SUBMIT')).toEqual('Emissions report');
    expect(pipe.transform('AVIATION_AER_UKETS_APPLICATION_REVIEW')).toEqual('Review emissions report');
    expect(pipe.transform('AVIATION_AER_UKETS_AMEND_WAIT_FOR_VERIFICATION')).toEqual(
      'Emissions report sent to verifier',
    );
    expect(pipe.transform('AVIATION_AER_UKETS_WAIT_FOR_VERIFICATION')).toEqual('Emissions report sent to verifier');
    expect(pipe.transform('AVIATION_AER_CORSIA_WAIT_FOR_VERIFICATION')).toEqual('Emissions report sent to verifier');
    expect(pipe.transform('AVIATION_AER_UKETS_WAIT_FOR_AMENDS')).toEqual('Emission report returned to operator');
    expect(pipe.transform('AVIATION_AER_UKETS_APPLICATION_AMENDS_SUBMIT')).toEqual('Amend your emissions report');

    expect(pipe.transform('AVIATION_DRE_UKETS_APPLICATION_SUBMIT')).toEqual('Determine emissions');
    expect(pipe.transform('AVIATION_DRE_UKETS_MAKE_PAYMENT')).toEqual('Pay emissions determination fee');
    expect(pipe.transform('AVIATION_DRE_UKETS_TRACK_PAYMENT')).toEqual('Track payment for emissions determination');
    expect(pipe.transform('AVIATION_DRE_UKETS_CONFIRM_PAYMENT')).toEqual('Track payment for emissions determination');
    expect(pipe.transform('AVIATION_DRE_UKETS_APPLICATION_PEER_REVIEW')).toEqual(
      'Peer review aviation emissions report determination',
    );
    expect(pipe.transform('AVIATION_DRE_UKETS_WAIT_FOR_PEER_REVIEW')).toEqual(
      'Aviation emissions report determination sent to peer reviewer',
    );

    expect(pipe.transform('AVIATION_ACCOUNT_CLOSURE_SUBMIT')).toEqual('Close account');
    expect(pipe.transform('EMP_VARIATION_UKETS_APPLICATION_SUBMIT')).toEqual(
      'Apply to vary your emissions monitoring plan',
    );

    expect(pipe.transform('EMP_VARIATION_CORSIA_APPLICATION_SUBMIT')).toEqual(
      'Apply to vary your emissions monitoring plan',
    );
    expect(pipe.transform('EMP_VARIATION_UKETS_APPLICATION_REVIEW')).toEqual(
      'Review emissions monitoring plan variation',
    );

    expect(pipe.transform('EMP_VARIATION_CORSIA_APPLICATION_REVIEW')).toEqual(
      'Review emissions monitoring plan variation',
    );
    expect(pipe.transform('EMP_VARIATION_UKETS_WAIT_FOR_REVIEW')).toEqual(
      `Emissions monitoring plan variation sent to regulator`,
    );
    expect(pipe.transform('EMP_VARIATION_CORSIA_WAIT_FOR_REVIEW')).toEqual(
      `Emissions monitoring plan variation sent to regulator`,
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

    expect(pipe.transform('EMP_ISSUANCE_CORSIA_WAIT_FOR_REVIEW')).toEqual(
      'Emissions monitoring plan application sent to regulator',
    );

    expect(pipe.transform('NON_COMPLIANCE_DAILY_PENALTY_NOTICE_WAIT_FOR_PEER_REVIEW')).toEqual(
      'Initial penalty notice sent to peer reviewer: non-compliance',
    );
    expect(pipe.transform('NON_COMPLIANCE_DAILY_PENALTY_NOTICE_APPLICATION_PEER_REVIEW')).toEqual(
      'Peer review initial penalty: non-compliance',
    );

    expect(pipe.transform('AVIATION_VIR_APPLICATION_SUBMIT')).toEqual(`Verifier improvement report`);
    expect(pipe.transform('AVIATION_VIR_WAIT_FOR_REVIEW')).toEqual(`Verifier improvement report sent to regulator`);
    expect(pipe.transform('AVIATION_VIR_APPLICATION_REVIEW')).toEqual(`Review verifier improvement report`);
    expect(pipe.transform('AVIATION_VIR_WAIT_FOR_RFI_RESPONSE')).toEqual(`Request for information sent to operator`);
    expect(pipe.transform('AVIATION_VIR_RFI_RESPONSE_SUBMIT')).toEqual(
      `Respond to regulator's request for information`,
    );
    expect(pipe.transform('AVIATION_VIR_RESPOND_TO_REGULATOR_COMMENTS')).toEqual(`Verifier improvement report`);

    expect(pipe.transform('RETURN_OF_ALLOWANCES_APPLICATION_SUBMIT')).toEqual(`Return of allowances`);
    expect(pipe.transform('RETURN_OF_ALLOWANCES_WAIT_FOR_PEER_REVIEW')).toEqual(`Return of allowances`);
    expect(pipe.transform('RETURN_OF_ALLOWANCES_APPLICATION_PEER_REVIEW')).toEqual(`Peer review return of allowances`);
    expect(pipe.transform('RETURN_OF_ALLOWANCES_RETURNED_APPLICATION_SUBMIT')).toEqual(`Returned allowances`);

    expect(pipe.transform('AVIATION_NON_COMPLIANCE_APPLICATION_SUBMIT')).toEqual(
      `Provide details of breach: non-compliance`,
    );
    expect(pipe.transform('AVIATION_NON_COMPLIANCE_DAILY_PENALTY_NOTICE')).toEqual(`Upload daily penalty notice`);
    expect(pipe.transform('AVIATION_NON_COMPLIANCE_DAILY_PENALTY_NOTICE_WAIT_FOR_PEER_REVIEW')).toEqual(
      `Initial penalty notice sent to peer reviewer: non-compliance`,
    );
    expect(pipe.transform('AVIATION_NON_COMPLIANCE_DAILY_PENALTY_NOTICE_APPLICATION_PEER_REVIEW')).toEqual(
      `Peer review initial penalty: non-compliance`,
    );
    expect(pipe.transform('AVIATION_NON_COMPLIANCE_NOTICE_OF_INTENT')).toEqual(
      `Upload notice of intent: non-compliance`,
    );
    expect(pipe.transform('AVIATION_NON_COMPLIANCE_NOTICE_OF_INTENT_WAIT_FOR_PEER_REVIEW')).toEqual(
      `Notice of intent sent to peer reviewer: non-compliance`,
    );
    expect(pipe.transform('AVIATION_NON_COMPLIANCE_NOTICE_OF_INTENT_APPLICATION_PEER_REVIEW')).toEqual(
      `Peer review notice of intent: non-compliance`,
    );
    expect(pipe.transform('AVIATION_NON_COMPLIANCE_CIVIL_PENALTY')).toEqual(`Upload penalty notice: non-compliance`);
    expect(pipe.transform('AVIATION_NON_COMPLIANCE_CIVIL_PENALTY_WAIT_FOR_PEER_REVIEW')).toEqual(
      `Penalty sent to peer reviewer: non-compliance`,
    );
    expect(pipe.transform('AVIATION_NON_COMPLIANCE_CIVIL_PENALTY_APPLICATION_PEER_REVIEW')).toEqual(
      `Peer review upload penalty: non-compliance`,
    );

    expect(pipe.transform(null)).toBeNull();
  });
});
