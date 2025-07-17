import { UserState } from '@core/store/auth';

import {
  AccountNoteResponse,
  AccountOperatorsUsersAuthoritiesInfoDTO,
  AerRequestMetadata,
  AviationAccountHeaderInfoDTO,
  InstallationAccountDTO,
  InstallationAccountHeaderInfoDTO,
  InstallationAccountPermitDTO,
  LocationOnShoreDTO,
  OperatorUserDTO,
  RequestDetailsSearchResults,
  RequestNoteResponse,
  RoleDTO,
  UserAuthorityInfoDTO,
} from 'pmrv-api';

export const mockOperatorListData: AccountOperatorsUsersAuthoritiesInfoDTO = {
  authorities: [
    {
      authorityStatus: 'ACTIVE',
      firstName: 'First',
      lastName: 'User',
      roleCode: 'operator_admin',
      roleName: 'Operator admin',
      userId: 'userTest1',
      authorityCreationDate: '2019-12-21T13:42:43.050682Z',
    },
    {
      authorityStatus: 'ACTIVE',
      firstName: 'John',
      lastName: 'Doe',
      roleCode: 'operator',
      roleName: 'Operator',
      userId: 'userTest2',
      authorityCreationDate: '2020-12-21T13:42:43.050682Z',
    },
    {
      authorityStatus: 'DISABLED',
      firstName: 'Darth',
      lastName: 'Vader',
      roleCode: 'operator',
      roleName: 'Operator',
      userId: 'userTest3',
      authorityCreationDate: '2020-10-13T13:42:43.050682Z',
    },
    {
      authorityStatus: 'ACTIVE',
      firstName: 'anakin',
      lastName: 'skywalker',
      roleCode: 'operator',
      roleName: 'Operator',
      userId: 'userTest4',
      authorityCreationDate: '2021-01-13T13:42:43.050682Z',
    },
  ] as UserAuthorityInfoDTO[],
  contactTypes: {
    PRIMARY: 'userTest1',
    SECONDARY: 'userTest3',
    SERVICE: 'userTest2',
    FINANCIAL: 'userTest4',
  },
  editable: true,
};

export const mockOperatorRoleCodes: RoleDTO[] = [
  {
    name: 'Operator admin',
    code: 'operator_admin',
  },
  {
    name: 'Operator',
    code: 'operator',
  },
  {
    name: 'Consultant / Agent',
    code: 'consultant_agent',
  },
  {
    name: 'Emitter Contact',
    code: 'emitter_contact',
  },
];

export const operator: OperatorUserDTO = {
  email: 'test@host.com',
  firstName: 'Mary',
  lastName: 'Za',
  mobileNumber: { countryCode: '30', number: '1234567890' },
  phoneNumber: { countryCode: '30', number: '123456780' },
};

export const operatorUserRole: UserState = {
  domainsLoginStatuses: {
    INSTALLATION: 'ENABLED',
  },
  roleType: 'OPERATOR',
  userId: 'asdf4',
};

export const mockedAccount: InstallationAccountDTO = {
  id: 1,
  name: 'accountName',
  accountType: 'INSTALLATION',
  status: 'LIVE',
  siteName: 'siteName',
  commencementDate: '',
  competentAuthority: 'ENGLAND',
  emissionTradingScheme: 'CORSIA',
  location: {
    type: 'ONSHORE',
    gridReference: 'NN166712',
    address: {
      line1: 'line',
      line2: null,
      city: 'town',
      country: 'GR',
      postcode: '1231',
    },
  } as LocationOnShoreDTO,
  sopId: 111,
  registryId: 222,
  installationCategory: 'A_LOW_EMITTER',
  emitterType: 'GHGE',
  subsistenceCategory: 'A',
  applicationType: 'NEW_PERMIT',
  legalEntity: {
    name: 'leName',
    type: 'LIMITED_COMPANY',
    address: {
      line1: 'line',
      line2: null,
      city: 'town',
      country: 'GR',
      postcode: '1231',
    },
    referenceNumber: '11111',
    holdingCompany: {
      name: 'TEST_HC',
      registrationNumber: 'TEST_REG_NUM',
      address: {
        line1: 'TEST_ADDR_LINE1',
        city: 'TEST_CITY',
        postcode: 'TEST_POSTCODE',
      },
    },
  },
  faStatus: true,
};

export const mockedAccountPermit: InstallationAccountPermitDTO = {
  account: mockedAccount,
  permit: {
    id: 'permitId',
    activationDate: '2023-01-01',
    fileDocument: {
      name: 'permitDoc.pdf',
      uuid: 'fdca5cc0-e2a4-4dd1-a5e3-107d58319f41',
    },
    permitAttachments: {
      'dff684b0-4e93-4b8e-a3b3-3e398976b7a2': 'att1.pdf',
      'ec0729c1-80a5-416b-8682-9d9cfa315585': 'att2.pdf',
    },
  },
};

export const mockedOffshoreAccount = {
  id: 38,
  name: 'Operator 11 Instaccount',
  accountType: 'INSTALLATION',
  status: 'APPROVED',
  siteName: 'siteName',
  location: {
    type: 'OFFSHORE',
    latitude: {
      degree: 12,
      minute: 12,
      second: 4,
      cardinalDirection: 'NORTH',
    },
    longitude: {
      degree: 3,
      minute: 3,
      second: 4,
      cardinalDirection: 'EAST',
    },
  },
  emitterId: 'EM00038',
  legalEntity: {
    name: 'Instaccount',
    type: 'LIMITED_COMPANY',
    address: {
      line1: 'line',
      line2: null,
      city: 'town',
      country: 'GR',
      postcode: '1231',
    },
    referenceNumber: '09546038',
  },
};

