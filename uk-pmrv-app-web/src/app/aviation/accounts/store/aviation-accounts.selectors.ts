import { map, OperatorFunction, pipe } from 'rxjs';

import {
  AccountDetailsHistoryListResponse,
  AviationAccountEmpDTO,
  AviationAccountReportingObligationFirstYearDTO,
  AviationAccountReportingStatusHistoryCreationDTO,
  AviationAccountReportingStatusHistoryListResponse,
  EmpDetailsDTO,
} from 'pmrv-api';

import { Paging } from '../../../shared/model';
import {
  AccountDetailsHistoryState,
  AviationAccountDetails,
  AviationAccountsState,
  CreateAccountState,
  ReportingStatusHistoryState,
  ReportingStatusListItem,
  ReportingStatusState,
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

export const selectReportingStatus: OperatorFunction<AviationAccountsState, ReportingStatusState> = pipe(
  map((state) => state.currentAccount?.reportingStatus),
);

export const selectUpsertReportingStatus: OperatorFunction<
  AviationAccountsState,
  AviationAccountReportingStatusHistoryCreationDTO
> = pipe(
  selectReportingStatus,
  map((state) => state.upsertStatus),
);

export const selectUpsertFyro: OperatorFunction<AviationAccountsState, AviationAccountReportingObligationFirstYearDTO> =
  pipe(map((state) => state.currentAccount?.upsertFirstYearOfReportingObligation));

export const selectReportingStatusHistory: OperatorFunction<
  AviationAccountsState,
  AviationAccountReportingStatusHistoryListResponse['reportingStatusHistoryList']
> = pipe(
  selectReportingStatusHistoryState,
  map((state) => state?.history),
);

export const selectReportingStatusHistoryTotal: OperatorFunction<AviationAccountsState, number> = pipe(
  selectReportingStatusHistoryState,
  map((state) => state?.total),
);

export const selectReportingStatusHistoryPaging: OperatorFunction<AviationAccountsState, Paging> = pipe(
  selectReportingStatusHistoryState,
  map((state) => state?.paging),
);

export const selectReportingStatusHistoryPage: OperatorFunction<AviationAccountsState, number> = pipe(
  selectReportingStatusHistoryPaging,
  map((paging) => paging?.page),
);
export const selectReportingStatusHistoryPageSize: OperatorFunction<AviationAccountsState, number> = pipe(
  selectReportingStatusHistoryPaging,
  map((paging) => paging?.pageSize),
);

export const selectReportingStatusesTotal: OperatorFunction<AviationAccountsState, number> = pipe(
  selectReportingStatus,
  map((state) => state?.total),
);

export const selectReportingStatusesPaging: OperatorFunction<AviationAccountsState, Paging> = pipe(
  selectReportingStatus,
  map((state) => state?.paging),
);

export const selectReportingStatusesPage: OperatorFunction<AviationAccountsState, number> = pipe(
  selectReportingStatusesPaging,
  map((paging) => paging?.page),
);
export const selectReportingStatusesPageSize: OperatorFunction<AviationAccountsState, number> = pipe(
  selectReportingStatusesPaging,
  map((paging) => paging?.pageSize),
);

export const selectReportingStatusesList: OperatorFunction<
  AviationAccountsState,
  Array<ReportingStatusListItem>
> = pipe(
  selectReportingStatus,
  map((state) => state?.statuses),
);

export const selectAccountDetailsHistoryState: OperatorFunction<AviationAccountsState, AccountDetailsHistoryState> =
  pipe(map((state) => state.currentAccount?.accountDetailsHistory));

export const selectAccountDetailsHistory: OperatorFunction<
  AviationAccountsState,
  AccountDetailsHistoryListResponse['accountDetailsHistoryList']
> = pipe(
  selectAccountDetailsHistoryState,
  map((state) => state?.history),
);

export const selectAccountDetailsHistoryTotal: OperatorFunction<AviationAccountsState, number> = pipe(
  selectAccountDetailsHistoryState,
  map((state) => state?.total),
);

export const selectAccountDetailsHistoryPaging: OperatorFunction<AviationAccountsState, Paging> = pipe(
  selectAccountDetailsHistoryState,
  map((state) => state?.paging),
);

export const selectAccountDetailsHistoryPage: OperatorFunction<AviationAccountsState, number> = pipe(
  selectAccountDetailsHistoryPaging,
  map((paging) => paging?.page),
);
export const selectAccountDetailsHistoryPageSize: OperatorFunction<AviationAccountsState, number> = pipe(
  selectAccountDetailsHistoryPaging,
  map((paging) => paging?.pageSize),
);
