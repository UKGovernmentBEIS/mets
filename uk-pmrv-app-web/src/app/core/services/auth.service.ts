import { Injectable } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { combineLatest, from, map, Observable, of, switchMap, tap } from 'rxjs';

import { UserState } from '@core/store/auth';
import { AuthStore } from '@core/store/auth/auth.store';
import { KeycloakService } from 'keycloak-angular';
import { KeycloakLoginOptions, KeycloakProfile } from 'keycloak-js';

import { ApplicationUserDTO, AuthoritiesService, TermsAndConditionsService, TermsDTO, UsersService } from 'pmrv-api';

@Injectable({ providedIn: 'root' })
export class AuthService {
  constructor(
    private readonly store: AuthStore,
    private readonly keycloakService: KeycloakService,
    private readonly usersService: UsersService,
    private readonly authorityService: AuthoritiesService,
    private readonly termsAndConditionsService: TermsAndConditionsService,
    private readonly route: ActivatedRoute,
  ) {}

  login(options?: KeycloakLoginOptions): Promise<void> {
    let leaf = this.route.snapshot;

    while (leaf.firstChild) {
      leaf = leaf.firstChild;
    }

    return this.keycloakService.login({
      ...options,
      ...(leaf.data?.blockSignInRedirect ? { redirectUri: location.origin } : null),
    });
  }

  logout(redirectPath = ''): Promise<void> {
    this.store.setIsLoggedIn(false);
    return this.keycloakService.logout(location.origin + redirectPath);
  }

  loadUser(): Observable<ApplicationUserDTO> {
    return this.usersService.getCurrentUser().pipe(tap((user) => this.store.setUser(user)));
  }

  loadUserState(): Observable<UserState> {
    return this.authorityService.getCurrentUserState().pipe(tap((userState) => this.store.setUserState(userState)));
  }

  checkUser(): Observable<void> {
    return this.store.getState().isLoggedIn === null
      ? this.loadIsLoggedIn().pipe(
          switchMap((res: boolean) =>
            res
              ? combineLatest([this.loadUserState(), this.loadTerms(), this.loadUser(), this.loadUserProfile()]).pipe(
                  map(() => undefined),
                )
              : of(undefined),
          ),
        )
      : of(undefined);
  }

  loadUserProfile(): Observable<KeycloakProfile> {
    return from(this.keycloakService.loadUserProfile()).pipe(tap((profile) => this.store.setUserProfile(profile)));
  }

  loadTerms(): Observable<TermsDTO> {
    return this.termsAndConditionsService.getLatestTerms().pipe(tap((terms) => this.store.setTerms(terms)));
  }

  loadIsLoggedIn(): Observable<boolean> {
    return from(this.keycloakService.isLoggedIn()).pipe(tap((isLoggedIn) => this.store.setIsLoggedIn(isLoggedIn)));
  }
}
