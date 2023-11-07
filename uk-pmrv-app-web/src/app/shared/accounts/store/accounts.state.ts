import { AccountSearchResultsInfoDTO, AviationAccountSearchResultsInfoDTO } from 'pmrv-api';

export type AccountSearchResult = AccountSearchResultsInfoDTO | AviationAccountSearchResultsInfoDTO;
export type AccountStatus = AccountSearchResultsInfoDTO['status'] | AviationAccountSearchResultsInfoDTO['status'];

export interface AccountsState {
  searchTerm: string;
  searchErrorSummaryVisible: boolean;
  accounts: AccountSearchResult[];
  total: number;
  paging: {
    page: number;
    pageSize: number;
  };
}

export const initialState: AccountsState = {
  searchTerm: null,
  searchErrorSummaryVisible: false,
  accounts: [],
  total: 0,
  paging: {
    page: 1,
    pageSize: 30,
  },
};
