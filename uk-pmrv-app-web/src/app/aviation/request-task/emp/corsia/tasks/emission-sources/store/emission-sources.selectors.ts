import { map, OperatorFunction, pipe } from 'rxjs';

import { RequestTaskState } from '@aviation/request-task/store';

import { EmpEmissionSources } from 'pmrv-api';

import { empQuery } from '../../../../shared/emp.selectors';

const selectEmissionSources: OperatorFunction<RequestTaskState, EmpEmissionSources> = pipe(
  empQuery.selectEmissionsMonitoringPlan,
  map((emp) => emp?.emissionSources),
);

export const emissionSourcesQuery = {
  selectEmissionSources,
};
