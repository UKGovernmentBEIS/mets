import { AerRequestTaskPayload } from '@aviation/request-task/store';
import produce from 'immer';

import { AviationAerCorsiaAggregatedEmissionsData } from 'pmrv-api';

/**
 * Clears 'aerSectionsCompleted.totalEmissionsCorsia' key on update of `aer.`
 */
export function applySideEffectsAggregatedFlightDataToTotalEmissions(
  payload: AerRequestTaskPayload,
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  update: AviationAerCorsiaAggregatedEmissionsData,
): AerRequestTaskPayload {
  return produce(payload, (draft) => {
    delete draft.aerSectionsCompleted.totalEmissionsCorsia;
  });
}
