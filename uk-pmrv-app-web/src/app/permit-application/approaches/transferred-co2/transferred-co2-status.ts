import { TaskItemStatus } from '@shared/task-list/task-list.interface';

import { TransferredCO2AndN2OMonitoringApproach } from 'pmrv-api';

import { PermitApplicationState } from '../../store/permit-application.state';

export function TRANSFERRED_CO2Status(state: PermitApplicationState): TaskItemStatus {
  return transferredCo2Statuses.every((status) => state.permitSectionsCompleted[status]?.[0])
    ? 'complete'
    : transferredCo2Statuses.some((status) => state.permitSectionsCompleted[status]?.[0]) ||
      TRANSFERRED_CO2_PipelineStatus(state) === 'in progress'
    ? 'in progress'
    : 'not started';
}

export function TRANSFERRED_CO2_PipelineStatus(state: PermitApplicationState): TaskItemStatus {
  const transportCO2AndN2OPipelineSystems = (
    state.permit?.monitoringApproaches?.TRANSFERRED_CO2_N2O as TransferredCO2AndN2OMonitoringApproach
  )?.transportCO2AndN2OPipelineSystems;

  return state.permitSectionsCompleted?.TRANSFERRED_CO2_N2O_Pipeline?.[0]
    ? 'complete'
    : transportCO2AndN2OPipelineSystems !== undefined
    ? 'in progress'
    : 'not started';
}

export const transferredCo2Statuses = [
  'TRANSFERRED_CO2_N2O_Deductions',
  'TRANSFERRED_CO2_N2O_Storage',
  'TRANSFERRED_CO2_N2O_Transport_Network_Approach',
  'TRANSFERRED_CO2_N2O_Pipeline',
] as const;

export type TransferredCo2Status = typeof transferredCo2Statuses[number];
