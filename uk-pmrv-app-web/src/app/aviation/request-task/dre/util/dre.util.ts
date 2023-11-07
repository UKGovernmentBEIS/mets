import { TaskItemStatus, TaskSection } from '@shared/task-list/task-list.interface';

import { AviationDreUkEtsApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { DreTaskKey } from '../../store';

export const dreHeaderTaskMap: Partial<Record<DreTaskKey, string>> = {
  dre: 'Aviation emissions',
};

export function getTaskStatusByTaskCompletionState(completionState?: boolean): TaskItemStatus {
  return completionState != null ? (completionState ? 'complete' : 'in progress') : 'not started';
}

export function resolveSubmissionTaskStatus(
  tasksCompleted: { [key: string]: boolean[] },
  availableTasks: string[],
): TaskItemStatus {
  return Object.keys(tasksCompleted).length >= availableTasks.length &&
    Object.values(tasksCompleted).every((tc) => tc[0] === true)
    ? 'not started'
    : 'cannot start yet';
}

export function getDreSections(payload: AviationDreUkEtsApplicationSubmitRequestTaskPayload): TaskSection<any>[] {
  return BASIC_AER_APPLICATION_TASKS.map((section) => {
    return {
      ...section,
      tasks: section.tasks.map((task) => {
        const status = getTaskStatusByTaskCompletionState(payload.sectionCompleted);

        const link = status === 'complete' ? 'dre/aviation-details/summary' : 'dre/aviation-details';

        return {
          ...task,
          status,
          link,
        };
      }),
    };
  });
}

const BASIC_AER_APPLICATION_TASKS: TaskSection<any>[] = [
  {
    title: 'Details',
    tasks: [
      {
        name: 'dre',
        linkText: dreHeaderTaskMap['dre'],
        link: 'dre/aviation-details',
      },
    ],
  },
];
