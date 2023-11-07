import { CommonTasksState } from '@tasks/store/common-tasks.state';

import {
  AirApplicationRespondToRegulatorCommentsRequestTaskPayload,
  AirImprovementCalculationCO2,
  AirImprovementCalculationPFC,
  AirImprovementMeasurement,
  OperatorAirImprovementAlreadyMadeResponse,
  OperatorAirImprovementNoResponse,
  OperatorAirImprovementYesResponse,
  RequestTaskDTO,
} from 'pmrv-api';

export const mockAirApplicationRespondPayload: AirApplicationRespondToRegulatorCommentsRequestTaskPayload = {
  payloadType: 'AIR_RESPOND_TO_REGULATOR_COMMENTS_PAYLOAD',
  airImprovements: {
    '1': {
      type: 'CALCULATION_CO2',
      categoryType: 'MAJOR',
      emissionSources: ['S1: Boiler'],
      sourceStreamReference: 'F1: Acetylene',
      parameter: 'Emission factor',
      tier: 'Tier 1',
    } as AirImprovementCalculationCO2,
    '2': {
      type: 'CALCULATION_PFC',
      categoryType: 'MAJOR',
      emissionSources: ['S1: Boiler'],
      sourceStreamReference: 'F1: Acetylene',
      emissionPoints: ['EP1: West side chimney'],
      parameter: 'Emission factor',
      tier: 'Tier 1',
    } as AirImprovementCalculationPFC,
    '3': {
      type: 'MEASUREMENT_CO2',
      categoryType: 'MAJOR',
      emissionSources: ['S1: Boiler'],
      sourceStreamReferences: ['F1: Acetylene'],
      emissionPoint: 'EP1: West side chimney',
      parameter: 'Applied standard Parameter',
      tier: 'Tier 2',
    } as AirImprovementMeasurement,
  },
  operatorImprovementResponses: {
    '1': {
      type: 'YES',
      proposal: 'Test proposal 1',
      proposedDate: '2040-06-03',
      files: ['11111111-1111-4111-a111-111111111111', '22222222-2222-4222-a222-222222222222'],
    } as OperatorAirImprovementYesResponse,
    '2': {
      type: 'NO',
      isCostUnreasonable: true,
      isTechnicallyInfeasible: true,
      technicalInfeasibilityExplanation: 'Test infeasibility explanation 2',
      files: ['11111111-1111-4111-a111-111111111111', '22222222-2222-4222-a222-222222222222'],
    } as OperatorAirImprovementNoResponse,
    '3': {
      type: 'ALREADY_MADE',
      explanation: 'Test explanation 3',
      improvementDate: '2020-06-03',
      files: [],
    } as OperatorAirImprovementAlreadyMadeResponse,
  },
  airSectionsCompleted: {},
  airAttachments: {
    '11111111-1111-4111-a111-111111111111': '100.png',
    '22222222-2222-4222-a222-222222222222': '200.png',
    '33333333-3333-4333-a333-333333333333': '300.png',
    '77777777-7777-4777-a777-777777777777': '700.png',
    '88888888-8888-4888-a888-888888888888': '800.png',
    '99999999-9999-4999-a999-999999999999': '900.png',
  },
  regulatorImprovementResponses: {
    '1': {
      improvementRequired: true,
      improvementDeadline: '2040-01-01',
      officialResponse: 'Test official response 1',
      comments: 'Test comment 1',
      files: ['44444444-4444-4444-a444-444444444444', '55555555-5555-4555-a555-555555555555'],
    },
    '2': {
      improvementRequired: false,
      officialResponse: 'Test official response 2',
      comments: 'Test comments 2',
      files: ['66666666-6666-4666-a666-666666666666'],
    },
    '3': {
      improvementRequired: false,
      officialResponse: 'Test official response 3',
      comments: 'Test comments 3',
    },
  },
  reviewAttachments: {
    '44444444-4444-4444-a444-444444444444': '400.png',
    '55555555-5555-4555-a555-555555555555': '500.png',
    '66666666-6666-4666-a666-666666666666': '600.png',
  },
  operatorImprovementFollowUpResponses: {
    '1': {
      improvementCompleted: true,
      dateCompleted: '2022-01-01',
      files: ['77777777-7777-4777-a777-777777777777', '88888888-8888-4888-a888-888888888888'],
    },
    '2': {
      improvementCompleted: false,
      reason: 'Test reason 2',
      files: ['99999999-9999-4999-a999-999999999999'],
    },
    '3': {
      improvementCompleted: false,
      reason: 'Test reason 3',
    },
  },
  airRespondToRegulatorCommentsSectionsCompleted: {
    1: true,
    2: false,
  },
};

export const mockStateRespond = {
  requestTaskItem: {
    allowedRequestTaskActions: [
      'AIR_SAVE_RESPOND_TO_REGULATOR_COMMENTS',
      'AIR_SUBMIT_RESPOND_TO_REGULATOR_COMMENTS',
      'AIR_UPLOAD_ATTACHMENT',
    ],
    requestInfo: {
      id: 'AIR00001-2022',
      type: 'AIR',
      competentAuthority: 'ENGLAND',
      accountId: 210,
      paymentCompleted: true,
      requestMetadata: {
        type: 'AIR',
        year: '2022',
        rfiResponseDates: [],
      },
    },
    requestTask: {
      id: 1,
      type: 'AIR_RESPOND_TO_REGULATOR_COMMENTS',
      assignable: true,
      assigneeFullName: 'Operator1 England',
      assigneeUserId: '0f15e721-7c71-4441-b818-5cb2bf2f162b',
      startDate: '2023-03-15T15:04:23.866188Z',
      daysRemaining: 100,
      payload: mockAirApplicationRespondPayload,
    } as RequestTaskDTO,
    userAssignCapable: true,
  },
  relatedTasks: [],
  timeLineActions: [],
  storeInitialized: true,
  isEditable: true,
  user: undefined,
} as CommonTasksState;
