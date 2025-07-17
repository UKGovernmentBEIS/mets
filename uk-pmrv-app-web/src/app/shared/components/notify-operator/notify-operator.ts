import { AccountType } from '@core/store';

import {
  AccountOperatorsUsersAuthoritiesInfoDTO,
  RequestActionUserInfo,
  RequestTaskActionProcessDTO,
  UserAuthorityInfoDTO,
} from 'pmrv-api';

export interface AccountOperatorUser {
  firstName?: string;
  lastName?: string;
  roleCode?: string;
  userId?: string;
  contactTypes?: Array<string>;
}

export type NotifyAccountOperatorUsersInfo = {
  [key: string]: RequestActionUserInfo;
};

export function toAccountOperatorUser(
  userAuthorityInfo: UserAuthorityInfoDTO,
  contactTypes: AccountOperatorsUsersAuthoritiesInfoDTO['contactTypes'],
): AccountOperatorUser {
  return {
    firstName: userAuthorityInfo.firstName,
    lastName: userAuthorityInfo.lastName,
    roleCode: userAuthorityInfo.roleCode,
    userId: userAuthorityInfo.userId,
    contactTypes: Object.keys(contactTypes).filter((key) => contactTypes[key] === userAuthorityInfo.userId),
  };
}

export const notifyAccountOperatorUsersInfoReduceCallback = (
  result: NotifyAccountOperatorUsersInfo,
  user: AccountOperatorUser,
): NotifyAccountOperatorUsersInfo => {
  return {
    ...result,
    [user.userId]: {
      contactTypes: user.contactTypes as RequestActionUserInfo['contactTypes'],
      name: user.firstName ? user.firstName + ' ' + user.lastName : user.lastName,
      roleCode: user.roleCode,
    },
  };
};

