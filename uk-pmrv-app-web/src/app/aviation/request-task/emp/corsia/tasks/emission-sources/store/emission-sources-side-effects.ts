import { EmpRequestTaskPayloadCorsia } from '@aviation/request-task/store';
import { EmpSideEffectFn } from '@aviation/request-task/store/delegates/emp-corsia/emp-corsia-store-side-effects.handler';

import { EmpEmissionSourcesCorsia } from 'pmrv-api';

import { applySideEffectsFUMMSubsections } from './fumm-subsection.side-effects';

const sideEffectsToApply: EmpSideEffectFn<EmpEmissionSourcesCorsia>[] = [applySideEffectsFUMMSubsections];

export function aggregateEmissionSourcesSideEffects(
  payload: EmpRequestTaskPayloadCorsia,
  update: EmpEmissionSourcesCorsia,
): EmpRequestTaskPayloadCorsia {
  let newPayload = payload;
  for (const sideEffect of sideEffectsToApply) {
    newPayload = sideEffect(newPayload, update);
  }
  return newPayload;
}
