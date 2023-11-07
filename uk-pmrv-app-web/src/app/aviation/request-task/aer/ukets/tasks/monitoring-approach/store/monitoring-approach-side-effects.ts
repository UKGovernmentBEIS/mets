import { AerRequestTaskPayload } from '@aviation/request-task/store';
import { AerSideEffectFn } from '@aviation/request-task/store/delegates/aer/aer-store-side-effects.handler';

import { AviationAerEmissionsMonitoringApproach } from 'pmrv-api';

import { applySideEffectsFUMMToDataGaps } from './fumm-subsection.side-effects';

const sideEffectsToApply: AerSideEffectFn<AviationAerEmissionsMonitoringApproach>[] = [applySideEffectsFUMMToDataGaps];

export function aerMonitoringApproachSideEffects(
  payload: AerRequestTaskPayload,
  update: AviationAerEmissionsMonitoringApproach,
): AerRequestTaskPayload {
  let newPayload = payload;

  for (const sideEffect of sideEffectsToApply) {
    newPayload = sideEffect(newPayload, update);
  }

  return newPayload;
}
