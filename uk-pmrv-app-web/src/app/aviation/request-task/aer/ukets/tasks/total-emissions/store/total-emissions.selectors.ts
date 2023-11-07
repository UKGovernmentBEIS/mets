import { map, OperatorFunction, pipe } from 'rxjs';

import { aerQuery } from '@aviation/request-task/aer/shared/aer.selectors';
import { RequestTaskState } from '@aviation/request-task/store';

import { AviationAerTotalEmissionsConfidentiality } from 'pmrv-api';

const selectTotalEmissionsChanges: OperatorFunction<RequestTaskState, AviationAerTotalEmissionsConfidentiality> = pipe(
  aerQuery.selectAer,
  map((aer) => aer.aviationAerTotalEmissionsConfidentiality),
);

export const totalEmissionsChangesQuery = {
  selectTotalEmissionsChanges,
};
