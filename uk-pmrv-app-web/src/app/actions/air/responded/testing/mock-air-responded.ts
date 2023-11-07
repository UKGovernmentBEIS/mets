import {
  AirApplicationRespondedToRegulatorCommentsRequestActionPayload,
  AirImprovementCalculationCO2,
  OperatorAirImprovementYesResponse,
} from 'pmrv-api';

import { CommonActionsState } from '../../../store/common-actions.state';

export const mockAirApplicationRespondedActionPayload: AirApplicationRespondedToRegulatorCommentsRequestActionPayload =
  {
    payloadType: 'AIR_APPLICATION_RESPONDED_TO_REGULATOR_COMMENTS_PAYLOAD',
    reportingYear: 2022,
    reference: 1,
    airImprovement: {
      type: 'CALCULATION_CO2',
      categoryType: 'MAJOR',
      emissionSources: ['S1: Boiler'],
      sourceStreamReference: 'F1: Acetylene',
      parameter: 'Emission factor',
      tier: 'Tier 1',
    } as AirImprovementCalculationCO2,
    operatorImprovementResponse: {
      type: 'YES',
      proposal: 'Test explanation 1',
      proposedDate: '2040-06-03',
      files: ['11111111-1111-4111-a111-111111111111', '22222222-2222-4222-a222-222222222222'],
    } as OperatorAirImprovementYesResponse,
    regulatorImprovementResponse: {
      improvementRequired: true,
      improvementDeadline: '2040-01-01',
      officialResponse: 'Test official response 1',
      comments: 'Test comment 1',
      files: ['44444444-4444-4444-a444-444444444444', '55555555-5555-4555-a555-555555555555'],
    },
    operatorImprovementFollowUpResponse: {
      improvementCompleted: true,
      dateCompleted: '2023-01-01',
    },
    airAttachments: {
      '11111111-1111-4111-a111-111111111111': '100.png',
      '22222222-2222-4222-a222-222222222222': '200.png',
    },
    reviewAttachments: {
      '44444444-4444-4444-a444-444444444444': '400.png',
      '55555555-5555-4555-a555-555555555555': '500.png',
    },
  };

export const mockState = {
  storeInitialized: true,
  action: {
    id: 102,
    type: 'AIR_APPLICATION_RESPONDED_TO_REGULATOR_COMMENTS',
    payload: mockAirApplicationRespondedActionPayload,
    requestId: 'AIR00001-2023',
    requestType: 'AIR',
    requestAccountId: 11,
    competentAuthority: 'ENGLAND',
    submitter: 'Operator1 England',
    creationDate: '2023-04-05T16:14:29.258067Z',
  },
} as CommonActionsState;
