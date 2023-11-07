import { EmpRequestTaskPayloadUkEts } from '@aviation/request-task/store';
import produce from 'immer';

import { EmpEmissionsMonitoringApproach } from 'pmrv-api';

export function applySideEffectsFUMMSubsections(
  payload: EmpRequestTaskPayloadUkEts,
  update: EmpEmissionsMonitoringApproach,
): EmpRequestTaskPayloadUkEts {
  const previousMonitoringApproach = payload.emissionsMonitoringPlan.emissionsMonitoringApproach.monitoringApproachType;
  if (previousMonitoringApproach === 'FUEL_USE_MONITORING' && update.monitoringApproachType !== 'FUEL_USE_MONITORING') {
    return produce(payload, (draft) => {
      delete draft.emissionsMonitoringPlan.methodAProcedures;
      delete draft.empSectionsCompleted.methodAProcedures;

      delete draft.emissionsMonitoringPlan.methodBProcedures;
      delete draft.empSectionsCompleted.methodBProcedures;

      delete draft.emissionsMonitoringPlan.blockOnBlockOffMethodProcedures;
      delete draft.empSectionsCompleted.blockOnBlockOffMethodProcedures;

      delete draft.emissionsMonitoringPlan.fuelUpliftMethodProcedures;
      delete draft.empSectionsCompleted.fuelUpliftMethodProcedures;

      delete draft.emissionsMonitoringPlan.blockHourMethodProcedures;
      delete draft.empSectionsCompleted.blockHourMethodProcedures;
    });
  }
  return payload;
}
