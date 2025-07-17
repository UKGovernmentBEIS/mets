import { map, OperatorFunction, pipe } from 'rxjs';

import { RequestTaskState } from '@aviation/request-task/store';

import { EmpAdditionalDocuments } from 'pmrv-api';

import { empQuery } from '../../emp.selectors';

const selectAdditionalDocuments: OperatorFunction<RequestTaskState, EmpAdditionalDocuments> = pipe(
  empQuery.selectEmissionsMonitoringPlan,
  map((emp) => emp.additionalDocuments),
);

export const additionalDocsQuery = {
  selectAdditionalDocuments,
};
