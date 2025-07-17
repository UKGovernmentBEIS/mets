import { AerRequestTaskPayload } from '@aviation/request-task/store';
import { AerSideEffectFn } from '@aviation/request-task/store/delegates/aer/aer.utils';

import { AviationAerCorsiaEmissionsReductionClaim } from 'pmrv-api';

import { applySideEffectsEmissionsReductionToTotalEmissions } from './emissions-reduction-total-emissions.side-effects';

const sideEffectsToApply: AerSideEffectFn<AviationAerCorsiaEmissionsReductionClaim>[] = [
  applySideEffectsEmissionsReductionToTotalEmissions,
];

export function aerCorsiaEmissionsReductionClaimSideEffects(
  payload: AerRequestTaskPayload,
  update: AviationAerCorsiaEmissionsReductionClaim,
): AerRequestTaskPayload {
  let newPayload = payload;

  for (const sideEffect of sideEffectsToApply) {
    newPayload = sideEffect(newPayload, update);
  }

  return newPayload;
}
