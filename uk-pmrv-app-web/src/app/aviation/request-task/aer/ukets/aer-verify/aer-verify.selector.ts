import { map, OperatorFunction, pipe } from 'rxjs';

import {
  AerVerifyTaskKey,
  AerVerifyTaskPayload,
  requestTaskQuery,
  RequestTaskState,
} from '@aviation/request-task/store';
import { TaskItemStatus } from '@shared/task-list/task-list.interface';

import {
  AviationAerEtsComplianceRules,
  AviationAerUkEtsApplicationVerificationSubmitRequestTaskPayload,
  AviationAerUkEtsVerificationReport,
  AviationAerUncorrectedNonConformities,
} from 'pmrv-api';

import { getTaskStatusByTaskCompletionState } from './util/aer-verify.util';

const selectPayload: OperatorFunction<
  RequestTaskState,
  AviationAerUkEtsApplicationVerificationSubmitRequestTaskPayload
> = pipe(
  requestTaskQuery.selectRequestTaskPayload,
  map((payload) => payload as AviationAerUkEtsApplicationVerificationSubmitRequestTaskPayload),
);

const selectVerificationReport: OperatorFunction<RequestTaskState, AviationAerUkEtsVerificationReport> = pipe(
  requestTaskQuery.selectRequestTaskPayload,
  map((payload) => (payload as AerVerifyTaskPayload).verificationReport),
);

const selectEtsComplianceRules: OperatorFunction<RequestTaskState, AviationAerEtsComplianceRules> = pipe(
  selectVerificationReport,
  map((payload) => (payload as AerVerifyTaskPayload).verificationReport?.etsComplianceRules),
);

const selectUncorrectedNonConformities: OperatorFunction<RequestTaskState, AviationAerUncorrectedNonConformities> =
  pipe(
    selectVerificationReport,
    map((payload) => (payload as AerVerifyTaskPayload).verificationReport?.uncorrectedNonConformities),
  );

function selectStatusForTask(task: AerVerifyTaskKey): OperatorFunction<RequestTaskState, TaskItemStatus> {
  return pipe(
    requestTaskQuery.selectRequestTaskPayload,
    map((payload) => {
      return getTaskStatusByTaskCompletionState(task, payload);
    }),
  );
}

export const aerVerifyQuery = {
  selectPayload,
  selectVerificationReport,
  selectEtsComplianceRules,
  selectUncorrectedNonConformities,
  selectStatusForTask,
};
