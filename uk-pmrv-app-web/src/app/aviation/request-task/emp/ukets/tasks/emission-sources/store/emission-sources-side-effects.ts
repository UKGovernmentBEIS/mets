import { EmpRequestTaskPayloadUkEts } from '@aviation/request-task/store';
import { EmpSideEffectFn } from '@aviation/request-task/store/delegates';

import { EmpEmissionSources } from 'pmrv-api';

import { applySideEffectsFUMMSubsections } from './fumm-subsection.side-effects';

const sideEffectsToApply: EmpSideEffectFn<EmpEmissionSources>[] = [applySideEffectsFUMMSubsections];

export function aggregateEmissionSourcesSideEffects(
  payload: EmpRequestTaskPayloadUkEts,
  update: EmpEmissionSources,
): EmpRequestTaskPayloadUkEts {
  let newPayload = payload;
  for (const sideEffect of sideEffectsToApply) {
    newPayload = sideEffect(newPayload, update);
  }
  return newPayload;
}
