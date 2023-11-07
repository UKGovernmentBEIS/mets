import { CommonTasksState } from '@tasks/store/common-tasks.state';

import {
  AirApplicationReviewRequestTaskPayload,
  AirImprovementCalculationCO2,
  AirImprovementCalculationPFC,
  AirImprovementMeasurement,
  OperatorAirImprovementAlreadyMadeResponse,
  OperatorAirImprovementNoResponse,
  OperatorAirImprovementYesResponse,
  RequestTaskDTO,
} from 'pmrv-api';

export const mockAirApplicationReviewPayload: AirApplicationReviewRequestTaskPayload = {
  payloadType: 'AIR_APPLICATION_REVIEW_PAYLOAD',
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
      proposal: 'Test explanation 1',
      proposedDate: '2040-06-03',
      files: ['11111111-1111-4111-a111-111111111111', '22222222-2222-4222-a222-222222222222'],
    } as OperatorAirImprovementYesResponse,
    '2': {
      type: 'NO',
      isCostUnreasonable: true,
      isTechnicallyInfeasible: true,
      technicalInfeasibilityExplanation: 'Test description 2',
      files: ['11111111-1111-4111-a111-111111111111', '22222222-2222-4222-a222-222222222222'],
    } as OperatorAirImprovementNoResponse,
    '3': {
      type: 'ALREADY_MADE',
      explanation: 'Test description 3',
      improvementDate: '2020-06-03',
      files: [],
    } as OperatorAirImprovementAlreadyMadeResponse,
  },
  airSectionsCompleted: {},
  airAttachments: {
    '11111111-1111-4111-a111-111111111111': '100.png',
    '22222222-2222-4222-a222-222222222222': '200.png',
    '33333333-3333-4333-a333-333333333333': '300.png',
  },
  regulatorReviewResponse: {
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
    reportSummary: 'Test summary',
  },
  reviewSectionsCompleted: {
    '1': true,
    '2': true,
    '3': true,
    provideSummary: true,
  },
  reviewAttachments: {
    '44444444-4444-4444-a444-444444444444': '400.png',
    '55555555-5555-4555-a555-555555555555': '500.png',
    '66666666-6666-4666-a666-666666666666': '600.png',
  },
  rfiAttachments: {},
};

export const mockStateReview = {
  requestTaskItem: {
    allowedRequestTaskActions: [
      'AIR_SAVE_REVIEW',
      'AIR_REVIEW_UPLOAD_ATTACHMENT',
      'AIR_NOTIFY_OPERATOR_FOR_DECISION',
      'RFI_SUBMIT',
      'RFI_UPLOAD_ATTACHMENT',
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
      type: 'AIR_APPLICATION_REVIEW',
      assignable: true,
      assigneeFullName: 'Regulator1 England',
      assigneeUserId: '0f15e721-7c71-4441-b818-5cb2bf2f162b',
      startDate: '2023-03-15T15:04:23.866188Z',
      payload: mockAirApplicationReviewPayload,
    } as RequestTaskDTO,
    userAssignCapable: true,
  },
  relatedTasks: [],
  timeLineActions: [],
  storeInitialized: true,
  isEditable: true,
  user: undefined,
} as CommonTasksState;
