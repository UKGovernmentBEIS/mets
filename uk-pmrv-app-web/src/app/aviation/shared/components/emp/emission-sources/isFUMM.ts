import { EmpRequestActionPayload } from '@aviation/request-action/store';
import { EmpRequestTaskPayloadUkEts } from '@aviation/request-task/store';

import {
  EmissionsMonitoringPlanCorsia,
  EmissionsMonitoringPlanUkEts,
  EmpEmissionsMonitoringApproach,
  EmpEmissionsMonitoringApproachCorsia,
} from 'pmrv-api';

import { EmpIssuanceApplicationSubmitRequestTaskPayload } from '../../../types';

/** FUMM workflow refers to a specific workflow in emission source that is triggered when a
 user selects "User fuel use monitoring" in the monitoring approach emp task
 @returns true if emissionsMonitoringApproach === 'FUEL_USE_MONITORING'
 */
export function isFUMM(
  payload:
    | EmpRequestTaskPayloadUkEts
    | EmpRequestActionPayload
    | EmpIssuanceApplicationSubmitRequestTaskPayload
    | undefined,
): boolean {
  return isFuelUseMonitoringExist(payload?.emissionsMonitoringPlan);
}

export function isFuelUseMonitoringExist(
  monitoringPlan: EmissionsMonitoringPlanUkEts | EmissionsMonitoringPlanCorsia,
): boolean {
  return extractMonitorinApproachType(monitoringPlan) === 'FUEL_USE_MONITORING';
}

type EmpMonitoringApproachType =
  | EmpEmissionsMonitoringApproachCorsia['monitoringApproachType']
  | EmpEmissionsMonitoringApproach['monitoringApproachType'];

// the need for this function should be removed. Ukets and Corsia should use emissionsMonitoringApproach key only
export function extractMonitorinApproachType(
  monitoringPlan: EmissionsMonitoringPlanUkEts | EmissionsMonitoringPlanCorsia,
): EmpMonitoringApproachType | undefined {
  const monitorinApproach = monitoringPlan?.['emissionsMonitoringApproach'];
  return monitorinApproach?.monitoringApproachType;
}
