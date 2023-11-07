import { CommonTasksState } from '@tasks/store/common-tasks.state';

import { RequestTaskDTO, VirApplicationRespondToRegulatorCommentsRequestTaskPayload } from 'pmrv-api';

export const mockVirApplicationRespondPayload: VirApplicationRespondToRegulatorCommentsRequestTaskPayload = {
  payloadType: 'VIR_RESPOND_TO_REGULATOR_COMMENTS_PAYLOAD',
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
  regulatorImprovementResponses: {
    A1: {
      improvementRequired: true,
      improvementDeadline: '2023-12-01',
      improvementComments: 'Test improvement comments A1',
      operatorActions: 'Test operator actions A1',
    },
    B1: {
      improvementRequired: true,
      improvementDeadline: '2023-12-01',
      improvementComments: 'Test improvement comments B1',
      operatorActions: 'Test operator actions B1',
    },
  },
  operatorImprovementFollowUpResponses: {
    B1: {
      improvementCompleted: true,
      dateCompleted: '2022-06-03',
    },
  },
  virRespondToRegulatorCommentsSectionsCompleted: {
    B1: false,
  },
};

export const mockStateRespond = {
  requestTaskItem: {
    allowedRequestTaskActions: ['VIR_SAVE_RESPOND_TO_REGULATOR_COMMENTS', 'VIR_SUBMIT_RESPOND_TO_REGULATOR_COMMENTS'],
    requestInfo: {
      id: 'VIR00001-2022',
      type: 'VIR',
      competentAuthority: 'ENGLAND',
      accountId: 210,
      paymentCompleted: true,
      requestMetadata: {
        type: 'VIR',
        year: '2022',
        rfiResponseDates: [],
      },
    },
    requestTask: {
      id: 1,
      type: 'VIR_RESPOND_TO_REGULATOR_COMMENTS',
      assignable: true,
      assigneeFullName: 'Operator1 England',
      assigneeUserId: '0f15e721-7c71-4441-b818-5cb2bf2f162b',
      startDate: '2023-03-15T15:04:23.866188Z',
      daysRemaining: 100,
      payload: mockVirApplicationRespondPayload,
    } as RequestTaskDTO,
    userAssignCapable: true,
  },
  relatedTasks: [],
  timeLineActions: [],
  storeInitialized: true,
  isEditable: true,
  user: undefined,
} as CommonTasksState;