export const returnToTextMapper = (requestTaskActionType: RequestTaskActionProcessDTO['requestTaskActionType']) => {
  switch (requestTaskActionType) {
    case 'PERMIT_ISSUANCE_NOTIFY_OPERATOR_FOR_DECISION':
      return 'Permit Determination';
    case 'PERMIT_TRANSFER_B_NOTIFY_OPERATOR_FOR_DECISION':
      return 'Permit Transfer';
    case 'PERMIT_VARIATION_NOTIFY_OPERATOR_FOR_DECISION':
    case 'PERMIT_VARIATION_NOTIFY_OPERATOR_FOR_DECISION_REGULATOR_LED':
      return 'Permit Variation';
    case 'PERMIT_SURRENDER_NOTIFY_OPERATOR_FOR_DECISION':
      return 'Permit Surrender';
    case 'PERMIT_SURRENDER_CESSATION_NOTIFY_OPERATOR_FOR_DECISION':
      return 'Surrender Cessation';
    case 'PERMIT_REVOCATION_NOTIFY_OPERATOR_FOR_SUBMISSION':
      return 'Permit Revocation';
    case 'PERMIT_REVOCATION_NOTIFY_OPERATOR_FOR_WITHDRAWAL':
      return 'Dashboard';
    case 'PERMIT_REVOCATION_NOTIFY_OPERATOR_FOR_CESSATION':
      return 'Revocation cessation';
    case 'PERMIT_NOTIFICATION_NOTIFY_OPERATOR_FOR_DECISION':
    case 'PERMIT_NOTIFICATION_FOLLOW_UP_NOTIFY_OPERATOR_FOR_DECISION':
      return 'Permit Notification';
    case 'DRE_SUBMIT_NOTIFY_OPERATOR':
      return 'Reportable emissions';
    case 'DOAL_PROCEED_TO_AUTHORITY_AND_NOTIFY_OPERATOR_FOR_DECISION':
      return 'Determination of activity level change';
    case 'DOAL_AUTHORITY_RESPONSE_NOTIFY_OPERATOR_FOR_DECISION':
      return 'Provide UK ETS Authority response for activity Level Change';
    case 'VIR_NOTIFY_OPERATOR_FOR_DECISION':
    case 'AVIATION_VIR_NOTIFY_OPERATOR_FOR_DECISION':
      return 'Review verifier improvement report';
    case 'AIR_NOTIFY_OPERATOR_FOR_DECISION':
      return 'Review Annual improvement report';
    case 'EMP_ISSUANCE_UKETS_NOTIFY_OPERATOR_FOR_DECISION':
    case 'EMP_ISSUANCE_CORSIA_NOTIFY_OPERATOR_FOR_DECISION':
      return 'Review emissions monitoring plan application';
    case 'EMP_VARIATION_UKETS_NOTIFY_OPERATOR_FOR_DECISION':
    case 'EMP_VARIATION_CORSIA_NOTIFY_OPERATOR_FOR_DECISION':
      return 'Review emissions monitoring plan variation';
    case 'NON_COMPLIANCE_DAILY_PENALTY_NOTICE_NOTIFY_OPERATOR':
      return 'Upload initial penalty notice: non-compliance';
    case 'NON_COMPLIANCE_CIVIL_PENALTY_NOTIFY_OPERATOR':
      return 'Upload penalty notice: non-compliance';
    case 'NON_COMPLIANCE_NOTICE_OF_INTENT_NOTIFY_OPERATOR':
      return 'Upload notice of intent: non-compliance';
    case 'WITHHOLDING_OF_ALLOWANCES_NOTIFY_OPERATOR_FOR_DECISION':
      return 'Withholding of allowances';
    case 'RETURN_OF_ALLOWANCES_NOTIFY_OPERATOR_FOR_DECISION':
      return 'Return of allowances';
    case 'WITHHOLDING_OF_ALLOWANCES_WITHDRAWAL_NOTIFY_OPERATOR_FOR_DECISION':
      return 'Withdraw withholding of allowances notice';
    case 'AVIATION_DRE_UKETS_SUBMIT_NOTIFY_OPERATOR':
      return 'Determine emissions';
    case 'EMP_VARIATION_UKETS_NOTIFY_OPERATOR_FOR_DECISION_REGULATOR_LED':
    case 'EMP_VARIATION_CORSIA_NOTIFY_OPERATOR_FOR_DECISION_REGULATOR_LED':
      return 'Vary the emissions monitoring plan';
    case 'INSTALLATION_AUDIT_SUBMIT_NOTIFY_OPERATOR':
      return 'Create audit report';
    case 'INSTALLATION_ONSITE_INSPECTION_SUBMIT_NOTIFY_OPERATOR':
      return 'Create an on-site inspection';
    case 'AVIATION_AER_CORSIA_ANNUAL_OFFSETTING_SUBMIT_NOTIFY_OPERATOR':
      return 'Calculate annual offsetting requirements';
    case 'AVIATION_AER_CORSIA_3YEAR_PERIOD_OFFSETTING_SUBMIT_NOTIFY_OPERATOR':
      return 'Calculate 3-year period offsetting requirements';
    case 'PERMANENT_CESSATION_NOTIFY_OPERATOR_FOR_DECISION':
      return 'Permanent cessation';
    case 'AVIATION_DOE_CORSIA_SUBMIT_NOTIFY_OPERATOR':
      return 'Estimate emissions';
  }
};

export const usersToNotifyHeaderMapper = (
  currentDomain: AccountType,
  requestTaskActionType: RequestTaskActionProcessDTO['requestTaskActionType'],
) => {
  switch (currentDomain) {
    case 'AVIATION':
      return 'Select other users';

    case 'INSTALLATION':
      return requestTaskActionType === 'PERMANENT_CESSATION_NOTIFY_OPERATOR_FOR_DECISION'
        ? 'Select any additional users you want to notify (optional)'
        : 'Select the users you want to notify';
  }
};

export const externalContactsToNotifyHeaderMapper = (
  currentDomain: AccountType,
  requestTaskActionType: RequestTaskActionProcessDTO['requestTaskActionType'],
) => {
  switch (currentDomain) {
    case 'AVIATION':
      return 'Select external contacts';

    case 'INSTALLATION':
      return requestTaskActionType === 'PERMANENT_CESSATION_NOTIFY_OPERATOR_FOR_DECISION'
        ? 'Select the external contacts you want to notify (optional)'
        : 'Select the external contacts you want to notify';
  }
};
