import { CommonTasksState } from '@tasks/store/common-tasks.state';

import {
  AirApplicationSubmitRequestTaskPayload,
  AirImprovementCalculationCO2,
  AirImprovementCalculationPFC,
  AirImprovementFallback,
  AirImprovementMeasurement,
  OperatorAirImprovementAlreadyMadeResponse,
  OperatorAirImprovementNoResponse,
  OperatorAirImprovementYesResponse,
  RequestTaskDTO,
} from 'pmrv-api';

export const mockAirApplicationSubmitPayload: AirApplicationSubmitRequestTaskPayload = {
  payloadType: 'AIR_APPLICATION_SUBMIT_PAYLOAD',
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
      files: ['11111111-1111-4111-a111-111111111111', '22222222-2222-4222-a222-222222222222'],
    } as OperatorAirImprovementNoResponse,
    '3': {
      type: 'ALREADY_MADE',
      explanation: 'Test description 3',
      improvementDate: '2020-06-03',
      files: ['11111111-1111-4111-a111-111111111111', '22222222-2222-4222-a222-222222222222'],
    } as OperatorAirImprovementAlreadyMadeResponse,
  },
  airSectionsCompleted: {
    '1': true,
    '2': false,
    '3': false,
    '4': false,
    '5': false,
  },
  airAttachments: {
    '11111111-1111-4111-a111-111111111111': '100.png',
    '22222222-2222-4222-a222-222222222222': '200.png',
  },
};

export const mockState = {
  requestTaskItem: {
    allowedRequestTaskActions: ['AIR_SAVE_APPLICATION', 'AIR_UPLOAD_ATTACHMENT'],
    requestInfo: {
      id: 'AIR00001-2022',
      type: 'AIR',
      competentAuthority: 'ENGLAND',
      accountId: 210,
      requestMetadata: {
        type: 'AIR',
        year: '2022',
      },
    },
    requestTask: {
      id: 1,
      type: 'AIR_APPLICATION_SUBMIT',
      assignable: true,
      assigneeFullName: 'Operator1 England',
      assigneeUserId: '0f15e721-7c71-4441-b818-5cb2bf2f162b',
      daysRemaining: -270,
      startDate: '2023-03-15T15:04:23.866188Z',
      payload: mockAirApplicationSubmitPayload,
    } as RequestTaskDTO,
    userAssignCapable: true,
  },
  relatedTasks: [],
  timeLineActions: [],
  storeInitialized: true,
  isEditable: true,
  user: undefined,
} as CommonTasksState;
