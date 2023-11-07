import { map, OperatorFunction, pipe } from 'rxjs';

import { requestActionQuery, RequestActionState, VirRequestActionPayload } from '@aviation/request-action/store';

import { VirVerificationData } from 'pmrv-api';

const selectRequestActionPayload: OperatorFunction<RequestActionState, VirRequestActionPayload> = pipe(
  requestActionQuery.selectRequestActionPayload,
  map((payload) => payload as VirRequestActionPayload),
);

const selectVerificationData: OperatorFunction<RequestActionState, VirVerificationData> = pipe(
  selectRequestActionPayload,
  map((payload) => payload.verificationData),
);

export const virQuery = {
  selectRequestActionPayload,
  selectVerificationData,
};
