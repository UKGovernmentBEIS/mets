import { map, OperatorFunction, pipe } from 'rxjs';

import { ReportingObligation } from '@aviation/request-task/aer/shared/reporting-obligation';
import { getTaskStatusByTaskCompletionState } from '@aviation/request-task/aer/shared/util/aer.util';
import { CorsiaRequestTypes } from '@aviation/request-task/util';
import { TaskItemStatus } from '@shared/task-list/task-list.interface';

import { EmpCorsiaOriginatedData, ServiceContactDetails } from 'pmrv-api';
import { AviationAerMonitoringPlanVersion } from 'pmrv-api';

import {
  Aer,
  AerCorsia,
  AerRequestTaskPayload,
  AerTaskKey,
  AerUkEtsRequestTaskPayload,
  requestTaskQuery,
  RequestTaskState,
} from '../../store';

const selectPayload: OperatorFunction<RequestTaskState, AerRequestTaskPayload> = pipe(
  requestTaskQuery.selectRequestTaskPayload,
  map((payload) => payload as AerRequestTaskPayload),
);

const selectAerAttachments: OperatorFunction<RequestTaskState, { [key: string]: string }> = pipe(
  selectPayload,
  map((payload) => payload.aerAttachments),
);

const selectReviewAttachments: OperatorFunction<RequestTaskState, { [key: string]: string }> = pipe(
  selectPayload,
  map((payload) => payload.reviewAttachments),
);

const selectServiceContactDetails: OperatorFunction<RequestTaskState, ServiceContactDetails> = pipe(
  selectPayload,
  map((payload) => payload.serviceContactDetails),
);

const selectAerMonitoringPlanVersions: OperatorFunction<RequestTaskState, AviationAerMonitoringPlanVersion[]> = pipe(
  selectPayload,
  map((payload) => payload.aerMonitoringPlanVersions),
);

const selectReportingObligation: OperatorFunction<RequestTaskState, ReportingObligation> = pipe(
  selectPayload,
  map((payload) => {
    return {
      reportingRequired: payload.reportingRequired ?? null,
      reportingObligationDetails: payload.reportingObligationDetails ?? null,
    } as ReportingObligation;
  }),
);

const selectAer: OperatorFunction<RequestTaskState, Aer> = pipe(
  requestTaskQuery.selectRequestTaskPayload,
  map((state) => (state as AerRequestTaskPayload)?.aer),
);

const selectAerCorsia: OperatorFunction<RequestTaskState, AerCorsia> = pipe(
  requestTaskQuery.selectRequestTaskPayload,
  map((state) => (state as AerRequestTaskPayload)?.aer),
);

const selectAerYear: OperatorFunction<RequestTaskState, number> = pipe(
  requestTaskQuery.selectRequestTaskPayload,
  map((state) => (state as AerRequestTaskPayload)?.reportingYear),
);

function selectAerTask<T extends keyof Aer>(task: T): OperatorFunction<RequestTaskState, Aer[T]> {
  return pipe(
    selectAer,
    map((state) => state[task]),
  );
}

function selectStatusForTask(
  task: AerTaskKey,
  isAmendsTask?: boolean,
  isCorsia?: boolean,
): OperatorFunction<RequestTaskState, TaskItemStatus> {
  return pipe(
    requestTaskQuery.selectRequestTaskPayload,
    map((payload) => {
      return getTaskStatusByTaskCompletionState(task, payload, isAmendsTask, isCorsia);
    }),
  );
}

const selectEmpCorsiaOriginatedData: OperatorFunction<RequestTaskState, EmpCorsiaOriginatedData> = pipe(
  selectPayload,
  map((payload) => payload?.empOriginatedData),
);

const selectIsCorsia: OperatorFunction<RequestTaskState, boolean> = pipe(
  requestTaskQuery.selectRequestInfo,
  map((request) => CorsiaRequestTypes.includes(request.type)),
);

const selectReviewDecisions: OperatorFunction<RequestTaskState, { [key: string]: any }> = pipe(
  requestTaskQuery.selectRequestTaskPayload,
  map((state) => (state as AerUkEtsRequestTaskPayload)?.reviewGroupDecisions),
);

const selectReviewSectionsCompleted: OperatorFunction<RequestTaskState, { [key: string]: boolean }> = pipe(
  requestTaskQuery.selectRequestTaskPayload,
  map((state) => (state as AerUkEtsRequestTaskPayload)?.reviewSectionsCompleted),
);

const selectAerSectionsCompleted: OperatorFunction<RequestTaskState, { [key: string]: boolean[] }> = pipe(
  requestTaskQuery.selectRequestTaskPayload,
  map((state) => (state as AerUkEtsRequestTaskPayload)?.aerSectionsCompleted),
);

export const aerQuery = {
  selectPayload,
  selectAerAttachments,
  selectReviewAttachments,
  selectServiceContactDetails,
  selectAerMonitoringPlanVersions,
  selectReportingObligation,
  selectAer,
  selectAerCorsia,
  selectAerYear,
  selectAerTask,
  selectStatusForTask,
  selectEmpCorsiaOriginatedData,
  selectIsCorsia,
  selectReviewDecisions,
  selectReviewSectionsCompleted,
  selectAerSectionsCompleted,
};
