import { ItemActionTypePipe } from './item-action-type.pipe';

describe('ItemActionTypePipe', () => {
  let pipe: ItemActionTypePipe;

  beforeAll(() => (pipe = new ItemActionTypePipe()));

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should properly transform action types', () => {
    expect(pipe.transform('INSTALLATION_ACCOUNT_OPENING_ACCEPTED')).toEqual(
      'The regulator accepted the installation account application',
    );
    expect(pipe.transform('INSTALLATION_ACCOUNT_OPENING_ACCOUNT_APPROVED')).toEqual(
      'Installation application approved',
    );
    expect(pipe.transform('INSTALLATION_ACCOUNT_OPENING_REJECTED')).toEqual(
      'The regulator rejected the installation account application',
    );
    expect(pipe.transform('INSTALLATION_ACCOUNT_OPENING_APPLICATION_SUBMITTED')).toEqual('Original application');

    expect(pipe.transform('PERMIT_ISSUANCE_APPLICATION_AMENDS_SUBMITTED')).toEqual(
      'Amended permit application submitted',
    );
    expect(pipe.transform('PERMIT_ISSUANCE_APPLICATION_DEEMED_WITHDRAWN')).toEqual(
      'Permit application deemed withdrawn',
    );
    expect(pipe.transform('PERMIT_ISSUANCE_APPLICATION_GRANTED')).toEqual('Permit application approved');
    expect(pipe.transform('PERMIT_ISSUANCE_APPLICATION_PEER_REVIEWER_ACCEPTED')).toEqual(
      'Peer review agreement submitted',
    );
    expect(pipe.transform('PERMIT_ISSUANCE_APPLICATION_PEER_REVIEWER_REJECTED')).toEqual(
      'Peer review disagreement submitted',
    );
    expect(pipe.transform('PERMIT_ISSUANCE_APPLICATION_REJECTED')).toEqual('Permit application rejected');
    expect(pipe.transform('PERMIT_ISSUANCE_APPLICATION_RETURNED_FOR_AMENDS')).toEqual(
      'Permit application returned for amends',
    );
    expect(pipe.transform('PERMIT_ISSUANCE_APPLICATION_SUBMITTED')).toEqual('New permit submitted');
    expect(pipe.transform('PERMIT_ISSUANCE_PEER_REVIEW_REQUESTED')).toEqual('Peer review requested');
    expect(pipe.transform('PERMIT_ISSUANCE_RECALLED_FROM_AMENDS')).toEqual('Permit application recalled');

    expect(pipe.transform('PERMIT_SURRENDER_APPLICATION_CANCELLED')).toEqual('Surrender request cancelled');
    expect(pipe.transform('PERMIT_SURRENDER_APPLICATION_PEER_REVIEWER_ACCEPTED')).toEqual(
      'Peer review agreement submitted',
    );
    expect(pipe.transform('PERMIT_SURRENDER_APPLICATION_PEER_REVIEWER_REJECTED')).toEqual(
      'Peer review disagreement submitted',
    );
    expect(pipe.transform('PERMIT_SURRENDER_APPLICATION_SUBMITTED')).toEqual('Surrender request submitted');
    expect(pipe.transform('PERMIT_SURRENDER_PEER_REVIEW_REQUESTED')).toEqual('Peer review requested');
    expect(pipe.transform('PERMIT_SURRENDER_CESSATION_COMPLETED')).toEqual('Cessation completed');
    expect(pipe.transform('PERMIT_SURRENDER_APPLICATION_DEEMED_WITHDRAWN')).toEqual(
      'Surrender request deemed withdrawn',
    );
    expect(pipe.transform('PERMIT_SURRENDER_APPLICATION_GRANTED')).toEqual('Surrender request approved');
    expect(pipe.transform('PERMIT_SURRENDER_APPLICATION_REJECTED')).toEqual('Surrender request rejected');

    expect(pipe.transform('PERMIT_REVOCATION_APPLICATION_CANCELLED')).toEqual('Revocation request cancelled');
    expect(pipe.transform('PERMIT_REVOCATION_PEER_REVIEW_REQUESTED')).toEqual('Peer review requested');
    expect(pipe.transform('PERMIT_REVOCATION_APPLICATION_PEER_REVIEWER_ACCEPTED')).toEqual(
      'Peer review agreement submitted',
    );
    expect(pipe.transform('PERMIT_REVOCATION_APPLICATION_PEER_REVIEWER_REJECTED')).toEqual(
      'Peer review disagreement submitted',
    );
    expect(pipe.transform('PERMIT_REVOCATION_CESSATION_COMPLETED')).toEqual('Cessation completed');
    expect(pipe.transform('PERMIT_REVOCATION_APPLICATION_SUBMITTED')).toEqual('Revocation request submitted');
    expect(pipe.transform('PERMIT_REVOCATION_APPLICATION_WITHDRAWN')).toEqual('Revocation request withdrawn');

    expect(pipe.transform('PERMIT_NOTIFICATION_APPLICATION_SUBMITTED')).toEqual('Notification submitted');
    expect(pipe.transform('PERMIT_NOTIFICATION_APPLICATION_CANCELLED')).toEqual('Notification cancelled');
    expect(pipe.transform('PERMIT_NOTIFICATION_FOLLOW_UP_RESPONSE_SUBMITTED')).toEqual('Follow up response submitted');
    expect(pipe.transform('PERMIT_NOTIFICATION_APPLICATION_GRANTED')).toEqual('Notification approved');
    expect(pipe.transform('PERMIT_NOTIFICATION_APPLICATION_REJECTED')).toEqual('Notification rejected');
    expect(pipe.transform('PERMIT_NOTIFICATION_FOLLOW_UP_DATE_EXTENDED')).toEqual(
      'Follow up response due date updated',
    );
    expect(pipe.transform('PERMIT_NOTIFICATION_PEER_REVIEW_REQUESTED')).toEqual('Peer review requested');
    expect(pipe.transform('PERMIT_NOTIFICATION_APPLICATION_PEER_REVIEWER_ACCEPTED')).toEqual(
      'Peer review agreement submitted',
    );
    expect(pipe.transform('PERMIT_NOTIFICATION_APPLICATION_PEER_REVIEWER_REJECTED')).toEqual(
      'Peer review disagreement submitted',
    );
    expect(pipe.transform('PERMIT_NOTIFICATION_APPLICATION_COMPLETED')).toEqual('Notification completed');
    expect(pipe.transform('PERMIT_NOTIFICATION_FOLLOW_UP_RETURNED_FOR_AMENDS')).toEqual(
      'Follow up response returned for amends',
    );
    expect(pipe.transform('PERMIT_NOTIFICATION_FOLLOW_UP_RECALLED_FROM_AMENDS')).toEqual('Follow up response recalled');
    expect(pipe.transform('PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_AMENDS_SUBMITTED')).toEqual(
      'Amended follow up response submitted',
    );
    expect(pipe.transform('PERMIT_NOTIFICATION_APPLICATION_CESSATION_COMPLETED')).toEqual('Regulator review completed');

    expect(pipe.transform('PERMIT_VARIATION_APPLICATION_SUBMITTED')).toEqual('Permit variation submitted');
    expect(pipe.transform('PERMIT_VARIATION_APPLICATION_CANCELLED')).toEqual('Variation application cancelled');
    expect(pipe.transform('PERMIT_VARIATION_PEER_REVIEW_REQUESTED')).toEqual('Peer review requested');
    expect(pipe.transform('PERMIT_VARIATION_APPLICATION_PEER_REVIEWER_ACCEPTED')).toEqual(
      'Peer review agreement submitted',
    );
    expect(pipe.transform('PERMIT_VARIATION_APPLICATION_PEER_REVIEWER_REJECTED')).toEqual(
      'Peer review disagreement submitted',
    );
    expect(pipe.transform('PERMIT_VARIATION_APPLICATION_REGULATOR_LED_APPROVED')).toEqual(
      'Variation application approved',
    );

    expect(pipe.transform('AER_APPLICATION_SUBMITTED')).toEqual('Emissions report submitted to regulator');
    expect(pipe.transform('AER_APPLICATION_SENT_TO_VERIFIER')).toEqual('Emissions report submitted to verifier');
    expect(pipe.transform('AER_RECALLED_FROM_VERIFICATION')).toEqual('Emissions report recalled');
    expect(pipe.transform('AER_APPLICATION_COMPLETED')).toEqual('Emissions report reviewed');
    expect(pipe.transform('AER_APPLICATION_VERIFICATION_SUBMITTED')).toEqual(
      'Verification statement submitted to operator',
    );
    expect(pipe.transform('AER_APPLICATION_RE_INITIATED')).toEqual('Emissions report returned for amends');
    expect(pipe.transform('AER_APPLICATION_AMENDS_SENT_TO_VERIFIER')).toEqual('Amended emissions report submitted');
    expect(pipe.transform('AER_APPLICATION_REVIEW_SKIPPED')).toEqual('Completed without review');
    expect(pipe.transform('AER_VERIFICATION_RETURNED_TO_OPERATOR')).toEqual(
      'Verifier returned to operator for changes',
    );

    expect(pipe.transform('VIR_APPLICATION_SUBMITTED')).toEqual('Verifier improvement report submitted');
    expect(pipe.transform('VIR_APPLICATION_REVIEWED')).toEqual('Verifier improvement report decision submitted');
    expect(pipe.transform('VIR_APPLICATION_RESPONDED_TO_REGULATOR_COMMENTS')).toEqual('Follow up response submitted');

    expect(pipe.transform('AIR_APPLICATION_SUBMITTED')).toEqual('Annual improvement report submitted');
    expect(pipe.transform('AIR_APPLICATION_REVIEWED')).toEqual('Annual improvement report decision submitted');
    expect(pipe.transform('AIR_APPLICATION_RESPONDED_TO_REGULATOR_COMMENTS')).toEqual('Follow up response submitted');

    expect(pipe.transform('VERIFICATION_STATEMENT_CANCELLED')).toEqual('Verification statement cancelled');

    expect(pipe.transform('PAYMENT_MARKED_AS_PAID')).toEqual('Payment marked as paid (BACS)');
    expect(pipe.transform('PAYMENT_CANCELLED')).toEqual('Payment task cancelled');
    expect(pipe.transform('PAYMENT_MARKED_AS_RECEIVED')).toEqual('Payment marked as received');
    expect(pipe.transform('PAYMENT_COMPLETED')).toEqual('Payment confirmed via GOV.UK pay');

    expect(pipe.transform('RDE_ACCEPTED')).toEqual('Deadline extension date accepted');
    expect(pipe.transform('RDE_CANCELLED')).toEqual('Deadline extension date rejected');
    expect(pipe.transform('RDE_REJECTED')).toEqual('Deadline extension date rejected');
    expect(pipe.transform('RDE_FORCE_REJECTED')).toEqual('Deadline extension date rejected');
    expect(pipe.transform('RDE_EXPIRED')).toEqual('Deadline extension date expired');
    expect(pipe.transform('RDE_FORCE_ACCEPTED')).toEqual('Deadline extension date approved');
    expect(pipe.transform('RDE_SUBMITTED')).toEqual('Deadline extension date requested');

    expect(pipe.transform('RFI_CANCELLED')).toEqual('Request for information withdrawn');
    expect(pipe.transform('RFI_EXPIRED')).toEqual('Request for information expired');
    expect(pipe.transform('RFI_RESPONSE_SUBMITTED')).toEqual('Request for information responded');
    expect(pipe.transform('RFI_SUBMITTED')).toEqual('Request for information sent');
    expect(pipe.transform('REQUEST_TERMINATED')).toEqual('Workflow terminated by the system');

    expect(pipe.transform('PERMIT_TRANSFER_APPLICATION_CANCELLED')).toEqual('Transfer application cancelled');
    expect(pipe.transform('PERMIT_TRANSFER_A_APPLICATION_SUBMITTED')).toEqual('Permit transfer started');
    expect(pipe.transform('PERMIT_TRANSFER_B_APPLICATION_SUBMITTED')).toEqual('Permit transfer submitted');
    expect(pipe.transform('PERMIT_TRANSFER_B_APPLICATION_RETURNED_FOR_AMENDS')).toEqual(
      'Permit transfer returned for amends',
    );
    expect(pipe.transform('PERMIT_TRANSFER_B_RECALLED_FROM_AMENDS')).toEqual('Permit transfer recalled');
    expect(pipe.transform('PERMIT_TRANSFER_B_APPLICATION_AMENDS_SUBMITTED')).toEqual(
      'Amended transfer application submitted',
    );
    expect(pipe.transform('PERMIT_TRANSFER_B_PEER_REVIEW_REQUESTED')).toEqual('Peer review requested');
    expect(pipe.transform('PERMIT_TRANSFER_B_APPLICATION_PEER_REVIEWER_ACCEPTED')).toEqual(
      'Peer review agreement submitted',
    );
    expect(pipe.transform('PERMIT_TRANSFER_B_APPLICATION_PEER_REVIEWER_REJECTED')).toEqual(
      'Peer review disagreement submitted',
    );
    expect(pipe.transform('PERMIT_TRANSFER_A_APPLICATION_DEEMED_WITHDRAWN')).toEqual(
      'Permit transfer deemed withdrawn',
    );
    expect(pipe.transform('PERMIT_TRANSFER_A_APPLICATION_GRANTED')).toEqual('Permit transfer approved');
    expect(pipe.transform('PERMIT_TRANSFER_A_APPLICATION_REJECTED')).toEqual('Permit transfer rejected');
    expect(pipe.transform('PERMIT_TRANSFER_B_APPLICATION_DEEMED_WITHDRAWN')).toEqual(
      'Permit transfer deemed withdrawn',
    );
    expect(pipe.transform('PERMIT_TRANSFER_B_APPLICATION_GRANTED')).toEqual('Permit transfer approved');
    expect(pipe.transform('PERMIT_TRANSFER_B_APPLICATION_REJECTED')).toEqual('Permit transfer rejected');

    expect(pipe.transform('DRE_APPLICATION_CANCELLED')).toEqual('Reportable emissions cancelled');
    expect(pipe.transform('DRE_APPLICATION_PEER_REVIEW_REQUESTED')).toEqual('Peer review requested');
    expect(pipe.transform('DRE_APPLICATION_PEER_REVIEWER_ACCEPTED')).toEqual('Peer review agreement submitted');
    expect(pipe.transform('DRE_APPLICATION_PEER_REVIEWER_REJECTED')).toEqual('Peer review disagreement submitted');

    expect(pipe.transform('DOAL_APPLICATION_PROCEEDED_TO_AUTHORITY')).toEqual(
      'Activity level determination sent to UK Authority',
    );
    expect(pipe.transform('DOAL_APPLICATION_CLOSED')).toEqual('Activity level determination closed');
    expect(pipe.transform('DOAL_APPLICATION_PEER_REVIEW_REQUESTED')).toEqual('Peer review requested');
    expect(pipe.transform('DOAL_APPLICATION_PEER_REVIEWER_ACCEPTED')).toEqual('Peer review agreement submitted');
    expect(pipe.transform('DOAL_APPLICATION_PEER_REVIEWER_REJECTED')).toEqual('Peer review disagreement submitted');
    expect(pipe.transform('DOAL_APPLICATION_ACCEPTED')).toEqual('Activity level determination accepted as approved');
    expect(pipe.transform('DOAL_APPLICATION_ACCEPTED_WITH_CORRECTIONS')).toEqual(
      'Activity level determination accepted as approved with corrections',
    );
    expect(pipe.transform('DOAL_APPLICATION_REJECTED')).toEqual('Activity level determination  not approved');
    expect(pipe.transform('DOAL_APPLICATION_CANCELLED')).toEqual('Activity level determination cancelled');

    expect(pipe.transform('BATCH_REISSUE_SUBMITTED')).toEqual('Batch variation submitted');
    expect(pipe.transform('BATCH_REISSUE_COMPLETED')).toEqual('Batch variation completed');
    expect(pipe.transform('REISSUE_COMPLETED')).toEqual('Batch variation completed');

    expect(pipe.transform('EMP_ISSUANCE_UKETS_APPLICATION_SUBMITTED')).toEqual('Submitted');
    expect(pipe.transform('EMP_ISSUANCE_UKETS_APPLICATION_APPROVED')).toEqual('Approved');
    expect(pipe.transform('EMP_ISSUANCE_UKETS_APPLICATION_DEEMED_WITHDRAWN')).toEqual('Withdrawn');
    expect(pipe.transform('EMP_ISSUANCE_UKETS_APPLICATION_RETURNED_FOR_AMENDS')).toEqual(
      'Returned to operator for changes',
    );
    expect(pipe.transform('EMP_ISSUANCE_CORSIA_APPLICATION_RETURNED_FOR_AMENDS')).toEqual(
      'Returned to operator for changes',
    );
    expect(pipe.transform('EMP_ISSUANCE_UKETS_APPLICATION_AMENDS_SUBMITTED')).toEqual('Changes submitted');
    expect(pipe.transform('EMP_ISSUANCE_UKETS_APPLICATION_PEER_REVIEWER_ACCEPTED')).toEqual(
      'Peer review agreement submitted',
    );
    expect(pipe.transform('EMP_ISSUANCE_UKETS_APPLICATION_PEER_REVIEWER_REJECTED')).toEqual(
      'Peer review disagreement submitted',
    );
    expect(pipe.transform('EMP_ISSUANCE_UKETS_PEER_REVIEW_REQUESTED')).toEqual('Peer review requested');
    expect(pipe.transform('EMP_ISSUANCE_UKETS_RECALLED_FROM_AMENDS')).toEqual('Recalled');

    expect(pipe.transform('EMP_VARIATION_UKETS_APPLICATION_SUBMITTED')).toEqual('Submitted');
    expect(pipe.transform('EMP_VARIATION_APPLICATION_CANCELLED')).toEqual('Cancelled');
    expect(pipe.transform('EMP_VARIATION_UKETS_APPLICATION_REGULATOR_LED_APPROVED')).toEqual('Approved');
    expect(pipe.transform('EMP_VARIATION_CORSIA_APPLICATION_REGULATOR_LED_APPROVED')).toEqual('Approved');
    expect(pipe.transform('EMP_VARIATION_UKETS_APPLICATION_APPROVED')).toEqual('Approved');
    expect(pipe.transform('EMP_VARIATION_UKETS_APPLICATION_DEEMED_WITHDRAWN')).toEqual('Deemed withdrawn');
    expect(pipe.transform('EMP_VARIATION_UKETS_APPLICATION_REJECTED')).toEqual('Rejected');
    expect(pipe.transform('EMP_VARIATION_CORSIA_APPLICATION_APPROVED')).toEqual('Approved');
    expect(pipe.transform('EMP_VARIATION_CORSIA_APPLICATION_DEEMED_WITHDRAWN')).toEqual('Deemed withdrawn');
    expect(pipe.transform('EMP_VARIATION_CORSIA_APPLICATION_REJECTED')).toEqual('Rejected');
    expect(pipe.transform('EMP_VARIATION_UKETS_PEER_REVIEW_REQUESTED')).toEqual('Peer review requested');
    expect(pipe.transform('EMP_VARIATION_UKETS_APPLICATION_PEER_REVIEWER_ACCEPTED')).toEqual(
      'Peer review agreement submitted',
    );
    expect(pipe.transform('EMP_VARIATION_UKETS_APPLICATION_PEER_REVIEWER_REJECTED')).toEqual(
      'Peer review disagreement submitted',
    );
    expect(pipe.transform('EMP_VARIATION_CORSIA_PEER_REVIEW_REQUESTED')).toEqual('Peer review requested');
    expect(pipe.transform('EMP_VARIATION_CORSIA_APPLICATION_PEER_REVIEWER_ACCEPTED')).toEqual(
      'Peer review agreement submitted',
    );
    expect(pipe.transform('EMP_VARIATION_CORSIA_APPLICATION_PEER_REVIEWER_REJECTED')).toEqual(
      'Peer review disagreement submitted',
    );
    expect(pipe.transform('EMP_VARIATION_UKETS_APPLICATION_AMENDS_SUBMITTED')).toEqual('Changes submitted');
    expect(pipe.transform('EMP_VARIATION_CORSIA_APPLICATION_AMENDS_SUBMITTED')).toEqual('Changes submitted');
    expect(pipe.transform('EMP_VARIATION_UKETS_RECALLED_FROM_AMENDS')).toEqual('Recalled');
    expect(pipe.transform('EMP_VARIATION_CORSIA_RECALLED_FROM_AMENDS')).toEqual('Recalled');

    expect(pipe.transform('EMP_VARIATION_UKETS_APPLICATION_RETURNED_FOR_AMENDS')).toEqual(
      'Returned to operator for changes',
    );

    expect(pipe.transform('EMP_ISSUANCE_CORSIA_APPLICATION_SUBMITTED')).toEqual('Submitted');
    expect(pipe.transform('EMP_VARIATION_CORSIA_APPLICATION_RETURNED_FOR_AMENDS')).toEqual(
      'Returned to operator for changes',
    );

    expect(pipe.transform('EMP_ISSUANCE_CORSIA_APPLICATION_RETURNED_FOR_AMENDS')).toEqual(
      'Returned to operator for changes',
    );
    expect(pipe.transform('EMP_ISSUANCE_CORSIA_APPLICATION_AMENDS_SUBMITTED')).toEqual('Changes submitted');
    expect(pipe.transform('EMP_ISSUANCE_CORSIA_RECALLED_FROM_AMENDS')).toEqual('Recalled');

    expect(pipe.transform('EMP_ISSUANCE_CORSIA_PEER_REVIEW_REQUESTED')).toEqual('Peer review requested');
    expect(pipe.transform('EMP_ISSUANCE_CORSIA_APPLICATION_PEER_REVIEWER_ACCEPTED')).toEqual(
      'Peer review agreement submitted',
    );
    expect(pipe.transform('EMP_ISSUANCE_CORSIA_APPLICATION_PEER_REVIEWER_REJECTED')).toEqual(
      'Peer review disagreement submitted',
    );
    expect(pipe.transform('EMP_ISSUANCE_CORSIA_APPLICATION_APPROVED')).toEqual('Approved');
    expect(pipe.transform('EMP_ISSUANCE_CORSIA_APPLICATION_DEEMED_WITHDRAWN')).toEqual('Withdrawn');

    expect(pipe.transform('NON_COMPLIANCE_APPLICATION_SUBMITTED')).toEqual('Non-compliance details provided');
    expect(pipe.transform('NON_COMPLIANCE_APPLICATION_CLOSED')).toEqual('Non Compliance closed');

    expect(pipe.transform('AVIATION_ACCOUNT_CLOSURE_CANCELLED')).toEqual('Account closure cancelled');
    expect(pipe.transform('AVIATION_ACCOUNT_CLOSURE_SUBMITTED')).toEqual('Account closed');

    expect(pipe.transform('NON_COMPLIANCE_DAILY_PENALTY_NOTICE_APPLICATION_SUBMITTED')).toEqual(
      'Initial penalty notice sent to operator',
    );
    expect(pipe.transform('NON_COMPLIANCE_DAILY_PENALTY_NOTICE_PEER_REVIEW_REQUESTED')).toEqual(
      'Peer review of initial penalty requested',
    );
    expect(pipe.transform('NON_COMPLIANCE_DAILY_PENALTY_NOTICE_PEER_REVIEWER_ACCEPTED')).toEqual(
      'Peer review agreement for initial penalty submitted',
    );
    expect(pipe.transform('NON_COMPLIANCE_DAILY_PENALTY_NOTICE_PEER_REVIEWER_REJECTED')).toEqual(
      'Peer review disagreement for initial penalty submitted',
    );

    expect(pipe.transform('NON_COMPLIANCE_NOTICE_OF_INTENT_APPLICATION_SUBMITTED')).toEqual(
      'Notice of intent sent to operator',
    );
    expect(pipe.transform('NON_COMPLIANCE_NOTICE_OF_INTENT_PEER_REVIEW_REQUESTED')).toEqual(
      'Peer review of notice of intent requested',
    );
    expect(pipe.transform('NON_COMPLIANCE_NOTICE_OF_INTENT_PEER_REVIEWER_ACCEPTED')).toEqual(
      'Peer review agreement for notice of intent submitted',
    );
    expect(pipe.transform('NON_COMPLIANCE_NOTICE_OF_INTENT_PEER_REVIEWER_REJECTED')).toEqual(
      'Peer review disagreement for notice of intent submitted',
    );

    expect(pipe.transform('NON_COMPLIANCE_CIVIL_PENALTY_APPLICATION_SUBMITTED')).toEqual(
      'Penalty notice sent to operator',
    );
    expect(pipe.transform('NON_COMPLIANCE_CIVIL_PENALTY_PEER_REVIEW_REQUESTED')).toEqual(
      'Peer review of penalty requested',
    );
    expect(pipe.transform('NON_COMPLIANCE_CIVIL_PENALTY_PEER_REVIEWER_ACCEPTED')).toEqual(
      'Peer review agreement for penalty submitted',
    );
    expect(pipe.transform('NON_COMPLIANCE_CIVIL_PENALTY_PEER_REVIEWER_REJECTED')).toEqual(
      'Peer review disagreement for penalty submitted',
    );
    expect(pipe.transform('NON_COMPLIANCE_FINAL_DETERMINATION_APPLICATION_SUBMITTED')).toEqual('Conclusion provided');

    expect(pipe.transform('AVIATION_AER_UKETS_APPLICATION_SUBMITTED')).toEqual(
      'Emissions report submitted to regulator',
    );
    expect(pipe.transform('AVIATION_AER_CORSIA_APPLICATION_SUBMITTED')).toEqual(
      'Emissions report submitted to regulator',
    );
    expect(pipe.transform('AVIATION_AER_UKETS_APPLICATION_SENT_TO_VERIFIER')).toEqual(
      'Emissions report submitted to verifier',
    );
    expect(pipe.transform('AVIATION_AER_CORSIA_APPLICATION_SENT_TO_VERIFIER')).toEqual(
      'Emissions report submitted to verifier',
    );

    expect(pipe.transform('AVIATION_AER_UKETS_APPLICATION_VERIFICATION_SUBMITTED')).toEqual(
      'Verification statement submitted to operator',
    );
    expect(pipe.transform('AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMITTED')).toEqual(
      'Verification statement submitted to operator',
    );
    expect(pipe.transform('AVIATION_AER_UKETS_APPLICATION_COMPLETED')).toEqual('Emissions report reviewed');
    expect(pipe.transform('AVIATION_AER_CORSIA_APPLICATION_COMPLETED')).toEqual('Emissions report reviewed');

    expect(pipe.transform('AVIATION_AER_RECALLED_FROM_VERIFICATION')).toEqual('Recalled from verifier');
    expect(pipe.transform('AVIATION_AER_APPLICATION_CANCELLED_DUE_TO_EXEPMT')).toEqual(
      'Cancelled due to exempt reporting status',
    );
    expect(pipe.transform('AVIATION_AER_APPLICATION_RE_INITIATED')).toEqual('Re-initiate after exemption');
    expect(pipe.transform('AVIATION_AER_UKETS_APPLICATION_RETURNED_FOR_AMENDS')).toEqual(
      'Returned to operator for changes',
    );
    expect(pipe.transform('AVIATION_AER_CORSIA_APPLICATION_RETURNED_FOR_AMENDS')).toEqual(
      'Returned to operator for changes',
    );

    expect(pipe.transform('AVIATION_AER_UKETS_APPLICATION_AMENDS_SUBMITTED')).toEqual('Changes submitted');
    expect(pipe.transform('AVIATION_AER_UKETS_APPLICATION_AMENDS_SENT_TO_VERIFIER')).toEqual('Changes submitted');
    expect(pipe.transform('AVIATION_AER_CORSIA_APPLICATION_AMENDS_SENT_TO_VERIFIER')).toEqual('Changes submitted');
    expect(pipe.transform('AVIATION_AER_CORSIA_APPLICATION_AMENDS_SUBMITTED')).toEqual('Changes submitted');

    expect(pipe.transform('AVIATION_AER_CORSIA_VERIFICATION_RETURNED_TO_OPERATOR')).toEqual(
      'Verifier returned report to operator for changes',
    );
    expect(pipe.transform('AVIATION_AER_UKETS_VERIFICATION_RETURNED_TO_OPERATOR')).toEqual(
      'Verifier returned report to operator for changes',
    );

    expect(pipe.transform('AVIATION_DRE_APPLICATION_CANCELLED')).toEqual('Reportable emissions cancelled');
    expect(pipe.transform('AVIATION_AER_UKETS_APPLICATION_CANCELLED_DUE_TO_DRE')).toEqual(
      'Cancelled due to a determination of emissions workflow',
    );
    expect(pipe.transform('AVIATION_DRE_UKETS_PEER_REVIEW_REQUESTED')).toEqual('Peer review requested');
    expect(pipe.transform('AVIATION_DRE_UKETS_PEER_REVIEWER_ACCEPTED')).toEqual('Peer review agreement submitted');
    expect(pipe.transform('AVIATION_DRE_UKETS_PEER_REVIEWER_REJECTED')).toEqual('Peer review disagreement submitted');
    expect(pipe.transform('AVIATION_DRE_UKETS_APPLICATION_SUBMITTED')).toEqual('Aviation emissions updated');

    expect(pipe.transform('AVIATION_VIR_APPLICATION_SUBMITTED')).toEqual('Verifier improvement report submitted');
    expect(pipe.transform('AVIATION_VIR_APPLICATION_REVIEWED')).toEqual(
      'Verifier improvement report decision submitted',
    );
    expect(pipe.transform('AVIATION_VIR_APPLICATION_RESPONDED_TO_REGULATOR_COMMENTS')).toEqual(
      'Follow up response submitted',
    );

    expect(pipe.transform('RETURN_OF_ALLOWANCES_APPLICATION_SUBMITTED')).toEqual('Return of allowances submitted');
    expect(pipe.transform('RETURN_OF_ALLOWANCES_PEER_REVIEW_REQUESTED')).toEqual(
      'Peer review of return of allowances requested',
    );
    expect(pipe.transform('RETURN_OF_ALLOWANCES_APPLICATION_PEER_REVIEWER_ACCEPTED')).toEqual(
      'Peer review agreement for return of allowances submitted',
    );
    expect(pipe.transform('RETURN_OF_ALLOWANCES_APPLICATION_PEER_REVIEWER_REJECTED')).toEqual(
      'Peer review disagreement for return of allowances submitted',
    );
    expect(pipe.transform('RETURN_OF_ALLOWANCES_APPLICATION_CANCELLED')).toEqual('Return of allowances cancelled');
    expect(pipe.transform('RETURN_OF_ALLOWANCES_RETURNED_APPLICATION_COMPLETED')).toEqual(
      'Returned allowances submitted',
    );

    expect(pipe.transform('INSTALLATION_ONSITE_INSPECTION_APPLICATION_PEER_REVIEW_REQUESTED')).toEqual(
      'Peer review requested',
    );
    expect(pipe.transform('INSTALLATION_ONSITE_INSPECTION_APPLICATION_PEER_REVIEWER_ACCEPTED')).toEqual(
      'Peer review agreement submitted',
    );
    expect(pipe.transform('INSTALLATION_ONSITE_INSPECTION_APPLICATION_PEER_REVIEWER_REJECTED')).toEqual(
      'Peer review disagreement submitted',
    );
    expect(pipe.transform('INSTALLATION_ONSITE_INSPECTION_APPLICATION_SUBMITTED')).toEqual(
      'On-site inspection submitted to Operator',
    );
    expect(pipe.transform('INSTALLATION_ONSITE_INSPECTION_OPERATOR_RESPONDED')).toEqual(
      'On-site inspection submitted to Regulator',
    );

    expect(pipe.transform('INSTALLATION_AUDIT_APPLICATION_PEER_REVIEW_REQUESTED')).toEqual('Peer review requested');
    expect(pipe.transform('INSTALLATION_AUDIT_APPLICATION_PEER_REVIEWER_ACCEPTED')).toEqual(
      'Peer review agreement submitted',
    );
    expect(pipe.transform('INSTALLATION_AUDIT_APPLICATION_PEER_REVIEWER_REJECTED')).toEqual(
      'Peer review disagreement submitted',
    );
    expect(pipe.transform('INSTALLATION_AUDIT_APPLICATION_SUBMITTED')).toEqual('Audit report submitted to Operator');

    expect(pipe.transform('AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_APPLICATION_SUBMITTED')).toEqual(
      'Annual offsetting requirements submitted to Operator',
    );
    expect(pipe.transform('AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_APPLICATION_PEER_REVIEW_REQUESTED')).toEqual(
      'Peer review requested',
    );
    expect(pipe.transform('AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_APPLICATION_PEER_REVIEW_ACCEPTED')).toEqual(
      'Peer review agreement submitted',
    );
    expect(pipe.transform('AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_APPLICATION_PEER_REVIEW_REJECTED')).toEqual(
      'Peer review disagreement submitted',
    );
    expect(pipe.transform('AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_APPLICATION_CANCELLED')).toEqual(
      'Annual offsetting requirements cancelled',
    );

    expect(pipe.transform('AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_APPLICATION_PEER_REVIEW_REQUESTED')).toEqual(
      'Peer review requested',
    );
    expect(pipe.transform('AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_APPLICATION_PEER_REVIEW_ACCEPTED')).toEqual(
      'Peer review agreement submitted',
    );
    expect(pipe.transform('AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_APPLICATION_PEER_REVIEW_REJECTED')).toEqual(
      'Peer review disagreement submitted',
    );
    expect(pipe.transform('AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_APPLICATION_CANCELLED')).toEqual(
      '3-year offsetting requirements cancelled',
    );
    expect(pipe.transform('AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_APPLICATION_SUBMITTED')).toEqual(
      '3-year offsetting requirements submitted to Operator',
    );

    expect(pipe.transform('BDR_APPLICATION_SENT_TO_VERIFIER')).toEqual('Baseline data report submitted to verifier');
    expect(pipe.transform('BDR_APPLICATION_SENT_TO_REGULATOR')).toEqual('Baseline data report submitted to regulator');
    expect(pipe.transform('BDR_RECALLED_FROM_VERIFICATION')).toEqual('Baseline data report recalled');
    expect(pipe.transform('BDR_APPLICATION_VERIFICATION_SUBMITTED')).toEqual(
      'Baseline data report verification statement submitted to operator',
    );
    expect(pipe.transform('BDR_VERIFICATION_RETURNED_TO_OPERATOR')).toEqual(
      'Baseline data report returned to operator for changes',
    );
    expect(pipe.transform('BDR_APPLICATION_AMENDS_SENT_TO_VERIFIER')).toEqual(
      'Baseline data report submitted to verifier',
    );
    expect(pipe.transform('BDR_APPLICATION_PEER_REVIEW_REQUESTED')).toEqual('Peer review requested');
    expect(pipe.transform('BDR_APPLICATION_RE_INITIATED')).toEqual('Baseline data report reopened');
    expect(pipe.transform('BDR_APPLICATION_PEER_REVIEW_ACCEPTED')).toEqual('Peer review agreement submitted');
    expect(pipe.transform('BDR_APPLICATION_PEER_REVIEW_REJECTED')).toEqual('Peer review disagreement submitted');

    expect(pipe.transform('PERMANENT_CESSATION_SUBMITTED')).toEqual('Permanent cessation started');
    expect(pipe.transform('PERMANENT_CESSATION_APPLICATION_CANCELLED')).toEqual('Permanent cessation cancelled');
    expect(pipe.transform('PERMANENT_CESSATION_APPLICATION_PEER_REVIEW_REQUESTED')).toEqual('Peer review requested');
    expect(pipe.transform('PERMANENT_CESSATION_APPLICATION_PEER_REVIEW_REJECTED')).toEqual(
      'Peer review disagreement submitted',
    );
    expect(pipe.transform('PERMANENT_CESSATION_APPLICATION_PEER_REVIEW_ACCEPTED')).toEqual(
      'Peer review agreement submitted',
    );
    expect(pipe.transform('PERMANENT_CESSATION_APPLICATION_SUBMITTED')).toEqual(
      'Permanent cessation notice sent to operator',
    );

    expect(pipe.transform('AVIATION_DOE_CORSIA_SUBMITTED')).toEqual('Aviation emissions updated');
    expect(pipe.transform('AVIATION_AER_CORSIA_APPLICATION_CANCELLED_DUE_TO_DOE')).toEqual(
      'Cancelled due to an estimation of emissions workflow',
    );
    expect(pipe.transform('AVIATION_DOE_CORSIA_PEER_REVIEW_REQUESTED')).toEqual('Peer review requested');
    expect(pipe.transform('AVIATION_DOE_CORSIA_PEER_REVIEWER_ACCEPTED')).toEqual('Peer review agreement submitted');
    expect(pipe.transform('AVIATION_DOE_CORSIA_PEER_REVIEWER_REJECTED')).toEqual('Peer review disagreement submitted');
    expect(pipe.transform('AVIATION_DOE_CORSIA_SUBMIT_CANCELLED')).toEqual('Estimation of emissions cancelled');

    expect(pipe.transform('ALR_APPLICATION_SENT_TO_VERIFIER')).toEqual('Activity level report submitted to verifier');
    expect(pipe.transform('ALR_APPLICATION_VERIFICATION_SUBMITTED')).toEqual(
      'Activity level report submitted to operator',
    );
    expect(pipe.transform('ALR_VERIFICATION_RETURNED_TO_OPERATOR')).toEqual(
      'Activity level report returned to operator for changes',
    );
    expect(pipe.transform('ALR_RECALLED_FROM_VERIFICATION')).toEqual('Activity level report recalled');

    expect(pipe.transform(undefined)).toEqual('Approved Application');
  });
});
