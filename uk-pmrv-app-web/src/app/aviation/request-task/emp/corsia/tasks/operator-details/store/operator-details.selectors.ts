import { map, OperatorFunction, pipe } from 'rxjs';

import { EmissionsMonitoringPlanCorsia, EmpCorsiaOperatorDetails } from 'pmrv-api';

import { RequestTaskState } from '../../../../../store';
import { empQuery } from '../../../../shared/emp.selectors';

const selectOperatorDetails: OperatorFunction<RequestTaskState, EmpCorsiaOperatorDetails> = pipe(
  empQuery.selectEmissionsMonitoringPlan,
  map((emp: unknown) => (emp as EmissionsMonitoringPlanCorsia)?.operatorDetails),
);
export const OperatorDetailsQuery = {
  selectOperatorDetails,
};
