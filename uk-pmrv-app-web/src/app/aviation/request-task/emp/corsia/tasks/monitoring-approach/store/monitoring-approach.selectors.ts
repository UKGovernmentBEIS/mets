import { map, OperatorFunction, pipe } from 'rxjs';

import { empCorsiaQuery } from '@aviation/request-task/emp/shared/emp-corsia.selectors';

import { EmpEmissionsMonitoringApproachCorsia } from 'pmrv-api';

import { RequestTaskState } from '../../../../../store';

const selectMonitoringApproachCorsia: OperatorFunction<RequestTaskState, EmpEmissionsMonitoringApproachCorsia> = pipe(
  empCorsiaQuery.selectEmissionsMonitoringPlanCorsia,
  map((emp) => emp?.emissionsMonitoringApproach),
);

export const monitoringApproachCorsiaQuery = {
  selectMonitoringApproachCorsia,
};
