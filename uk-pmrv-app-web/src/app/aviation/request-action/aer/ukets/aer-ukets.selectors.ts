import { map, OperatorFunction, pipe } from 'rxjs';

import { AerUkEtsRequestActionPayload, requestActionQuery, RequestActionState } from '@aviation/request-action/store';

const selectRequestActionPayload: OperatorFunction<RequestActionState, AerUkEtsRequestActionPayload> = pipe(
  requestActionQuery.selectRequestActionPayload,
  map((payload) => payload as AerUkEtsRequestActionPayload),
);

export const aerQuery = {
  selectRequestActionPayload,
};
