import { map, OperatorFunction, pipe } from 'rxjs';

import { requestTaskQuery, RequestTaskState } from '@aviation/request-task/store';
import { EmpRequestTaskPayloadCorsia } from '@aviation/request-task/store/request-task.types';
import { notDisplayDiffComponent } from '@aviation/request-task/util';
import { extractMonitorinApproachType } from '@aviation/shared/components/emp/emission-sources/isFUMM';
import { TaskItemStatus } from '@shared/task-list/task-list.interface';

import {
  EmissionsMonitoringPlanCorsia,
  EmissionsMonitoringPlanCorsiaContainer,
  EmpEmissionsMonitoringApproachCorsia,
  EmpVariationCorsiaDetails,
} from 'pmrv-api';

import { getTaskStatusByTaskCompletionState } from './util/emp.util';

const selectEmissionsMonitoringPlanCorsia: OperatorFunction<RequestTaskState, EmissionsMonitoringPlanCorsia> = pipe(
  requestTaskQuery.selectRequestTaskPayload,
  map((state) => (state as EmpRequestTaskPayloadCorsia)?.emissionsMonitoringPlan),
);
const selectPayload: OperatorFunction<RequestTaskState, EmpRequestTaskPayloadCorsia> = pipe(
  requestTaskQuery.selectRequestTaskPayload,
  map((payload) => payload as EmpRequestTaskPayloadCorsia),
);

function selectStatusForTask(task: any): OperatorFunction<RequestTaskState, TaskItemStatus> {
  return pipe(
    selectPayload,
    map((payload) => {
      return getTaskStatusByTaskCompletionState(task, payload);
    }),
  );
}
const selectOriginalEmpContainer: OperatorFunction<RequestTaskState, EmissionsMonitoringPlanCorsiaContainer> = pipe(
  selectPayload,
  map((payload) => (!notDisplayDiffComponent.includes(payload.payloadType) ? payload.originalEmpContainer : null)),
);
const selectVariationDetails: OperatorFunction<RequestTaskState, EmpVariationCorsiaDetails> = pipe(
  selectPayload,
  map((payload) => payload.empVariationDetails),
);

const selectVariationRegulatorLedReason: OperatorFunction<RequestTaskState, string> = pipe(
  selectPayload,
  map((payload) => payload.reasonRegulatorLed),
);

const selectMonitoringApproachType: OperatorFunction<
  RequestTaskState,
  EmpEmissionsMonitoringApproachCorsia['monitoringApproachType'] | undefined
> = pipe(
  selectEmissionsMonitoringPlanCorsia,
  map(
    (payload) =>
      extractMonitorinApproachType(payload) as EmpEmissionsMonitoringApproachCorsia['monitoringApproachType'],
  ),
);

export const empCorsiaQuery = {
  selectEmissionsMonitoringPlanCorsia,
  selectStatusForTask,
  selectPayload,
  selectVariationDetails,
  selectOriginalEmpContainer,
  selectVariationRegulatorLedReason,
  selectMonitoringApproachType,
};
