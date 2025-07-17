import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { TaskItemStatus } from '@shared/task-list/task-list.interface';

export function mmpEnergyFlowsStatus(state: PermitApplicationState): TaskItemStatus {
  const mmpEnergyFlows = state.permit?.monitoringMethodologyPlans?.digitizedPlan?.energyFlows;
  return state.permitSectionsCompleted?.['mmpEnergyFlows']?.[0]
    ? 'complete'
    : mmpEnergyFlows !== undefined
      ? 'in progress'
      : 'not started';
}
