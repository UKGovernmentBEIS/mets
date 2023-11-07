import { AerRequestTaskPayload } from '@aviation/request-task/store';
import produce from 'immer';

import { AviationAerCorsiaEmissionsReductionClaim } from 'pmrv-api';

/**
 * Clears 'aerSectionsCompleted.totalEmissionsCorsia' key on update of `aer.aggregatedEmissionsData`
 */
export function applySideEffectsEmissionsReductionToTotalEmissions(
  payload: AerRequestTaskPayload,
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  update: AviationAerCorsiaEmissionsReductionClaim,
): AerRequestTaskPayload {
  return produce(payload, (draft) => {
    delete draft.aerSectionsCompleted.totalEmissionsCorsia;
  });
}
