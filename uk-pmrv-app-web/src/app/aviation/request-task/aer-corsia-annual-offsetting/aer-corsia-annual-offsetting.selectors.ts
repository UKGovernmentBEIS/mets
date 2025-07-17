import { map, OperatorFunction, pipe } from 'rxjs';

import { AerCorsiaAnnualOffsettingPayload, requestTaskQuery, RequestTaskState } from '../store';

const selectPayload: OperatorFunction<RequestTaskState, AerCorsiaAnnualOffsettingPayload> = pipe(
  requestTaskQuery.selectRequestTaskPayload,
  map((payload) => payload as AerCorsiaAnnualOffsettingPayload),
);

const selectAerCorsiaAnnualOffsetting: OperatorFunction<
  RequestTaskState,
  AerCorsiaAnnualOffsettingPayload['aviationAerCorsiaAnnualOffsetting']
> = pipe(
  selectPayload,
  map((payload) => payload.aviationAerCorsiaAnnualOffsetting),
);

const selectaAerCorsiaAnnualOffsettingSectionsCompleted: OperatorFunction<
  RequestTaskState,
  AerCorsiaAnnualOffsettingPayload['aviationAerCorsiaAnnualOffsettingSectionsCompleted']
> = pipe(
  selectPayload,
  map((payload) => payload.aviationAerCorsiaAnnualOffsettingSectionsCompleted),
);

export const aerCorsiaAnnualOffsettingQuery = {
  selectPayload,
  selectAerCorsiaAnnualOffsetting,
  selectaAerCorsiaAnnualOffsettingSectionsCompleted,
};
