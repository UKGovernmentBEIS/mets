import { ReissueCompletedRequestActionPayload } from 'pmrv-api';

import { CommonActionsState } from '../../store/common-actions.state';

export const mockCompletedActionState: CommonActionsState = {
  action: {
    id: 1,
    type: 'REISSUE_COMPLETED',
    competentAuthority: 'ENGLAND',
    creationDate: '2023-06-08T16:35:40.18009Z',
    requestId: 'requestI',
    requestType: 'PERMIT_REISSUE',
    submitter: 'submitter1',
    payload: {
      payloadType: 'PERMIT_REISSUE_COMPLETED_PAYLOAD',
      signatory: 'b12938d1-2eee-4e4e-928e-cff5d66f320b',
      signatoryName: 'signName',
      submitter: 'submitter1',
      officialNotice: {
        name: 'Batch_variation_notice.pdf',
        uuid: 'a186a964-5abc-4074-8a0f-84c15ae9c404',
      },
      document: {
        name: 'UK-E-IN-00048 v6.pdf',
        uuid: 'ade6dc46-2357-436e-922b-6760b7514a7d',
      },
    } as ReissueCompletedRequestActionPayload,
  },
  storeInitialized: true,
};
