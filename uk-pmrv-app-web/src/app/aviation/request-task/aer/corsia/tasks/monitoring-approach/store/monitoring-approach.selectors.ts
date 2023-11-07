import { map, OperatorFunction, pipe } from 'rxjs';

import { AviationAerCorsia, AviationAerCorsiaMonitoringApproach } from 'pmrv-api';

import { RequestTaskState } from '../../../../../store';
import { aerQuery } from '../../../../shared/aer.selectors';

const selectMonitoringApproach: OperatorFunction<RequestTaskState, AviationAerCorsiaMonitoringApproach> = pipe(
  aerQuery.selectAer,
  map((aer) => (aer as unknown as AviationAerCorsia).monitoringApproach),
);

export const monitoringApproachCorsiaQuery = {
  selectMonitoringApproach,
};
