import { Paging } from '@shared/model';

import { ItemDTO } from 'pmrv-api';

export type WorkflowItemsAssignmentType = 'assigned-to-me' | 'assigned-to-others' | 'unassigned';

export interface DashboardState {
  activeTab: WorkflowItemsAssignmentType;
  items: ItemDTO[];
  total: number;
  paging: Paging;
  order: 'NEWEST_FIRST' | 'NEAREST_DUE_DATE';
  filter: string;
  accountSearchTerm: string;
}

export const initialState: DashboardState = {
  activeTab: 'assigned-to-me',
  items: [],
  total: 0,
  paging: {
    page: 1,
    pageSize: 10,
  },
  order: 'NEWEST_FIRST',
  filter: '',
  accountSearchTerm: '',
};
