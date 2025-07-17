import { map, OperatorFunction, pipe } from 'rxjs';

import { AerVerifyCorsiaTaskPayload, requestTaskQuery, RequestTaskState } from '@aviation/request-task/store';

import {
  AviationAerCorsia,
  AviationAerCorsiaApplicationReviewRequestTaskPayload,
  AviationAerReportingObligationDetails,
} from 'pmrv-api';

const selectPayload: OperatorFunction<RequestTaskState, AviationAerCorsiaApplicationReviewRequestTaskPayload> = pipe(
  requestTaskQuery.selectRequestTaskPayload,
  map((payload) => payload as AerVerifyCorsiaTaskPayload),
);

const selectAer: OperatorFunction<RequestTaskState, AviationAerCorsia> = pipe(
  selectPayload,
  map((payload) => payload.aer),
);

const selectAerAttachments: OperatorFunction<RequestTaskState, { [p: string]: string }> = pipe(
  selectPayload,
  map((payload) => payload.aerAttachments),
);

const selectReportingObligationDetails: OperatorFunction<RequestTaskState, AviationAerReportingObligationDetails> =
  pipe(
    selectPayload,
    map((payload) => payload.reportingObligationDetails),
  );

const selectReportingObligationRequired: OperatorFunction<RequestTaskState, boolean> = pipe(
  selectPayload,
  map((payload) => payload.reportingRequired),
);

export const aerReviewCorsiaQuery = {
  selectPayload,
  selectAer,
  selectAerAttachments,
  selectReportingObligationDetails,
  selectReportingObligationRequired,
};
