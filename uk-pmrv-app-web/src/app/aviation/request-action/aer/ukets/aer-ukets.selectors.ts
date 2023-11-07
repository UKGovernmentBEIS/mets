import { map, OperatorFunction, pipe } from 'rxjs';

import { AerRequestActionPayload, requestActionQuery, RequestActionState } from '@aviation/request-action/store';

const selectRequestActionPayload: OperatorFunction<RequestActionState, AerRequestActionPayload> = pipe(
  requestActionQuery.selectRequestActionPayload,
  map((payload) => payload as AerRequestActionPayload),
);

export const aerQuery = {
  selectRequestActionPayload,
};
