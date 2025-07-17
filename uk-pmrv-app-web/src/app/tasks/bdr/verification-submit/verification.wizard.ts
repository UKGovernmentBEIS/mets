import { BDRApplicationVerificationSubmitRequestTaskPayload } from 'pmrv-api';

export function submitVerificationWizardComplete(payload: BDRApplicationVerificationSubmitRequestTaskPayload): boolean {
  return (
    payload?.verificationSectionsCompleted?.['opinionStatement']?.[0] === true &&
    payload?.verificationSectionsCompleted?.['overallDecision']?.[0] === true
  );
}
