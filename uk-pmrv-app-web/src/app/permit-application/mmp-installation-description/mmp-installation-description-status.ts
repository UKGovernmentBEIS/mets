import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { TaskItemStatus } from '@shared/task-list/task-list.interface';

export function mmpInstallationDescriptionStatus(state: PermitApplicationState): TaskItemStatus {
  const mmpInstallationDescription = state.permit?.monitoringMethodologyPlans?.digitizedPlan?.installationDescription;
  return state.permitSectionsCompleted?.mmpInstallationDescription?.[0]
    ? 'complete'
    : mmpInstallationDescription !== undefined
      ? 'in progress'
      : 'not started';
}
