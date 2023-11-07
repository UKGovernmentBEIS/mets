import { map, OperatorFunction, pipe } from 'rxjs';

import produce from 'immer';
import moment from 'moment';

import {
  ItemDTO,
  RequestActionInfoDTO,
  RequestInfoDTO,
  RequestTaskDTO,
  RequestTaskItemDTO,
  RequestTaskPayload,
} from 'pmrv-api';

import { RequestTaskState } from './request-task.state';
import {
  AerRequestTaskPayload,
  DreRequestTaskPayload,
  EmpRequestTaskPayloadUkEts,
  SectionsCompleted,
} from './request-task.types';

const selectRequestTaskItem: OperatorFunction<RequestTaskState, RequestTaskItemDTO> = pipe(
  map((state) => state?.requestTaskItem),
);
const selectRelatedActions: OperatorFunction<RequestTaskState, RequestTaskItemDTO['allowedRequestTaskActions']> = pipe(
  selectRequestTaskItem,
  map((state) => state?.allowedRequestTaskActions),
);
const selectRequestInfo: OperatorFunction<RequestTaskState, RequestInfoDTO> = pipe(
  selectRequestTaskItem,
  map((state) => state?.requestInfo),
);
const selectRequestTask: OperatorFunction<RequestTaskState, RequestTaskDTO> = pipe(
  selectRequestTaskItem,
  map((state) => state?.requestTask),
);
const selectUserAssignCapable: OperatorFunction<RequestTaskState, boolean> = pipe(
  selectRequestTaskItem,
  map((state) => state?.userAssignCapable),
);
const selectRequestTaskPayload: OperatorFunction<RequestTaskState, RequestTaskPayload> = pipe(
  selectRequestTask,
  map((state) => state?.payload),
);
const selectAssigneeUserId: OperatorFunction<RequestTaskState, string> = pipe(
  selectRequestTask,
  map((state) => state?.assigneeUserId),
);
const selectAssigneeFullName: OperatorFunction<RequestTaskState, string> = pipe(
  selectRequestTask,
  map((state) => state?.assigneeFullName),
);
const selectRequestTaskType: OperatorFunction<RequestTaskState, RequestTaskDTO['type']> = pipe(
  selectRequestTask,
  map((state) => state?.type),
);
const selectRelatedTasks: OperatorFunction<RequestTaskState, ItemDTO[]> = pipe(
  map(
    (state) =>
      state?.relatedTasks.filter((t) => {
        return t.taskId !== state?.requestTaskItem.requestTask.id;
      }) ?? [],
  ),
);
const selectTimeline: OperatorFunction<RequestTaskState, RequestActionInfoDTO[]> = pipe(
  map((state) =>
    produce(state.timeline, (timeline) =>
      timeline.sort((a, b) => (moment(a.creationDate).isBefore(moment(b.creationDate)) ? 1 : -1)),
    ),
  ),
);

const selectIsTaskReassigned: OperatorFunction<RequestTaskState, boolean> = pipe(
  map((state) => state?.isTaskReassigned),
);
const selectTaskReassignedTo: OperatorFunction<RequestTaskState, string> = pipe(
  map((state) => state?.taskReassignedTo),
);

const selectIsEditable: OperatorFunction<RequestTaskState, boolean> = pipe(map((state) => state?.isEditable));

const selectTasksCompleted: OperatorFunction<RequestTaskState, SectionsCompleted> = pipe(
  selectRequestTaskPayload,
  map((payload) => {
    switch (payload?.payloadType) {
      case 'AVIATION_DRE_UKETS_APPLICATION_SUBMIT_PAYLOAD':
        return (payload as DreRequestTaskPayload)?.sectionCompleted;
      case 'AVIATION_AER_UKETS_APPLICATION_SUBMIT_PAYLOAD':
        return (payload as AerRequestTaskPayload)?.aerSectionsCompleted;
      default:
        return (payload as EmpRequestTaskPayloadUkEts)?.empSectionsCompleted;
    }
  }),
);

const selectCompetentAuthority: OperatorFunction<RequestTaskState, RequestInfoDTO['competentAuthority']> = pipe(
  selectRequestInfo,
  map((state) => state.competentAuthority),
);

export const requestTaskQuery = {
  selectRequestTaskItem,
  selectRequestInfo,
  selectRequestTask,
  selectRequestTaskPayload,
  selectUserAssignCapable,
  selectAssigneeUserId,
  selectAssigneeFullName,
  selectRequestTaskType,
  selectRelatedTasks,
  selectRelatedActions,
  selectTimeline,
  selectIsTaskReassigned,
  selectTaskReassignedTo,
  selectIsEditable,
  selectTasksCompleted,
  selectCompetentAuthority,
};
