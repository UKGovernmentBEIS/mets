import { map, OperatorFunction, pipe } from 'rxjs';

import {
  AerCorsiaRequestActionPayload,
  AerRequestActionPayload,
  requestActionQuery,
  RequestActionState,
} from '@aviation/request-action/store';

const selectRequestActionPayload: OperatorFunction<
  RequestActionState,
  AerRequestActionPayload | AerCorsiaRequestActionPayload
> = pipe(
  requestActionQuery.selectRequestActionPayload,
  map((payload) => payload as AerRequestActionPayload),
);

export const aerCommonQuery = {
  selectRequestActionPayload,
};
