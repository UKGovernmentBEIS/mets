import { VirApplicationReviewedRequestActionPayload } from 'pmrv-api';

import { CommonActionsState } from '../../../store/common-actions.state';

export const mockVirApplicationReviewedRequestActionPayload: VirApplicationReviewedRequestActionPayload = {
  payloadType: 'VIR_APPLICATION_REVIEWED_PAYLOAD',
  reportingYear: 2023,
  verificationData: {
    uncorrectedNonConformities: {
      B1: {
        reference: 'B1',
        explanation: 'Test uncorrectedNonConformity',
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
    B1: {
      isAddressed: true,
      addressedDescription: 'Test description B1',
      addressedDate: '2022-01-01',
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
  regulatorReviewResponse: {
    regulatorImprovementResponses: {
      B1: {
        improvementRequired: true,
        improvementDeadline: '2024-01-01',
        improvementComments: 'Comments B1',
        operatorActions: 'Actions B1',
      },
      E1: {
        improvementRequired: true,
        improvementDeadline: '2024-01-01',
        improvementComments: 'Comments E1',
        operatorActions: 'Actions E1',
      },
    },
    reportSummary: 'LGTM',
  },
  officialNotice: {
    name: 'recommended_improvements.pdf',
    uuid: 'fb355af4-2163-443e-b9ea-7c483bc217f9',
  },
  usersInfo: {
    '252cd19d-b2f4-4e6b-ba32-a225d0777c98': {
      name: 'Regulator1 England',
    },
    '43edf1df-a814-4ce5-8839-e6b2c80cd63e': {
      name: 'Operator2 England',
      roleCode: 'operator_admin',
      contactTypes: ['SECONDARY'],
    },
    '0f15e721-7c71-4441-b818-5cb2bf2f162b': {
      name: 'Operator1 England',
      roleCode: 'operator_admin',
      contactTypes: ['FINANCIAL', 'PRIMARY', 'SERVICE'],
    },
  },
  decisionNotification: {
    operators: ['43edf1df-a814-4ce5-8839-e6b2c80cd63e'],
    externalContacts: [1],
    signatory: '252cd19d-b2f4-4e6b-ba32-a225d0777c98',
  },
};

export const mockState = {
  storeInitialized: true,
  action: {
    id: 102,
    type: 'VIR_APPLICATION_REVIEWED',
    payload: mockVirApplicationReviewedRequestActionPayload,
    requestId: 'VIR00001-2023',
    requestType: 'VIR',
    requestAccountId: 11,
    competentAuthority: 'ENGLAND',
    submitter: 'Regulator1 England',
    creationDate: '2023-04-05T16:14:29.258067Z',
  },
} as CommonActionsState;
