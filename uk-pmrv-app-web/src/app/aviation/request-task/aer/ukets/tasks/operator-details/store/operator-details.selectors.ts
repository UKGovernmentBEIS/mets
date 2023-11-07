import { map, OperatorFunction, pipe } from 'rxjs';

import { AviationOperatorDetails } from 'pmrv-api';

import { RequestTaskState } from '../../../../../store';
import { aerQuery } from '../../../../shared/aer.selectors';

const selectOperatorDetails: OperatorFunction<RequestTaskState, AviationOperatorDetails> = pipe(
  aerQuery.selectAer,
  map((aer) => aer?.operatorDetails),
);
export const OperatorDetailsQuery = {
  selectOperatorDetails,
};
