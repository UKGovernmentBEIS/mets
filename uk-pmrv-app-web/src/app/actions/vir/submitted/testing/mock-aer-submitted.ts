import { VirApplicationSubmittedRequestActionPayload } from 'pmrv-api';

import { CommonActionsState } from '../../../store/common-actions.state';

export const mockVirApplicationSubmittedRequestActionPayload: VirApplicationSubmittedRequestActionPayload = {
  payloadType: 'VIR_APPLICATION_SUBMITTED_PAYLOAD',
  reportingYear: 2023,
  verificationData: {
    uncorrectedMisstatements: {
      A1: {
        reference: 'A1',
        explanation: 'Test uncorrectedMisstatement A1',
        materialEffect: true,
      },
      A2: {
        reference: 'A2',
        explanation: 'Test uncorrectedMisstatement A2',
        materialEffect: false,
      },
    },
    uncorrectedNonConformities: {
      B1: {
        reference: 'B1',
        explanation: 'Test uncorrectedNonConformity',
        materialEffect: true,
      },
    },
    uncorrectedNonCompliances: {
      C1: {
        reference: 'C1',
        explanation: 'Test uncorrectedNonCompliance',
        materialEffect: true,
      },
    },
    recommendedImprovements: {
      D1: {
        reference: 'D1',
        explanation: 'Test recommended improvement',
      },
    },
    priorYearIssues: {
      E1: {
        reference: 'E1',
        explanation: 'Test priorYearIssue',
      },
    },
  },
  operatorImprovementResponses: {
    A1: {
      isAddressed: true,
      addressedDescription: 'Test description A1',
      addressedDate: '2040-06-03',
      uploadEvidence: true,
      files: [
        '74313a81-7182-4ead-9018-8f9e298ceb68',
        '6eca6133-491f-47d2-9085-949adaae025b',
        '10c7da4c-7c72-42a8-bb7f-043c6aaf0d72',
      ],
    },
    A2: {
      isAddressed: false,
      addressedDescription: 'Test description A2',
      addressedDate: '2022-01-01',
      uploadEvidence: false,
    },
    B1: {
      isAddressed: true,
      addressedDescription: 'Test description B1',
      addressedDate: '2022-01-01',
      uploadEvidence: false,
    },
    C1: {
      isAddressed: false,
      addressedDescription: 'ghjmtk',
      uploadEvidence: false,
    },
    D1: {
      isAddressed: false,
      addressedDescription: 'Test description D1',
      uploadEvidence: false,
      files: ['122a4648-efad-45fa-a58b-d3b0d68ec593'],
    },
    E1: {
      isAddressed: false,
      addressedDescription: 'Test description E11',
      addressedDate: '2022-01-01',
      uploadEvidence: false,
    },
  },
  virAttachments: {
    '74313a81-7182-4ead-9018-8f9e298ceb68': '100.png',
    '6eca6133-491f-47d2-9085-949adaae025b': '200.png',
    '10c7da4c-7c72-42a8-bb7f-043c6aaf0d72': '300.png',
    '122a4648-efad-45fa-a58b-d3b0d68ec593': '400.png',
  },
};

export const mockState = {
  storeInitialized: true,
  action: {
    id: 102,
    type: 'VIR_APPLICATION_SUBMITTED',
    payload: mockVirApplicationSubmittedRequestActionPayload,
    requestId: 'VIR00011-2022',
    requestType: 'VIR',
    requestAccountId: 11,
    competentAuthority: 'ENGLAND',
    submitter: 'Operator1 England',
    creationDate: '2023-04-05T16:14:29.258067Z',
  },
} as CommonActionsState;
