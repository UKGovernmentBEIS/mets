import { ExtendedMiReportResult } from '../core/mi-interfaces';

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

export const mockCustomMiReportResult: ExtendedMiReportResult = {
  reportType: 'CUSTOM',
  columnNames: ['id', 'name', 'competent_authority'],
  results: [
    { id: 1, name: 'Name 1', competent_authority: 'ENGLAND' },
    { id: 2, name: 'Name 2', competent_authority: 'WALES' },
  ],
};
