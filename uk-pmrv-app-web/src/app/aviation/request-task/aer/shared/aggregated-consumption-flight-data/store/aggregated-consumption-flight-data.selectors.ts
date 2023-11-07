import { map, OperatorFunction, pipe } from 'rxjs';

import { AviationAerUkEtsAggregatedEmissionsData } from 'pmrv-api';

import { RequestTaskState } from '../../../../store';
import { aerQuery } from '../../../shared/aer.selectors';

const selectAggregatedEmissionsData: OperatorFunction<RequestTaskState, AviationAerUkEtsAggregatedEmissionsData> = pipe(
  aerQuery.selectAer,
  map((aer) => aer.aggregatedEmissionsData),
);

export const selectAggregatedEmissionsDataQuery = {
  selectAggregatedEmissionsData: selectAggregatedEmissionsData,
};
