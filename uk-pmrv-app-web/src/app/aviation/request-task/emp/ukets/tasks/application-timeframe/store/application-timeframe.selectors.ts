import { map, OperatorFunction, pipe } from 'rxjs';

import { EmpApplicationTimeframeInfo } from 'pmrv-api';

import { RequestTaskState } from '../../../../../store';
import { empQuery } from '../../../../shared/emp.selectors';

const selectApplicationTimeframeInfo: OperatorFunction<RequestTaskState, EmpApplicationTimeframeInfo> = pipe(
  empQuery.selectEmissionsMonitoringPlan,
  map((emp) => emp?.applicationTimeframeInfo),
);

export const applicationTimeframeInfoQuery = {
  selectApplicationTimeframeInfo,
};
