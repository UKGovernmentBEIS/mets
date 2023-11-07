import {
  AirApplicationReviewedRequestActionPayload,
  AirImprovementCalculationCO2,
  AirImprovementCalculationPFC,
  AirImprovementMeasurement,
  OperatorAirImprovementAlreadyMadeResponse,
  OperatorAirImprovementNoResponse,
  OperatorAirImprovementYesResponse,
} from 'pmrv-api';

import { CommonActionsState } from '../../../store/common-actions.state';

export const mockAirApplicationReviewedRequestActionPayload: AirApplicationReviewedRequestActionPayload = {
  payloadType: 'AIR_APPLICATION_REVIEWED_PAYLOAD',
  reportingYear: 2023,
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
    type: 'AIR_APPLICATION_REVIEWED',
    payload: mockAirApplicationReviewedRequestActionPayload,
    requestId: 'AIR00001-2023',
    requestType: 'AIR',
    requestAccountId: 11,
    competentAuthority: 'ENGLAND',
    submitter: 'Regulator1 England',
    creationDate: '2023-04-05T16:14:29.258067Z',
  },
} as CommonActionsState;
