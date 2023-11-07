import { map, OperatorFunction, pipe } from 'rxjs';

import { EmpRequestActionPayload, requestActionQuery, RequestActionState } from '@aviation/request-action/store';

const selectRequestActionPayload: OperatorFunction<RequestActionState, EmpRequestActionPayload> = pipe(
  requestActionQuery.selectRequestActionPayload,
  map((payload) => payload as EmpRequestActionPayload),
);

export const empQuery = {
  selectRequestActionPayload,
};
