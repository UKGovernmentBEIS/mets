import { map, OperatorFunction, pipe } from 'rxjs';

import { AviationAccountEmpDTO, AviationAccountReportingStatusHistoryDTO, EmpDetailsDTO } from 'pmrv-api';

import { Paging } from '../../../shared/model';
import {
  AviationAccountDetails,
  AviationAccountsState,
  CreateAccountState,
  ReportingStatusHistoryState,
} from './aviation-accounts.state';

export const selectCreateAccountState: OperatorFunction<AviationAccountsState, CreateAccountState> = pipe(
  map((state) => state.createAccount),
);
export const selectNewAccount: OperatorFunction<AviationAccountsState, AviationAccountDetails> = pipe(
  selectCreateAccountState,
  map((state) => state.newAccount),
);
export const selectIsInitiallySubmitted: OperatorFunction<AviationAccountsState, boolean> = pipe(
  selectCreateAccountState,
  map((state) => state.isInitiallySubmitted),
);
export const selectIsSubmitted: OperatorFunction<AviationAccountsState, boolean> = pipe(
  selectCreateAccountState,
  map((state) => state.isSubmitted),
);

export const selectAccount: OperatorFunction<AviationAccountsState, AviationAccountEmpDTO> = pipe(
  map((state) => state.currentAccount?.account),
);

export const selectAccountInfo: OperatorFunction<AviationAccountsState, AviationAccountDetails> = pipe(
  selectAccount,
  map((state) => state?.aviationAccount),
);

export const selectAccountEmp: OperatorFunction<AviationAccountsState, EmpDetailsDTO> = pipe(
  selectAccount,
  map((state) => state?.emp),
);

export const selectReportingStatusHistoryState: OperatorFunction<AviationAccountsState, ReportingStatusHistoryState> =
  pipe(map((state) => state.currentAccount?.reportingStatusHistory));

export const selectReportingStatusHistory: OperatorFunction<
  AviationAccountsState,
  AviationAccountReportingStatusHistoryDTO[]
> = pipe(
  selectReportingStatusHistoryState,
  map((state) => state?.history),
);

export const selectTotal: OperatorFunction<AviationAccountsState, number> = pipe(
  selectReportingStatusHistoryState,
  map((state) => state?.total),
);

export const selectPaging: OperatorFunction<AviationAccountsState, Paging> = pipe(
  selectReportingStatusHistoryState,
  map((state) => state?.paging),
);

export const selectPage: OperatorFunction<AviationAccountsState, number> = pipe(
  selectPaging,
  map((paging) => paging?.page),
);
export const selectPageSize: OperatorFunction<AviationAccountsState, number> = pipe(
  selectPaging,
  map((paging) => paging?.pageSize),
);
