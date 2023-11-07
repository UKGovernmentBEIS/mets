import { EmpRequestTaskPayloadCorsia } from '@aviation/request-task/store';
import produce from 'immer';

import { EmpEmissionsMonitoringApproachCorsia } from 'pmrv-api';

export function applySideEffectsToEmissionSources(
  payload: EmpRequestTaskPayloadCorsia,
  update: EmpEmissionsMonitoringApproachCorsia,
): EmpRequestTaskPayloadCorsia {
  const previousMonitoringApproach = payload.emissionsMonitoringPlan.emissionsMonitoringApproach.monitoringApproachType;

  if (previousMonitoringApproach === 'FUEL_USE_MONITORING' && update.monitoringApproachType !== 'FUEL_USE_MONITORING') {
    return produce(payload, (draft) => {
      draft.emissionsMonitoringPlan.emissionSources?.aircraftTypes?.forEach(
        (at) => delete at.fuelConsumptionMeasuringMethod,
      );

      delete draft.emissionsMonitoringPlan.emissionSources?.multipleFuelConsumptionMethodsExplanation;
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
