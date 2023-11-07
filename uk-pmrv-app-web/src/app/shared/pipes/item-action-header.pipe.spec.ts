import { RequestActionDTO } from 'pmrv-api';

import { ItemActionHeaderPipe } from './item-action-header.pipe';

describe('ItemActionHeaderPipe', () => {
  let pipe: ItemActionHeaderPipe;

  const baseRequestAction: Omit<RequestActionDTO, 'type'> = {
    id: 1,
    payload: {},
    submitter: 'John Bolt',
    creationDate: '2021-03-29T12:26:36.000Z',
  };

  beforeAll(() => (pipe = new ItemActionHeaderPipe()));

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should return the installation accounts', () => {
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'INSTALLATION_ACCOUNT_OPENING_ACCEPTED',
      }),
    ).toEqual('The regulator accepted the installation account application');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'INSTALLATION_ACCOUNT_OPENING_ACCOUNT_APPROVED',
      }),
    ).toEqual('Installation application approved');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'INSTALLATION_ACCOUNT_OPENING_REJECTED',
      }),
    ).toEqual('The regulator rejected the installation account application');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'INSTALLATION_ACCOUNT_OPENING_APPLICATION_SUBMITTED',
      }),
    ).toEqual('Original application');
  });

  it('should display the approved application title', () => {
    expect(pipe.transform({})).toEqual('Approved Application');
  });

  it('should return the permit issuance application returned for amends title', () => {
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'PERMIT_ISSUANCE_APPLICATION_RETURNED_FOR_AMENDS',
      }),
    ).toEqual('Permit application returned for amends by John Bolt');
  });

  it('should return the permit surrender applications', () => {
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'PERMIT_SURRENDER_APPLICATION_CANCELLED',
      }),
    ).toEqual('Surrender request cancelled by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'PERMIT_SURRENDER_APPLICATION_PEER_REVIEWER_ACCEPTED',
      }),
    ).toEqual('Peer review agreement submitted by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'PERMIT_SURRENDER_APPLICATION_PEER_REVIEWER_REJECTED',
      }),
    ).toEqual('Peer review disagreement submitted by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'PERMIT_SURRENDER_APPLICATION_SUBMITTED',
      }),
    ).toEqual('Surrender request submitted by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'PERMIT_SURRENDER_PEER_REVIEW_REQUESTED',
      }),
    ).toEqual('Peer review requested by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'PERMIT_SURRENDER_CESSATION_COMPLETED',
      }),
    ).toEqual('Cessation completed by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'PERMIT_SURRENDER_APPLICATION_DEEMED_WITHDRAWN',
      }),
    ).toEqual('Surrender request deemed withdrawn by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'PERMIT_SURRENDER_APPLICATION_GRANTED',
      }),
    ).toEqual('Surrender request approved by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'PERMIT_SURRENDER_APPLICATION_REJECTED',
      }),
    ).toEqual('Surrender request rejected by John Bolt');
  });

  it('should return the permit notifications', () => {
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'PERMIT_NOTIFICATION_APPLICATION_SUBMITTED',
      }),
    ).toEqual('Notification submitted by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'PERMIT_NOTIFICATION_FOLLOW_UP_DATE_EXTENDED',
      }),
    ).toEqual('Follow up response due date updated by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'PERMIT_NOTIFICATION_APPLICATION_CANCELLED',
      }),
    ).toEqual('Notification cancelled');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'PERMIT_NOTIFICATION_FOLLOW_UP_RESPONSE_SUBMITTED',
      }),
    ).toEqual('Follow up response submitted by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'PERMIT_NOTIFICATION_APPLICATION_COMPLETED',
      }),
    ).toEqual('Notification completed by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'PERMIT_NOTIFICATION_FOLLOW_UP_RETURNED_FOR_AMENDS',
      }),
    ).toEqual('Follow up response returned for amends by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'PERMIT_NOTIFICATION_FOLLOW_UP_RECALLED_FROM_AMENDS',
      }),
    ).toEqual('Follow up response recalled by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_AMENDS_SUBMITTED',
      }),
    ).toEqual('Amended follow up response submitted by John Bolt');
  });

  it('should return the permit variation actions', () => {
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'PERMIT_VARIATION_APPLICATION_SUBMITTED',
      }),
    ).toEqual('Permit variation submitted by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'PERMIT_VARIATION_PEER_REVIEW_REQUESTED',
      }),
    ).toEqual('Peer review requested by John Bolt');
  });

  it('should return the permit variation regulator led action', () => {
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'PERMIT_VARIATION_APPLICATION_REGULATOR_LED_APPROVED',
      }),
    ).toEqual('Variation application approved by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'PERMIT_VARIATION_PEER_REVIEW_REQUESTED',
      }),
    ).toEqual('Peer review requested by John Bolt');
  });

  it('should return the aer relatives', () => {
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'AER_APPLICATION_SUBMITTED',
      }),
    ).toEqual('Emissions report submitted by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'AER_APPLICATION_SENT_TO_VERIFIER',
      }),
    ).toEqual('Emissions report submitted by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'AER_RECALLED_FROM_VERIFICATION',
      }),
    ).toEqual('Emissions report recalled by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'AER_APPLICATION_COMPLETED',
      }),
    ).toEqual('Emissions report reviewed by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'AER_APPLICATION_VERIFICATION_SUBMITTED',
      }),
    ).toEqual('Verification statement submitted by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'AER_APPLICATION_AMENDS_SENT_TO_VERIFIER',
      }),
    ).toEqual('Amended emissions report submitted by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'AER_APPLICATION_RE_INITIATED',
      }),
    ).toEqual('Emissions report returned for amends by John Bolt');
  });

  it('should return the vir relatives', () => {
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'VIR_APPLICATION_SUBMITTED',
      }),
    ).toEqual('Verifier improvement report submitted by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'VIR_APPLICATION_REVIEWED',
      }),
    ).toEqual('Verifier improvement report decision submitted by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'VIR_APPLICATION_RESPONDED_TO_REGULATOR_COMMENTS',
      }),
    ).toEqual('Follow up response submitted by John Bolt');
  });

  it('should return the air relatives', () => {
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'AIR_APPLICATION_SUBMITTED',
      }),
    ).toEqual('Annual improvement report submitted by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'AIR_APPLICATION_REVIEWED',
      }),
    ).toEqual('Annual improvement report decision submitted by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'AIR_APPLICATION_RESPONDED_TO_REGULATOR_COMMENTS',
      }),
    ).toEqual('Follow up response submitted by John Bolt');
  });

  it('should return the verification body amendments', () => {
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'VERIFICATION_STATEMENT_CANCELLED',
      }),
    ).toEqual('Verification statement cancelled due to a change of verification body');
  });

  it('should return the payments', () => {
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'PAYMENT_MARKED_AS_PAID',
      }),
    ).toEqual('Payment marked as paid by John Bolt (BACS)');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'PAYMENT_CANCELLED',
      }),
    ).toEqual('Payment task cancelled by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'PAYMENT_MARKED_AS_RECEIVED',
      }),
    ).toEqual('Payment marked as received by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'PAYMENT_COMPLETED',
      }),
    ).toEqual('Payment confirmed via GOV.UK pay');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'PERMIT_VARIATION_APPLICATION_PEER_REVIEWER_ACCEPTED',
      }),
    ).toEqual('Peer review agreement submitted by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'PERMIT_VARIATION_APPLICATION_PEER_REVIEWER_REJECTED',
      }),
    ).toEqual('Peer review disagreement submitted by John Bolt');
  });

  it('should return transfer actions', () => {
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'PERMIT_TRANSFER_A_APPLICATION_SUBMITTED',
      }),
    ).toEqual('Permit transfer started by John Bolt');
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'PERMIT_TRANSFER_B_APPLICATION_SUBMITTED',
      }),
    ).toEqual('Permit transfer submitted by John Bolt');
  });

  it('should return DRE', () => {
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'DRE_APPLICATION_PEER_REVIEW_REQUESTED',
      }),
    ).toEqual('Peer review requested by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'DRE_APPLICATION_CANCELLED',
      }),
    ).toEqual('Reportable emissions cancelled by John Bolt');
  });

  it('should return DOAL', () => {
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'DOAL_APPLICATION_PROCEEDED_TO_AUTHORITY',
      }),
    ).toEqual('Activity level determination sent to UK Authority by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'DOAL_APPLICATION_CLOSED',
      }),
    ).toEqual('Activity level determination closed by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'DOAL_APPLICATION_PEER_REVIEW_REQUESTED',
      }),
    ).toEqual('Peer review requested by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'DOAL_APPLICATION_PEER_REVIEWER_ACCEPTED',
      }),
    ).toEqual('Peer review agreement submitted by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'DOAL_APPLICATION_PEER_REVIEWER_REJECTED',
      }),
    ).toEqual('Peer review disagreement submitted by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'DOAL_APPLICATION_ACCEPTED',
      }),
    ).toEqual('Activity level determination accepted as approved by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'DOAL_APPLICATION_ACCEPTED_WITH_CORRECTIONS',
      }),
    ).toEqual('Activity level determination accepted as approved with corrections by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'DOAL_APPLICATION_REJECTED',
      }),
    ).toEqual('Activity level determination  not approved by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'DOAL_APPLICATION_CANCELLED',
      }),
    ).toEqual('Activity level determination cancelled by John Bolt');
  });

  it('should return batch reissue submitted', () => {
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'BATCH_REISSUE_SUBMITTED',
      }),
    ).toEqual('Batch variation submitted by John Bolt');
  });

  it('should return batch reissue completed', () => {
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'BATCH_REISSUE_COMPLETED',
      }),
    ).toEqual('Batch variation completed by John Bolt');
  });

  it('should return batch reissue completed', () => {
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'REISSUE_COMPLETED',
      }),
    ).toEqual('Batch variation completed by John Bolt');
  });

  it('should return EMP variation application approved', () => {
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'EMP_VARIATION_UKETS_APPLICATION_APPROVED',
      }),
    ).toEqual('Approved by John Bolt');
  });

  it('should return EMP corsia variation application approved', () => {
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'EMP_VARIATION_CORSIA_APPLICATION_APPROVED',
      }),
    ).toEqual('Approved by John Bolt');
  });

  it('should return EMP application UK ETS', () => {
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'EMP_ISSUANCE_UKETS_APPLICATION_SUBMITTED',
      }),
    ).toEqual('Submitted by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'EMP_ISSUANCE_UKETS_APPLICATION_APPROVED',
      }),
    ).toEqual('Approved by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'EMP_ISSUANCE_UKETS_APPLICATION_DEEMED_WITHDRAWN',
      }),
    ).toEqual('Withdrawn by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'EMP_ISSUANCE_UKETS_PEER_REVIEW_REQUESTED',
      }),
    ).toEqual('Peer review requested by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'EMP_ISSUANCE_UKETS_APPLICATION_PEER_REVIEWER_ACCEPTED',
      }),
    ).toEqual('Peer review agreement submitted by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'EMP_ISSUANCE_UKETS_APPLICATION_PEER_REVIEWER_REJECTED',
      }),
    ).toEqual('Peer review disagreement submitted by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'EMP_ISSUANCE_CORSIA_PEER_REVIEW_REQUESTED',
      }),
    ).toEqual('Peer review requested by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'EMP_ISSUANCE_CORSIA_APPLICATION_PEER_REVIEWER_ACCEPTED',
      }),
    ).toEqual('Peer review agreement submitted by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'EMP_ISSUANCE_CORSIA_APPLICATION_PEER_REVIEWER_REJECTED',
      }),
    ).toEqual('Peer review disagreement submitted by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'EMP_ISSUANCE_CORSIA_APPLICATION_APPROVED',
      }),
    ).toEqual('Approved by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'EMP_ISSUANCE_CORSIA_APPLICATION_DEEMED_WITHDRAWN',
      }),
    ).toEqual('Withdrawn by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'EMP_ISSUANCE_UKETS_APPLICATION_RETURNED_FOR_AMENDS',
      }),
    ).toEqual('Returned to operator for changes by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'EMP_ISSUANCE_UKETS_RECALLED_FROM_AMENDS',
      }),
    ).toEqual('Recalled by John Bolt');
  });

  it('should return EMP variation', () => {
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'EMP_VARIATION_UKETS_APPLICATION_SUBMITTED',
      }),
    ).toEqual('Submitted by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'EMP_VARIATION_APPLICATION_CANCELLED',
      }),
    ).toEqual('Cancelled by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'EMP_VARIATION_UKETS_APPLICATION_AMENDS_SUBMITTED',
      }),
    ).toEqual('Changes submitted by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'EMP_VARIATION_CORSIA_APPLICATION_AMENDS_SUBMITTED',
      }),
    ).toEqual('Changes submitted by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'EMP_VARIATION_UKETS_APPLICATION_RETURNED_FOR_AMENDS',
      }),
    ).toEqual('Returned to operator for changes by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'EMP_VARIATION_CORSIA_APPLICATION_RETURNED_FOR_AMENDS',
      }),
    ).toEqual('Returned to operator for changes by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'EMP_VARIATION_UKETS_PEER_REVIEW_REQUESTED',
      }),
    ).toEqual('Peer review requested by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'EMP_VARIATION_CORSIA_PEER_REVIEW_REQUESTED',
      }),
    ).toEqual('Peer review requested by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'EMP_VARIATION_UKETS_APPLICATION_PEER_REVIEWER_ACCEPTED',
      }),
    ).toEqual('Peer review agreement submitted by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'EMP_VARIATION_CORSIA_APPLICATION_PEER_REVIEWER_REJECTED',
      }),
    ).toEqual('Peer review disagreement submitted by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'EMP_VARIATION_CORSIA_APPLICATION_PEER_REVIEWER_ACCEPTED',
      }),
    ).toEqual('Peer review agreement submitted by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'EMP_VARIATION_UKETS_APPLICATION_PEER_REVIEWER_REJECTED',
      }),
    ).toEqual('Peer review disagreement submitted by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'EMP_VARIATION_UKETS_RECALLED_FROM_AMENDS',
      }),
    ).toEqual('Recalled by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'EMP_VARIATION_CORSIA_RECALLED_FROM_AMENDS',
      }),
    ).toEqual('Recalled by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'EMP_VARIATION_UKETS_APPLICATION_REGULATOR_LED_APPROVED',
      }),
    ).toEqual('Approved by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'EMP_VARIATION_CORSIA_APPLICATION_REGULATOR_LED_APPROVED',
      }),
    ).toEqual('Approved by John Bolt');
  });

  it('should return EMP Corsia variation operator led', () => {
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'EMP_VARIATION_CORSIA_PEER_REVIEW_REQUESTED',
      }),
    ).toEqual('Peer review requested by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'EMP_VARIATION_CORSIA_APPLICATION_PEER_REVIEWER_ACCEPTED',
      }),
    ).toEqual('Peer review agreement submitted by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'EMP_VARIATION_CORSIA_APPLICATION_PEER_REVIEWER_REJECTED',
      }),
    ).toEqual('Peer review disagreement submitted by John Bolt');
  });

  it('should return EMP application CORSIA', () => {
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'EMP_ISSUANCE_CORSIA_APPLICATION_SUBMITTED',
      }),
    ).toEqual('Submitted by John Bolt');
  });

  it('should return Changes', () => {
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'EMP_ISSUANCE_CORSIA_APPLICATION_AMENDS_SUBMITTED',
      }),
    ).toEqual('Changes submitted by John Bolt');
  });

  it('should return recalled from Amends', () => {
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'EMP_ISSUANCE_CORSIA_RECALLED_FROM_AMENDS',
      }),
    ).toEqual('Recalled by John Bolt');
  });

  it('should return Non compliance', () => {
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'NON_COMPLIANCE_APPLICATION_SUBMITTED',
      }),
    ).toEqual('Non-compliance details provided by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'NON_COMPLIANCE_APPLICATION_CLOSED',
      }),
    ).toEqual('Non Compliance closed by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'NON_COMPLIANCE_DAILY_PENALTY_NOTICE_APPLICATION_SUBMITTED',
      }),
    ).toEqual('Initial penalty sent to operator by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'NON_COMPLIANCE_DAILY_PENALTY_NOTICE_PEER_REVIEW_REQUESTED',
      }),
    ).toEqual('Peer review of initial penalty requested by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'NON_COMPLIANCE_DAILY_PENALTY_NOTICE_PEER_REVIEWER_ACCEPTED',
      }),
    ).toEqual('Peer review agreement for initial penalty submitted by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'NON_COMPLIANCE_DAILY_PENALTY_NOTICE_PEER_REVIEWER_REJECTED',
      }),
    ).toEqual('Peer review disagreement for initial penalty submitted by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'NON_COMPLIANCE_NOTICE_OF_INTENT_APPLICATION_SUBMITTED',
      }),
    ).toEqual('Notice of intent sent to operator by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'NON_COMPLIANCE_NOTICE_OF_INTENT_PEER_REVIEW_REQUESTED',
      }),
    ).toEqual('Peer review of notice of intent requested by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'NON_COMPLIANCE_NOTICE_OF_INTENT_PEER_REVIEWER_ACCEPTED',
      }),
    ).toEqual('Peer review agreement for notice of intent submitted by John Bolt');

    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'NON_COMPLIANCE_NOTICE_OF_INTENT_PEER_REVIEWER_REJECTED',
      }),
    ).toEqual('Peer review disagreement for notice of intent submitted by John Bolt');
  });

  it('should return Aviation Account Closure Cancelled', () => {
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'AVIATION_ACCOUNT_CLOSURE_CANCELLED',
      }),
    ).toEqual('Account closure cancelled by John Bolt');
  });

  it('should return Aviation Account Closure Submitted', () => {
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'AVIATION_ACCOUNT_CLOSURE_SUBMITTED',
      }),
    ).toEqual('Account closed by John Bolt');
  });

  it('should return Submitted to verifier', () => {
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'AVIATION_AER_UKETS_APPLICATION_SENT_TO_VERIFIER',
      }),
    ).toEqual('Submitted to verifier by John Bolt');
  });

  it('should return Submitted to verifier', () => {
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'AVIATION_AER_CORSIA_APPLICATION_SENT_TO_VERIFIER',
      }),
    ).toEqual('Submitted to verifier by John Bolt');
  });

  it('should return Verification statement submitted', () => {
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'AVIATION_AER_UKETS_APPLICATION_VERIFICATION_SUBMITTED',
      }),
    ).toEqual('Verification statement submitted by John Bolt');
  });

  it('should return amends Submitted to verifier', () => {
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'AVIATION_AER_UKETS_APPLICATION_AMENDS_SENT_TO_VERIFIER',
      }),
    ).toEqual('Changes submitted by John Bolt');
  });

  it('should return verification submitted', () => {
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'AVIATION_AER_UKETS_APPLICATION_VERIFICATION_SUBMITTED',
      }),
    ).toEqual('Verification statement submitted by John Bolt');
  });

  it('should return emissions report reviewed', () => {
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'AVIATION_AER_UKETS_APPLICATION_COMPLETED',
      }),
    ).toEqual('Emissions report reviewed by John Bolt');
  });

  it('should return corsia emissions report reviewed', () => {
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'AVIATION_AER_CORSIA_APPLICATION_COMPLETED',
      }),
    ).toEqual('Emissions report reviewed by John Bolt');
  });

  it('should return Recalled from verifier', () => {
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'AVIATION_AER_RECALLED_FROM_VERIFICATION',
      }),
    ).toEqual('Recalled from verifier by John Bolt');
  });

  it('should return aer returned for amends', () => {
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'AVIATION_AER_UKETS_APPLICATION_RETURNED_FOR_AMENDS',
      }),
    ).toEqual('Returned to operator for changes by John Bolt');
  });

  it('should return Submitted to regulator', () => {
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'AVIATION_AER_UKETS_APPLICATION_SUBMITTED',
      }),
    ).toEqual('Submitted to regulator by John Bolt');
  });

  it('should return corsia Submitted to regulator', () => {
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'AVIATION_AER_CORSIA_APPLICATION_SUBMITTED',
      }),
    ).toEqual('Submitted to regulator by John Bolt');
  });

  it('should return amends Submitted to regulator', () => {
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'AVIATION_AER_UKETS_APPLICATION_AMENDS_SUBMITTED',
      }),
    ).toEqual('Changes submitted by John Bolt');
  });

  it('should return Reportable emissions cancelled', () => {
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'AVIATION_DRE_APPLICATION_CANCELLED',
      }),
    ).toEqual('Reportable emissions cancelled by John Bolt');
  });

  it('should return Peer review requested', () => {
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'AVIATION_DRE_UKETS_PEER_REVIEW_REQUESTED',
      }),
    ).toEqual('Peer review requested by John Bolt');
  });

  it('should return Peer review agreement submitted', () => {
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'AVIATION_DRE_UKETS_PEER_REVIEWER_ACCEPTED',
      }),
    ).toEqual('Peer review agreement submitted by John Bolt');
  });

  it('should return Peer review disagreement submitted', () => {
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'AVIATION_DRE_UKETS_PEER_REVIEWER_REJECTED',
      }),
    ).toEqual('Peer review disagreement submitted by John Bolt');
  });

  it('should return Aviation VIR submitted', () => {
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'AVIATION_VIR_APPLICATION_SUBMITTED',
      }),
    ).toEqual('Verifier improvement report submitted by John Bolt');
  });

  it('should return Aviation VIR reviewed', () => {
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'AVIATION_VIR_APPLICATION_REVIEWED',
      }),
    ).toEqual('Verifier improvement report decision submitted by John Bolt');
  });

  it('should return Aviation VIR responded', () => {
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'AVIATION_VIR_APPLICATION_RESPONDED_TO_REGULATOR_COMMENTS',
      }),
    ).toEqual('Follow up response submitted by John Bolt');
  });

  it('should return Conclusion of non compliance', () => {
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'NON_COMPLIANCE_FINAL_DETERMINATION_APPLICATION_SUBMITTED',
      }),
    ).toEqual('Conclusion provided by John Bolt');
  });

  it('should return Return of allowances', () => {
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'RETURN_OF_ALLOWANCES_APPLICATION_SUBMITTED',
      }),
    ).toEqual('Return of allowances submitted by John Bolt');
  });
  it('should return Returned allowances', () => {
    expect(
      pipe.transform({
        ...baseRequestAction,
        type: 'RETURN_OF_ALLOWANCES_RETURNED_APPLICATION_COMPLETED',
      }),
    ).toEqual('Returned allowances submitted by John Bolt');
  });
});
