import { DoeTaskKey } from '@aviation/request-task/store';
import { TaskItemStatus, TaskSection } from '@shared/task-list/task-list.interface';

import {
  AviationDoECorsiaApplicationSubmitRequestTaskPayload,
  RequestTaskActionProcessDTO,
  RequestTaskDTO,
} from 'pmrv-api';

export const doeHeaderTaskMap: Partial<Record<DoeTaskKey, string>> = {
  doe: 'Aviation emissions',
};

const BASIC_DOE_APPLICATION_TASKS: TaskSection<any>[] = [
  {
    title: 'Details',
    tasks: [
      {
        name: 'doe',
        linkText: doeHeaderTaskMap['doe'],
        link: 'doe/aviation-details',
      },
    ],
  },
];

export function getTaskStatusByTaskCompletionState(completionState?: boolean): TaskItemStatus {
  return completionState != null ? (completionState ? 'complete' : 'in progress') : 'not started';
}

export function getDoeSections(payload: AviationDoECorsiaApplicationSubmitRequestTaskPayload): TaskSection<any>[] {
  return BASIC_DOE_APPLICATION_TASKS.map((section) => {
    return {
      ...section,
      tasks: section.tasks.map((task) => {
        const status = getTaskStatusByTaskCompletionState(payload.sectionCompleted);

        const link = status === 'complete' ? 'doe/aviation-details/summary' : 'doe/aviation-details';

        return {
          ...task,
          status,
          link,
        };
      }),
    };
  });
}

export function doeDocumentPreviewRequestTaskActionTypesMap(
  requestTaskType: RequestTaskDTO['type'],
): RequestTaskActionProcessDTO['requestTaskActionType'] {
  switch (requestTaskType) {
    case 'AVIATION_DOE_CORSIA_APPLICATION_PEER_REVIEW':
      return 'AVIATION_DOE_CORSIA_SUBMIT_PEER_REVIEW_DECISION';
  }
}
