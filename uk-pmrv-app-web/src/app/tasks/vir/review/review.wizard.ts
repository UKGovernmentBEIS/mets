import { RegulatorImprovementResponse, VirApplicationReviewRequestTaskPayload } from 'pmrv-api';

export function reviewWizardComplete(payload: VirApplicationReviewRequestTaskPayload): boolean {
  const verificationData = payload?.verificationData;
  const flattenedReferences = [
    ...Object.keys(verificationData?.uncorrectedNonConformities || {}),
    ...Object.keys(verificationData?.recommendedImprovements || {}),
    ...Object.keys(verificationData?.priorYearIssues || {}),
  ];

  return (
    flattenedReferences.every((reference) => payload?.reviewSectionsCompleted?.[reference] === true) &&
    payload?.reviewSectionsCompleted?.['createSummary'] === true
  );
}

export function regulatorImprovementResponseComplete(
  reference: string,
  regulatorReviewResponse?: VirApplicationReviewRequestTaskPayload['regulatorReviewResponse'],
): boolean {
  return regulatorReviewResponse
    ? improvementComplete(regulatorReviewResponse?.regulatorImprovementResponses?.[reference])
    : false;
}

function improvementComplete(regulatorImprovementResponse: RegulatorImprovementResponse): boolean {
  return (
    (regulatorImprovementResponse?.improvementRequired === true &&
      !!regulatorImprovementResponse?.improvementDeadline) ||
    regulatorImprovementResponse?.improvementRequired === false
  );
}

export function reportSummaryComplete(payload: VirApplicationReviewRequestTaskPayload) {
  return !!payload?.regulatorReviewResponse?.reportSummary;
}
