import { VirApplicationRespondToRegulatorCommentsRequestTaskPayload } from 'pmrv-api';

export function commentsResponseWizardComplete(
  reference: string,
  payload: VirApplicationRespondToRegulatorCommentsRequestTaskPayload,
): boolean {
  const operatorImprovementFollowUpResponse = payload?.operatorImprovementFollowUpResponses?.[reference];
  if (operatorImprovementFollowUpResponse) {
    return (
      (operatorImprovementFollowUpResponse?.improvementCompleted === true &&
        !!operatorImprovementFollowUpResponse?.dateCompleted) ||
      (operatorImprovementFollowUpResponse?.improvementCompleted === false &&
        !!operatorImprovementFollowUpResponse?.reason)
    );
  }

  return false;
}
