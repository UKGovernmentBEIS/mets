import { UserState } from '@core/store/auth';

import { AuthorityManagePermissionDTO, RegulatorUserDTO, RegulatorUsersAuthoritiesInfoDTO } from 'pmrv-api';

export const mockRegulatorsRouteData: { regulators: RegulatorUsersAuthoritiesInfoDTO } = {
  regulators: {
    caUsers: [
      {
        userId: '1reg',
        firstName: 'Alfyn',
        lastName: 'Octo',
        authorityStatus: 'DISABLED',
        locked: false,
        authorityCreationDate: '2020-12-14T12:38:12.846716Z',
      },
      {
        userId: '2reg',
        firstName: 'Therion',
        lastName: 'Path',
        authorityStatus: 'ACTIVE',
        locked: true,
        authorityCreationDate: '2020-12-15T12:38:12.846716Z',
      },
      {
        userId: '3reg',
        firstName: 'Olberik',
        lastName: 'Traveler',
        authorityStatus: 'ACTIVE',
        locked: true,
        authorityCreationDate: '2020-11-10T12:38:12.846716Z',
      },
      {
        userId: '4reg',
        firstName: 'andrew',
        lastName: 'webber',
        authorityStatus: 'ACTIVE',
        locked: false,
        authorityCreationDate: '2021-01-10T12:38:12.846716Z',
      },
      {
        userId: '5reg',
        firstName: 'William',
        lastName: 'Walker',
        authorityStatus: 'PENDING',
        locked: true,
        authorityCreationDate: '2021-02-8T12:38:12.846716Z',
      },
    ],
    editable: true,
  },
};

export const mockRegulatorUserStatus: UserState = {
  domainsLoginStatuses: { INSTALLATION: 'ENABLED' },
  roleType: 'REGULATOR',
  userId: '111',
};

export const mockRegulatorUser: {
  user: RegulatorUserDTO;
  permissions: AuthorityManagePermissionDTO;
} = {
  user: {
    email: 'test@host.com',
    firstName: 'John',
    lastName: 'Doe',
    jobTitle: 'developer',
    phoneNumber: '23456',
    mobileNumber: '55444',
    signature: {
      name: 'mySignature.bmp',
      uuid: '60fe9548-ac65-492a-b057-60033b0fbbed',
    },
  },
  permissions: {
    editable: true,
    permissions: {
      ADD_OPERATOR_ADMIN: 'EXECUTE',
      ASSIGN_REASSIGN_TASKS: 'NONE',
      MANAGE_USERS_AND_CONTACTS: 'NONE',
      MANAGE_VERIFICATION_BODIES: 'NONE',
      REVIEW_INSTALLATION_ACCOUNT: 'VIEW_ONLY',
      REVIEW_PERMIT_APPLICATION: 'VIEW_ONLY',
      PEER_REVIEW_PERMIT_APPLICATION: 'VIEW_ONLY',
      REVIEW_PERMIT_SURRENDER: 'VIEW_ONLY',
      PEER_REVIEW_PERMIT_SURRENDER: 'VIEW_ONLY',
      SUBMIT_PERMIT_REVOCATION: 'VIEW_ONLY',
      PEER_REVIEW_PERMIT_REVOCATION: 'VIEW_ONLY',
      REVIEW_PERMIT_NOTIFICATION: 'VIEW_ONLY',
      PEER_REVIEW_PERMIT_NOTIFICATION: 'VIEW_ONLY',
      SUBMIT_REVIEW_PERMIT_VARIATION: 'VIEW_ONLY',
      PEER_REVIEW_PERMIT_VARIATION: 'VIEW_ONLY',
      REVIEW_PERMIT_TRANSFER: 'VIEW_ONLY',
      PEER_REVIEW_PERMIT_TRANSFER: 'VIEW_ONLY',
      SUBMIT_PERMIT_BATCH_REISSUE: 'VIEW_ONLY',
      REVIEW_AER: 'VIEW_ONLY',
      REVIEW_VIR: 'VIEW_ONLY',
      SUBMIT_DRE: 'VIEW_ONLY',
      PEER_REVIEW_DRE: 'VIEW_ONLY',
      SUBMIT_NON_COMPLIANCE: 'VIEW_ONLY',
      PEER_REVIEW_NON_COMPLIANCE: 'VIEW_ONLY',
      REVIEW_NER: 'VIEW_ONLY',
      PEER_REVIEW_NER: 'VIEW_ONLY',
      REVIEW_AIR: 'VIEW_ONLY',
      SUBMIT_DOAL: 'VIEW_ONLY',
      PEER_REVIEW_DOAL: 'VIEW_ONLY',
      REVIEW_EMP_APPLICATION: 'VIEW_ONLY',
      PEER_REVIEW_EMP_APPLICATION: 'VIEW_ONLY',
      SUBMIT_AVIATION_DRE: 'VIEW_ONLY',
      PEER_REVIEW_AVIATION_DRE: 'VIEW_ONLY',
      AVIATION_ACCOUNT_CLOSURE: 'VIEW_ONLY',
      SUBMIT_REVIEW_EMP_VARIATION: 'VIEW_ONLY',
      PEER_REVIEW_EMP_VARIATION: 'VIEW_ONLY',
      SUBMIT_WITHHOLDING_OF_ALLOWANCES: 'VIEW_ONLY',
      PEER_REVIEW_WITHHOLDING_OF_ALLOWANCES: 'VIEW_ONLY',
      SUBMIT_RETURN_OF_ALLOWANCES: 'VIEW_ONLY',
      PEER_REVIEW_RETURN_OF_ALLOWANCES: 'VIEW_ONLY',
      REVIEW_AVIATION_AER: 'VIEW_ONLY',
      SUBMIT_EMP_BATCH_REISSUE: 'VIEW_ONLY',
      SUBMIT_AVIATION_NON_COMPLIANCE: 'VIEW_ONLY',
      PEER_REVIEW_AVIATION_NON_COMPLIANCE: 'VIEW_ONLY',
      REVIEW_AVIATION_VIR: 'VIEW_ONLY',
    },
  },
};

