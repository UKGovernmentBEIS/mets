import { map, OperatorFunction, pipe } from 'rxjs';

import { EmpAdditionalDocuments } from 'pmrv-api';

import { RequestTaskState } from '../../../../store';
import { empQuery } from '../../../shared/emp.selectors';

const selectAdditionalDocuments: OperatorFunction<RequestTaskState, EmpAdditionalDocuments> = pipe(
  empQuery.selectEmissionsMonitoringPlan,
  map((emp) => emp.additionalDocuments),
);

export const additionalDocsQuery = {
  selectAdditionalDocuments,
};
