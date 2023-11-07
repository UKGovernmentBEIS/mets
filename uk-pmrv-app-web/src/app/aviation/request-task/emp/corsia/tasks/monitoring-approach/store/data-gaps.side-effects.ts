import { EmpRequestTaskPayloadCorsia } from '@aviation/request-task/store';
import produce from 'immer';

import { EmpEmissionsMonitoringApproachCorsia } from 'pmrv-api';

export function applySideEffectsDataGapsSubsections(
  payload: EmpRequestTaskPayloadCorsia,
  update: EmpEmissionsMonitoringApproachCorsia,
): EmpRequestTaskPayloadCorsia {
  const previousMonitoringApproach = payload.emissionsMonitoringPlan.emissionsMonitoringApproach.monitoringApproachType;

  if (previousMonitoringApproach !== update.monitoringApproachType) {
    return produce(payload, (draft) => {
      delete draft.emissionsMonitoringPlan.dataGaps?.dataGaps;
      delete draft.emissionsMonitoringPlan.dataGaps?.secondaryDataSources;
      delete draft.empSectionsCompleted.dataGaps;
    });
  }

  return payload;
}
