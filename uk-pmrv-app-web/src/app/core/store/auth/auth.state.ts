import { KeycloakProfile } from 'keycloak-js';

import { ApplicationUserDTO, TermsDTO, UserStateDTO } from 'pmrv-api';

export type AccountType = 'INSTALLATION' | 'AVIATION';
export type LoginStatus = UserStateDTO['domainsLoginStatuses'][keyof UserStateDTO['domainsLoginStatuses']];

export type DomainsLoginStatuses = {
  [key in AccountType]?: LoginStatus;
};

export interface UserState extends UserStateDTO {
  domainsLoginStatuses?: DomainsLoginStatuses;
}

export interface AuthState {
  user: ApplicationUserDTO;
  userProfile: KeycloakProfile;
  userState: UserState;
  terms: TermsDTO;
  isLoggedIn: boolean;
  currentDomain: UserState['lastLoginDomain'];
  switchingDomain: UserState['lastLoginDomain'];
}

export const initialState: AuthState = {
  user: null,
  userProfile: null,
  userState: null,
  terms: null,
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
