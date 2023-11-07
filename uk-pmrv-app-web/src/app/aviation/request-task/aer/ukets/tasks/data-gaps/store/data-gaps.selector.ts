import { map, OperatorFunction, pipe } from 'rxjs';

import { aerQuery } from '@aviation/request-task/aer/shared/aer.selectors';
import { RequestTaskState } from '@aviation/request-task/store';

import { AviationAerDataGaps } from 'pmrv-api';

const selectAviationAerDataGaps: OperatorFunction<RequestTaskState, AviationAerDataGaps> = pipe(
  aerQuery.selectAer,
  map((aer) => aer?.dataGaps),
);

export const aviationAerDataGapsQuery = {
  selectAviationAerDataGaps,
};
