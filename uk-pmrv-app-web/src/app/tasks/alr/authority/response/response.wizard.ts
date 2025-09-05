import { ALRAuthorityResponseSubmitRequestTaskPayload } from 'pmrv-api';

export function responseWizardComplete(
  payload: ALRAuthorityResponseSubmitRequestTaskPayload,
  enableViewSummary: boolean,
): boolean {
  return (
    !!payload?.authorityReviewOutcome &&
    !!payload.authorityReviewOutcome?.authorityResponse &&
    !!payload.authorityReviewOutcome.authorityResponse?.type &&
    (payload.authorityReviewOutcome.authorityResponse?.type === 'INVALID' ||
      (!!payload?.authorityReviewOutcome &&
        !!payload.authorityReviewOutcome?.authorityResponse &&
        !!payload.authorityReviewOutcome.authorityResponse?.type &&
        payload.authorityReviewOutcome.authorityResponse?.['documents']?.length > 0) ||
      enableViewSummary)
  );
}