export const mockedOffshoreAccountPermit = {
  account: mockedOffshoreAccount,
  permit: {
    id: 'permitId',
    permitAttachments: {},
  },
};

export const mockWorkflowResults: RequestDetailsSearchResults = {
  requestDetails: [
    {
      id: '2',
      requestType: 'PERMIT_ISSUANCE',
      requestStatus: 'IN_PROGRESS',
      creationDate: new Date('2022-12-12').toISOString(),
    },
    {
      id: '1',
      requestType: 'INSTALLATION_ACCOUNT_OPENING',
      requestStatus: 'APPROVED',
      creationDate: new Date('2022-12-11').toISOString(),
    },
  ],
  total: 2,
};

export const mockInspectionsResults: RequestDetailsSearchResults = {
  requestDetails: [
    {
      id: 'INS00055-2022-1',
      requestType: 'INSTALLATION_AUDIT',
      requestStatus: 'COMPLETED',
      creationDate: '2024-08-27',
      requestMetadata: {
        type: 'INSTALLATION_INSPECTION',
        year: '2022',
      } as any,
    },
    {
      id: 'INS00055-2021-1',
      requestType: 'INSTALLATION_AUDIT',
      requestStatus: 'IN_PROGRESS',
      creationDate: '2024-08-26',
      requestMetadata: {
        type: 'INSTALLATION_INSPECTION',
        year: '2021',
      } as any,
    },
    {
      id: 'INS00055-4',
      requestType: 'INSTALLATION_ONSITE_INSPECTION',
      requestStatus: 'IN_PROGRESS',
      creationDate: '2024-08-26',
      requestMetadata: {
        type: 'INSTALLATION_INSPECTION',
        year: '2024',
      } as any,
    },
    {
      id: 'INS00055-3',
      requestType: 'INSTALLATION_ONSITE_INSPECTION',
      requestStatus: 'COMPLETED',
      creationDate: '2024-08-26',
      requestMetadata: {
        type: 'INSTALLATION_INSPECTION',
        year: '2024',
      } as any,
    },
  ],
};

export const mockReportsResults: RequestDetailsSearchResults = {
  requestDetails: [
    {
      id: '1',
      requestType: 'AER',
      requestStatus: 'COMPLETED',
      creationDate: '2021-03-26',
      requestMetadata: {
        emissions: '10',
        overallAssessmentType: 'NOT_VERIFIED',
        type: 'AER',
        year: 2021,
      } as AerRequestMetadata,
    },
    {
      id: '2',
      requestType: 'AER',
      requestStatus: 'COMPLETED',
      creationDate: '2020-01-13',
      requestMetadata: {
        emissions: '5',
        overallAssessmentType: 'VERIFIED_AS_SATISFACTORY',
        type: 'AER',
        year: 2020,
      } as AerRequestMetadata,
    },
    {
      id: '3',
      requestType: 'AER',
      requestStatus: 'IN_PROGRESS',
      creationDate: '2020-02-01',
      requestMetadata: {
        emissions: '10',
        overallAssessmentType: 'VERIFIED_WITH_COMMENTS',
        type: 'AER',
        year: 2020,
      } as AerRequestMetadata,
    },
  ],
  total: 3,
};

export const mockYearEmissionsResults = {
  '2020': '10',
};

export const mockAccountNotesResults: AccountNoteResponse = {
  accountNotes: [
    {
      lastUpdatedOn: '2022-11-24T14:00:12.723Z',
      payload: { note: 'The note 1', files: { '0500d8b5-8cfb-4430-8edd-75f7612a7287': 'file 1' } },
      submitter: 'Submitter 1',
    },
    {
      lastUpdatedOn: '2022-11-25T15:00:12.723Z',
      payload: { note: 'The note 2' },
      submitter: 'Submitter 2',
    },
  ],
  totalItems: 2,
};

export const mockRequestNotesResults: RequestNoteResponse = {
  requestNotes: [
    {
      lastUpdatedOn: '2022-11-24T14:00:12.723Z',
      payload: { note: 'The note 1', files: { '0500d8b5-8cfb-4430-8edd-75f7612a7287': 'file 1' } },
      submitter: 'Submitter 1',
    },
    {
      lastUpdatedOn: '2022-11-25T15:00:12.723Z',
      payload: { note: 'The note 2' },
      submitter: 'Submitter 2',
    },
  ],
  totalItems: 2,
};

export const mockedAccountHeaderInfo: InstallationAccountHeaderInfoDTO = {
  id: 1,
  emitterType: 'GHGE',
  installationCategory: 'A',
  name: 'accountName',
  permitId: 'permitId',
  status: 'LIVE',
};

export const mockedAviationAccountHeaderInfo: AviationAccountHeaderInfoDTO = {
  id: 1,
  name: 'aviationUser',
  empId: 'empId',
  status: 'LIVE',
  emissionTradingScheme: 'UK_ETS_AVIATION',
};