export const mockRegulatorRolePermissions = [
  'REVIEW_INSTALLATION_ACCOUNT',
  'MANAGE_USERS_AND_CONTACTS',
  'MANAGE_VERIFICATION_BODIES',
  'ADD_OPERATOR_ADMIN',
  'ASSIGN_REASSIGN_TASKS',
  'REVIEW_PERMIT_APPLICATION',
  'PEER_REVIEW_PERMIT_APPLICATION',
  'REVIEW_PERMIT_SURRENDER',
  'PEER_REVIEW_PERMIT_SURRENDER',
  'SUBMIT_PERMIT_REVOCATION',
  'PEER_REVIEW_PERMIT_REVOCATION',
  'REVIEW_PERMIT_NOTIFICATION',
  'PEER_REVIEW_PERMIT_NOTIFICATION',
  'SUBMIT_REVIEW_PERMIT_VARIATION',
  'PEER_REVIEW_PERMIT_VARIATION',
  'REVIEW_PERMIT_TRANSFER',
  'PEER_REVIEW_PERMIT_TRANSFER',
  'SUBMIT_PERMIT_BATCH_REISSUE',
  'REVIEW_AER',
  'REVIEW_VIR',
  'SUBMIT_DRE',
  'PEER_REVIEW_DRE',
  'SUBMIT_NON_COMPLIANCE',
  'PEER_REVIEW_NON_COMPLIANCE',
  'REVIEW_NER',
  'PEER_REVIEW_NER',
  'REVIEW_AIR',
  'SUBMIT_DOAL',
  'PEER_REVIEW_DOAL',
  'REVIEW_EMP_APPLICATION',
  'PEER_REVIEW_EMP_APPLICATION',
  'SUBMIT_AVIATION_DRE',
  'PEER_REVIEW_AVIATION_DRE',
  'AVIATION_ACCOUNT_CLOSURE',
  'SUBMIT_REVIEW_EMP_VARIATION',
  'PEER_REVIEW_EMP_VARIATION',
  'SUBMIT_WITHHOLDING_OF_ALLOWANCES',
  'PEER_REVIEW_WITHHOLDING_OF_ALLOWANCES',
  'SUBMIT_RETURN_OF_ALLOWANCES',
  'PEER_REVIEW_RETURN_OF_ALLOWANCES',
  'REVIEW_AVIATION_AER',
  'SUBMIT_EMP_BATCH_REISSUE',
  'SUBMIT_AVIATION_NON_COMPLIANCE',
  'PEER_REVIEW_AVIATION_NON_COMPLIANCE',
  'REVIEW_AVIATION_VIR',
];

