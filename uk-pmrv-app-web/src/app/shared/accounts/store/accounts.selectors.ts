import { map, OperatorFunction, pipe } from 'rxjs';

import { Paging } from '../../model';
import { AccountsState } from './accounts.state';

export const selectSearchTerm: OperatorFunction<AccountsState, string> = pipe(map((state) => state.searchTerm));
export const selectSearchErrorSummaryVisible: OperatorFunction<AccountsState, boolean> = pipe(
  map((state) => state.searchErrorSummaryVisible),
);
export const selectAccounts: OperatorFunction<AccountsState, AccountsState['accounts']> = pipe(
  map((state) => state.accounts),
);
export const selectTotal: OperatorFunction<AccountsState, number> = pipe(map((state) => state.total));
export const selectPaging: OperatorFunction<AccountsState, Paging> = pipe(map((state) => state.paging));
export const selectPage: OperatorFunction<AccountsState, number> = pipe(
  selectPaging,
  map((paging) => paging.page),
);
export const selectPageSize: OperatorFunction<AccountsState, number> = pipe(
  selectPaging,
  map((paging) => paging.pageSize),
);
