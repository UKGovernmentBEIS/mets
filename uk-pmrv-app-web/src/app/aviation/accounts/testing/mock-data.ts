import {
  AviationAccountEmpDTO,
  AviationAccountReportingStatusHistoryListResponse,
  RequestDetailsSearchResults,
} from 'pmrv-api';

import { ReportingStatusListItem } from '../store';

export const mockedAccount: AviationAccountEmpDTO = {
  aviationAccount: {
    accountType: 'AVIATION',
    commencementDate: '2023-01-01',
    competentAuthority: 'ENGLAND',
    crcoCode: 'TEST',
    emissionTradingScheme: 'CORSIA',
    id: 1,
    name: 'TEST',
    sopId: 3,
    status: 'NEW',
  },
};

export const mockedAccountClosed: AviationAccountEmpDTO = {
  aviationAccount: {
    ...mockedAccount.aviationAccount,
    status: 'CLOSED',
    closureReason: 'reason provided',
    closedByName: 'Regulator A Name',
    closingDate: '2023-05-05T03:00:00Z',
  },
};

export const mockWorkflowEMPResults: RequestDetailsSearchResults = {
  requestDetails: [
    {
      id: '2',
      requestType: 'EMP_ISSUANCE_UKETS',
      requestStatus: 'IN_PROGRESS',
      creationDate: new Date('2022-12-12').toISOString(),
    },
  ],
  total: 1,
};

export const mockReportingStatusHistoryResults: AviationAccountReportingStatusHistoryListResponse = {
  reportingStatusHistoryList: [
    {
      status: 'REQUIRED_TO_REPORT',
      reason: 'some reason',
      year: 2022,
      submissionDate: new Date('2022-12-12').toISOString(),
      submitterName: 'Ted Lasso',
    },
    {
      status: 'EXEMPT_COMMERCIAL',
      reason: 'another reason',
      year: 2022,
      submissionDate: new Date('2022-11-12').toISOString(),
      submitterName: 'Roy Kent',
    },
    {
      status: 'EXEMPT_COMMERCIAL',
      reason: 'some other reason',
      year: 2022,
      submissionDate: new Date('2022-09-12').toISOString(),
      submitterName: 'Dani Rojas',
    },
  ],
  total: 3,
};

export const mockReportingStatusResults: ReportingStatusListItem[] = [
  {
    status: 'EXEMPT_COMMERCIAL',
    year: '2025',
    lastUpdate: '2025-12-16T15:13:06.235553Z',
  },
  {
    status: 'REQUIRED_TO_REPORT',
    year: '2024',
    lastUpdate: '2025-12-16T14:10:53.51396Z',
  },
];
