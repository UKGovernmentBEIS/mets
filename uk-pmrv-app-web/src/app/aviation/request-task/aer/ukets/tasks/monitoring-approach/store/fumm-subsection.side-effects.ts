import { AerRequestTaskPayload } from '@aviation/request-task/store';
import produce from 'immer';

import { AviationAerEmissionsMonitoringApproach } from 'pmrv-api';

export function applySideEffectsFUMMToDataGaps(
  payload: AerRequestTaskPayload,
  update: AviationAerEmissionsMonitoringApproach,
): AerRequestTaskPayload {
  const previousMonitoringApproach = payload.aer?.monitoringApproach.monitoringApproachType;

  if (previousMonitoringApproach === 'FUEL_USE_MONITORING' && update.monitoringApproachType !== 'FUEL_USE_MONITORING') {
    return produce(payload, (draft) => {
      delete draft.aer.dataGaps;
      delete draft.aerSectionsCompleted.dataGaps;
    });
  }

  return payload;
}
