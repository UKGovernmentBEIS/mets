import { UserState } from '@core/store/auth';

import { ItemDTO, RequestActionInfoDTO, RequestTaskItemDTO } from 'pmrv-api';

export interface CommonTasksState {
  requestTaskItem: RequestTaskItemDTO;
  relatedTasks: ItemDTO[];
  timeLineActions: RequestActionInfoDTO[];
  storeInitialized: boolean;
  isEditable: boolean;
  user: UserState;
}

export const initialState: CommonTasksState = {
  requestTaskItem: undefined,
  relatedTasks: undefined,
  timeLineActions: undefined,
  storeInitialized: false,
  isEditable: undefined,
  user: undefined,
};
