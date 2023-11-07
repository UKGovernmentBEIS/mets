import { Injectable } from '@angular/core';

import { AuthState, initialState } from '@core/store/auth/auth.state';
import { KeycloakProfile } from 'keycloak-js';

import { ApplicationUserDTO, TermsDTO } from 'pmrv-api';

import { Store } from '../store';
import { UserState } from './auth.state';

@Injectable({ providedIn: 'root' })
export class AuthStore extends Store<AuthState> {
  constructor() {
    super(initialState);
  }

  setIsLoggedIn(isLoggedIn: boolean) {
    this.setState({ ...this.getState(), isLoggedIn });
  }

  setCurrentDomain(currentDomain: UserState['lastLoginDomain']) {
    this.setState({
      ...this.getState(),
      currentDomain,
    });
  }

  setSwitchingDomain(switchingDomain: UserState['lastLoginDomain']) {
    this.setState({
      ...this.getState(),
      switchingDomain,
    });
  }

  setUser(user: ApplicationUserDTO) {
    this.setState({ ...this.getState(), user });
  }

  setUserProfile(userProfile: KeycloakProfile) {
    this.setState({ ...this.getState(), userProfile });
  }

  setUserState(userState: UserState) {
    this.setState({ ...this.getState(), userState });
  }

  setLastLoginDomain(lastLoginDomain: UserState['lastLoginDomain']) {
    this.setUserState({
      ...this.getState().userState,
      lastLoginDomain,
    });
  }

  setTerms(terms: TermsDTO) {
    this.setState({ ...this.getState(), terms });
  }

  reset(): void {
    this.setState(initialState);
  }
}
