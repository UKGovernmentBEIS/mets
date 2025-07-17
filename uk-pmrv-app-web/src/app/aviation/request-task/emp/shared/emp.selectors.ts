import { map, OperatorFunction, pipe } from 'rxjs';

import { CorsiaRequestTypes, notDisplayDiffComponent } from '@aviation/request-task/util';
import { TaskItemStatus } from '@shared/task-list/task-list.interface';

import {
  EmissionsMonitoringPlanCorsia,
  EmissionsMonitoringPlanUkEts,
  EmissionsMonitoringPlanUkEtsContainer,
  EmpAcceptedVariationDecisionDetails,
  EmpIssuanceReviewDecision,
  EmpVariationReviewDecision,
  EmpVariationUkEtsDetails,
  EmpVariationUkEtsRegulatorLedReason,
  ServiceContactDetails,
} from 'pmrv-api';

import {
  EmissionsMonitoringPlan,
  EmpRequestTaskPayload,
  EmpRequestTaskPayloadCorsia,
  EmpRequestTaskPayloadUkEts,
  EmpTaskKey,
  requestTaskQuery,
  RequestTaskState,
} from '../../store';
import { EmpDetermination, EmpReviewDecision, getTaskStatusByTaskCompletionState } from './util/emp.util';

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

const selectReviewDecisions: OperatorFunction<RequestTaskState, { [key: string]: EmpReviewDecision }> = pipe(
  requestTaskQuery.selectRequestTaskPayload,
  map((state) => (state as EmpRequestTaskPayload)?.reviewGroupDecisions),
);

const selectReviewSectionsCompleted: OperatorFunction<RequestTaskState, { [key: string]: boolean }> = pipe(
  requestTaskQuery.selectRequestTaskPayload,
  map((state) => (state as EmpRequestTaskPayload)?.reviewSectionsCompleted),
);

const selectIssuanceReviewDecisions: OperatorFunction<RequestTaskState, { [key: string]: EmpIssuanceReviewDecision }> =
  pipe(
    requestTaskQuery.selectRequestTaskPayload,
    map((state) => (state as EmpRequestTaskPayload)?.reviewGroupDecisions),
  );

const selectVariationReviewDecisions: OperatorFunction<
  RequestTaskState,
  { [key: string]: EmpVariationReviewDecision }
> = pipe(
  requestTaskQuery.selectRequestTaskPayload,
  map((state) => (state as EmpRequestTaskPayload)?.reviewGroupDecisions),
);

const selectVariationRegLedDecisions: OperatorFunction<
  RequestTaskState,
  { [key: string]: EmpAcceptedVariationDecisionDetails }
> = pipe(
  requestTaskQuery.selectRequestTaskPayload,
  map((state) => (state as EmpRequestTaskPayloadUkEts)?.reviewGroupDecisions),
);

const selectVariationDetailsReviewDecision: OperatorFunction<RequestTaskState, EmpVariationReviewDecision> = pipe(
  requestTaskQuery.selectRequestTaskPayload,
  map((state) => (state as EmpRequestTaskPayload)?.empVariationDetailsReviewDecision),
);

const selectVariationDetailsReviewCompleted: OperatorFunction<RequestTaskState, boolean> = pipe(
  requestTaskQuery.selectRequestTaskPayload,
  map((state) => (state as EmpRequestTaskPayload)?.empVariationDetailsReviewCompleted),
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

const selectDetermination: OperatorFunction<RequestTaskState, EmpDetermination> = pipe(
  requestTaskQuery.selectRequestTaskPayload,
  map((state) => (state as EmpRequestTaskPayload)?.determination),
);

export const empQuery = {
  selectPayload,
  selectEmpAttachments,
  selectReviewAttachments,
  selectServiceContactDetails,
  selectEmissionsMonitoringPlan,
  selectEmissionsMonitoringPlanCorsia,
  selectReviewDecisions,
  selectIssuanceReviewDecisions,
  selectVariationReviewDecisions,
  selectVariationRegLedDecisions,
  selectVariationDetailsReviewDecision,
  selectVariationDetailsReviewCompleted,
  selectOriginalEmpContainer,
  selectReviewSectionsCompleted,
  selectEmpTask,
  selectStatusForTask,
  selectDetermination,
  selectVariationDetails,
  selectVariationRegulatorLedReason,
  selectVariationDetailsCompleted,
  selectIsCorsia,
};
