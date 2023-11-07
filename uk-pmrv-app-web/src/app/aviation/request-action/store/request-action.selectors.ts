import { map, OperatorFunction, pipe } from 'rxjs';

import { CorsiaRequestTypes } from '@aviation/request-task/util';

import { RequestActionDTO, RequestActionPayload } from 'pmrv-api';

import { RequestActionState } from './request-action.state';

const selectRequestActionItem: OperatorFunction<RequestActionState, RequestActionDTO> = pipe(
  map((state) => state?.requestActionItem),
);
const selectCreationDate: OperatorFunction<RequestActionState, RequestActionDTO['creationDate']> = pipe(
  selectRequestActionItem,
  map((state) => state?.creationDate),
);
const selectRegulatorViewer: OperatorFunction<RequestActionState, boolean> = pipe(
  map((state) => state?.regulatorViewer),
);
const selectRequestActionType: OperatorFunction<RequestActionState, RequestActionDTO['type']> = pipe(
  selectRequestActionItem,
  map((state) => state?.type),
);
const selectRequestActionPayload: OperatorFunction<RequestActionState, RequestActionPayload> = pipe(
  selectRequestActionItem,
  map((state) => state?.payload as RequestActionPayload),
);
const selectRequestType: OperatorFunction<RequestActionState, RequestActionDTO['requestType']> = pipe(
  selectRequestActionItem,
  map((state) => state?.requestType),
);
const selectIsCorsia: OperatorFunction<RequestActionState, boolean> = pipe(
  selectRequestActionItem,
  map((state) => CorsiaRequestTypes.includes(state?.requestType)),
);

export const requestActionQuery = {
  selectRequestActionItem,
  selectCreationDate,
  selectRegulatorViewer,
  selectRequestActionType,
  selectRequestActionPayload,
  selectRequestType,
  selectIsCorsia,
};
