import { EmpRequestTaskPayloadUkEts } from '@aviation/request-task/store';
import produce from 'immer';

import { EmpEmissionsMonitoringApproach } from 'pmrv-api';

export function applySideEffectsToDataGaps(
  payload: EmpRequestTaskPayloadUkEts,
  update: EmpEmissionsMonitoringApproach,
): EmpRequestTaskPayloadUkEts {
  const previousMonitoringApproach = payload.emissionsMonitoringPlan.emissionsMonitoringApproach.monitoringApproachType;
  if (previousMonitoringApproach === 'FUEL_USE_MONITORING' && update.monitoringApproachType !== 'FUEL_USE_MONITORING') {
    return produce(payload, (draft) => {
      delete draft.emissionsMonitoringPlan.dataGaps;
      delete draft.empSectionsCompleted.dataGaps;

      delete draft.reviewSectionsCompleted?.DATA_GAPS;
      delete draft.reviewGroupDecisions?.DATA_GAPS;
    });
  }
  return payload;
}
