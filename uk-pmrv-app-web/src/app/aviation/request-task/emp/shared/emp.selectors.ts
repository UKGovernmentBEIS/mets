import { map, OperatorFunction, pipe } from 'rxjs';

import { CorsiaRequestTypes, notDisplayDiffComponent } from '@aviation/request-task/util';
import { TaskItemStatus } from '@shared/task-list/task-list.interface';

import {
  EmissionsMonitoringPlanCorsia,
  EmissionsMonitoringPlanUkEts,
  EmissionsMonitoringPlanUkEtsContainer,
  EmpAcceptedVariationDecisionDetails,
  EmpIssuanceDetermination,
  EmpIssuanceReviewDecision,
  EmpVariationReviewDecision,
  EmpVariationUkEtsDetails,
  EmpVariationUkEtsRegulatorLedReason,
  ServiceContactDetails,
} from 'pmrv-api';

import {
  EmissionsMonitoringPlan,
  EmpRequestTaskPayloadCorsia,
  EmpRequestTaskPayloadUkEts,
  EmpTaskKey,
  requestTaskQuery,
  RequestTaskState,
} from '../../store';
import { getTaskStatusByTaskCompletionState } from './util/emp.util';

const selectPayload: OperatorFunction<RequestTaskState, EmpRequestTaskPayloadUkEts> = pipe(
  requestTaskQuery.selectRequestTaskPayload,
  map((payload) => payload as EmpRequestTaskPayloadUkEts),
);
const selectIsCorsia: OperatorFunction<RequestTaskState, boolean> = pipe(
  requestTaskQuery.selectRequestInfo,
  map((request) => CorsiaRequestTypes.includes(request.type)),
);
const selectEmpAttachments: OperatorFunction<RequestTaskState, { [key: string]: string }> = pipe(
  selectPayload,
  map((payload) => payload.empAttachments),
);

const selectReviewAttachments: OperatorFunction<RequestTaskState, { [key: string]: string }> = pipe(
  selectPayload,
  map((payload) => payload.reviewAttachments),
);

const selectServiceContactDetails: OperatorFunction<RequestTaskState, ServiceContactDetails> = pipe(
  selectPayload,
  map((payload) => payload.serviceContactDetails),
);

const selectVariationDetails: OperatorFunction<RequestTaskState, EmpVariationUkEtsDetails> = pipe(
  selectPayload,
  map((payload) => payload.empVariationDetails),
);

const selectVariationRegulatorLedReason: OperatorFunction<RequestTaskState, EmpVariationUkEtsRegulatorLedReason> = pipe(
  selectPayload,
  map((payload) => payload.reasonRegulatorLed),
);

const selectVariationDetailsCompleted: OperatorFunction<RequestTaskState, boolean> = pipe(
  selectPayload,
  map((payload) => payload.empVariationDetailsCompleted),
);

const selectOriginalEmpContainer: OperatorFunction<RequestTaskState, EmissionsMonitoringPlanUkEtsContainer> = pipe(
  selectPayload,
  map((payload) => (!notDisplayDiffComponent.includes(payload.payloadType) ? payload.originalEmpContainer : null)),
);

const selectEmissionsMonitoringPlan: OperatorFunction<RequestTaskState, EmissionsMonitoringPlanUkEts> = pipe(
  requestTaskQuery.selectRequestTaskPayload,
  map((state) => (state as EmpRequestTaskPayloadUkEts)?.emissionsMonitoringPlan),
);
const selectEmissionsMonitoringPlanCorsia: OperatorFunction<RequestTaskState, EmissionsMonitoringPlanCorsia> = pipe(
  requestTaskQuery.selectRequestTaskPayload,
  map((state) => (state as EmpRequestTaskPayloadCorsia)?.emissionsMonitoringPlan),
);
const selectReviewDecisions: OperatorFunction<RequestTaskState, { [key: string]: EmpIssuanceReviewDecision }> = pipe(
  requestTaskQuery.selectRequestTaskPayload,
  map((state) => (state as EmpRequestTaskPayloadUkEts)?.reviewGroupDecisions),
);

const selectReviewSectionsCompleted: OperatorFunction<RequestTaskState, { [key: string]: boolean }> = pipe(
  requestTaskQuery.selectRequestTaskPayload,
  map((state) => (state as EmpRequestTaskPayloadUkEts)?.reviewSectionsCompleted),
);

const selectVariationReviewDecisions: OperatorFunction<
  RequestTaskState,
  { [key: string]: EmpVariationReviewDecision }
> = pipe(
  requestTaskQuery.selectRequestTaskPayload,
  map((state) => (state as EmpRequestTaskPayloadUkEts)?.reviewGroupDecisions),
);

const selectVariationRegLedDecisions: OperatorFunction<
  RequestTaskState,
  { [key: string]: EmpAcceptedVariationDecisionDetails }
> = pipe(
  requestTaskQuery.selectRequestTaskPayload,
  map((state) => (state as EmpRequestTaskPayloadUkEts)?.reviewGroupDecisions),
);

const selectVariationDetailsReviewDecisions: OperatorFunction<RequestTaskState, EmpVariationReviewDecision> = pipe(
  requestTaskQuery.selectRequestTaskPayload,
  map((state) => (state as EmpRequestTaskPayloadUkEts)?.empVariationDetailsReviewDecision),
);

const selectVariationDetailsReviewCompleted: OperatorFunction<RequestTaskState, boolean> = pipe(
  requestTaskQuery.selectRequestTaskPayload,
  map((state) => (state as EmpRequestTaskPayloadUkEts)?.empVariationDetailsReviewCompleted),
);

function selectEmpTask<T extends keyof EmissionsMonitoringPlan>(
  task: T,
): OperatorFunction<RequestTaskState, EmissionsMonitoringPlan[T]> {
  return pipe(
    selectEmissionsMonitoringPlan,
    map((state) => state[task]),
  );
}

function selectStatusForTask(task: EmpTaskKey): OperatorFunction<RequestTaskState, TaskItemStatus> {
  return pipe(
    selectPayload,
    map((payload) => {
      return getTaskStatusByTaskCompletionState(task, payload);
    }),
  );
}

const selectReviewDetermination: OperatorFunction<RequestTaskState, EmpIssuanceDetermination> = pipe(
  requestTaskQuery.selectRequestTaskPayload,
  map((state) => (state as EmpRequestTaskPayloadUkEts)?.determination),
);

export const empQuery = {
  selectPayload,
  selectEmpAttachments,
  selectReviewAttachments,
  selectServiceContactDetails,
  selectEmissionsMonitoringPlan,
  selectEmissionsMonitoringPlanCorsia,
  selectReviewDecisions,
  selectVariationReviewDecisions,
  selectVariationRegLedDecisions,
  selectVariationDetailsReviewDecisions,
  selectVariationDetailsReviewCompleted,
  selectOriginalEmpContainer,
  selectReviewSectionsCompleted,
  selectEmpTask,
  selectStatusForTask,
  selectReviewDetermination,
  selectVariationDetails,
  selectVariationRegulatorLedReason,
  selectVariationDetailsCompleted,
  selectIsCorsia,
};
