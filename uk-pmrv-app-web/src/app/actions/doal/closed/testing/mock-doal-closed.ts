import { DoalApplicationClosedRequestActionPayload } from 'pmrv-api';

import { CommonActionsState } from '../../../store/common-actions.state';

export const mockDoalApplicationClosedRequestActionPayload: DoalApplicationClosedRequestActionPayload = {
  payloadType: 'DOAL_APPLICATION_CLOSED_PAYLOAD',
  determination: {
    type: 'CLOSED',
    reason: 'Reason',
  },
};

export const mockState = {
  storeInitialized: true,
  action: {
    id: 102,
    type: 'DOAL_APPLICATION_CLOSED',
    payload: mockDoalApplicationClosedRequestActionPayload,
    requestId: 'DOAL00011-2021-1',
    requestType: 'DOAL',
    requestAccountId: 11,
    competentAuthority: 'ENGLAND',
    submitter: 'Regulator1 England',
    creationDate: '2023-04-05T16:14:29.258067Z',
  },
} as CommonActionsState;
