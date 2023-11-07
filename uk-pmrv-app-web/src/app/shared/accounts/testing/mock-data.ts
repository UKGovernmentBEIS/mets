import { UserState } from '@core/store/auth';

import { AccountSearchResults } from 'pmrv-api';

export const operatorUserRole: UserState = {
  domainsLoginStatuses: {
    INSTALLATION: 'ENABLED',
  },
  roleType: 'OPERATOR',
  userId: 'asdf4',
};

export const regulatorUserRole: UserState = {
  domainsLoginStatuses: {
    INSTALLATION: 'ENABLED',
  },
  roleType: 'REGULATOR',
  userId: 'asdf4',
};

export const mockAccountResults: AccountSearchResults = {
  accounts: [
    { id: 1, name: 'account1', emitterId: 'EM00001', status: 'LIVE', legalEntityName: 'le1' },
    { id: 1, name: 'account2', emitterId: 'EM00002', status: 'LIVE', legalEntityName: 'le2' },
    { id: 1, name: 'account3', emitterId: 'EM00003', status: 'LIVE', legalEntityName: 'le3' },
  ],
  total: 3,
};
