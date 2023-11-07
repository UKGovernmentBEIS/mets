import {
  OperatorAirImprovementAlreadyMadeResponse,
  OperatorAirImprovementNoResponse,
  OperatorAirImprovementYesResponse,
} from 'pmrv-api';

export type OperatorAirImprovementResponseAll = OperatorAirImprovementYesResponse &
  OperatorAirImprovementNoResponse &
  OperatorAirImprovementAlreadyMadeResponse;
