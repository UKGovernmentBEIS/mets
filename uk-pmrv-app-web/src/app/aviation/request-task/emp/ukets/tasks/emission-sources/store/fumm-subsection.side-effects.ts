import { EmpRequestTaskPayloadUkEts } from '@aviation/request-task/store';
import { FuelConsumptionMeasuringMethods } from '@aviation/shared/components/emp/emission-sources/aircraft-type/fuel-consumption-measuring-methods';
import produce from 'immer';

import { EmpEmissionSources } from 'pmrv-api';

export function applySideEffectsFUMMSubsections(
  payload: EmpRequestTaskPayloadUkEts,
  update: EmpEmissionSources,
): EmpRequestTaskPayloadUkEts {
  const previousAircraftTypes = payload.emissionsMonitoringPlan.emissionSources.aircraftTypes;
  const currentAircraftTypes = update.aircraftTypes;
  let payloadResult: EmpRequestTaskPayloadUkEts = payload;

  FuelConsumptionMeasuringMethods.forEach((method) => {
    const hadMethod = previousAircraftTypes?.some((type) => type.fuelConsumptionMeasuringMethod === method.value);

    const hasMethod = currentAircraftTypes?.some((type) => type.fuelConsumptionMeasuringMethod === method.value);

    if (hadMethod && !hasMethod) {
      payloadResult = produce(payload, (draft) => {
        if (method.value === 'METHOD_A') {
          delete draft.emissionsMonitoringPlan.methodAProcedures;
          delete draft.empSectionsCompleted.methodAProcedures;
        } else if (method.value === 'METHOD_B') {
          delete draft.emissionsMonitoringPlan.methodBProcedures;
          delete draft.empSectionsCompleted.methodBProcedures;
        } else if (method.value === 'BLOCK_ON_BLOCK_OFF') {
          delete draft.emissionsMonitoringPlan.blockOnBlockOffMethodProcedures;
          delete draft.empSectionsCompleted.blockOnBlockOffMethodProcedures;
        } else if (method.value === 'FUEL_UPLIFT') {
          delete draft.emissionsMonitoringPlan.fuelUpliftMethodProcedures;
          delete draft.empSectionsCompleted.fuelUpliftMethodProcedures;
        } else if (method.value === 'BLOCK_HOUR') {
          delete draft.emissionsMonitoringPlan.blockHourMethodProcedures;
          delete draft.empSectionsCompleted.blockHourMethodProcedures;
        }
      });
    }
  });

  const previousStateNotBlockOnBlockOff = previousAircraftTypes?.some(
    (type) => type.fuelConsumptionMeasuringMethod !== 'BLOCK_ON_BLOCK_OFF',
  );

  const currentStateNotBlockOnBlockOff = currentAircraftTypes?.some(
    (type) => type.fuelConsumptionMeasuringMethod !== 'BLOCK_ON_BLOCK_OFF',
  );

  if (currentStateNotBlockOnBlockOff) {
    if (!previousStateNotBlockOnBlockOff) {
      payloadResult = produce(payloadResult, (draft) => {
        if (draft.empSectionsCompleted.managementProcedures) {
          draft.empSectionsCompleted.managementProcedures = [false];
        }
        delete draft.reviewSectionsCompleted?.MANAGEMENT_PROCEDURES;
      });
    }
  } else {
    if (previousStateNotBlockOnBlockOff) {
      payloadResult = produce(payloadResult, (draft) => {
        delete draft.emissionsMonitoringPlan.managementProcedures?.upliftQuantityCrossChecks;
      });
    }
  }

  return payloadResult;
}
