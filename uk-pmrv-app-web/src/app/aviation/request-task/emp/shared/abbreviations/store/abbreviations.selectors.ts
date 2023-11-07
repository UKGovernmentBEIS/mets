import { map, OperatorFunction, pipe } from 'rxjs';

import { EmpAbbreviations } from 'pmrv-api';

import { RequestTaskState } from '../../../../store';
import { empQuery } from '../../../shared/emp.selectors';

const selectAbbreviations: OperatorFunction<RequestTaskState, EmpAbbreviations> = pipe(
  empQuery.selectEmissionsMonitoringPlan,
  map((emp) => emp?.abbreviations),
);
export const abbreviationsQuery = {
  selectAbbreviations,
};
