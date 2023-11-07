import { map, OperatorFunction, pipe } from 'rxjs';

import { EmpEmissionsMonitoringApproach, EmpManagementProcedures } from 'pmrv-api';

import { RequestTaskState } from '../../../../../store';
import { empQuery } from '../../../../shared/emp.selectors';

const selectMonitoringApproach: OperatorFunction<RequestTaskState, EmpEmissionsMonitoringApproach> = pipe(
  empQuery.selectEmissionsMonitoringPlan,
  map((emp) => emp?.emissionsMonitoringApproach),
);

const selectManagementProcedures: OperatorFunction<RequestTaskState, EmpManagementProcedures> = pipe(
  empQuery.selectEmissionsMonitoringPlan,
  map((emp) => emp?.managementProcedures),
);

export const monitoringApproachQuery = {
  selectMonitoringApproach,
  selectManagementProcedures,
};
