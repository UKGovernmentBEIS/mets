import { map, OperatorFunction, pipe } from 'rxjs';

import {
  AerCorsiaRequestActionPayload,
  AerUkEtsRequestActionPayload,
  requestActionQuery,
  RequestActionState,
} from '@aviation/request-action/store';
import { CorsiaRequestTypes } from '@aviation/request-task/util';

const selectRequestActionPayload: OperatorFunction<
  RequestActionState,
  AerUkEtsRequestActionPayload | AerCorsiaRequestActionPayload
> = pipe(
  requestActionQuery.selectRequestActionPayload,
  map((payload) => payload as AerUkEtsRequestActionPayload | AerCorsiaRequestActionPayload),
);

const selectIsCorsia: OperatorFunction<RequestActionState, boolean> = pipe(
  requestActionQuery.selectRequestType,
  map((type) => CorsiaRequestTypes.includes(type)),
);

export const aerCommonQuery = {
  selectRequestActionPayload,
  selectIsCorsia,
};
