import { APP_BASE_HREF } from '@angular/common';
import { Inject, Injectable } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { combineLatest, from, map, Observable, of, switchMap, tap } from 'rxjs';

import { selectIsFeatureEnabled } from '@core/config/config.selectors';
import { ConfigStore } from '@core/config/config.store';
import { UserState } from '@core/store/auth';
import { AuthStore } from '@core/store/auth/auth.store';
import { KeycloakService } from 'keycloak-angular';
import { KeycloakLoginOptions, KeycloakProfile } from 'keycloak-js';

import { AuthoritiesService, TermsAndConditionsService, UserDTO, UsersService, UserTermsVersionDTO } from 'pmrv-api';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly _baseRedirectUri = new URL(this.baseHref, location.origin).toString();
  get baseRedirectUri() {
    return this._baseRedirectUri;
  }

  constructor(
    private readonly authStore: AuthStore,
    private readonly configStore: ConfigStore,
    private readonly keycloakService: KeycloakService,
    private readonly usersService: UsersService,
    private readonly authorityService: AuthoritiesService,
    private readonly termsAndConditionsService: TermsAndConditionsService,
    private readonly route: ActivatedRoute,
    @Inject(APP_BASE_HREF) private baseHref: string,
  ) {}

  login(options?: KeycloakLoginOptions): Promise<void> {
    let leaf = this.route.snapshot;

    while (leaf.firstChild) {
      leaf = leaf.firstChild;
    }

    return this.keycloakService.login({
      ...options,
      ...(leaf.data?.blockSignInRedirect ? { redirectUri: this._baseRedirectUri } : null),
    });
  }

  logout(redirectPath = ''): Promise<void> {
    return this.keycloakService
      .logout(this._baseRedirectUri + redirectPath)
      .then(() => this.authStore.setIsLoggedIn(false));
  }

  loadUser(): Observable<UserDTO> {
    return this.usersService.getCurrentUser().pipe(tap((user) => this.authStore.setUser(user)));
  }

  loadUserState(): Observable<UserState> {
    return this.authorityService.getCurrentUserState().pipe(tap((userState) => this.authStore.setUserState(userState)));
  }

  checkUser(): Observable<void> {
    return this.authStore.getState().isLoggedIn === null
      ? this.loadIsLoggedIn().pipe(
          switchMap((res: boolean) =>
            res
              ? combineLatest([
                  this.loadUserState(),
                  this.loadUserTerms(),
                  this.loadUser(),
                  this.loadUserProfile(),
                ]).pipe(map(() => undefined))
              : of(undefined),
          ),
        )
      : of(undefined);
  }

  loadUserProfile(): Observable<KeycloakProfile> {
    return from(this.keycloakService.loadUserProfile()).pipe(tap((profile) => this.authStore.setUserProfile(profile)));
  }

  loadUserTerms(): Observable<UserTermsVersionDTO> {
    return this.configStore.pipe(selectIsFeatureEnabled('terms')).pipe(
      switchMap((termsEnabled) => (termsEnabled ? this.termsAndConditionsService.getUserTerms() : of(null))),
      tap((userTerms) => this.authStore.setUserTerms(userTerms)),
    );
  }

  loadIsLoggedIn(): Observable<boolean> {
    return of(this.keycloakService.isLoggedIn()).pipe(tap((isLoggedIn) => this.authStore.setIsLoggedIn(isLoggedIn)));
  }
}