export const mockRegulatorBasePermissions = [
  {
    name: 'Regulator admin team',
    code: 'regulator_admin_team',
    rolePermissions: {
      REVIEW_INSTALLATION_ACCOUNT: 'VIEW_ONLY',
      MANAGE_USERS_AND_CONTACTS: 'NONE',
      MANAGE_VERIFICATION_BODIES: 'NONE',
      ADD_OPERATOR_ADMIN: 'NONE',
      ASSIGN_REASSIGN_TASKS: 'EXECUTE',
      REVIEW_PERMIT_APPLICATION: 'VIEW_ONLY',
      PEER_REVIEW_PERMIT_APPLICATION: 'VIEW_ONLY',
      REVIEW_PERMIT_SURRENDER: 'VIEW_ONLY',
      PEER_REVIEW_PERMIT_SURRENDER: 'VIEW_ONLY',
      SUBMIT_PERMIT_REVOCATION: 'VIEW_ONLY',
      PEER_REVIEW_PERMIT_REVOCATION: 'VIEW_ONLY',
      REVIEW_PERMIT_NOTIFICATION: 'VIEW_ONLY',
      PEER_REVIEW_PERMIT_NOTIFICATION: 'VIEW_ONLY',
      SUBMIT_REVIEW_PERMIT_VARIATION: 'VIEW_ONLY',
      PEER_REVIEW_PERMIT_VARIATION: 'VIEW_ONLY',
      REVIEW_PERMIT_TRANSFER: 'VIEW_ONLY',
      PEER_REVIEW_PERMIT_TRANSFER: 'VIEW_ONLY',
      SUBMIT_PERMIT_BATCH_REISSUE: 'NONE',
      REVIEW_AER: 'VIEW_ONLY',
      REVIEW_VIR: 'VIEW_ONLY',
      SUBMIT_DRE: 'VIEW_ONLY',
      PEER_REVIEW_DRE: 'VIEW_ONLY',
      SUBMIT_NON_COMPLIANCE: 'VIEW_ONLY',
      PEER_REVIEW_NON_COMPLIANCE: 'VIEW_ONLY',
      REVIEW_NER: 'VIEW_ONLY',
      PEER_REVIEW_NER: 'VIEW_ONLY',
      REVIEW_AIR: 'VIEW_ONLY',
      SUBMIT_DOAL: 'VIEW_ONLY',
      PEER_REVIEW_DOAL: 'VIEW_ONLY',
      REVIEW_EMP_APPLICATION: 'VIEW_ONLY',
      PEER_REVIEW_EMP_APPLICATION: 'VIEW_ONLY',
      SUBMIT_AVIATION_DRE: 'VIEW_ONLY',
      PEER_REVIEW_AVIATION_DRE: 'VIEW_ONLY',
      AVIATION_ACCOUNT_CLOSURE: 'VIEW_ONLY',
      SUBMIT_REVIEW_EMP_VARIATION: 'VIEW_ONLY',
      PEER_REVIEW_EMP_VARIATION: 'VIEW_ONLY',
      SUBMIT_WITHHOLDING_OF_ALLOWANCES: 'VIEW_ONLY',
      PEER_REVIEW_WITHHOLDING_OF_ALLOWANCES: 'VIEW_ONLY',
      SUBMIT_RETURN_OF_ALLOWANCES: 'VIEW_ONLY',
      PEER_REVIEW_RETURN_OF_ALLOWANCES: 'VIEW_ONLY',
      REVIEW_AVIATION_AER: 'VIEW_ONLY',
      SUBMIT_EMP_BATCH_REISSUE: 'NONE',
      SUBMIT_AVIATION_NON_COMPLIANCE: 'VIEW_ONLY',
      PEER_REVIEW_AVIATION_NON_COMPLIANCE: 'VIEW_ONLY',
      REVIEW_AVIATION_VIR: 'VIEW_ONLY',
    },
  },
  {
    name: 'Regulator team leader',
    code: 'regulator_team_leader',
    rolePermissions: {
      REVIEW_INSTALLATION_ACCOUNT: 'EXECUTE',
      MANAGE_USERS_AND_CONTACTS: 'NONE',
      MANAGE_VERIFICATION_BODIES: 'NONE',
      ADD_OPERATOR_ADMIN: 'NONE',
      ASSIGN_REASSIGN_TASKS: 'EXECUTE',
      REVIEW_PERMIT_APPLICATION: 'EXECUTE',
      PEER_REVIEW_PERMIT_APPLICATION: 'EXECUTE',
      REVIEW_PERMIT_SURRENDER: 'EXECUTE',
      PEER_REVIEW_PERMIT_SURRENDER: 'EXECUTE',
      SUBMIT_PERMIT_REVOCATION: 'EXECUTE',
      PEER_REVIEW_PERMIT_REVOCATION: 'EXECUTE',
      REVIEW_PERMIT_NOTIFICATION: 'EXECUTE',
      PEER_REVIEW_PERMIT_NOTIFICATION: 'EXECUTE',
      SUBMIT_REVIEW_PERMIT_VARIATION: 'EXECUTE',
      PEER_REVIEW_PERMIT_VARIATION: 'EXECUTE',
      REVIEW_PERMIT_TRANSFER: 'EXECUTE',
      PEER_REVIEW_PERMIT_TRANSFER: 'EXECUTE',
      SUBMIT_PERMIT_BATCH_REISSUE: 'VIEW_ONLY',
      REVIEW_AER: 'EXECUTE',
      REVIEW_VIR: 'EXECUTE',
      SUBMIT_DRE: 'EXECUTE',
      PEER_REVIEW_DRE: 'VIEW_ONLY',
      SUBMIT_NON_COMPLIANCE: 'VIEW_ONLY',
      PEER_REVIEW_NON_COMPLIANCE: 'VIEW_ONLY',
      REVIEW_NER: 'VIEW_ONLY',
      PEER_REVIEW_NER: 'VIEW_ONLY',
      REVIEW_AIR: 'EXECUTE',
      SUBMIT_DOAL: 'EXECUTE',
      PEER_REVIEW_DOAL: 'EXECUTE',
      REVIEW_EMP_APPLICATION: 'EXECUTE',
      PEER_REVIEW_EMP_APPLICATION: 'EXECUTE',
      SUBMIT_AVIATION_DRE: 'EXECUTE',
      PEER_REVIEW_AVIATION_DRE: 'EXECUTE',
      AVIATION_ACCOUNT_CLOSURE: 'EXECUTE',
      SUBMIT_REVIEW_EMP_VARIATION: 'EXECUTE',
      PEER_REVIEW_EMP_VARIATION: 'EXECUTE',
      SUBMIT_WITHHOLDING_OF_ALLOWANCES: 'EXECUTE',
      PEER_REVIEW_WITHHOLDING_OF_ALLOWANCES: 'EXECUTE',
      SUBMIT_RETURN_OF_ALLOWANCES: 'EXECUTE',
      PEER_REVIEW_RETURN_OF_ALLOWANCES: 'EXECUTE',
      REVIEW_AVIATION_AER: 'EXECUTE',
      SUBMIT_EMP_BATCH_REISSUE: 'VIEW_ONLY',
      SUBMIT_AVIATION_NON_COMPLIANCE: 'EXECUTE',
      PEER_REVIEW_AVIATION_NON_COMPLIANCE: 'EXECUTE',
      REVIEW_AVIATION_VIR: 'EXECUTE',
    },
  },
  {
    name: 'CA super user',
    code: 'ca_super_user',
    rolePermissions: {
      REVIEW_INSTALLATION_ACCOUNT: 'EXECUTE',
      MANAGE_USERS_AND_CONTACTS: 'EXECUTE',
      MANAGE_VERIFICATION_BODIES: 'EXECUTE',
      ADD_OPERATOR_ADMIN: 'EXECUTE',
      ASSIGN_REASSIGN_TASKS: 'EXECUTE',
      REVIEW_PERMIT_APPLICATION: 'EXECUTE',
      PEER_REVIEW_PERMIT_APPLICATION: 'EXECUTE',
      REVIEW_PERMIT_SURRENDER: 'EXECUTE',
      PEER_REVIEW_PERMIT_SURRENDER: 'EXECUTE',
      SUBMIT_PERMIT_REVOCATION: 'EXECUTE',
      PEER_REVIEW_PERMIT_REVOCATION: 'EXECUTE',
      REVIEW_PERMIT_NOTIFICATION: 'EXECUTE',
      PEER_REVIEW_PERMIT_NOTIFICATION: 'EXECUTE',
      SUBMIT_REVIEW_PERMIT_VARIATION: 'EXECUTE',
      PEER_REVIEW_PERMIT_VARIATION: 'EXECUTE',
      REVIEW_PERMIT_TRANSFER: 'EXECUTE',
      PEER_REVIEW_PERMIT_TRANSFER: 'EXECUTE',
      SUBMIT_PERMIT_BATCH_REISSUE: 'EXECUTE',
      REVIEW_AER: 'EXECUTE',
      REVIEW_VIR: 'EXECUTE',
      SUBMIT_DRE: 'EXECUTE',
      PEER_REVIEW_DRE: 'VIEW_ONLY',
      SUBMIT_NON_COMPLIANCE: 'VIEW_ONLY',
      PEER_REVIEW_NON_COMPLIANCE: 'VIEW_ONLY',
      REVIEW_NER: 'VIEW_ONLY',
      PEER_REVIEW_NER: 'VIEW_ONLY',
      REVIEW_AIR: 'EXECUTE',
      SUBMIT_DOAL: 'EXECUTE',
      PEER_REVIEW_DOAL: 'EXECUTE',
      REVIEW_EMP_APPLICATION: 'EXECUTE',
      PEER_REVIEW_EMP_APPLICATION: 'EXECUTE',
      SUBMIT_AVIATION_DRE: 'EXECUTE',
      PEER_REVIEW_AVIATION_DRE: 'EXECUTE',
      AVIATION_ACCOUNT_CLOSURE: 'EXECUTE',
      SUBMIT_REVIEW_EMP_VARIATION: 'EXECUTE',
      PEER_REVIEW_EMP_VARIATION: 'EXECUTE',
      SUBMIT_WITHHOLDING_OF_ALLOWANCES: 'EXECUTE',
      PEER_REVIEW_WITHHOLDING_OF_ALLOWANCES: 'EXECUTE',
      SUBMIT_RETURN_OF_ALLOWANCES: 'EXECUTE',
      PEER_REVIEW_RETURN_OF_ALLOWANCES: 'EXECUTE',
      REVIEW_AVIATION_AER: 'EXECUTE',
      SUBMIT_EMP_BATCH_REISSUE: 'EXECUTE',
      SUBMIT_AVIATION_NON_COMPLIANCE: 'EXECUTE',
      PEER_REVIEW_AVIATION_NON_COMPLIANCE: 'EXECUTE',
      REVIEW_AVIATION_VIR: 'EXECUTE',
    },
  },
  {
    name: 'PMRV super user',
    code: 'pmrv_super_user',
    rolePermissions: {
      REVIEW_INSTALLATION_ACCOUNT: 'EXECUTE',
      MANAGE_USERS_AND_CONTACTS: 'EXECUTE',
      MANAGE_VERIFICATION_BODIES: 'EXECUTE',
      ADD_OPERATOR_ADMIN: 'EXECUTE',
      ASSIGN_REASSIGN_TASKS: 'EXECUTE',
      REVIEW_PERMIT_APPLICATION: 'EXECUTE',
      PEER_REVIEW_PERMIT_APPLICATION: 'EXECUTE',
      REVIEW_PERMIT_SURRENDER: 'EXECUTE',
      PEER_REVIEW_PERMIT_SURRENDER: 'EXECUTE',
      SUBMIT_PERMIT_REVOCATION: 'EXECUTE',
      PEER_REVIEW_PERMIT_REVOCATION: 'EXECUTE',
      REVIEW_PERMIT_NOTIFICATION: 'EXECUTE',
      PEER_REVIEW_PERMIT_NOTIFICATION: 'EXECUTE',
      SUBMIT_REVIEW_PERMIT_VARIATION: 'EXECUTE',
      PEER_REVIEW_PERMIT_VARIATION: 'EXECUTE',
      REVIEW_PERMIT_TRANSFER: 'EXECUTE',
      PEER_REVIEW_PERMIT_TRANSFER: 'EXECUTE',
      SUBMIT_PERMIT_BATCH_REISSUE: 'EXECUTE',
      REVIEW_AER: 'EXECUTE',
      REVIEW_VIR: 'EXECUTE',
      SUBMIT_DRE: 'EXECUTE',
      PEER_REVIEW_DRE: 'VIEW_ONLY',
      SUBMIT_NON_COMPLIANCE: 'VIEW_ONLY',
      PEER_REVIEW_NON_COMPLIANCE: 'VIEW_ONLY',
      REVIEW_NER: 'VIEW_ONLY',
      PEER_REVIEW_NER: 'VIEW_ONLY',
      REVIEW_AIR: 'EXECUTE',
      SUBMIT_DOAL: 'EXECUTE',
      PEER_REVIEW_DOAL: 'EXECUTE',
      REVIEW_EMP_APPLICATION: 'EXECUTE',
      PEER_REVIEW_EMP_APPLICATION: 'EXECUTE',
      SUBMIT_AVIATION_DRE: 'EXECUTE',
      PEER_REVIEW_AVIATION_DRE: 'EXECUTE',
      AVIATION_ACCOUNT_CLOSURE: 'EXECUTE',
      SUBMIT_REVIEW_EMP_VARIATION: 'EXECUTE',
      PEER_REVIEW_EMP_VARIATION: 'EXECUTE',
      SUBMIT_WITHHOLDING_OF_ALLOWANCES: 'EXECUTE',
      PEER_REVIEW_WITHHOLDING_OF_ALLOWANCES: 'EXECUTE',
      SUBMIT_RETURN_OF_ALLOWANCES: 'EXECUTE',
      PEER_REVIEW_RETURN_OF_ALLOWANCES: 'EXECUTE',
      REVIEW_AVIATION_AER: 'EXECUTE',
      SUBMIT_EMP_BATCH_REISSUE: 'EXECUTE',
      SUBMIT_AVIATION_NON_COMPLIANCE: 'EXECUTE',
      PEER_REVIEW_AVIATION_NON_COMPLIANCE: 'EXECUTE',
      REVIEW_AVIATION_VIR: 'EXECUTE',
    },
  },
  {
    name: 'Regulator technical officer',
    code: 'regulator_technical_officer',
    rolePermissions: {
      REVIEW_INSTALLATION_ACCOUNT: 'EXECUTE',
      MANAGE_USERS_AND_CONTACTS: 'NONE',
      MANAGE_VERIFICATION_BODIES: 'NONE',
      ADD_OPERATOR_ADMIN: 'NONE',
      ASSIGN_REASSIGN_TASKS: 'EXECUTE',
      REVIEW_PERMIT_APPLICATION: 'EXECUTE',
      PEER_REVIEW_PERMIT_APPLICATION: 'EXECUTE',
      REVIEW_PERMIT_SURRENDER: 'EXECUTE',
      PEER_REVIEW_PERMIT_SURRENDER: 'EXECUTE',
      SUBMIT_PERMIT_REVOCATION: 'EXECUTE',
      PEER_REVIEW_PERMIT_REVOCATION: 'EXECUTE',
      REVIEW_PERMIT_NOTIFICATION: 'EXECUTE',
      PEER_REVIEW_PERMIT_NOTIFICATION: 'EXECUTE',
      SUBMIT_REVIEW_PERMIT_VARIATION: 'EXECUTE',
      PEER_REVIEW_PERMIT_VARIATION: 'EXECUTE',
      REVIEW_PERMIT_TRANSFER: 'EXECUTE',
      PEER_REVIEW_PERMIT_TRANSFER: 'EXECUTE',
      SUBMIT_PERMIT_BATCH_REISSUE: 'VIEW_ONLY',
      REVIEW_AER: 'EXECUTE',
      REVIEW_VIR: 'EXECUTE',
      SUBMIT_DRE: 'EXECUTE',
      PEER_REVIEW_DRE: 'VIEW_ONLY',
      SUBMIT_NON_COMPLIANCE: 'VIEW_ONLY',
      PEER_REVIEW_NON_COMPLIANCE: 'VIEW_ONLY',
      REVIEW_NER: 'VIEW_ONLY',
      PEER_REVIEW_NER: 'VIEW_ONLY',
      REVIEW_AIR: 'EXECUTE',
      SUBMIT_DOAL: 'EXECUTE',
      PEER_REVIEW_DOAL: 'EXECUTE',
      REVIEW_EMP_APPLICATION: 'EXECUTE',
      PEER_REVIEW_EMP_APPLICATION: 'EXECUTE',
      SUBMIT_AVIATION_DRE: 'EXECUTE',
      PEER_REVIEW_AVIATION_DRE: 'EXECUTE',
      AVIATION_ACCOUNT_CLOSURE: 'EXECUTE',
      SUBMIT_REVIEW_EMP_VARIATION: 'EXECUTE',
      PEER_REVIEW_EMP_VARIATION: 'EXECUTE',
      SUBMIT_WITHHOLDING_OF_ALLOWANCES: 'EXECUTE',
      PEER_REVIEW_WITHHOLDING_OF_ALLOWANCES: 'EXECUTE',
      SUBMIT_RETURN_OF_ALLOWANCES: 'EXECUTE',
      PEER_REVIEW_RETURN_OF_ALLOWANCES: 'EXECUTE',
      REVIEW_AVIATION_AER: 'EXECUTE',
      SUBMIT_EMP_BATCH_REISSUE: 'VIEW_ONLY',
      SUBMIT_AVIATION_NON_COMPLIANCE: 'EXECUTE',
      PEER_REVIEW_AVIATION_NON_COMPLIANCE: 'EXECUTE',
      REVIEW_AVIATION_VIR: 'EXECUTE',
    },
  },
];

