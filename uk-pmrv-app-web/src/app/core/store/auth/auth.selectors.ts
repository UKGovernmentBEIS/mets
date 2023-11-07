import { map, OperatorFunction, pipe } from 'rxjs';

import { AccountType, AuthState, DomainsLoginStatuses, LoginStatus } from '@core/store/auth/auth.state';
import { KeycloakProfile } from 'keycloak-js';

import { ApplicationUserDTO, TermsDTO } from 'pmrv-api';

import { UserState } from './auth.state';

export const selectCurrentDomain: OperatorFunction<AuthState, AccountType> = map((state) => state.currentDomain);
export const selectSwitchingDomain: OperatorFunction<AuthState, AccountType> = pipe(
  map((state) => state.switchingDomain),
);
export const selectUserProfile: OperatorFunction<AuthState, KeycloakProfile> = map((state) => state.userProfile);
export const selectTerms: OperatorFunction<AuthState, TermsDTO> = map((state) => state.terms);
export const selectIsLoggedIn: OperatorFunction<AuthState, boolean> = map((state) => state.isLoggedIn);
export const selectUser: OperatorFunction<AuthState, ApplicationUserDTO> = map((state) => state.user);
export const selectUserState: OperatorFunction<AuthState, UserState> = map((state) => state.userState);
export const selectDomainsLoginStatuses: OperatorFunction<AuthState, DomainsLoginStatuses> = pipe(
  selectUserState,
  map((state) => state?.domainsLoginStatuses),
);
export const selectUserRoleType: OperatorFunction<AuthState, UserState['roleType']> = pipe(
  selectUserState,
  map((state) => state?.roleType),
);
export const selectUserId: OperatorFunction<AuthState, string> = pipe(
  selectUserState,
  map((state) => state?.userId),
);
export const selectLoginStatus = (domain: AccountType): OperatorFunction<AuthState, LoginStatus> =>
  pipe(
    selectDomainsLoginStatuses,
    map((statuses) => statuses?.[domain]),
  );
export const selectCanSwitchDomain: OperatorFunction<AuthState, boolean> = pipe(
  map(
    ({ currentDomain, userState }) =>
      Object.entries(userState.domainsLoginStatuses).filter(([domain, loginStatus]) => {
        return loginStatus !== 'NO_AUTHORITY' || domain === 'INSTALLATION' || userState.roleType === 'REGULATOR';
      }).length > 1 && !!currentDomain,
  ),
);
