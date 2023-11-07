import { map, OperatorFunction, pipe } from 'rxjs';

import { RequestTaskState } from '@aviation/request-task/store';

import { EmpBlockHourMethodProcedures } from 'pmrv-api';

import { empQuery } from '../../emp.selectors';

const selectBlockHourProcedures: OperatorFunction<RequestTaskState, EmpBlockHourMethodProcedures> = pipe(
  empQuery.selectEmissionsMonitoringPlan,
  map((emp) => emp?.blockHourMethodProcedures),
);

export const blockHourProceduresQuery = {
  selectBlockHourProcedures,
};
