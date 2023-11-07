import { map, OperatorFunction, pipe } from 'rxjs';

import { RequestTaskState } from '@aviation/request-task/store';

import { EmpAdditionalDocuments } from 'pmrv-api';

import { aerQuery } from '../../aer.selectors';

const selectAdditionalDocuments: OperatorFunction<RequestTaskState, EmpAdditionalDocuments> = pipe(
  aerQuery.selectAer,
  map((aer) => aer.additionalDocuments),
);

export const additionalDocsQuery = {
  selectAdditionalDocuments,
};
