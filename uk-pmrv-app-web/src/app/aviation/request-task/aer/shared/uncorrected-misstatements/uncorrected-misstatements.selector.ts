import { map, OperatorFunction, pipe } from 'rxjs';

import { getTaskStatusByTaskCompletionState as getCorsiaTaskStatusByTaskCompletionState } from '@aviation/request-task/aer/corsia/aer-verify/util/aer-verify-corsia.util';
import { getTaskStatusByTaskCompletionState } from '@aviation/request-task/aer/ukets/aer-verify/util/aer-verify.util';
import {
  AerVerify,
  AerVerifyCorsia,
  AerVerifyCorsiaTaskKey,
  AerVerifyTaskKey,
  requestTaskQuery,
  RequestTaskState,
} from '@aviation/request-task/store';
import { TaskItemStatus } from '@shared/task-list/task-list.interface';

import {
  AviationAerCorsiaVerificationReport,
  AviationAerUkEtsVerificationReport,
  AviationAerUncorrectedMisstatements,
} from 'pmrv-api';

const selectPayload: OperatorFunction<RequestTaskState, AerVerify | AerVerifyCorsia> = pipe(
  requestTaskQuery.selectRequestTaskPayload,
  map((payload) => payload),
);

const selectVerificationReport: OperatorFunction<
  RequestTaskState,
  AviationAerUkEtsVerificationReport | AviationAerCorsiaVerificationReport
> = pipe(
  selectPayload,
  map((payload) => payload.verificationReport),
);

const selectUncorrectedMisstatements: OperatorFunction<RequestTaskState, AviationAerUncorrectedMisstatements> = pipe(
  selectVerificationReport,
  map((verificationReport) => verificationReport.uncorrectedMisstatements),
);

function selectStatusForTask(
  task: AerVerifyTaskKey | AerVerifyCorsiaTaskKey,
): OperatorFunction<RequestTaskState, TaskItemStatus> {
  return pipe(
    requestTaskQuery.selectRequestTaskPayload,
    map((payload) => {
      return payload.payloadType === 'AVIATION_AER_CORSIA_APPLICATION_VERIFICATION_SUBMIT_PAYLOAD'
        ? getCorsiaTaskStatusByTaskCompletionState(task as AerVerifyCorsiaTaskKey, payload)
        : getTaskStatusByTaskCompletionState(task as AerVerifyTaskKey, payload);
    }),
  );
}

export const uncorrectedMisstatementsQuery = {
  selectPayload,
  selectVerificationReport,
  selectUncorrectedMisstatements,
  selectStatusForTask,
};
