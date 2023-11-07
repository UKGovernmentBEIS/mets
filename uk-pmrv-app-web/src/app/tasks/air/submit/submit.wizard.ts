import {
  AirApplicationSubmitRequestTaskPayload,
  OperatorAirImprovementAlreadyMadeResponse,
  OperatorAirImprovementNoResponse,
  OperatorAirImprovementResponse,
  OperatorAirImprovementYesResponse,
} from 'pmrv-api';

export function submitWizardComplete(payload: AirApplicationSubmitRequestTaskPayload): boolean {
  const airImprovementsKeys = Object.keys(payload?.airImprovements);

  return airImprovementsKeys.every((reference) => payload?.airSectionsCompleted?.[reference] === true);
}

export function operatorImprovementResponseComplete(response: OperatorAirImprovementResponse): boolean {
  switch (response?.type) {
    case 'YES':
      return isYesResponseComplete(response as OperatorAirImprovementYesResponse);
    case 'NO':
      return isNoResponseComplete(response as OperatorAirImprovementNoResponse);
    case 'ALREADY_MADE':
      return isAlreadyMadeResponseComplete(response as OperatorAirImprovementAlreadyMadeResponse);
    default:
      return false;
  }
}

function isYesResponseComplete(response: OperatorAirImprovementYesResponse) {
  return !!response?.proposal && !!response?.proposedDate;
}

function isNoResponseComplete(response: OperatorAirImprovementNoResponse) {
  return (
    (response?.isTechnicallyInfeasible === true && !!response?.technicalInfeasibilityExplanation) ||
    response?.isCostUnreasonable === true
  );
}

function isAlreadyMadeResponseComplete(response: OperatorAirImprovementAlreadyMadeResponse) {
  return !!response?.explanation;
}
