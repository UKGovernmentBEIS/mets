import { map, OperatorFunction, pipe } from 'rxjs';

import { aerQuery } from '@aviation/request-task/aer/shared/aer.selectors';
import { RequestTaskState } from '@aviation/request-task/store';

import { AviationAerMonitoringPlanChanges } from 'pmrv-api';

const selectMonitoringPlanChanges: OperatorFunction<RequestTaskState, AviationAerMonitoringPlanChanges> = pipe(
  aerQuery.selectAer,
  map((aer) => aer.aerMonitoringPlanChanges),
);

export const monitoringPlanChangesQuery = {
  selectMonitoringPlanChanges,
};
