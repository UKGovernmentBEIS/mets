import {
  AirApplicationSubmittedRequestActionPayload,
  AirImprovementCalculationCO2,
  AirImprovementCalculationPFC,
  AirImprovementFallback,
  AirImprovementMeasurement,
  OperatorAirImprovementAlreadyMadeResponse,
  OperatorAirImprovementNoResponse,
  OperatorAirImprovementYesResponse,
} from 'pmrv-api';

import { CommonActionsState } from '../../../store/common-actions.state';

export const mockAirApplicationSubmittedRequestActionPayload: AirApplicationSubmittedRequestActionPayload = {
  payloadType: 'AIR_APPLICATION_SUBMITTED_PAYLOAD',
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
    '4': {
      type: 'MEASUREMENT_N2O',
      categoryType: 'MAJOR',
      emissionSources: ['S1: Boiler'],
      sourceStreamReferences: ['F1: Acetylene'],
      emissionPoint: 'EP1: West side chimney',
      parameter: 'Applied standard Parameter',
      tier: 'Tier 2',
    } as AirImprovementMeasurement,
    '5': {
      type: 'FALLBACK',
      categoryType: 'MAJOR',
      emissionSources: ['S1: Boiler'],
      sourceStreamReference: 'F1: Acetylene',
    } as AirImprovementFallback,
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
      files: ['33333333-3333-4333-a333-333333333333'],
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
};

export const mockState = {
  storeInitialized: true,
  action: {
    id: 102,
    type: 'AIR_APPLICATION_SUBMITTED',
    payload: mockAirApplicationSubmittedRequestActionPayload,
    requestId: 'AIR00011-2022',
    requestType: 'AIR',
    requestAccountId: 11,
    competentAuthority: 'ENGLAND',
    submitter: 'Operator1 England',
    creationDate: '2023-04-05T16:14:29.258067Z',
  },
} as CommonActionsState;
