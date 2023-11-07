import { map, OperatorFunction, pipe } from 'rxjs';

import { AviationAerEmissionsMonitoringApproach } from 'pmrv-api';

import { RequestTaskState } from '../../../../../store';
import { aerQuery } from '../../../../shared/aer.selectors';

const selectMonitoringApproach: OperatorFunction<RequestTaskState, AviationAerEmissionsMonitoringApproach> = pipe(
  aerQuery.selectAer,
  map((aer) => aer.monitoringApproach),
);

export const monitoringPlanChangesQuery = {
  selectMonitoringApproach,
};
