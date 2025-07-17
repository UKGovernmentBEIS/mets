import { InherentCO2MonitoringApproach } from 'pmrv-api';

import { TaskItemStatus } from '../../../shared/task-list/task-list.interface';
import { PermitApplicationState } from '../../store/permit-application.state';

export function INHERENT_CO2Status(state: PermitApplicationState): TaskItemStatus {
  return state.permitSectionsCompleted?.INHERENT_CO2?.[0]
    ? 'complete'
    : (state.permit?.monitoringApproaches?.INHERENT_CO2 as InherentCO2MonitoringApproach)
          ?.inherentReceivingTransferringInstallations?.length > 0
      ? 'in progress'
      : 'not started';
}

export const inherentCo2Statuses = ['INHERENT_CO2'] as const;
export type InherentCo2Status = (typeof inherentCo2Statuses)[number];
