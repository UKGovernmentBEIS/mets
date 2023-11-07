import { AerApplicationSubmitRequestTaskPayload } from 'pmrv-api';

export function buildTaskData(taskKey, payload: AerApplicationSubmitRequestTaskPayload, emissionPointEmissions) {
  const monitoringApproachEmissions = payload.aer.monitoringApproachEmissions;
  const calculation = monitoringApproachEmissions[taskKey] as any;

  const data = {
    monitoringApproachEmissions: {
      ...monitoringApproachEmissions,
      [taskKey]: {
        ...calculation,
        type: taskKey,
        emissionPointEmissions: emissionPointEmissions,
      },
    },
  };

  return data;
}
