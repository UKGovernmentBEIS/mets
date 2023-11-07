import { TaskItemStatus } from '@shared/task-list/task-list.interface';

import { AerApplicationSubmitRequestTaskPayload, InherentCO2Emissions } from 'pmrv-api';

export function inherentCo2Status(payload: AerApplicationSubmitRequestTaskPayload): TaskItemStatus {
  const inherentInstallations = (payload?.aer?.monitoringApproachEmissions?.['INHERENT_CO2'] as InherentCO2Emissions)
    ?.inherentReceivingTransferringInstallations;
  const inherentSectionsCompleted = payload?.aerSectionsCompleted['INHERENT_CO2']?.[0];

  return inherentSectionsCompleted ? 'complete' : inherentInstallations?.length ? 'in progress' : 'not started';
}
