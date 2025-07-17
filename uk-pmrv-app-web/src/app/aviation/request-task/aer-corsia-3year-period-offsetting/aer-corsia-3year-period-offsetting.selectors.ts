import { map, OperatorFunction, pipe } from 'rxjs';

import { AviationAerCorsia3YearPeriodOffsettingApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { requestTaskQuery, RequestTaskState } from '../store';

const selectPayload: OperatorFunction<
  RequestTaskState,
  AviationAerCorsia3YearPeriodOffsettingApplicationSubmitRequestTaskPayload
> = pipe(
  requestTaskQuery.selectRequestTaskPayload,
  map((payload) => payload as AviationAerCorsia3YearPeriodOffsettingApplicationSubmitRequestTaskPayload),
);

const selectAerCorsia3YearOffsetting: OperatorFunction<
  RequestTaskState,
  AviationAerCorsia3YearPeriodOffsettingApplicationSubmitRequestTaskPayload['aviationAerCorsia3YearPeriodOffsetting']
> = pipe(
  requestTaskQuery.selectRequestTaskPayload,
  map(
    (payload) =>
      (payload as AviationAerCorsia3YearPeriodOffsettingApplicationSubmitRequestTaskPayload)
        .aviationAerCorsia3YearPeriodOffsetting,
  ),
);

const selectAerCorsia3YearPeriodOffsettingSectionsCompleted: OperatorFunction<
  RequestTaskState,
  AviationAerCorsia3YearPeriodOffsettingApplicationSubmitRequestTaskPayload['aviationAerCorsia3YearPeriodOffsettingSectionsCompleted']
> = pipe(
  requestTaskQuery.selectRequestTaskPayload,
  map(
    (payload) =>
      (payload as AviationAerCorsia3YearPeriodOffsettingApplicationSubmitRequestTaskPayload)
        .aviationAerCorsia3YearPeriodOffsettingSectionsCompleted || {},
  ),
);

export const aerCorsia3YearOffsettingQuery = {
  selectPayload,
  selectAerCorsia3YearOffsetting,
  selectAerCorsia3YearPeriodOffsettingSectionsCompleted,
};
