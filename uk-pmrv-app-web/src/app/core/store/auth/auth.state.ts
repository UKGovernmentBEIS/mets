import { KeycloakProfile } from 'keycloak-js';

import { UserDTO, UserStateDTO, UserTermsVersionDTO } from 'pmrv-api';

export type AccountType = 'INSTALLATION' | 'AVIATION';
export type LoginStatus = UserStateDTO['domainsLoginStatuses'][keyof UserStateDTO['domainsLoginStatuses']];

export type DomainsLoginStatuses = {
  [key in AccountType]?: LoginStatus;
};

export interface UserState extends UserStateDTO {
  domainsLoginStatuses?: DomainsLoginStatuses;
}

export interface AuthState {
  user: UserDTO;
  userProfile: KeycloakProfile;
  userState: UserState;
  userTerms: UserTermsVersionDTO;
  isLoggedIn: boolean;
  currentDomain: UserState['lastLoginDomain'];
  switchingDomain: UserState['lastLoginDomain'];
}

export const initialState: AuthState = {
  user: null,
  userProfile: null,
  userState: null,
  userTerms: null,
  isLoggedIn: null,
  currentDomain: null,
  switchingDomain: null,
};

export function onlyAssociatedWithDomain(domain: UserState['lastLoginDomain'], userState: UserState): boolean {
  return (
    userState &&
    userState.domainsLoginStatuses[domain] !== 'NO_AUTHORITY' &&
    Object.entries(userState.domainsLoginStatuses)
      .filter((entry) => entry[0] !== domain)
      .every((entry) => entry[1] === 'NO_AUTHORITY')
  );
}
