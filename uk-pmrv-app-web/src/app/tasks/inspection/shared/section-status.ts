import { TaskItemStatus } from '@shared/task-list/task-list.interface';

import {
  InstallationAuditApplicationSubmitRequestTaskPayload,
  InstallationInspectionOperatorRespondRequestTaskPayload,
  RequestTaskDTO,
} from 'pmrv-api';

import { isInstallationInspectionRespondCompleted } from '../respond/submit.wizard';
import {
  isInstallationInspectionDetailsSubmitCompleted,
  isInstallationInspectionFollowUpSubmitCompleted,
  onSiteInspectionDateValid,
  responseDeadlineValid,
} from '../submit/submit.wizard';

export const followUpStatusKeySubmit = 'followUpAction';

export const resolveSectionStatusSubmit = (
  payload: InstallationAuditApplicationSubmitRequestTaskPayload,
  statusKey: string,
): TaskItemStatus => {
  if (
    !payload?.installationInspection?.responseDeadline &&
    !payload?.installationInspection?.followUpActions?.length &&
    (payload?.installationInspection?.followUpActionsRequired === null ||
      payload?.installationInspection?.followUpActionsRequired === undefined)
  ) {
    return 'not started';
  } else {
    return payload?.installationInspectionSectionsCompleted[statusKey]
      ? isInstallationInspectionFollowUpSubmitCompleted(payload?.installationInspection)
        ? payload?.installationInspection?.followUpActionsRequired !== false
          ? responseDeadlineValid(payload?.installationInspection.responseDeadline)
            ? 'complete'
            : 'needs review'
          : 'complete'
        : 'in progress'
      : 'in progress';
  }
};

export const statusKeyRespond = 'followUpActionsResponse';

export const resolveSectionStatusRespond = (
  payload: InstallationInspectionOperatorRespondRequestTaskPayload,
  statusKey: number,
): TaskItemStatus => {
  return isInstallationInspectionRespondCompleted(payload?.followupActionsResponses, statusKey.toString()) &&
    payload?.installationInspectionOperatorRespondSectionsCompleted[statusKey]
    ? 'complete'
    : payload?.followupActionsResponses[statusKey]
      ? 'in progress'
      : 'not started';
};

export const followUpActionSectionsCompleted = (
  payload: InstallationInspectionOperatorRespondRequestTaskPayload,
): boolean => {
  return (
    payload.installationInspection.followUpActions.find(
      (_, key) => !payload.installationInspectionOperatorRespondSectionsCompleted[key],
    ) === undefined
  );
};

export const resolveSectionStatusRespondSend = (
  payload: InstallationInspectionOperatorRespondRequestTaskPayload,
): TaskItemStatus => {
  return followUpActionSectionsCompleted(payload) ? 'not started' : 'cannot start yet';
};

export const detailsStatusKeySubmit = 'details';

export const resolveDetailsSectionStatusSubmit = (
  payload: InstallationAuditApplicationSubmitRequestTaskPayload,
  taskType: RequestTaskDTO['type'],
  statusKey: string,
): TaskItemStatus => {
  if (payload?.installationInspection?.details?.officerNames?.length === 0) {
    return 'not started';
  } else {
    return payload?.installationInspectionSectionsCompleted[statusKey]
      ? isInstallationInspectionDetailsSubmitCompleted(payload?.installationInspection)
        ? taskType === 'INSTALLATION_AUDIT_APPLICATION_SUBMIT' ||
          taskType === 'INSTALLATION_AUDIT_APPLICATION_PEER_REVIEW'
          ? 'complete'
          : onSiteInspectionDateValid(payload?.installationInspection.details.date)
            ? 'complete'
            : 'needs review'
        : 'in progress'
      : 'in progress';
  }
};
