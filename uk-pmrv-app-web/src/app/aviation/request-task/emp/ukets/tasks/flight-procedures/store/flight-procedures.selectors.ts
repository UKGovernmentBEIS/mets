import { map, OperatorFunction, pipe } from 'rxjs';

import { empQuery } from '@aviation/request-task/emp/shared/emp.selectors';
import { RequestTaskState } from '@aviation/request-task/store';

import { EmpFlightAndAircraftProcedures } from 'pmrv-api';

const selectFlightProcedures: OperatorFunction<RequestTaskState, EmpFlightAndAircraftProcedures> = pipe(
  empQuery.selectEmissionsMonitoringPlan,
  map((emp) => emp?.flightAndAircraftProcedures),
);
export const flightProceduresQuery = {
  selectFlightProcedures,
};
