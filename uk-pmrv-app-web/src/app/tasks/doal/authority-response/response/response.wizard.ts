import { DoalAuthorityResponseRequestTaskPayload } from 'pmrv-api';

export function responseWizardComplete(
  payload: DoalAuthorityResponseRequestTaskPayload,
  enableViewSummary: boolean,
): boolean {
  return (
    !!payload?.doalAuthority &&
    !!payload.doalAuthority?.authorityResponse &&
    !!payload.doalAuthority.authorityResponse?.type &&
    (payload.doalAuthority.authorityResponse?.type === 'INVALID' || enableViewSummary)
  );
}
