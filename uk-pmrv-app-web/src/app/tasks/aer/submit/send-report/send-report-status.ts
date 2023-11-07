import { TaskItemStatus } from '@shared/task-list/task-list.interface';
import { getAvailableSections, getSectionStatus } from '@tasks/aer/core/aer-task-statuses';

import { AerApplicationSubmitRequestTaskPayload, RequestTaskPayload } from 'pmrv-api';

export function sendReportStatus(payload: RequestTaskPayload): TaskItemStatus {
  return payload.payloadType === 'AER_APPLICATION_SUBMIT_PAYLOAD' ||
    payload.payloadType === 'AER_APPLICATION_AMENDS_SUBMIT_PAYLOAD'
    ? getAvailableSections(payload).every((key) => {
        const monitoringApproachEmissions =
          (payload as AerApplicationSubmitRequestTaskPayload).aer?.monitoringApproachEmissions ?? [];

        let isSectionCompleted = false;

        switch (key) {
          case 'emissionPoints': {
            isSectionCompleted =
              monitoringApproachEmissions['MEASUREMENT_CO2'] !== undefined ||
              monitoringApproachEmissions['MEASUREMENT_N2O'] !== undefined
                ? getSectionStatus(key, payload as AerApplicationSubmitRequestTaskPayload) === 'complete'
                : true;
            break;
          }
          case 'MEASUREMENT_CO2':
          case 'MEASUREMENT_N2O':
          case 'CALCULATION_CO2':
          case 'CALCULATION_PFC':
          case 'INHERENT_CO2':
          case 'FALLBACK':
            isSectionCompleted =
              monitoringApproachEmissions[key] !== undefined
                ? getSectionStatus(key, payload as AerApplicationSubmitRequestTaskPayload) === 'complete'
                : true;
            break;
          default:
            isSectionCompleted =
              getSectionStatus(key, payload as AerApplicationSubmitRequestTaskPayload) === 'complete';
        }

        return isSectionCompleted;
      })
      ? 'not started'
      : 'cannot start yet'
    : 'complete';
}
