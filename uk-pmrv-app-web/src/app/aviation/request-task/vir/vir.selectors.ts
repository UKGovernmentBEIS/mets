import { map, OperatorFunction, pipe } from 'rxjs';

import { requestTaskQuery, RequestTaskState, VirRequestTaskPayload } from '@aviation/request-task/store';

import {
  OperatorImprovementFollowUpResponse,
  OperatorImprovementResponse,
  RegulatorImprovementResponse,
  RegulatorReviewResponse,
  VirVerificationData,
} from 'pmrv-api';

const selectPayload: OperatorFunction<RequestTaskState, VirRequestTaskPayload> = pipe(
  requestTaskQuery.selectRequestTaskPayload,
  map((payload) => payload as VirRequestTaskPayload),
);

const selectVerificationData: OperatorFunction<RequestTaskState, VirVerificationData> = pipe(
  selectPayload,
  map((payload) => payload.verificationData),
);

const selectOperatorImprovementResponses: OperatorFunction<
  RequestTaskState,
  { [key: string]: OperatorImprovementResponse }
> = pipe(
  selectPayload,
  map((payload) => payload.operatorImprovementResponses),
);

const selectRegulatorReviewResponse: OperatorFunction<RequestTaskState, RegulatorReviewResponse> = pipe(
  selectPayload,
  map((payload) => payload.regulatorReviewResponse),
);

const selectRegulatorImprovementResponses: OperatorFunction<
  RequestTaskState,
  { [key: string]: RegulatorImprovementResponse }
> = pipe(
  selectRegulatorReviewResponse,
  map((regulatorReviewResponse) => regulatorReviewResponse.regulatorImprovementResponses),
);

const selectRespondRegulatorImprovementResponses: OperatorFunction<
  RequestTaskState,
  { [key: string]: RegulatorImprovementResponse }
> = pipe(
  selectPayload,
  map((payload) => payload.regulatorImprovementResponses),
);

const selectOperatorImprovementFollowUpResponses: OperatorFunction<
  RequestTaskState,
  { [key: string]: OperatorImprovementFollowUpResponse }
> = pipe(
  selectPayload,
  map((payload) => payload.operatorImprovementFollowUpResponses),
);

const selectAttachments: OperatorFunction<RequestTaskState, { [key: string]: string }> = pipe(
  selectPayload,
  map((payload) => payload.virAttachments),
);

const selectSectionsCompleted: OperatorFunction<RequestTaskState, { [key: string]: boolean }> = pipe(
  selectPayload,
  map((payload) => payload.virSectionsCompleted),
);

const selectReviewSectionsCompleted: OperatorFunction<RequestTaskState, { [key: string]: boolean }> = pipe(
  selectPayload,
  map((payload) => payload.reviewSectionsCompleted),
);

const selectRespondToRegulatorCommentsSectionsCompleted: OperatorFunction<
  RequestTaskState,
  { [key: string]: boolean }
> = pipe(
  selectPayload,
  map((payload) => payload.virRespondToRegulatorCommentsSectionsCompleted),
);

export const virQuery = {
  selectPayload,
  selectVerificationData,
  selectOperatorImprovementResponses,
  selectRegulatorReviewResponse,
  selectRegulatorImprovementResponses,
  selectRespondRegulatorImprovementResponses,
  selectOperatorImprovementFollowUpResponses,
  selectAttachments,
  selectSectionsCompleted,
  selectReviewSectionsCompleted,
  selectRespondToRegulatorCommentsSectionsCompleted,
};
