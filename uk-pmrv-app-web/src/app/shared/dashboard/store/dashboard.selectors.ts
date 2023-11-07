import { map, OperatorFunction, pipe } from 'rxjs';

import { DashboardState, WorkflowItemsAssignmentType } from '@shared/dashboard/store/dashboard.state';
import { Paging } from '@shared/model';

import { ItemDTO } from 'pmrv-api';

export const selectActiveTab: OperatorFunction<DashboardState, WorkflowItemsAssignmentType> = pipe(
  map((state) => state.activeTab),
);
export const selectItems: OperatorFunction<DashboardState, ItemDTO[]> = pipe(map((state) => state.items));
export const selectTotal: OperatorFunction<DashboardState, number> = pipe(map((state) => state.total));
export const selectPaging: OperatorFunction<DashboardState, Paging> = pipe(map((state) => state.paging));
export const selectPage: OperatorFunction<DashboardState, number> = pipe(
  selectPaging,
  map((state) => state.page),
);
export const selectPageSize: OperatorFunction<DashboardState, number> = pipe(
  selectPaging,
  map((state) => state.pageSize),
);
