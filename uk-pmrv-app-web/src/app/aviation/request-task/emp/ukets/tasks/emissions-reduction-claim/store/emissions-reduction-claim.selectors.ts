import { map, OperatorFunction, pipe } from 'rxjs';

import { EmpEmissionsReductionClaim } from 'pmrv-api';

import { RequestTaskState } from '../../../../../store';
import { empQuery } from '../../../../shared/emp.selectors';

const selectEmissionsReductionClaim: OperatorFunction<RequestTaskState, EmpEmissionsReductionClaim> = pipe(
  empQuery.selectEmissionsMonitoringPlan,
  map((emp) => emp?.emissionsReductionClaim),
);
export const emissionsReductionClaimQuery = {
  selectEmissionsReductionClaim,
};
