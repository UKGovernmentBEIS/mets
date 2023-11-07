import { map, OperatorFunction, pipe } from 'rxjs';

import { AviationAerCorsiaEmissionsReductionClaim } from 'pmrv-api';

import { RequestTaskState } from '../../../../../store';
import { aerQuery } from '../../../../shared/aer.selectors';

const selectEmissionsReductionClaim: OperatorFunction<RequestTaskState, AviationAerCorsiaEmissionsReductionClaim> =
  pipe(
    aerQuery.selectAerCorsia,
    map((aer) => aer?.emissionsReductionClaim),
  );
export const emissionsReductionClaimQuery = {
  selectEmissionsReductionClaim,
};
