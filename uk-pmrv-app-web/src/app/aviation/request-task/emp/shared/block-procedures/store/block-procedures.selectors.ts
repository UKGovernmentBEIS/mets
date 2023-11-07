import { map, OperatorFunction, pipe } from 'rxjs';

import { RequestTaskState } from '@aviation/request-task/store';

import { EmpBlockOnBlockOffMethodProcedures } from 'pmrv-api';

import { empQuery } from '../../emp.selectors';

const selectBlockProcedures: OperatorFunction<
  RequestTaskState,
  EmpBlockOnBlockOffMethodProcedures['fuelConsumptionPerFlight']
> = pipe(
  empQuery.selectEmissionsMonitoringPlan,
  map((emp) => emp?.blockOnBlockOffMethodProcedures?.fuelConsumptionPerFlight),
);

export const blockProceduresQuery = {
  selectBlockProcedures,
};
