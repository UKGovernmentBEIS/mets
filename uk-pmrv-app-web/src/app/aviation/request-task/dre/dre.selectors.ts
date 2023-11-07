import { map, OperatorFunction, pipe } from 'rxjs';

import { AviationDre } from 'pmrv-api';

import { DreRequestTaskPayload, requestTaskQuery, RequestTaskState } from '../store';

const selectPayload: OperatorFunction<RequestTaskState, DreRequestTaskPayload> = pipe(
  requestTaskQuery.selectRequestTaskPayload,
  map((payload) => payload as DreRequestTaskPayload),
);

const selectDre: OperatorFunction<RequestTaskState, AviationDre> = pipe(
  selectPayload,
  map((payload) => payload?.dre),
);

const selectSectionCompleted: OperatorFunction<RequestTaskState, boolean> = pipe(
  selectPayload,
  map((payload) => payload?.sectionCompleted),
);

export const dreQuery = {
  selectPayload,
  selectDre,
  selectSectionCompleted,
};