export const mockRegulatorPermissionGroups = {
  REVIEW_INSTALLATION_ACCOUNT: ['NONE', 'VIEW_ONLY', 'EXECUTE'],
  MANAGE_USERS_AND_CONTACTS: ['NONE', 'EXECUTE'],
  MANAGE_VERIFICATION_BODIES: ['NONE', 'EXECUTE'],
  ADD_OPERATOR_ADMIN: ['NONE', 'EXECUTE'],
  ASSIGN_REASSIGN_TASKS: ['NONE', 'EXECUTE'],
  REVIEW_PERMIT_APPLICATION: ['NONE', 'VIEW_ONLY', 'EXECUTE'],
  PEER_REVIEW_PERMIT_APPLICATION: ['NONE', 'VIEW_ONLY', 'EXECUTE'],
  REVIEW_PERMIT_SURRENDER: ['NONE', 'VIEW_ONLY', 'EXECUTE'],
  PEER_REVIEW_PERMIT_SURRENDER: ['NONE', 'VIEW_ONLY', 'EXECUTE'],
  SUBMIT_PERMIT_REVOCATION: ['NONE', 'VIEW_ONLY', 'EXECUTE'],
  PEER_REVIEW_PERMIT_REVOCATION: ['NONE', 'VIEW_ONLY', 'EXECUTE'],
  REVIEW_PERMIT_NOTIFICATION: ['NONE', 'VIEW_ONLY', 'EXECUTE'],
  PEER_REVIEW_PERMIT_NOTIFICATION: ['NONE', 'VIEW_ONLY', 'EXECUTE'],
  SUBMIT_REVIEW_PERMIT_VARIATION: ['NONE', 'VIEW_ONLY', 'EXECUTE'],
  PEER_REVIEW_PERMIT_VARIATION: ['NONE', 'VIEW_ONLY', 'EXECUTE'],
  REVIEW_PERMIT_TRANSFER: ['NONE', 'VIEW_ONLY', 'EXECUTE'],
  PEER_REVIEW_PERMIT_TRANSFER: ['NONE', 'VIEW_ONLY', 'EXECUTE'],
  SUBMIT_PERMIT_BATCH_REISSUE: ['NONE', 'VIEW_ONLY', 'EXECUTE'],
  REVIEW_AER: ['NONE', 'VIEW_ONLY', 'EXECUTE'],
  REVIEW_VIR: ['NONE', 'VIEW_ONLY', 'EXECUTE'],
  SUBMIT_DRE: ['NONE', 'VIEW_ONLY', 'EXECUTE'],
  PEER_REVIEW_DRE: ['NONE', 'VIEW_ONLY', 'EXECUTE'],
  SUBMIT_NON_COMPLIANCE: ['NONE', 'VIEW_ONLY', 'EXECUTE'],
  PEER_REVIEW_NON_COMPLIANCE: ['NONE', 'VIEW_ONLY', 'EXECUTE'],
  REVIEW_NER: ['NONE', 'VIEW_ONLY', 'EXECUTE'],
  PEER_REVIEW_NER: ['NONE', 'VIEW_ONLY', 'EXECUTE'],
  REVIEW_AIR: ['NONE', 'VIEW_ONLY', 'EXECUTE'],
  SUBMIT_DOAL: ['NONE', 'VIEW_ONLY', 'EXECUTE'],
  PEER_REVIEW_DOAL: ['NONE', 'VIEW_ONLY', 'EXECUTE'],
  REVIEW_EMP_APPLICATION: ['NONE', 'VIEW_ONLY', 'EXECUTE'],
  PEER_REVIEW_EMP_APPLICATION: ['NONE', 'VIEW_ONLY', 'EXECUTE'],
  SUBMIT_REVIEW_EMP_VARIATION: ['NONE', 'VIEW_ONLY', 'EXECUTE'],
  PEER_REVIEW_EMP_VARIATION: ['NONE', 'VIEW_ONLY', 'EXECUTE'],
  SUBMIT_AVIATION_DRE: ['NONE', 'VIEW_ONLY', 'EXECUTE'],
  PEER_REVIEW_AVIATION_DRE: ['NONE', 'VIEW_ONLY', 'EXECUTE'],
  AVIATION_ACCOUNT_CLOSURE: ['NONE', 'VIEW_ONLY', 'EXECUTE'],
  SUBMIT_WITHHOLDING_OF_ALLOWANCES: ['NONE', 'VIEW_ONLY', 'EXECUTE'],
  PEER_REVIEW_WITHHOLDING_OF_ALLOWANCES: ['NONE', 'VIEW_ONLY', 'EXECUTE'],
  SUBMIT_RETURN_OF_ALLOWANCES: ['NONE', 'VIEW_ONLY', 'EXECUTE'],
  PEER_REVIEW_RETURN_OF_ALLOWANCES: ['NONE', 'VIEW_ONLY', 'EXECUTE'],
  REVIEW_AVIATION_AER: ['NONE', 'VIEW_ONLY', 'EXECUTE'],
  SUBMIT_EMP_BATCH_REISSUE: ['NONE', 'VIEW_ONLY', 'EXECUTE'],
  SUBMIT_AVIATION_NON_COMPLIANCE: ['NONE', 'VIEW_ONLY', 'EXECUTE'],
  PEER_REVIEW_AVIATION_NON_COMPLIANCE: ['NONE', 'VIEW_ONLY', 'EXECUTE'],
  REVIEW_AVIATION_VIR: ['NONE', 'VIEW_ONLY', 'EXECUTE'],
};