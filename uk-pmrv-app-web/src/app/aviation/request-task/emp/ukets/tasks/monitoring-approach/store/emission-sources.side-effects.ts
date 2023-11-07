import { EmpRequestTaskPayloadUkEts } from '@aviation/request-task/store';
import produce from 'immer';

import { EmpEmissionsMonitoringApproach } from 'pmrv-api';

export function applySideEffectsToEmissionSources(
  payload: EmpRequestTaskPayloadUkEts,
  update: EmpEmissionsMonitoringApproach,
): EmpRequestTaskPayloadUkEts {
  const previousMonitoringApproach = payload.emissionsMonitoringPlan.emissionsMonitoringApproach.monitoringApproachType;

  if (previousMonitoringApproach === 'FUEL_USE_MONITORING' && update.monitoringApproachType !== 'FUEL_USE_MONITORING') {
    return produce(payload, (draft) => {
      draft.emissionsMonitoringPlan.emissionSources?.aircraftTypes?.forEach(
        (at) => delete at.fuelConsumptionMeasuringMethod,
      );

      delete draft.emissionsMonitoringPlan.emissionSources?.multipleFuelConsumptionMethodsExplanation;
      delete draft.emissionsMonitoringPlan.emissionSources?.additionalAircraftMonitoringApproach;
    });
  }

  if (update.monitoringApproachType === 'FUEL_USE_MONITORING' && previousMonitoringApproach !== 'FUEL_USE_MONITORING') {
    return produce(payload, (draft) => {
      const emissionSourcesExist = draft.empSectionsCompleted?.emissionSources;

      if (emissionSourcesExist && emissionSourcesExist[0] === true) {
        draft.empSectionsCompleted.emissionSources = [false];
      }
    });
  }

  return payload;
}
