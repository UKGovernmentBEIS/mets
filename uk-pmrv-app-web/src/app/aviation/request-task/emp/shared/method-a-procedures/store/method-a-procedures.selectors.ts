import { map, OperatorFunction, pipe } from 'rxjs';

import { RequestTaskState } from '@aviation/request-task/store';

import { EmpMethodAProcedures } from 'pmrv-api';

import { empQuery } from '../../emp.selectors';

const selectMethodAProcedures: OperatorFunction<RequestTaskState, EmpMethodAProcedures> = pipe(
  empQuery.selectEmissionsMonitoringPlan,
  map((emp) => emp?.methodAProcedures),
);
export const methodAProceduresQuery = {
  selectFummMethodA: selectMethodAProcedures,
};
