import {
  AviationAccountCreationDTO,
  AviationAccountEmpDTO,
  AviationAccountReportingStatusHistoryDTO,
  LegalEntityDTO,
  LocationDTO,
} from 'pmrv-api';

export interface CreateAccountState {
  newAccount: AviationAccountCreationDTO | null;
  isInitiallySubmitted: boolean;
  isSubmitted: boolean;
}

export interface AviationAccountsState {
  createAccount: CreateAccountState;
  currentAccount: CurrentAccountState;
}

export interface CurrentAccountState {
  account: AviationAccountEmpDTO;
  reportingStatusHistory: ReportingStatusHistoryState;
}

export interface ReportingStatusHistoryState {
  history: AviationAccountReportingStatusHistoryDTO[];
  total: number;
  paging: {
    page: number;
    pageSize: number;
  };
}

export interface AviationAccountDetails {
  acceptedDate?: string;
  accountType?: 'AVIATION' | 'INSTALLATION';
  commencementDate?: string;
  competentAuthority?: 'ENGLAND' | 'NORTHERN_IRELAND' | 'OPRED' | 'SCOTLAND' | 'WALES';
  crcoCode?: string;
  emissionTradingScheme?: 'CORSIA' | 'EU_ETS_INSTALLATIONS' | 'UK_ETS_AVIATION' | 'UK_ETS_INSTALLATIONS';
  id?: number;
  legalEntity?: LegalEntityDTO;
  location?: LocationDTO;
  name?: string;
  registryId?: number;
  sopId?: number;
  status?: 'LIVE' | 'NEW' | 'CLOSED';
  reportingStatus?: 'EXEMPT_COMMERCIAL' | 'EXEMPT_NON_COMMERCIAL' | 'REQUIRED_TO_REPORT';
  reportingStatusReason?: string;
  closureReason?: string;
  closedByName?: string;
  closingDate?: string;
}

export const initialCreateAccountState: CreateAccountState = {
  newAccount: null,
  isInitiallySubmitted: false,
  isSubmitted: false,
};

export const initialCurrentAccountState: CurrentAccountState = {
  account: null,
  reportingStatusHistory: {
    history: [],
    total: 0,
    paging: {
      page: 1,
      pageSize: 30,
    },
  },
};

export const initialState: AviationAccountsState = {
  createAccount: initialCreateAccountState,
  currentAccount: initialCurrentAccountState,
};
