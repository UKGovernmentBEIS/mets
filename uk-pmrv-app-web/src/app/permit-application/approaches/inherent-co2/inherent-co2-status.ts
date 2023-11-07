import { InherentCO2MonitoringApproach } from 'pmrv-api';

import { TaskItemStatus } from '../../../shared/task-list/task-list.interface';
import { StatusKey } from '../../shared/types/permit-task.type';
import { PermitApplicationState } from '../../store/permit-application.state';

export function INHERENT_CO2Status(state: PermitApplicationState, key: StatusKey): TaskItemStatus {
  return state.permitSectionsCompleted[key]?.[0]
    ? 'complete'
    : (state.permit?.monitoringApproaches[key] as InherentCO2MonitoringApproach)
        ?.inherentReceivingTransferringInstallations?.length > 0
    ? 'in progress'
    : 'not started';
}

export const inherentCo2Statuses = ['INHERENT_CO2'] as const;
export type InherentCo2Status = typeof inherentCo2Statuses[number];
