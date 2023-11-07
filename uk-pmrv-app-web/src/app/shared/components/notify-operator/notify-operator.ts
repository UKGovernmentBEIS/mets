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

export function toNotifyAccountOperatorUsersInfo(
  result: NotifyAccountOperatorUsersInfo,
  user: AccountOperatorUser,
): NotifyAccountOperatorUsersInfo {
  return {
    ...result,
    [user.userId]: {
      contactTypes: user.contactTypes as RequestActionUserInfo['contactTypes'],
      name: user.firstName ? user.firstName + ' ' + user.lastName : user.lastName,
      roleCode: user.roleCode,
    },
  };
}

export function isAviationTaskActionType(
  requestTaskActionType: RequestTaskActionProcessDTO['requestTaskActionType'],
): boolean {
  return [
    'EMP_ISSUANCE_UKETS_NOTIFY_OPERATOR_FOR_DECISION',
    'EMP_ISSUANCE_CORSIA_NOTIFY_OPERATOR_FOR_DECISION',
    'EMP_VARIATION_UKETS_NOTIFY_OPERATOR_FOR_DECISION',
    'EMP_VARIATION_CORSIA_NOTIFY_OPERATOR_FOR_DECISION',
    'EMP_VARIATION_UKETS_NOTIFY_OPERATOR_FOR_DECISION_REGULATOR_LED',
    'AVIATION_DRE_UKETS_SUBMIT_NOTIFY_OPERATOR',
    'AVIATION_VIR_NOTIFY_OPERATOR_FOR_DECISION',
  ].includes(requestTaskActionType);
}

export function isForAviationDocuments(
  requestTaskActionType: RequestTaskActionProcessDTO['requestTaskActionType'],
): boolean {
  return [
    'EMP_ISSUANCE_UKETS_NOTIFY_OPERATOR_FOR_DECISION',
    'EMP_ISSUANCE_CORSIA_NOTIFY_OPERATOR_FOR_DECISION',
    'EMP_VARIATION_UKETS_NOTIFY_OPERATOR_FOR_DECISION',
    'EMP_VARIATION_CORSIA_NOTIFY_OPERATOR_FOR_DECISION',
    'EMP_VARIATION_UKETS_NOTIFY_OPERATOR_FOR_DECISION_REGULATOR_LED',
    'AVIATION_DRE_UKETS_SUBMIT_NOTIFY_OPERATOR',
  ].includes(requestTaskActionType);
}
