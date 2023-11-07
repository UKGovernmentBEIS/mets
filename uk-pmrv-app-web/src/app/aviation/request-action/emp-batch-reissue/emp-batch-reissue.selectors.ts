import { map, OperatorFunction, pipe } from 'rxjs';

import { requestActionQuery, RequestActionState } from '@aviation/request-action/store';

import {
  BatchReissueCompletedRequestActionPayload,
  BatchReissueSubmittedRequestActionPayload,
  ReissueCompletedRequestActionPayload,
} from 'pmrv-api';

const selectBatchReissueSubmittedRequestActionPayload: OperatorFunction<
  RequestActionState,
  BatchReissueSubmittedRequestActionPayload
> = pipe(
  requestActionQuery.selectRequestActionPayload,
  map((payload) => payload as BatchReissueSubmittedRequestActionPayload),
);

const selectBatchReissueCompletedRequestActionPayload: OperatorFunction<
  RequestActionState,
  BatchReissueCompletedRequestActionPayload
> = pipe(
  requestActionQuery.selectRequestActionPayload,
  map((payload) => payload as BatchReissueCompletedRequestActionPayload),
);

const selectReissueCompletedRequestActionPayload: OperatorFunction<
  RequestActionState,
  ReissueCompletedRequestActionPayload
> = pipe(
  requestActionQuery.selectRequestActionPayload,
  map((payload) => payload as ReissueCompletedRequestActionPayload),
);

export const query = {
  selectBatchReissueSubmittedRequestActionPayload,
  selectBatchReissueCompletedRequestActionPayload,

  selectReissueCompletedRequestActionPayload,
};
