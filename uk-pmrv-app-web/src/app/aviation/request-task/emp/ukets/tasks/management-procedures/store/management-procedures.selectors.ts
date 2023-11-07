import { map, OperatorFunction, pipe } from 'rxjs';

import { EmpManagementProcedures } from 'pmrv-api';

import { RequestTaskState } from '../../../../../store';
import { empQuery } from '../../../../shared/emp.selectors';

const selectManagementProcedures: OperatorFunction<RequestTaskState, EmpManagementProcedures> = pipe(
  empQuery.selectEmissionsMonitoringPlan,
  map((emp) => emp?.managementProcedures),
);

export const managementProceduresQuery = {
  selectManagementProcedures,
};
