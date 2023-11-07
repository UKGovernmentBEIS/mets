import { InjectionToken } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';

import { AerApplicationSubmitRequestTaskPayload } from 'pmrv-api';

export const AER_APPROACHES_FORM = new InjectionToken<UntypedFormGroup>('Aer approaches form');

export function buildTaskData(taskKey, payload: AerApplicationSubmitRequestTaskPayload, sourceStreamEmissions) {
  const monitoringApproachEmissions = payload.aer.monitoringApproachEmissions;
  const approach = monitoringApproachEmissions[taskKey] as any;

  const data = {
    monitoringApproachEmissions: {
      ...monitoringApproachEmissions,
      [taskKey]: {
        ...approach,
        type: taskKey,
        sourceStreamEmissions: sourceStreamEmissions,
      },
    },
  };

  return data;
}

export function areCalculationsTiersExtraConditionsMet(parameterMonitoringTiers): boolean {
  const netCalorificValue = parameterMonitoringTiers?.find((stream) => stream.type === 'NET_CALORIFIC_VALUE');
  const emissionFactor = parameterMonitoringTiers?.find((stream) => stream.type === 'EMISSION_FACTOR');
  const oxidationFactor = parameterMonitoringTiers?.find((stream) => stream.type === 'OXIDATION_FACTOR');

  return !!(
    netCalorificValue?.tier === 'TIER_2A' &&
    (emissionFactor?.tier === 'TIER_2A' || emissionFactor?.tier === 'TIER_2') &&
    !!oxidationFactor
  );
}
