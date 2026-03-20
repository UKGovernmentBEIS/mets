import { BDRS2ApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

export function submitVerificationWizardComplete(
  payload: BDRS2ApplicationVerificationSubmitRequestTaskPayload,
): boolean {
  return (
    payload?.verificationSectionsCompleted?.['opinionStatement']?.[0] === true &&
    payload?.verificationSectionsCompleted?.['overallDecision']?.[0] === true
  );
}
