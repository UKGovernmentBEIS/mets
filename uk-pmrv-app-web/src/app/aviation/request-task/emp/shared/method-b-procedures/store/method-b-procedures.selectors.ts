import { map, OperatorFunction, pipe } from 'rxjs';

import { RequestTaskState } from '@aviation/request-task/store';

import { EmpMethodBProcedures } from 'pmrv-api';

import { empQuery } from '../../emp.selectors';

const selectMethodBProcedures: OperatorFunction<RequestTaskState, EmpMethodBProcedures> = pipe(
  empQuery.selectEmissionsMonitoringPlan,
  map((emp) => emp?.methodBProcedures),
);
export const methodBProceduresQuery = {
  selectFummMethodB: selectMethodBProcedures,
};
