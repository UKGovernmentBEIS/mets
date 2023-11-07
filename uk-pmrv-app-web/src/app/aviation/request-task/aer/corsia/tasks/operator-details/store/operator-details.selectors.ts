import { map, OperatorFunction, pipe } from 'rxjs';

import { AviationCorsiaOperatorDetails } from 'pmrv-api';

import { RequestTaskState } from '../../../../../store';
import { aerQuery } from '../../../../shared/aer.selectors';

const selectOperatorDetails: OperatorFunction<RequestTaskState, AviationCorsiaOperatorDetails> = pipe(
  aerQuery.selectAerCorsia,
  map((aer) => aer?.operatorDetails),
);
export const OperatorDetailsQuery = {
  selectOperatorDetails,
};
