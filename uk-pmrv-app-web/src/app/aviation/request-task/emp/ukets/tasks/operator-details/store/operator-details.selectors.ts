import { map, OperatorFunction, pipe } from 'rxjs';

import { EmpOperatorDetails } from 'pmrv-api';

import { RequestTaskState } from '../../../../../store';
import { empQuery } from '../../../../shared/emp.selectors';

const selectOperatorDetails: OperatorFunction<RequestTaskState, EmpOperatorDetails> = pipe(
  empQuery.selectEmissionsMonitoringPlan,
  map((emp) => emp?.operatorDetails),
);
export const OperatorDetailsQuery = {
  selectOperatorDetails,
};
