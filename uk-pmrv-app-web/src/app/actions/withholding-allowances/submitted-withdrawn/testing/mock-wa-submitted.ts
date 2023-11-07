import { WithholdingOfAllowancesApplicationSubmittedRequestActionPayload } from 'pmrv-api';

import { CommonActionsState } from '../../../store/common-actions.state';

export const mockPayload: WithholdingOfAllowancesApplicationSubmittedRequestActionPayload = {
  payloadType: 'WITHHOLDING_OF_ALLOWANCES_APPLICATION_SUBMITTED_PAYLOAD',
  withholdingOfAllowances: {
    year: 2023,
    reasonType: 'ASSESSING_TRANSFER_OF_AVIATION_FREE_ALLOCATION_UNDER_ARTICLE_34Q',
  },
  decisionNotification: {
    signatory: 'signatory',
  },
};

export const mockState = {
  storeInitialized: true,
  action: {
    id: 102,
    type: 'WITHHOLDING_OF_ALLOWANCES_APPLICATION_SUBMITTED',
    payload: mockPayload,
    requestId: 'requestId',
    requestType: 'WITHHOLDING_OF_ALLOWANCES',
    requestAccountId: 11,
    competentAuthority: 'ENGLAND',
    submitter: 'Operator1 England',
    creationDate: '2023-04-05T16:14:29.258067Z',
  },
} as CommonActionsState;
