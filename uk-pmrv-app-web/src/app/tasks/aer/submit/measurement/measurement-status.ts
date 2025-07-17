import { InjectionToken } from '@angular/core';
import { UntypedFormGroup } from '@angular/forms';

import { TaskItemStatus } from '@shared/task-list/task-list.interface';

import { AerApplicationSubmitRequestTaskPayload } from 'pmrv-api';

export const AER_MEASUREMENT_FORM = new InjectionToken<UntypedFormGroup>('Aer measurement form');

export function emissionPointEmissionsStatus(taskKey, payload, emissionPointEmissionsIndex): TaskItemStatus {
  const emissionPointEmissionsValid = isEmissionPointEmissionsValid(taskKey, payload, emissionPointEmissionsIndex);

  return emissionPointEmissionsValid
    ? payload.aerSectionsCompleted[taskKey]?.[emissionPointEmissionsIndex]
      ? 'complete'
      : 'in progress'
    : 'needs review';
}

export function isEmissionPointEmissionsValid(taskKey, payload, emissionPointEmissionsIndex) {
  const emissionPoints = payload.aer?.emissionPoints;
  const sourceStreams = payload.aer?.sourceStreams;
  const emissionSources = payload.aer?.emissionSources;

  const emissionPointEmission = (payload.aer?.monitoringApproachEmissions[taskKey] as any)?.emissionPointEmissions?.[
    emissionPointEmissionsIndex
  ];

  const doesEmissionPointExist = !!emissionPoints?.find(
    (emissionPoint) => emissionPoint?.id === emissionPointEmission?.emissionPoint,
  );

  const doSourceStreamsExist = emissionPointEmission?.sourceStreams?.every((id) =>
    sourceStreams?.find((sourceStream) => sourceStream.id === id),
  );

  const doEmissionSourcesExist = emissionPointEmission?.emissionSources?.every((id) =>
    emissionSources?.find((emissionSource) => emissionSource.id === id),
  );

  return doesEmissionPointExist && doSourceStreamsExist && doEmissionSourcesExist;
}

export function getMeasurementStatus(taskKey, payload: AerApplicationSubmitRequestTaskPayload): TaskItemStatus {
  const emissionPointEmissions = (payload.aer?.monitoringApproachEmissions[taskKey] as any)?.emissionPointEmissions;

  const emissionPointEmissionsStatuses =
    emissionPointEmissions?.length > 0
      ? emissionPointEmissions.map((_, index) => emissionPointEmissionsStatus(taskKey, payload, index))
      : [];

  return !payload.aerSectionsCompleted['emissionSources']?.[0] ||
    !payload.aerSectionsCompleted['sourceStreams']?.[0] ||
    !payload.aerSectionsCompleted['emissionPoints']?.[0]
    ? 'cannot start yet'
    : emissionPointEmissionsStatuses.find((status) => status === 'needs review')
      ? 'needs review'
      : !!emissionPointEmissionsStatuses.length &&
          emissionPointEmissionsStatuses.every((status) => status === 'complete')
        ? 'complete'
        : emissionPointEmissionsStatuses.length
          ? 'in progress'
          : 'not started';
}

export function getCompletionStatus(taskKey, payload, index, status) {
  const measurement = payload.aer.monitoringApproachEmissions[taskKey] as any;

  const numOfEmissionPointEmissions = measurement?.emissionPointEmissions?.length ?? 0;
  const numOfAerSectionsCompletedStatuses = payload.aerSectionsCompleted?.[taskKey]?.length ?? 0;

  return numOfEmissionPointEmissions === numOfAerSectionsCompletedStatuses
    ? updateAerSectionsCompletedArray(taskKey, payload, index, status)
    : initiateAerSectionsCompletedArray(numOfEmissionPointEmissions, index);
}

function updateAerSectionsCompletedArray(taskKey, payload, index, status) {
  const measurement = payload.aer.monitoringApproachEmissions[taskKey] as any;

  return measurement?.emissionPointEmissions && measurement?.emissionPointEmissions?.[index]
    ? payload.aerSectionsCompleted[taskKey]?.map((item, idx) => (index === idx ? status : item))
    : [...(payload.aerSectionsCompleted[taskKey] ?? []), false];
}

// when aer is created with prefilled emission point emissions, aerSectionsCompleted should be initiated
function initiateAerSectionsCompletedArray(numOfEmissionPointEmissions, index) {
  return Array(
    numOfEmissionPointEmissions === index ? numOfEmissionPointEmissions + 1 : numOfEmissionPointEmissions,
  ).fill(false);
}
