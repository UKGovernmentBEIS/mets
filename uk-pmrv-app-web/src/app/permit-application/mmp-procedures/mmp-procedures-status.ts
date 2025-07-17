import { PermitApplicationState } from '@permit-application/store/permit-application.state';
import { TaskItemStatus } from '@shared/task-list/task-list.interface';

export function mmpProceduresStatus(state: PermitApplicationState): TaskItemStatus {
  const mmpProcedures = state.permit?.monitoringMethodologyPlans?.digitizedPlan?.procedures;
  return state.permitSectionsCompleted?.mmpProcedures?.[0]
    ? 'complete'
    : mmpProcedures !== undefined
      ? 'in progress'
      : 'not started';
}

export const procedureTypes = [
  'ASSIGNMENT_OF_RESPONSIBILITIES',
  'MONITORING_PLAN_APPROPRIATENESS',
  'DATA_FLOW_ACTIVITIES',
  'CONTROL_ACTIVITIES',
];
