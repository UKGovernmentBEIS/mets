import { BatchReissueCompletedRequestActionPayload, BatchReissueSubmittedRequestActionPayload } from 'pmrv-api';

import { CommonActionsState } from '../../store/common-actions.state';

export const mockSubmittedActionState: CommonActionsState = {
  action: {
    id: 1,
    type: 'BATCH_REISSUE_SUBMITTED',
    competentAuthority: 'ENGLAND',
    creationDate: '2023-06-08T16:35:40.18009Z',
    requestId: 'requestI',
    requestType: 'PERMIT_BATCH_REISSUE',
    submitter: 'submitter1',
    payload: {
      payloadType: 'PERMIT_BATCH_REISSUE_SUBMITTED_PAYLOAD',
      signatory: 'b12938d1-2eee-4e4e-928e-cff5d66f320b',
      signatoryName: 'signName',
      submitter: 'submitter1',
      filters: {
        accountStatuses: ['LIVE'],
        emitterTypes: ['GHGE'],
        installationCategories: ['A_LOW_EMITTER'],
      },
    } as BatchReissueSubmittedRequestActionPayload,
  },
  storeInitialized: true,
};

export const mockCompletedActionState: CommonActionsState = {
  action: {
    id: 1,
    type: 'BATCH_REISSUE_COMPLETED',
    competentAuthority: 'ENGLAND',
    creationDate: '2023-06-08T16:35:40.18009Z',
    requestId: 'requestI',
    requestType: 'PERMIT_BATCH_REISSUE',
    submitter: 'submitter1',
    payload: {
      payloadType: 'PERMIT_BATCH_REISSUE_COMPLETED_PAYLOAD',
      signatory: 'b12938d1-2eee-4e4e-928e-cff5d66f320b',
      signatoryName: 'signName',
      submitter: 'submitter1',
      filters: {
        accountStatuses: ['LIVE'],
        emitterTypes: ['GHGE'],
        installationCategories: ['A_LOW_EMITTER'],
      },
      numberOfAccounts: 10,
      report: {
        name: 'BRI0048-E.csv',
        uuid: '04d5ccb2-b9d5-4304-b665-b187d4f1ed2c',
      },
    } as BatchReissueCompletedRequestActionPayload,
  },
  storeInitialized: true,
};
