import { RequestTaskItemDTO } from 'pmrv-api';

import { InspectionSubmitRequestTaskPayload } from '../shared/inspection';
import { isInstallationInspectionFollowUpSubmitCompleted } from './submit.wizard';

export function canSendPeerReview(
  payload: InspectionSubmitRequestTaskPayload,
  allowedActions: RequestTaskItemDTO['allowedRequestTaskActions'],
): boolean {
  return (
    isInstallationInspectionFollowUpSubmitCompleted(payload.installationInspection) &&
    payload?.installationInspectionSectionsCompleted?.followUpAction &&
    (allowedActions.includes('INSTALLATION_AUDIT_REQUEST_PEER_REVIEW') ||
      allowedActions.includes('INSTALLATION_ONSITE_INSPECTION_REQUEST_PEER_REVIEW'))
  );
}
