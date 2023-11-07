import { EmpRequestTaskPayloadCorsia } from '@aviation/request-task/store';
import { EmpSideEffectFn } from '@aviation/request-task/store/delegates/emp-corsia/emp-corsia-store-side-effects.handler';

import { EmpEmissionsMonitoringApproachCorsia } from 'pmrv-api';

import { applySideEffectsDataGapsSubsections } from './data-gaps.side-effects';
import { applySideEffectsToEmissionSources } from './emission-sources.side-effects';
import { applySideEffectsFUMMSubsections } from './fumm-subsection.side-effects';

const sideEffectsToApply: EmpSideEffectFn<EmpEmissionsMonitoringApproachCorsia>[] = [
  applySideEffectsToEmissionSources,
  applySideEffectsFUMMSubsections,
  applySideEffectsDataGapsSubsections,
];

export function aggregateMonitoringApproachSideEffects(
  payload: EmpRequestTaskPayloadCorsia,
  update: EmpEmissionsMonitoringApproachCorsia,
): EmpRequestTaskPayloadCorsia {
  let newPayload = payload;

  for (const sideEffect of sideEffectsToApply) {
    newPayload = sideEffect(newPayload, update);
  }

  return newPayload;
}
