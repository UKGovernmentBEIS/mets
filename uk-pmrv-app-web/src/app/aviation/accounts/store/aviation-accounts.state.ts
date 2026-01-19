import { Paging } from '@shared/model';

import {
  AviationAccountCreationDTO,
  AviationAccountEmpDTO,
  AviationAccountReportingStatusHistoryCreationDTO,
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
  reportingStatus: ReportingStatusState;
}

export interface ReportingStatusHistoryState {
  history: AviationAccountReportingStatusHistoryDTO[];
  total: number;
  paging: {
    page: number;
    pageSize: number;
  };
}

export interface ReportingStatusState {
  statuses: Array<ReportingStatusListItem>;
  currentStatus?: ReportingStatusListItem;
  upsertStatus?: AviationAccountReportingStatusHistoryCreationDTO;
  total: number;
  paging: Paging;
}

export interface ReportingStatusListItem {
  status: AviationAccountReportingStatusHistoryDTO['status'];
  isReported?: boolean;
  reason?: string;
  year: string;
  lastUpdate?: string;
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
  closureReason?: string;
  closedByName?: string;
  closingDate?: string;
}

export const initialReportingStatusState: ReportingStatusState = {
  statuses: [],
  total: 0,
  paging: {
    page: 1,
    pageSize: 5,
  },
};

export const initialCreateAccountState: CreateAccountState = {
  newAccount: null,
  isInitiallySubmitted: false,
  isSubmitted: false,
};

export const initialCurrentAccountState: CurrentAccountState = {
  account: null,
  reportingStatus: initialReportingStatusState,
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
