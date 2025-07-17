import { map, OperatorFunction, pipe } from 'rxjs';

import { AviationDoECorsia } from 'pmrv-api';

import { DoeRequestTaskPayload, requestTaskQuery, RequestTaskState } from '../store';

const selectPayload: OperatorFunction<RequestTaskState, DoeRequestTaskPayload> = pipe(
  requestTaskQuery.selectRequestTaskPayload,
  map((payload) => payload as DoeRequestTaskPayload),
);

const selectDoe: OperatorFunction<RequestTaskState, AviationDoECorsia> = pipe(
  selectPayload,
  map((payload) => payload?.doe),
);

const selectSectionCompleted: OperatorFunction<RequestTaskState, boolean> = pipe(
  selectPayload,
  map((payload) => payload?.sectionCompleted),
);

export const doeQuery = {
  selectPayload,
  selectDoe,
  selectSectionCompleted,
};
