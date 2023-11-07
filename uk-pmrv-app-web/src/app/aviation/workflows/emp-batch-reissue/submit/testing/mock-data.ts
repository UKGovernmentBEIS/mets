import { EmpBatchReissueState } from '@aviation/workflows/emp-batch-reissue/submit/store/emp-batch-reissue.state';

import { RegulatorUsersAuthoritiesInfoDTO } from 'pmrv-api';

export const mockSubmitCompletedState: EmpBatchReissueState = {
  reportingStatuses: ['REQUIRED_TO_REPORT', 'EXEMPT_COMMERCIAL'],
  emissionTradingSchemes: ['UK_ETS_AVIATION'],
  signatory: '3reg',
};

export const regulators: RegulatorUsersAuthoritiesInfoDTO = {
  caUsers: [
    {
      userId: '1reg',
      firstName: 'fn1',
      lastName: 'ln1',
      authorityStatus: 'ACTIVE',
      locked: false,
      authorityCreationDate: '2020-12-15T12:38:12.846716Z',
    },
    {
      userId: '2reg',
      firstName: 'fn2',
      lastName: 'ln2',
      authorityStatus: 'ACTIVE',
      locked: false,
      authorityCreationDate: '2020-11-10T12:38:12.846716Z',
    },
    {
      userId: '3reg',
      firstName: 'fn3',
      lastName: 'ln3',
      authorityStatus: 'ACTIVE',
      locked: false,
      authorityCreationDate: '2021-01-10T12:38:12.846716Z',
    },
    {
      userId: '4reg',
      firstName: 'fn4',
      lastName: 'ln4',
      authorityStatus: 'PENDING',
      locked: true,
      authorityCreationDate: '2021-02-8T12:38:12.846716Z',
    },
    {
      userId: '5reg',
      firstName: 'fn5',
      lastName: 'ln5',
      authorityStatus: 'DISABLED',
      locked: false,
      authorityCreationDate: '2020-12-14T12:38:12.846716Z',
    },
  ],
  editable: true,
};
