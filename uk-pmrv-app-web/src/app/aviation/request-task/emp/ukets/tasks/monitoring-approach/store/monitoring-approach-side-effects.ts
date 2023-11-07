import { EmpEmissionsMonitoringApproach } from 'pmrv-api';

import { EmpRequestTaskPayloadUkEts } from '../../../../../store';
import { EmpSideEffectFn } from '../../../../../store/delegates';
import { applySideEffectsToDataGaps } from './data-gaps.side-effects';
import { applySideEffectsToEmissionSources } from './emission-sources.side-effects';
import { applySideEffectsFUMMSubsections } from './fumm-subsection.side-effects';
import { applySideEffectsToManagementProcedures } from './management-procedures.side-effects';

const sideEffectsToApply: EmpSideEffectFn<EmpEmissionsMonitoringApproach>[] = [
  applySideEffectsToManagementProcedures,
  applySideEffectsToEmissionSources,
  applySideEffectsFUMMSubsections,
  applySideEffectsToDataGaps,
];

export function aggregateMonitoringApproachSideEffects(
  payload: EmpRequestTaskPayloadUkEts,
  update: EmpEmissionsMonitoringApproach,
): EmpRequestTaskPayloadUkEts {
  let newPayload = payload;
  for (const sideEffect of sideEffectsToApply) {
    newPayload = sideEffect(newPayload, update);
  }
  return newPayload;
}
