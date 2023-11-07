import { RequestActionInfoDTO } from 'pmrv-api';

import { TimelineItemLinkPipe } from './timeline-item-link.pipe';

describe('TimelineItemLinkPipe', () => {
  let pipe: TimelineItemLinkPipe;

  const requestAction: RequestActionInfoDTO = {
    id: 1,
    submitter: 'John Bolt',
    creationDate: '2021-03-29T12:26:36.000Z',
  };

  beforeAll(() => (pipe = new TimelineItemLinkPipe()));

  it('should create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should return link with relative path reference to previous directory', () => {
    requestAction.type = 'PERMIT_ISSUANCE_APPLICATION_SUBMITTED';
    expect(pipe.transform(requestAction)).toEqual(['/permit-issuance', 'action', requestAction.id]);
  });

  it('should return link with relative path reference two directories up', () => {
    requestAction.type = 'PERMIT_ISSUANCE_APPLICATION_SUBMITTED';
    expect(pipe.transform(requestAction)).toEqual(['/permit-issuance', 'action', requestAction.id]);
  });

  it('should return link for amends', () => {
    requestAction.type = 'PERMIT_ISSUANCE_APPLICATION_RETURNED_FOR_AMENDS';
    expect(pipe.transform(requestAction)).toEqual([
      '/permit-issuance',
      'action',
      requestAction.id,
      'review',
      'return-for-amends',
    ]);
  });

  it('should return link for peer review submission', () => {
    requestAction.type = 'PERMIT_ISSUANCE_APPLICATION_PEER_REVIEWER_ACCEPTED';
    expect(pipe.transform(requestAction)).toEqual([
      '/permit-issuance',
      'action',
      requestAction.id,
      'review',
      'peer-reviewer-submitted',
    ]);

    requestAction.type = 'PERMIT_ISSUANCE_APPLICATION_PEER_REVIEWER_REJECTED';
    expect(pipe.transform(requestAction)).toEqual([
      '/permit-issuance',
      'action',
      requestAction.id,
      'review',
      'peer-reviewer-submitted',
    ]);
  });

  it('should return empty link', () => {
    const noLinkActionTypes: RequestActionInfoDTO['type'][] = [
      'PERMIT_ISSUANCE_APPLICATION_AMENDS_SUBMITTED',
      'PERMIT_ISSUANCE_PEER_REVIEW_REQUESTED',
      'PERMIT_ISSUANCE_RECALLED_FROM_AMENDS',

      'PERMIT_SURRENDER_APPLICATION_CANCELLED',
      'PERMIT_SURRENDER_PEER_REVIEW_REQUESTED',

      'PERMIT_REVOCATION_APPLICATION_CANCELLED',

      'PERMIT_NOTIFICATION_APPLICATION_CANCELLED',
      'PERMIT_NOTIFICATION_FOLLOW_UP_DATE_EXTENDED',
      'PERMIT_NOTIFICATION_FOLLOW_UP_RECALLED_FROM_AMENDS',
      'PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_AMENDS_SUBMITTED',

      'RDE_ACCEPTED',
      'RDE_CANCELLED',
      'RDE_EXPIRED',

      'RFI_CANCELLED',
      'RFI_EXPIRED',

      'REQUEST_TERMINATED',

      'AER_RECALLED_FROM_VERIFICATION',

      'VERIFICATION_STATEMENT_CANCELLED',

      'PERMIT_TRANSFER_APPLICATION_CANCELLED',
      'PERMIT_TRANSFER_B_RECALLED_FROM_AMENDS',
      'PERMIT_TRANSFER_B_APPLICATION_AMENDS_SUBMITTED',
      'PERMIT_TRANSFER_B_PEER_REVIEW_REQUESTED',

      'DRE_APPLICATION_PEER_REVIEW_REQUESTED',

      'DOAL_APPLICATION_CANCELLED',
      'DOAL_APPLICATION_PEER_REVIEW_REQUESTED',

      'EMP_ISSUANCE_UKETS_PEER_REVIEW_REQUESTED',
      'EMP_ISSUANCE_CORSIA_PEER_REVIEW_REQUESTED',
      'EMP_ISSUANCE_UKETS_RECALLED_FROM_AMENDS',
      'EMP_ISSUANCE_CORSIA_RECALLED_FROM_AMENDS',
      'AVIATION_DRE_APPLICATION_CANCELLED',
      'AVIATION_AER_UKETS_APPLICATION_CANCELLED_DUE_TO_DRE',
      'AVIATION_DRE_UKETS_PEER_REVIEW_REQUESTED',
      'EMP_VARIATION_APPLICATION_CANCELLED',
      'EMP_VARIATION_UKETS_PEER_REVIEW_REQUESTED',
      'EMP_VARIATION_CORSIA_PEER_REVIEW_REQUESTED',
      'EMP_VARIATION_UKETS_RECALLED_FROM_AMENDS',
      'EMP_VARIATION_CORSIA_RECALLED_FROM_AMENDS',
    ];

    noLinkActionTypes.forEach((type) => {
      requestAction.type = type;
      expect(pipe.transform(requestAction)).toBeNull();
    });
  });

  it('should return link for permit issuance decision', () => {
    requestAction.type = 'PERMIT_ISSUANCE_APPLICATION_GRANTED';
    expect(pipe.transform(requestAction)).toEqual([
      '/permit-issuance',
      'action',
      requestAction.id,
      'review',
      'decision-summary',
    ]);

    requestAction.type = 'PERMIT_ISSUANCE_APPLICATION_DEEMED_WITHDRAWN';
    expect(pipe.transform(requestAction)).toEqual([
      '/permit-issuance',
      'action',
      requestAction.id,
      'review',
      'decision-summary',
    ]);

    requestAction.type = 'PERMIT_ISSUANCE_APPLICATION_REJECTED';
    expect(pipe.transform(requestAction)).toEqual([
      '/permit-issuance',
      'action',
      requestAction.id,
      'review',
      'decision-summary',
    ]);
  });

  it('should return link for installation account', () => {
    requestAction.type = 'INSTALLATION_ACCOUNT_OPENING_ACCEPTED';
    expect(pipe.transform(requestAction)).toEqual(['/installation-account', 'submitted-decision', requestAction.id]);

    requestAction.type = 'INSTALLATION_ACCOUNT_OPENING_REJECTED';
    expect(pipe.transform(requestAction)).toEqual(['/installation-account', 'submitted-decision', requestAction.id]);

    requestAction.type = 'INSTALLATION_ACCOUNT_OPENING_ACCOUNT_APPROVED';
    expect(pipe.transform(requestAction)).toEqual(['/installation-account', 'summary', requestAction.id]);

    requestAction.type = 'INSTALLATION_ACCOUNT_OPENING_APPLICATION_SUBMITTED';
    expect(pipe.transform(requestAction)).toEqual(['/installation-account', 'summary', requestAction.id]);

    requestAction.type = 'PERMIT_SURRENDER_CESSATION_COMPLETED';
    expect(pipe.transform(requestAction)).toEqual([
      '/permit-surrender',
      'action',
      requestAction.id,
      'cessation',
      'completed',
    ]);
  });

  it('should return link for payment', () => {
    requestAction.type = 'PAYMENT_MARKED_AS_PAID';
    expect(pipe.transform(requestAction)).toEqual(['/payment', 'actions', requestAction.id, 'paid']);

    requestAction.type = 'PAYMENT_CANCELLED';
    expect(pipe.transform(requestAction)).toEqual(['/payment', 'actions', requestAction.id, 'cancelled']);

    requestAction.type = 'PAYMENT_MARKED_AS_RECEIVED';
    expect(pipe.transform(requestAction)).toEqual(['/payment', 'actions', requestAction.id, 'received']);

    requestAction.type = 'PAYMENT_COMPLETED';
    expect(pipe.transform(requestAction)).toEqual(['/payment', 'actions', requestAction.id, 'completed']);
  });

  it('should return link for notification', () => {
    requestAction.type = 'PERMIT_NOTIFICATION_FOLLOW_UP_RETURNED_FOR_AMENDS';
    expect(pipe.transform(requestAction)).toEqual([
      '/actions',
      requestAction.id,
      'permit-notification',
      'follow-up-return-for-amends',
    ]);
  });

  it('should return link for variation submitted', () => {
    requestAction.type = 'PERMIT_VARIATION_APPLICATION_SUBMITTED';
    expect(pipe.transform(requestAction)).toEqual(['/permit-variation', 'action', requestAction.id]);
  });

  it('should return link for an accepted  by a peer review variation  ', () => {
    requestAction.type = 'PERMIT_VARIATION_APPLICATION_PEER_REVIEWER_ACCEPTED';
    expect(pipe.transform(requestAction)).toEqual([
      '/permit-variation',
      'action',
      requestAction.id,
      'review',
      'peer-reviewer-submitted',
    ]);
  });
  it('should return link for a rejected by a peer review variation  ', () => {
    requestAction.type = 'PERMIT_VARIATION_APPLICATION_PEER_REVIEWER_REJECTED';
    expect(pipe.transform(requestAction)).toEqual([
      '/permit-variation',
      'action',
      requestAction.id,
      'review',
      'peer-reviewer-submitted',
    ]);
  });
  it('should return link for  a  variation peer review  request', () => {
    requestAction.type = 'PERMIT_VARIATION_PEER_REVIEW_REQUESTED';
    expect(pipe.transform(requestAction)).toEqual(null);
  });
  it('should return link for variation regulator led approved', () => {
    requestAction.type = 'PERMIT_VARIATION_APPLICATION_REGULATOR_LED_APPROVED';
    expect(pipe.transform(requestAction)).toEqual([
      '/permit-variation',
      'action',
      requestAction.id,
      'review',
      'decision-summary',
    ]);
  });

  it('should return link for permit transfer a', () => {
    requestAction.type = 'PERMIT_TRANSFER_A_APPLICATION_SUBMITTED';
    expect(pipe.transform(requestAction)).toEqual(['/actions', requestAction.id, 'permit-transfer-a', 'submitted']);
  });

  it('should return link for permit transfer b', () => {
    requestAction.type = 'PERMIT_TRANSFER_B_APPLICATION_SUBMITTED';
    expect(pipe.transform(requestAction)).toEqual(['/permit-transfer', 'action', requestAction.id]);

    requestAction.type = 'PERMIT_TRANSFER_B_APPLICATION_RETURNED_FOR_AMENDS';
    expect(pipe.transform(requestAction)).toEqual([
      '/permit-transfer',
      'action',
      requestAction.id,
      'review',
      'return-for-amends',
    ]);

    requestAction.type = 'PERMIT_TRANSFER_B_APPLICATION_PEER_REVIEWER_ACCEPTED';
    expect(pipe.transform(requestAction)).toEqual([
      '/permit-transfer',
      'action',
      requestAction.id,
      'review',
      'peer-reviewer-submitted',
    ]);

    requestAction.type = 'PERMIT_TRANSFER_B_APPLICATION_PEER_REVIEWER_REJECTED';
    expect(pipe.transform(requestAction)).toEqual([
      '/permit-transfer',
      'action',
      requestAction.id,
      'review',
      'peer-reviewer-submitted',
    ]);

    requestAction.type = 'PERMIT_TRANSFER_A_APPLICATION_GRANTED';
    expect(pipe.transform(requestAction)).toEqual([
      '/permit-transfer',
      'action',
      requestAction.id,
      'review',
      'decision-summary',
    ]);

    requestAction.type = 'PERMIT_TRANSFER_A_APPLICATION_DEEMED_WITHDRAWN';
    expect(pipe.transform(requestAction)).toEqual([
      '/permit-transfer',
      'action',
      requestAction.id,
      'review',
      'decision-summary',
    ]);

    requestAction.type = 'PERMIT_TRANSFER_A_APPLICATION_REJECTED';
    expect(pipe.transform(requestAction)).toEqual([
      '/permit-transfer',
      'action',
      requestAction.id,
      'review',
      'decision-summary',
    ]);

    requestAction.type = 'PERMIT_TRANSFER_B_APPLICATION_GRANTED';
    expect(pipe.transform(requestAction)).toEqual([
      '/permit-transfer',
      'action',
      requestAction.id,
      'review',
      'decision-summary',
    ]);

    requestAction.type = 'PERMIT_TRANSFER_B_APPLICATION_DEEMED_WITHDRAWN';
    expect(pipe.transform(requestAction)).toEqual([
      '/permit-transfer',
      'action',
      requestAction.id,
      'review',
      'decision-summary',
    ]);

    requestAction.type = 'PERMIT_TRANSFER_B_APPLICATION_REJECTED';
    expect(pipe.transform(requestAction)).toEqual([
      '/permit-transfer',
      'action',
      requestAction.id,
      'review',
      'decision-summary',
    ]);
  });

  it('should return link for aer', () => {
    requestAction.type = 'AER_RECALLED_FROM_VERIFICATION';
    expect(pipe.transform(requestAction)).toEqual(null);

    requestAction.type = 'AER_APPLICATION_RE_INITIATED';
    expect(pipe.transform(requestAction)).toEqual(null);

    requestAction.type = 'AER_APPLICATION_VERIFICATION_SUBMITTED';
    expect(pipe.transform(requestAction)).toEqual(['/actions', requestAction.id, 'aer', 'submitted']);

    requestAction.type = 'AER_APPLICATION_SUBMITTED';
    expect(pipe.transform(requestAction)).toEqual(['/actions', requestAction.id, 'aer', 'submitted']);

    requestAction.type = 'AER_APPLICATION_SENT_TO_VERIFIER';
    expect(pipe.transform(requestAction)).toEqual(['/actions', requestAction.id, 'aer', 'submitted']);

    requestAction.type = 'AER_APPLICATION_COMPLETED';
    expect(pipe.transform(requestAction)).toEqual(['/actions', requestAction.id, 'aer', 'completed']);

    requestAction.type = 'AER_APPLICATION_AMENDS_SENT_TO_VERIFIER';
    expect(pipe.transform(requestAction)).toEqual(['/actions', requestAction.id, 'aer', 'submitted']);
  });

  it('should return link for vir', () => {
    requestAction.type = 'VIR_APPLICATION_SUBMITTED';
    expect(pipe.transform(requestAction)).toEqual(['/actions', requestAction.id, 'vir', 'submitted']);

    requestAction.type = 'VIR_APPLICATION_REVIEWED';
    expect(pipe.transform(requestAction)).toEqual(['/actions', requestAction.id, 'vir', 'reviewed']);

    requestAction.type = 'VIR_APPLICATION_RESPONDED_TO_REGULATOR_COMMENTS';
    expect(pipe.transform(requestAction)).toEqual(['/actions', requestAction.id, 'vir', 'responded']);
  });

  it('should return link for air', () => {
    requestAction.type = 'AIR_APPLICATION_SUBMITTED';
    expect(pipe.transform(requestAction)).toEqual(['/actions', requestAction.id, 'air', 'submitted']);

    requestAction.type = 'AIR_APPLICATION_REVIEWED';
    expect(pipe.transform(requestAction)).toEqual(['/actions', requestAction.id, 'air', 'reviewed']);

    requestAction.type = 'AIR_APPLICATION_RESPONDED_TO_REGULATOR_COMMENTS';
    expect(pipe.transform(requestAction)).toEqual(['/actions', requestAction.id, 'air', 'responded']);
  });

  it('should return link for dre', () => {
    requestAction.type = 'DRE_APPLICATION_SUBMITTED';
    expect(pipe.transform(requestAction)).toEqual(['/actions', requestAction.id, 'dre', 'submitted']);

    requestAction.type = 'DRE_APPLICATION_PEER_REVIEWER_ACCEPTED';
    expect(pipe.transform(requestAction)).toEqual(['/actions', requestAction.id, 'dre', 'peer-review-decision']);

    requestAction.type = 'DRE_APPLICATION_PEER_REVIEWER_REJECTED';
    expect(pipe.transform(requestAction)).toEqual(['/actions', requestAction.id, 'dre', 'peer-review-decision']);
  });

  it('should return link for doal', () => {
    requestAction.type = 'DOAL_APPLICATION_PROCEEDED_TO_AUTHORITY';
    expect(pipe.transform(requestAction)).toEqual(['/actions', requestAction.id, 'doal', 'proceeded']);

    requestAction.type = 'DOAL_APPLICATION_CLOSED';
    expect(pipe.transform(requestAction)).toEqual(['/actions', requestAction.id, 'doal', 'closed']);

    requestAction.type = 'DOAL_APPLICATION_PEER_REVIEWER_ACCEPTED';
    expect(pipe.transform(requestAction)).toEqual(['/actions', requestAction.id, 'doal', 'peer-review-decision']);
    requestAction.type = 'DOAL_APPLICATION_PEER_REVIEWER_REJECTED';
    expect(pipe.transform(requestAction)).toEqual(['/actions', requestAction.id, 'doal', 'peer-review-decision']);

    requestAction.type = 'DOAL_APPLICATION_ACCEPTED';
    expect(pipe.transform(requestAction)).toEqual(['/actions', requestAction.id, 'doal', 'completed']);
    requestAction.type = 'DOAL_APPLICATION_ACCEPTED_WITH_CORRECTIONS';
    expect(pipe.transform(requestAction)).toEqual(['/actions', requestAction.id, 'doal', 'completed']);
    requestAction.type = 'DOAL_APPLICATION_REJECTED';
    expect(pipe.transform(requestAction)).toEqual(['/actions', requestAction.id, 'doal', 'completed']);
  });

  it('should return link for permit batch reissue submitted', () => {
    requestAction.type = 'BATCH_REISSUE_SUBMITTED';
    expect(pipe.transform(requestAction)).toEqual([
      '/actions',
      requestAction.id,
      'permit-batch-variation',
      'submitted',
    ]);
  });

  it('should return link for emp batch reissue submitted', () => {
    requestAction.type = 'BATCH_REISSUE_SUBMITTED';
    expect(pipe.transform(requestAction, false, true)).toEqual([
      '/actions',
      requestAction.id,
      'emp-batch-variation',
      'batch-reissue',
      'submitted',
    ]);
  });

  it('should return link for permit batch reissue completed', () => {
    requestAction.type = 'BATCH_REISSUE_COMPLETED';
    expect(pipe.transform(requestAction)).toEqual([
      '/actions',
      requestAction.id,
      'permit-batch-variation',
      'completed',
    ]);
  });

  it('should return link for emp batch reissue completed', () => {
    requestAction.type = 'BATCH_REISSUE_COMPLETED';
    expect(pipe.transform(requestAction, false, true)).toEqual([
      '/actions',
      requestAction.id,
      'emp-batch-variation',
      'batch-reissue',
      'completed',
    ]);
  });

  it('should return link for permit reissue completed', () => {
    requestAction.type = 'REISSUE_COMPLETED';
    expect(pipe.transform(requestAction)).toEqual(['/actions', requestAction.id, 'variation', 'completed']);
  });

  it('should return link for emp reissue completed', () => {
    requestAction.type = 'REISSUE_COMPLETED';
    expect(pipe.transform(requestAction, false, true)).toEqual([
      '/actions',
      requestAction.id,
      'emp-batch-variation',
      'reissue',
      'completed',
    ]);
  });

  it('should return link for EMP', () => {
    requestAction.type = 'EMP_ISSUANCE_UKETS_APPLICATION_SUBMITTED';
    expect(pipe.transform(requestAction)).toEqual(['/aviation', 'actions', requestAction.id]);
  });

  it('should return link for EMP approved by regulator', () => {
    requestAction.type = 'EMP_ISSUANCE_UKETS_APPLICATION_APPROVED';
    expect(pipe.transform(requestAction)).toEqual(['/aviation', 'actions', requestAction.id, 'decision-summary']);
  });

  it('should return link for EMP withdrawn by regulator', () => {
    requestAction.type = 'EMP_ISSUANCE_UKETS_APPLICATION_DEEMED_WITHDRAWN';
    expect(pipe.transform(requestAction)).toEqual(['/aviation', 'actions', requestAction.id, 'decision-summary']);
  });

  it('should return link for EMP returned to operator for changes', () => {
    requestAction.type = 'EMP_ISSUANCE_UKETS_APPLICATION_RETURNED_FOR_AMENDS';
    expect(pipe.transform(requestAction)).toEqual(['/aviation', 'actions', requestAction.id, 'return-for-amends']);
  });

  it('should return link for ammended EMP submitted', () => {
    requestAction.type = 'EMP_ISSUANCE_UKETS_APPLICATION_AMENDS_SUBMITTED';
    expect(pipe.transform(requestAction)).toEqual(['/aviation', 'actions', requestAction.id]);
  });

  it('should return link for ammended EMP CORSIA submitted', () => {
    requestAction.type = 'EMP_ISSUANCE_CORSIA_APPLICATION_AMENDS_SUBMITTED';
    expect(pipe.transform(requestAction)).toEqual(['/aviation', 'actions', requestAction.id]);
  });

  it('should return link for EMP CORSIA returned to operator for changes', () => {
    requestAction.type = 'EMP_ISSUANCE_CORSIA_APPLICATION_RETURNED_FOR_AMENDS';
    expect(pipe.transform(requestAction)).toEqual(['/aviation', 'actions', requestAction.id, 'return-for-amends']);
  });

  it('should return link for EMP CORSIA approved by regulator', () => {
    requestAction.type = 'EMP_ISSUANCE_CORSIA_APPLICATION_APPROVED';
    expect(pipe.transform(requestAction)).toEqual(['/aviation', 'actions', requestAction.id, 'decision-summary']);
  });

  it('should return link for EMP CORSIA withdrawn by regulator', () => {
    requestAction.type = 'EMP_ISSUANCE_CORSIA_APPLICATION_DEEMED_WITHDRAWN';
    expect(pipe.transform(requestAction)).toEqual(['/aviation', 'actions', requestAction.id, 'decision-summary']);
  });

  it('should return link for non compliance closed', () => {
    requestAction.type = 'NON_COMPLIANCE_APPLICATION_CLOSED';
    expect(pipe.transform(requestAction)).toEqual(['/actions', requestAction.id, 'non-compliance', 'closed']);
  });

  it('should return link for non compliance', () => {
    requestAction.type = 'NON_COMPLIANCE_APPLICATION_SUBMITTED';
    expect(pipe.transform(requestAction)).toEqual(['/actions', requestAction.id, 'non-compliance', 'submitted']);
  });

  it('should return link for Aviation Account Closure Cancelled', () => {
    requestAction.type = 'AVIATION_ACCOUNT_CLOSURE_CANCELLED';
    expect(pipe.transform(requestAction)).toEqual(null);
  });

  it('should return link for Aviation Account Closure Submitted', () => {
    requestAction.type = 'AVIATION_ACCOUNT_CLOSURE_SUBMITTED';
    expect(pipe.transform(requestAction)).toEqual(['/actions', requestAction.id, 'account-closure-submitted']);
  });

  it('should return link for EMP Variation UK ETS', () => {
    requestAction.type = 'EMP_VARIATION_UKETS_APPLICATION_SUBMITTED';
    expect(pipe.transform(requestAction)).toEqual(['/aviation', 'actions', requestAction.id]);

    requestAction.type = 'EMP_VARIATION_UKETS_APPLICATION_REGULATOR_LED_APPROVED';
    expect(pipe.transform(requestAction)).toEqual(['/aviation', 'actions', requestAction.id, 'decision-summary']);

    requestAction.type = 'EMP_VARIATION_CORSIA_APPLICATION_REGULATOR_LED_APPROVED';
    expect(pipe.transform(requestAction)).toEqual(['/aviation', 'actions', requestAction.id, 'decision-summary']);

    requestAction.type = 'EMP_VARIATION_UKETS_APPLICATION_APPROVED';
    expect(pipe.transform(requestAction)).toEqual(['/aviation', 'actions', requestAction.id, 'decision-summary']);

    requestAction.type = 'EMP_VARIATION_UKETS_APPLICATION_DEEMED_WITHDRAWN';
    expect(pipe.transform(requestAction)).toEqual(['/aviation', 'actions', requestAction.id, 'decision-summary']);

    requestAction.type = 'EMP_VARIATION_UKETS_APPLICATION_REJECTED';
    expect(pipe.transform(requestAction)).toEqual(['/aviation', 'actions', requestAction.id, 'decision-summary']);

    requestAction.type = 'EMP_VARIATION_CORSIA_APPLICATION_APPROVED';
    expect(pipe.transform(requestAction)).toEqual(['/aviation', 'actions', requestAction.id, 'decision-summary']);

    requestAction.type = 'EMP_VARIATION_CORSIA_APPLICATION_DEEMED_WITHDRAWN';
    expect(pipe.transform(requestAction)).toEqual(['/aviation', 'actions', requestAction.id, 'decision-summary']);

    requestAction.type = 'EMP_VARIATION_CORSIA_APPLICATION_REJECTED';
    expect(pipe.transform(requestAction)).toEqual(['/aviation', 'actions', requestAction.id, 'decision-summary']);

    requestAction.type = 'EMP_VARIATION_UKETS_APPLICATION_PEER_REVIEWER_ACCEPTED';
    expect(pipe.transform(requestAction)).toEqual([
      '/aviation',
      'actions',
      requestAction.id,
      'peer-reviewer-submitted',
    ]);

    requestAction.type = 'EMP_VARIATION_UKETS_APPLICATION_PEER_REVIEWER_REJECTED';
    expect(pipe.transform(requestAction)).toEqual([
      '/aviation',
      'actions',
      requestAction.id,
      'peer-reviewer-submitted',
    ]);

    requestAction.type = 'EMP_VARIATION_CORSIA_APPLICATION_PEER_REVIEWER_ACCEPTED';
    expect(pipe.transform(requestAction)).toEqual([
      '/aviation',
      'actions',
      requestAction.id,
      'peer-reviewer-submitted',
    ]);

    requestAction.type = 'EMP_VARIATION_CORSIA_APPLICATION_PEER_REVIEWER_REJECTED';
    expect(pipe.transform(requestAction)).toEqual([
      '/aviation',
      'actions',
      requestAction.id,
      'peer-reviewer-submitted',
    ]);

    requestAction.type = 'EMP_VARIATION_UKETS_APPLICATION_RETURNED_FOR_AMENDS';
    expect(pipe.transform(requestAction)).toEqual(['/aviation', 'actions', requestAction.id, 'return-for-amends']);

    requestAction.type = 'EMP_VARIATION_CORSIA_APPLICATION_RETURNED_FOR_AMENDS';
    expect(pipe.transform(requestAction)).toEqual(['/aviation', 'actions', requestAction.id, 'return-for-amends']);

    requestAction.type = 'EMP_ISSUANCE_UKETS_APPLICATION_AMENDS_SUBMITTED';
    expect(pipe.transform(requestAction)).toEqual(['/aviation', 'actions', requestAction.id]);
  });

  it('should return link for EMP Issuance CORSIA', () => {
    requestAction.type = 'EMP_ISSUANCE_CORSIA_APPLICATION_SUBMITTED';
    expect(pipe.transform(requestAction)).toEqual(['/aviation', 'actions', requestAction.id]);
  });

  it('should return link for EMP Variation CORSIA', () => {
    requestAction.type = 'EMP_VARIATION_CORSIA_APPLICATION_SUBMITTED';
    expect(pipe.transform(requestAction)).toEqual(['/aviation', 'actions', requestAction.id]);
  });

  it('should return link for EMP CORSIA peer review accepted', () => {
    requestAction.type = 'EMP_ISSUANCE_CORSIA_PEER_REVIEW_REQUESTED';
    expect(pipe.transform(requestAction)).toEqual(null);
  });

  it('should return link for EMP CORSIA peer review accepted', () => {
    requestAction.type = 'EMP_ISSUANCE_CORSIA_APPLICATION_PEER_REVIEWER_ACCEPTED';
    expect(pipe.transform(requestAction)).toEqual([
      '/aviation',
      'actions',
      requestAction.id,
      'peer-reviewer-submitted',
    ]);
  });

  it('should return link for EMP CORSIA peer review accepted', () => {
    requestAction.type = 'EMP_ISSUANCE_CORSIA_APPLICATION_PEER_REVIEWER_REJECTED';
    expect(pipe.transform(requestAction)).toEqual([
      '/aviation',
      'actions',
      requestAction.id,
      'peer-reviewer-submitted',
    ]);
  });

  it('should return link for daily penalty notice submitted', () => {
    requestAction.type = 'NON_COMPLIANCE_DAILY_PENALTY_NOTICE_APPLICATION_SUBMITTED';
    expect(pipe.transform(requestAction)).toEqual([
      '/actions',
      requestAction.id,
      'non-compliance',
      'daily-penalty-notice-submitted',
    ]);
  });

  it('should return link for daily penalty notice peer review accepted', () => {
    requestAction.type = 'NON_COMPLIANCE_DAILY_PENALTY_NOTICE_PEER_REVIEWER_ACCEPTED';
    expect(pipe.transform(requestAction)).toEqual([
      '/actions',
      requestAction.id,
      'non-compliance',
      'dpn-peer-review-decision',
    ]);
  });

  it('should return link for daily penalty notice peer review rejected', () => {
    requestAction.type = 'NON_COMPLIANCE_DAILY_PENALTY_NOTICE_PEER_REVIEWER_REJECTED';
    expect(pipe.transform(requestAction)).toEqual([
      '/actions',
      requestAction.id,
      'non-compliance',
      'dpn-peer-review-decision',
    ]);
  });

  it('should return link for rfi', () => {
    requestAction.type = 'RFI_SUBMITTED';
    expect(pipe.transform(requestAction, false, true)).toEqual([
      '/aviation/rfi',
      'action',
      requestAction.id,
      'rfi-submitted',
    ]);

    requestAction.type = 'RFI_SUBMITTED';
    expect(pipe.transform(requestAction, false, false)).toEqual(['/rfi', 'action', requestAction.id, 'rfi-submitted']);

    requestAction.type = 'RFI_RESPONSE_SUBMITTED';
    expect(pipe.transform(requestAction, false, true)).toEqual([
      '/aviation/rfi',
      'action',
      requestAction.id,
      'rfi-response-submitted',
    ]);

    requestAction.type = 'RFI_RESPONSE_SUBMITTED';
    expect(pipe.transform(requestAction, false, false)).toEqual([
      '/rfi',
      'action',
      requestAction.id,
      'rfi-response-submitted',
    ]);
  });

  it('should return link for rde', () => {
    requestAction.type = 'RDE_SUBMITTED';
    expect(pipe.transform(requestAction, false, true)).toEqual([
      '/aviation/rde',
      'action',
      requestAction.id,
      'rde-submitted',
    ]);

    requestAction.type = 'RDE_SUBMITTED';
    expect(pipe.transform(requestAction, false, false)).toEqual(['/rde', 'action', requestAction.id, 'rde-submitted']);

    requestAction.type = 'RDE_REJECTED';
    expect(pipe.transform(requestAction, false, true)).toEqual([
      '/aviation/rde',
      'action',
      requestAction.id,
      'rde-response-submitted',
    ]);

    requestAction.type = 'RDE_REJECTED';
    expect(pipe.transform(requestAction, false, false)).toEqual([
      '/rde',
      'action',
      requestAction.id,
      'rde-response-submitted',
    ]);

    requestAction.type = 'RDE_FORCE_REJECTED';
    expect(pipe.transform(requestAction, false, true)).toEqual([
      '/aviation/rde',
      'action',
      requestAction.id,
      'rde-manual-approval-submitted',
    ]);

    requestAction.type = 'RDE_FORCE_REJECTED';
    expect(pipe.transform(requestAction, false, false)).toEqual([
      '/rde',
      'action',
      requestAction.id,
      'rde-manual-approval-submitted',
    ]);
  });
  it('should not return link for daily penalty notice peer review request', () => {
    requestAction.type = 'NON_COMPLIANCE_DAILY_PENALTY_NOTICE_PEER_REVIEW_REQUESTED';
    expect(pipe.transform(requestAction)).toEqual(null);
  });

  it('should return link for notice of intent submitted', () => {
    requestAction.type = 'NON_COMPLIANCE_NOTICE_OF_INTENT_APPLICATION_SUBMITTED';
    expect(pipe.transform(requestAction)).toEqual([
      '/actions',
      requestAction.id,
      'non-compliance',
      'notice-of-intent-submitted',
    ]);
  });

  it('should return link for notice of intent peer review accepted', () => {
    requestAction.type = 'NON_COMPLIANCE_NOTICE_OF_INTENT_PEER_REVIEWER_ACCEPTED';
    expect(pipe.transform(requestAction)).toEqual([
      '/actions',
      requestAction.id,
      'non-compliance',
      'noi-peer-review-decision',
    ]);
  });

  it('should return link for notice of intent peer review rejected', () => {
    requestAction.type = 'NON_COMPLIANCE_NOTICE_OF_INTENT_PEER_REVIEWER_REJECTED';
    expect(pipe.transform(requestAction)).toEqual([
      '/actions',
      requestAction.id,
      'non-compliance',
      'noi-peer-review-decision',
    ]);
  });

  it('should not return link for notice of intent peer review request', () => {
    requestAction.type = 'NON_COMPLIANCE_NOTICE_OF_INTENT_PEER_REVIEW_REQUESTED';
    expect(pipe.transform(requestAction)).toEqual(null);
  });

  it('should return link for civil penalty notice submitted', () => {
    requestAction.type = 'NON_COMPLIANCE_CIVIL_PENALTY_APPLICATION_SUBMITTED';
    expect(pipe.transform(requestAction)).toEqual([
      '/actions',
      requestAction.id,
      'non-compliance',
      'civil-penalty-notice-submitted',
    ]);
  });

  it('should return link for civil penalty notice peer review accepted', () => {
    requestAction.type = 'NON_COMPLIANCE_CIVIL_PENALTY_PEER_REVIEWER_ACCEPTED';
    expect(pipe.transform(requestAction)).toEqual([
      '/actions',
      requestAction.id,
      'non-compliance',
      'cpn-peer-review-decision',
    ]);
  });

  it('should return link for civil penalty notice peer review rejected', () => {
    requestAction.type = 'NON_COMPLIANCE_CIVIL_PENALTY_PEER_REVIEWER_REJECTED';
    expect(pipe.transform(requestAction)).toEqual([
      '/actions',
      requestAction.id,
      'non-compliance',
      'cpn-peer-review-decision',
    ]);
  });

  it('should not return link for civil penalty notice peer review request', () => {
    requestAction.type = 'NON_COMPLIANCE_CIVIL_PENALTY_PEER_REVIEW_REQUESTED';
    expect(pipe.transform(requestAction)).toEqual(null);
  });

  it('should return link for cunclusion of non compliance', () => {
    requestAction.type = 'NON_COMPLIANCE_FINAL_DETERMINATION_APPLICATION_SUBMITTED';
    expect(pipe.transform(requestAction)).toEqual([
      '/actions',
      requestAction.id,
      'non-compliance',
      'conclusion-submitted',
    ]);
  });

  it('should return link for aviation aer submitted', () => {
    requestAction.type = 'AVIATION_AER_UKETS_APPLICATION_SUBMITTED';
    expect(pipe.transform(requestAction)).toEqual(['/aviation', 'actions', requestAction.id]);
  });

  it('should return link for aviation aer corsia submitted', () => {
    requestAction.type = 'AVIATION_AER_CORSIA_APPLICATION_SUBMITTED';
    expect(pipe.transform(requestAction)).toEqual(['/aviation', 'actions', requestAction.id]);
  });

  it('should return link for aviation aer amends submitted', () => {
    requestAction.type = 'AVIATION_AER_UKETS_APPLICATION_AMENDS_SUBMITTED';
    expect(pipe.transform(requestAction)).toEqual(['/aviation', 'actions', requestAction.id]);
  });

  it('should return link for aviation aer verification submitted', () => {
    requestAction.type = 'AVIATION_AER_UKETS_APPLICATION_VERIFICATION_SUBMITTED';
    expect(pipe.transform(requestAction)).toEqual(['/aviation', 'actions', requestAction.id]);
  });

  it('should not return link for aviation aer recalled from verification', () => {
    requestAction.type = 'AVIATION_AER_RECALLED_FROM_VERIFICATION';
    expect(pipe.transform(requestAction)).toEqual(null);
  });
  it('should not return link for aviation aer cancelled due to exempt', () => {
    requestAction.type = 'AVIATION_AER_APPLICATION_CANCELLED_DUE_TO_EXEPMT';
    expect(pipe.transform(requestAction)).toEqual(null);
  });
  it('should not return link for aviation aer re iniatiated after exemption', () => {
    requestAction.type = 'AVIATION_AER_APPLICATION_RE_INITIATED';
    expect(pipe.transform(requestAction)).toEqual(null);
  });

  it('should return link for aviation aer sent to verifier', () => {
    requestAction.type = 'AVIATION_AER_UKETS_APPLICATION_SENT_TO_VERIFIER';
    expect(pipe.transform(requestAction)).toEqual(['/aviation', 'actions', requestAction.id]);
  });

  it('should return link for aviation aer amends sent to verifier', () => {
    requestAction.type = 'AVIATION_AER_UKETS_APPLICATION_AMENDS_SENT_TO_VERIFIER';
    expect(pipe.transform(requestAction)).toEqual(['/aviation', 'actions', requestAction.id]);
  });

  it('should return link for aviation aer returned for amends', () => {
    requestAction.type = 'AVIATION_AER_UKETS_APPLICATION_RETURNED_FOR_AMENDS';
    expect(pipe.transform(requestAction)).toEqual([
      '/aviation',
      'actions',
      requestAction.id,
      'aer',
      'return-for-amends',
    ]);
  });
  it('should return link for aviation aer completed', () => {
    requestAction.type = 'AVIATION_AER_UKETS_APPLICATION_COMPLETED';
    expect(pipe.transform(requestAction)).toEqual(['/aviation', 'actions', requestAction.id, 'aer', 'aer-completed']);
  });

  it('should return link for aviation aer corsia completed', () => {
    requestAction.type = 'AVIATION_AER_CORSIA_APPLICATION_COMPLETED';
    expect(pipe.transform(requestAction)).toEqual([
      '/aviation',
      'actions',
      requestAction.id,
      'aer-corsia',
      'aer-completed',
    ]);
  });

  it('should return link for aviation aer corsia sent to verifier', () => {
    requestAction.type = 'AVIATION_AER_CORSIA_APPLICATION_SENT_TO_VERIFIER';
    expect(pipe.transform(requestAction)).toEqual(['/aviation', 'actions', requestAction.id]);
  });

  it('should return link for aviation aer corsia verification statement submitted', () => {
    requestAction.type = 'AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMITTED';
    expect(pipe.transform(requestAction)).toEqual(['/aviation', 'actions', requestAction.id]);
  });

  it('should return link for return of alowances submitted', () => {
    requestAction.type = 'RETURN_OF_ALLOWANCES_APPLICATION_SUBMITTED';
    expect(pipe.transform(requestAction)).toEqual(['/actions', requestAction.id, 'return-of-allowances', 'submitted']);
  });
  it('should return link for return of alowances peer review accepted', () => {
    requestAction.type = 'RETURN_OF_ALLOWANCES_APPLICATION_PEER_REVIEWER_ACCEPTED';
    expect(pipe.transform(requestAction)).toEqual([
      '/actions',
      requestAction.id,
      'return-of-allowances',
      'roa-peer-review-decision',
    ]);
  });
  it('should return link for return of alowances peer review rejected', () => {
    requestAction.type = 'RETURN_OF_ALLOWANCES_APPLICATION_PEER_REVIEWER_REJECTED';
    expect(pipe.transform(requestAction)).toEqual([
      '/actions',
      requestAction.id,
      'return-of-allowances',
      'roa-peer-review-decision',
    ]);
  });

  it('should not return link for return of alowances peer review request', () => {
    requestAction.type = 'RETURN_OF_ALLOWANCES_PEER_REVIEW_REQUESTED';
    expect(pipe.transform(requestAction)).toEqual(null);
  });
  it('should not return link for return of alowances cancel', () => {
    requestAction.type = 'RETURN_OF_ALLOWANCES_APPLICATION_CANCELLED';
    expect(pipe.transform(requestAction)).toEqual(null);
  });

  it('should return link for returned alowances', () => {
    requestAction.type = 'RETURN_OF_ALLOWANCES_RETURNED_APPLICATION_COMPLETED';
    expect(pipe.transform(requestAction)).toEqual(['/actions', requestAction.id, 'return-of-allowances', 'returned']);
  });

  it('should return link for EMP peer review accepted', () => {
    requestAction.type = 'EMP_ISSUANCE_UKETS_APPLICATION_PEER_REVIEWER_ACCEPTED';
    expect(pipe.transform(requestAction)).toEqual([
      '/aviation',
      'actions',
      requestAction.id,
      'peer-reviewer-submitted',
    ]);
  });
  it('should return link for EMP peer review accepted', () => {
    requestAction.type = 'EMP_ISSUANCE_UKETS_APPLICATION_PEER_REVIEWER_REJECTED';
    expect(pipe.transform(requestAction)).toEqual([
      '/aviation',
      'actions',
      requestAction.id,
      'peer-reviewer-submitted',
    ]);
  });

  it('should return link for DRE peer review accepted', () => {
    requestAction.type = 'AVIATION_DRE_UKETS_PEER_REVIEWER_ACCEPTED';
    expect(pipe.transform(requestAction)).toEqual([
      '/aviation',
      'actions',
      requestAction.id,
      'peer-reviewer-submitted',
    ]);
  });

  it('should return link for DRE peer review accepted', () => {
    requestAction.type = 'AVIATION_DRE_UKETS_PEER_REVIEWER_REJECTED';
    expect(pipe.transform(requestAction)).toEqual([
      '/aviation',
      'actions',
      requestAction.id,
      'peer-reviewer-submitted',
    ]);
  });

  it('should return link for DRE submitted', () => {
    requestAction.type = 'AVIATION_DRE_UKETS_APPLICATION_SUBMITTED';
    expect(pipe.transform(requestAction)).toEqual([
      '/aviation',
      'actions',
      requestAction.id,
      'aviation-emissions-updated',
    ]);
  });

  it('should return link for Aviation VIR', () => {
    requestAction.type = 'AVIATION_VIR_APPLICATION_SUBMITTED';
    expect(pipe.transform(requestAction)).toEqual(['/aviation', 'actions', requestAction.id]);

    requestAction.type = 'AVIATION_VIR_APPLICATION_RESPONDED_TO_REGULATOR_COMMENTS';
    expect(pipe.transform(requestAction)).toEqual(['/aviation', 'actions', requestAction.id, 'vir', 'responded']);

    requestAction.type = 'AVIATION_VIR_APPLICATION_REVIEWED';
    expect(pipe.transform(requestAction)).toEqual([
      '/aviation',
      'actions',
      requestAction.id,
      'vir',
      'decision-summary',
    ]);
  });
});
