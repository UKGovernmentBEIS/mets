import { TaskItemStatus } from '@shared/task-list/task-list.interface';

import { AerApplicationSubmitRequestTaskPayload, FallbackEmissions } from 'pmrv-api';

export function fallbackStatus(payload: AerApplicationSubmitRequestTaskPayload): TaskItemStatus {
  const sourceStreamEmissions = (payload.aer?.monitoringApproachEmissions?.FALLBACK as FallbackEmissions)
    ?.sourceStreams;
  const aerSectionsCompleted = payload?.aerSectionsCompleted;
  const sourceStreams = payload.aer?.sourceStreams;
  const sourceStreamsExist = sourceStreamEmissions?.every((id) =>
    sourceStreams?.find((sourceStream) => sourceStream.id === id),
  );

  return !aerSectionsCompleted['sourceStreams']?.[0]
    ? 'cannot start yet'
    : !sourceStreamsExist && sourceStreamEmissions?.length
    ? 'needs review'
    : !!sourceStreamEmissions?.length && aerSectionsCompleted?.['FALLBACK']?.[0]
    ? 'complete'
    : sourceStreamEmissions?.length
    ? 'in progress'
    : 'not started';
}
