import { map, OperatorFunction, pipe } from 'rxjs';

import { RequestTaskState } from '@aviation/request-task/store';

import { EmpFuelUpliftMethodProcedures } from 'pmrv-api';

import { empQuery } from '../../emp.selectors';

const selectFuelUpliftProcedures: OperatorFunction<RequestTaskState, EmpFuelUpliftMethodProcedures> = pipe(
  empQuery.selectEmissionsMonitoringPlan,
  map((emp) => emp?.fuelUpliftMethodProcedures),
);

export const fuelUpliftProceduresQuery = {
  selectFuelUpliftProcedures,
};
