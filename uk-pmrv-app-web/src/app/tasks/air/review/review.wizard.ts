import { AirApplicationReviewRequestTaskPayload, RegulatorAirImprovementResponse } from 'pmrv-api';

export function reviewWizardComplete(payload: AirApplicationReviewRequestTaskPayload): boolean {
  const airImprovementsKeys = Object.keys(payload?.airImprovements);

  return (
    airImprovementsKeys.every(
      (reference) =>
        regulatorImprovementResponseComplete(reference, payload?.regulatorReviewResponse) &&
        payload?.reviewSectionsCompleted?.[reference] === true,
    ) && payload?.reviewSectionsCompleted?.['provideSummary'] === true
  );
}

export function regulatorImprovementResponseComplete(
  reference: string,
  regulatorReviewResponse?: AirApplicationReviewRequestTaskPayload['regulatorReviewResponse'],
): boolean {
  return regulatorReviewResponse
    ? improvementComplete(regulatorReviewResponse?.regulatorImprovementResponses?.[reference])
    : false;
}

function improvementComplete(regulatorImprovementResponse: RegulatorAirImprovementResponse): boolean {
  return (
    improvementRequiredComplete(regulatorImprovementResponse) &&
    !!regulatorImprovementResponse?.officialResponse &&
    !!regulatorImprovementResponse?.comments
  );
}

function improvementRequiredComplete(regulatorImprovementResponse: RegulatorAirImprovementResponse): boolean {
  return (
    (regulatorImprovementResponse?.improvementRequired === true &&
      !!regulatorImprovementResponse?.improvementDeadline &&
      new Date(regulatorImprovementResponse?.improvementDeadline) > new Date()) ||
    regulatorImprovementResponse?.improvementRequired === false
  );
}

export function reportSummaryComplete(payload: AirApplicationReviewRequestTaskPayload) {
  return !!payload?.regulatorReviewResponse?.reportSummary;
}
