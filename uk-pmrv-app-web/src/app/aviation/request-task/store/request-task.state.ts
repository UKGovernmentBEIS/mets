import { TaskItemStatus } from '@shared/task-list/task-list.interface';

import { ItemDTO, RequestActionInfoDTO, RequestTaskItemDTO } from 'pmrv-api';

interface TaskState {
  status: TaskItemStatus;
}

export type TasksState = { [key: string]: TaskState };

export interface RequestTaskState {
  requestTaskItem: RequestTaskItemDTO;
  relatedTasks: ItemDTO[];
  timeline: RequestActionInfoDTO[];
  isTaskReassigned: boolean;
  taskReassignedTo: string;
  isEditable: boolean;
  tasksState: TasksState;
}

export const initialState: RequestTaskState = {
  requestTaskItem: null,
  relatedTasks: [],
  timeline: [],
  isTaskReassigned: false,
  taskReassignedTo: null,
  isEditable: false,
  tasksState: {
    abbreviations: { status: 'not started' },
  },
};
