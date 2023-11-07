import { VirApplicationRespondedToRegulatorCommentsRequestActionPayload } from 'pmrv-api';

import { CommonActionsState } from '../../../store/common-actions.state';

export const mockVirApplicationRespondedActionPayload: VirApplicationRespondedToRegulatorCommentsRequestActionPayload =
  {
    payloadType: 'VIR_APPLICATION_RESPONDED_TO_REGULATOR_COMMENTS_PAYLOAD',
    reportingYear: 2022,
    virAttachments: {
      '09101409-b5ee-45f4-b936-fa99cbd14fa4': '100.bmp',
      '8804d46d-fd79-4e35-afdc-ac483381544e': '300.png',
      'd3f6ed17-5918-4d45-beaa-1cd28de87584': '1.png',
      'd9876b4d-89b6-49e2-9684-06d21dffd392': '1.png',
      'fcd04477-bf66-4bfe-ae1f-5e9dfde80f9c': '300.png',
    },
    verifierUncorrectedItem: {
      reference: 'B1',
      explanation: 'yru',
      materialEffect: true,
    },
    operatorImprovementResponse: {
      isAddressed: true,
      addressedDescription: 'Has been addressed B1',
      addressedDate: '2022-01-01',
      uploadEvidence: true,
      files: ['d9876b4d-89b6-49e2-9684-06d21dffd392', '8804d46d-fd79-4e35-afdc-ac483381544e'],
    },
    regulatorImprovementResponse: {
      improvementRequired: true,
      improvementDeadline: '2024-01-01',
      improvementComments: 'Comments B1',
      operatorActions: 'Actions B1',
    },
    operatorImprovementFollowUpResponse: {
      improvementCompleted: true,
      dateCompleted: '2023-01-01',
    },
  };

export const mockState = {
  storeInitialized: true,
  action: {
    id: 102,
    type: 'VIR_APPLICATION_RESPONDED_TO_REGULATOR_COMMENTS',
    payload: mockVirApplicationRespondedActionPayload,
    requestId: 'VIR00001-2023',
    requestType: 'VIR',
    requestAccountId: 11,
    competentAuthority: 'ENGLAND',
    submitter: 'Operator1 England',
    creationDate: '2023-04-05T16:14:29.258067Z',
  },
} as CommonActionsState;
