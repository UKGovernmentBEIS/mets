import { AerRequestTaskPayload } from '@aviation/request-task/store';
import { AerSideEffectFn } from '@aviation/request-task/store/delegates/aer/aer-store-side-effects.handler';

import { AviationAerCorsiaAggregatedEmissionsData } from 'pmrv-api';

import { applySideEffectsAggregatedFlightDataToTotalEmissions } from './aggregated-flight-data-total-emissions.side-effects';

const sideEffectsToApply: AerSideEffectFn<AviationAerCorsiaAggregatedEmissionsData>[] = [
  applySideEffectsAggregatedFlightDataToTotalEmissions,
];

export function aerCorsiaAggregatedConsumptionFlightDataClaimSideEffects(
  payload: AerRequestTaskPayload,
  update: AviationAerCorsiaAggregatedEmissionsData,
): AerRequestTaskPayload {
  let newPayload = payload;

  for (const sideEffect of sideEffectsToApply) {
    newPayload = sideEffect(newPayload, update);
  }

  return newPayload;
}
