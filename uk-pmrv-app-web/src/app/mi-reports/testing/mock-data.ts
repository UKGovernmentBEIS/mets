import { MiReportUserDefinedHistoryResults, MiReportUserDefinedResults } from 'pmrv-api';

import { ExtendedMiReportResult } from '../core/mi-interfaces';

export const mockStandardReports = [
  { id: 1, miReportType: 'LIST_OF_ACCOUNTS_USERS_CONTACTS' },
  { id: 2, miReportType: 'COMPLETED_WORK' },
  { id: 3, miReportType: 'REGULATOR_OUTSTANDING_REQUEST_TASKS' },
];

export const mockMiReportUserDefinedResults = {
  queries: [
    {
      id: 1,
      reportName: 'Test report 1',
      description: 'This is a dummy report 1',
      categories: [
        { id: 2, name: 'Cat 2' },
        { id: 4, name: 'Cat 4' },
        { id: 8, name: 'Cat 8' },
      ],
    },
    {
      id: 2,
      reportName: 'Test report 2',
      description: 'This is a dummy report 2',
      categories: [{ id: 3, name: 'Cat 3' }],
    },
  ],
  total: 2,
} as MiReportUserDefinedResults;

export const mockMiReportCategories = [
  { id: 2, name: 'Cat 2' },
  { id: 3, name: 'Cat 3' },
];

export const mockAccountsUsersContactsMiReportResult = {
  reportType: 'LIST_OF_ACCOUNTS_USERS_CONTACTS',
  columnNames: [
    'Account type',
    'Account ID',
    'Account name',
    'Account status',
    'Permit ID',
    'permitType',
    'Legal Entity name',
    'Is User Primary contact?',
    'Is User Secondary contact?',
    'Is User Financial contact?',
    'Is User Service contact?',
    'User status',
    'name',
    'telephone',
    'email',
    'User role',
  ],
  results: [
    {
      userId: '0b4294cd-ed28-4c12-aa93-602bf13e1a74',
      'Account type': 'INSTALLATION',
      'Account ID': 1,
      'Account name': 'Installation name',
      'Account status': 'NEW',
      'Legal Entity name': 'Legal entity',
      'Is User Primary contact?': true,
      'Is User Secondary contact?': false,
      'Is User Financial contact?': true,
      'Is User Service contact?': true,
      'User status': 'ACTIVE',
      name: 'Obi Wan Kenobi',
      telephone: '+442345254566656565',
      email: 'owk@mail.com',
      'User role': 'Operator admin',
    },
    {
      userId: '"4c78efcc-dd6a-4342-a9d8-a68028702728"',
      'Account type': 'INSTALLATION',
      'Account ID': 31,
      'Account name': 'Installation name 2',
      'Account status': 'LIVE',
      'Legal Entity name': 'Legal entity 2',
      'Is User Primary contact?': true,
      'Is User Secondary contact?': false,
      'Is User Financial contact?': true,
      'Is User Service contact?': false,
      'User status': 'ACTIVE',
      name: 'Darth Vader',
      telephone: '+442345254566656562',
      email: 'dv@mail.gr',
      'User role': 'Operator admin',
    },
  ],
};

export const mockExecutedRequestActionMiReportResult = {
  reportType: 'COMPLETED_WORK',
  columnNames: [
    'Account type',
    'Account ID',
    'Account name',
    'Account status',
    'Legal Entity name',
    'Permit ID',
    'Workflow ID',
    'Workflow type',
    'Workflow status',
    'Timeline event type',
    'Timeline event Completed by',
    'Timeline event Date Completed',
  ],
  results: [
    {
      'Account type': 'INSTALLATION',
      'Account ID': 1,
      'Account name': 'Installation name',
      'Account status': 'NEW',
      'Legal Entity name': 'Legal entity Name',
      'Permit ID': 'UK-W-15',
      'Workflow ID': 'REQ-123',
      'Workflow status': 'IN_PROGRESS',
      'Workflow type': 'PERMIT_ISSUANCE',
      'Timeline event Date Completed': '2022-08-12',
      'Timeline event Completed by': 'Teo James',
      'Timeline event type': 'PERMIT_ISSUANCE_APPLICATION_GRANTED',
    },
  ],
};

export const mockVerificationBodiesUsersMiReportResult = {
  reportType: 'LIST_OF_VERIFICATION_BODY_USERS',
  columnNames: [
    'Verification body name',
    'Account status',
    'Accreditation reference number',
    'Is accredited for UK ETS Installations?',
    'Is accredited for EU ETS Installations?',
    'Is accredited for UK ETS Aviations?',
    'Is accredited for CORSIA?',
    'User role',
    'Name',
    'email',
    'telephone',
    'User status',
    'Last logon',
  ],
  results: [
    {
      'Verification body name': 'Sample Verification Body Organisation',
      'Account status': 'PENDING',
      'Accreditation reference number': '1111111',
      'Is accredited for UK ETS Installations?': false,
      'Is accredited for EU ETS Installations?': false,
      'Is accredited for UK ETS Aviations?': false,
      'Is accredited for CORSIA?': true,
      'User role': 'Verifier admin',
      Name: 'VerifierFirst1 VerifierLast1',
      email: 'g@r',
      telephone: '111111',
      'User status': 'PENDING',
      'Last logon': '06 Νοεμβρίου 2022 09:23:26',
    },
    {
      'Verification body name': 'VerificationBody2',
      'Account status': 'PENDING',
      'Accreditation reference number': '222222',
      'Is accredited for UK ETS Installations?': true,
      'Is accredited for EU ETS Installations?': false,
      'Is accredited for UK ETS Aviations?': false,
      'Is accredited for CORSIA?': false,
      'User role': 'Verifier admin',
      Name: 'First2 Last2',
      email: 'last2@o.com',
      telephone: '222222',
      'User status': 'PENDING',
      'Last logon': '07 Νοεμβρίου 2022 09:23:26',
    },
  ],
};

export const mockMiReportUserDefinedHistoryResults: MiReportUserDefinedHistoryResults = {
  total: 12,
  results: [
    {
      submissionDate: '2026-04-09T09:22:00Z',
      submittedBy: 'Regulator England',
      reasonForChange: 'Added region_name join and updated ORDER BY clause.',
      reportName: 'Active installations by permit type and region',
      categories: 'Workflow Submission Status, Management, Financial',
      description: 'Returns all active installation accounts grouped by permit type and region.',
      queryDefinition: 'SELECT\n  a.account_id,\n  r.region_name\nFROM accounts a',
      changeType: 'UPDATE',
    },
    {
      submissionDate: '2026-03-04T10:20:00Z',
      submittedBy: 'Regulator England',
      reportName: 'Active installations by permit type and region',
      categories: 'Workflow Submission Status, Management',
      description: 'Returns all active installation accounts grouped by permit type and region.',
      queryDefinition: 'SELECT * FROM accounts',
      changeType: 'CREATE',
    },
  ],
};

export const mockCustomMiReportResult: ExtendedMiReportResult = {
  reportType: 'CUSTOM',
  columnNames: ['id', 'name', 'competent_authority'],
  results: [
    { id: 1, name: 'Name 1', competent_authority: 'ENGLAND' },
    { id: 2, name: 'Name 2', competent_authority: 'WALES' },
  ],
};
