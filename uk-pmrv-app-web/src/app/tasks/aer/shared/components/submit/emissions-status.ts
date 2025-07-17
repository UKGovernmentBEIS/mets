import { TaskItemStatus } from '@shared/task-list/task-list.interface';

import { AerApplicationSubmitRequestTaskPayload } from 'pmrv-api';

export function sourceStreamEmissionStatus(taskKey, payload, sourceStreamEmissionIndex): TaskItemStatus {
  const sourceStreamEmissionValid = isSourceStreamEmissionValid(taskKey, payload, sourceStreamEmissionIndex);

  return sourceStreamEmissionValid
    ? payload.aerSectionsCompleted[taskKey]?.[sourceStreamEmissionIndex]
      ? 'complete'
      : 'in progress'
    : 'needs review';
}

export function isSourceStreamEmissionValid(taskKey, payload, sourceStreamEmissionIndex) {
  const sourceStreams = payload.aer?.sourceStreams;
  const emissionSources = payload.aer?.emissionSources;

  const sourceStreamEmission = (payload.aer?.monitoringApproachEmissions[taskKey] as any)?.sourceStreamEmissions?.[
    sourceStreamEmissionIndex
  ];

  const doesSourceStreamExist = !!sourceStreams?.find(
    (sourceStream) => sourceStream?.id === sourceStreamEmission?.sourceStream,
  );

  const doEmissionSourcesExist = sourceStreamEmission?.emissionSources?.every((id) =>
    emissionSources?.find((emissionSource) => emissionSource.id === id),
  );

  return doesSourceStreamExist && doEmissionSourcesExist;
}

export function getSourceStreamsStatus(taskKey, payload: AerApplicationSubmitRequestTaskPayload): TaskItemStatus {
  const sourceStreamEmissions = (payload.aer?.monitoringApproachEmissions?.[taskKey] as any)?.sourceStreamEmissions;

  const sourceStreamEmissionsStatuses =
    sourceStreamEmissions?.length > 0
      ? sourceStreamEmissions.map((_, index) => sourceStreamEmissionStatus(taskKey, payload, index))
      : [];

  return !payload.aerSectionsCompleted['emissionSources']?.[0] || !payload.aerSectionsCompleted['sourceStreams']?.[0]
    ? 'cannot start yet'
    : sourceStreamEmissionsStatuses.find((status) => status === 'needs review')
      ? 'needs review'
      : !!sourceStreamEmissionsStatuses.length && sourceStreamEmissionsStatuses.every((status) => status === 'complete')
        ? 'complete'
        : sourceStreamEmissionsStatuses.length
          ? 'in progress'
          : 'not started';
}

export function getCompletionStatus(taskKey, payload, index, status) {
  const calculation = payload.aer.monitoringApproachEmissions[taskKey] as any;

  const numOfSourceStreamEmissions = calculation?.sourceStreamEmissions?.length ?? 0;
  const numOfAerSectionsCompletedStatuses = payload.aerSectionsCompleted?.[taskKey]?.length ?? 0;

  return numOfSourceStreamEmissions === numOfAerSectionsCompletedStatuses
    ? updateAerSectionsCompletedArray(taskKey, payload, index, status)
    : initiateAerSectionsCompletedArray(numOfSourceStreamEmissions, index);
}

function updateAerSectionsCompletedArray(taskKey, payload, index, status) {
  const calculation = payload.aer.monitoringApproachEmissions[taskKey] as any;

  return calculation?.sourceStreamEmissions && calculation?.sourceStreamEmissions?.[index]
    ? payload.aerSectionsCompleted[taskKey]?.map((item, idx) => (index === idx ? status : item))
    : [...(payload.aerSectionsCompleted[taskKey] ?? []), false];
}

// when aer is created with prefilled source stream emissions, aerSectionsCompleted should be initiated
function initiateAerSectionsCompletedArray(numOfSourceStreamEmissions, index) {
  return Array(numOfSourceStreamEmissions === index ? numOfSourceStreamEmissions + 1 : numOfSourceStreamEmissions).fill(
    false,
  );
}
