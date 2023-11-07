import { getTaskStatusByTaskCompletionState } from '@aviation/request-task/aer/shared/util/aer.util';
import { AerRequestTaskPayload } from '@aviation/request-task/store';
import produce from 'immer';

import { calculateAffectedFlightsPercentage } from '../util/data-gaps.util';

export function applySideEffectsAffectedFlightsPercentageToDataGaps(
  payload: AerRequestTaskPayload,
): AerRequestTaskPayload {
  const previousAerPayload = payload.aer;
  const status = getTaskStatusByTaskCompletionState('dataGaps', payload);

  if (['complete'].includes(status) && previousAerPayload?.dataGaps?.exist) {
    const newAffectedFlightsPercentage = calculateAffectedFlightsPercentage(
      previousAerPayload.aggregatedEmissionsData.aggregatedEmissionDataDetails,
      previousAerPayload.dataGaps.dataGaps,
    );

    return produce(payload, (draft) => {
      draft.aer.dataGaps.affectedFlightsPercentage = newAffectedFlightsPercentage;
    });
  }

  return payload;
}
