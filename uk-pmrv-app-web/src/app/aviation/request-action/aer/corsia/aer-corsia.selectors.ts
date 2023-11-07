import { map, OperatorFunction, pipe } from 'rxjs';

import { AerCorsiaRequestActionPayload, requestActionQuery, RequestActionState } from '@aviation/request-action/store';

import { AviationAerCorsia, AviationAerCorsiaVerificationReport } from 'pmrv-api';

const selectRequestActionPayload: OperatorFunction<RequestActionState, AerCorsiaRequestActionPayload> = pipe(
  requestActionQuery.selectRequestActionPayload,
  map((payload) => payload as AerCorsiaRequestActionPayload),
);

const selectAer: OperatorFunction<RequestActionState, AviationAerCorsia> = pipe(
  selectRequestActionPayload,
  map((payload) => payload.aer),
);

const selectVerificationReport: OperatorFunction<RequestActionState, AviationAerCorsiaVerificationReport> = pipe(
  selectRequestActionPayload,
  map((payload) => payload.verificationReport),
);

const selectAerAttachments: OperatorFunction<RequestActionState, { [p: string]: string }> = pipe(
  selectRequestActionPayload,
  map((payload) => payload.aerAttachments),
);

export const aerCorsiaQuery = {
  selectRequestActionPayload,
  selectAer,
  selectVerificationReport,
  selectAerAttachments,
};
