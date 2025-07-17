import { AerRequestTaskPayload } from '@aviation/request-task/store';
import { AerSideEffectFn } from '@aviation/request-task/store/delegates/aer/aer.utils';

import { AviationAerDataGaps } from 'pmrv-api';

import { applySideEffectsAffectedFlightsPercentageToDataGaps } from './recalculate-side-effects';

const sideEffectsToApply: AerSideEffectFn<AviationAerDataGaps>[] = [
  applySideEffectsAffectedFlightsPercentageToDataGaps,
];

export function aerDataGapsSideEffects(
  payload: AerRequestTaskPayload,
  update: AviationAerDataGaps,
): AerRequestTaskPayload {
  let newPayload = payload;

  for (const sideEffect of sideEffectsToApply) {
    newPayload = sideEffect(newPayload, update);
  }

  return newPayload;
}
