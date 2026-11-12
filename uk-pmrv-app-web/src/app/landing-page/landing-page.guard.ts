import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { combineLatest, first, map, Observable, switchMap } from 'rxjs';

import { selectIsFeatureEnabled } from '@core/config/config.selectors';
import { ConfigStore } from '@core/config/config.store';
import { AuthService } from '@core/services/auth.service';
import {
  AuthStore,
  hasDomainLogin,
  selectIsLoggedIn,
  selectSwitchingDomain,
  selectUserState,
  selectUserTerms,
  UserState,
} from '@core/store/auth';
import { LatestTermsStore } from '@core/store/latest-terms/latest-terms.store';

import { environment } from '../../environments/environment';

@Injectable()
export class LandingPageGuard {
  private readonly installationDashboard = this.router.parseUrl('dashboard');
  private readonly aviationDashboard = this.router.parseUrl('aviation/dashboard');

  constructor(
    private readonly authStore: AuthStore,
    private readonly configStore: ConfigStore,
    private readonly latestTermsStore: LatestTermsStore,
    private readonly authService: AuthService,
    private readonly router: Router,
  ) {}

  canDeactivate(): boolean {
    this.authStore.setSwitchingDomain(null);
    return true;
  }

  canActivate(route?: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    const requestedDomain = this.parseRequestedDomain(route);

    return this.authService.checkUser().pipe(
      switchMap(() =>
        combineLatest([
          this.authStore.pipe(selectIsLoggedIn),
          this.authStore.pipe(selectUserState),
          this.latestTermsStore,
          this.configStore.pipe(selectIsFeatureEnabled('terms')),
          this.configStore.pipe(selectIsFeatureEnabled('serviceGatewayEnabled')),
          this.authStore.pipe(selectUserTerms),
          this.authStore.pipe(selectSwitchingDomain),
        ]),
      ),
      map(
        ([
          isLoggedIn,
          userState,
          latestTerms,
          termsFeatureEnabled,
          serviceGatewayEnabled,
          userTerms,
          storedSwitchingDomain,
        ]) => {
          if (!isLoggedIn) {
            if (!environment.production) {
              return true; // show app's landing page
            }

            if (!serviceGatewayEnabled) {
              return true; // show app's landing page
            }

            // otherwise redirect to gateway's landing page
            const url = new URL(location.origin);
            url.searchParams.set(
              'appRedirectPath',
              /(\/registration\/|\/2fa\/|\/forgot-password\/\/error\/)/.test(location.href)
                ? this.authService.baseRedirectUri
                : location.href,
            );
            window.location.href = url.toString();
            return false;
          }

          if (!userState.roleType) {
            return true;
          }

          if (termsFeatureEnabled && latestTerms.version !== userTerms.termsVersion) {
            return this.router.parseUrl('terms');
          }

          // A `?domain=` query param (from the gateway's per-domain service links) acts
          // as the replacement for the old header domain-picker: when the user actually
          // has authority for the requested domain, treat it exactly like a manual switch.
          // An operator requesting a domain they have NO_AUTHORITY for is also honoured,
          // so isSwitchingToNoAuthorityDomain below can show the no-account message instead
          // of the request being silently dropped.
          const requestedDomainIsOperatorNoAuthority =
            requestedDomain &&
            userState.roleType === 'OPERATOR' &&
            userState.domainsLoginStatuses?.[requestedDomain] === 'NO_AUTHORITY';

          const switchingDomain =
            requestedDomain && (hasDomainLogin(userState, requestedDomain) || requestedDomainIsOperatorNoAuthority)
              ? requestedDomain
              : storedSwitchingDomain;

          // Deferred to a microtask: authStore is itself one of the combineLatest
          // sources above, so setting it synchronously here would re-enter this
          // same map() while it's still emitting for the current value.
          if (requestedDomain && switchingDomain === requestedDomain && storedSwitchingDomain !== requestedDomain) {
            queueMicrotask(() => this.authStore.setSwitchingDomain(requestedDomain));
          }

          if (['REGULATOR', 'VERIFIER'].includes(userState.roleType) && this.allNoAuthority(userState)) {
            return this.router.parseUrl('dashboard');
          }

          if (
            this.shouldShowDisabled(userState) ||
            this.allNoAuthority(userState) ||
            this.isSwitchingToNoAuthorityDomain(userState, switchingDomain) ||
            this.shouldShowAccepted(userState, switchingDomain)
          ) {
            return true;
          }

          return this.resolveInitialNavigation(userState, switchingDomain);
        },
      ),
      first(),
    );
  }

  private parseRequestedDomain(route: ActivatedRouteSnapshot | undefined): 'INSTALLATION' | 'AVIATION' | null {
    const domain = route?.queryParamMap.get('domain');
    return domain === 'INSTALLATION' || domain === 'AVIATION' ? domain : null;
  }

  private resolveInitialNavigation(
    userState: UserState,
    switchingDomain: UserState['lastLoginDomain'],
  ): boolean | UrlTree {
    const firstLogin = userState.lastLoginDomain == null;
    if (firstLogin) {
      return this.resolveFirstLoginNavigation(userState);
    } else {
      return this.resolveSubsequentLoginNavigation(userState, switchingDomain);
    }
  }

  private resolveFirstLoginNavigation(userState: UserState): boolean | UrlTree {
    const hasAviationLogin = userState.domainsLoginStatuses.AVIATION !== 'NO_AUTHORITY';
    const hasInstallationLogin = userState.domainsLoginStatuses.INSTALLATION !== 'NO_AUTHORITY';

    if (['REGULATOR', 'VERIFIER'].includes(userState.roleType) || hasInstallationLogin || !hasAviationLogin) {
      return this.installationDashboard;
    }

    return this.aviationDashboard;
  }

  private resolveSubsequentLoginNavigation(
    userState: UserState,
    switchingDomain: UserState['lastLoginDomain'],
  ): boolean | UrlTree {
    const isRegulatorOrVerifier = ['REGULATOR', 'VERIFIER'].includes(userState.roleType);
    const lastLoginDomain = userState.lastLoginDomain;
    const [hasInstallationLogin, hasAviationLogin] = this.hasLogins(userState);

    if (this.router.getCurrentNavigation()?.extras?.state?.addAnotherInstallation) {
      return true;
    }

    if (isRegulatorOrVerifier || (hasInstallationLogin && hasAviationLogin)) {
      // A regulator/verifier or dual-domain operator can be pointed at either domain
      // explicitly (via switchingDomain); otherwise fall back to their last domain.
      const targetDomain = switchingDomain ?? lastLoginDomain;
      return targetDomain === 'INSTALLATION' ? this.installationDashboard : this.aviationDashboard;
    }

    if (hasAviationLogin && switchingDomain !== 'INSTALLATION') {
      return this.aviationDashboard;
    }

    if (hasInstallationLogin) {
      return this.installationDashboard;
    }

    return true;
  }

  private shouldShowAccepted(userState: UserState, switchingDomain: UserState['lastLoginDomain']): boolean {
    const lastLogin = userState.lastLoginDomain;
    const instLoginStatus = userState.domainsLoginStatuses.INSTALLATION;
    const avLoginStatus = userState.domainsLoginStatuses.AVIATION;

    const shouldShowAcceptedInstallation =
      (lastLogin === 'INSTALLATION' || switchingDomain === 'INSTALLATION' || lastLogin == null) &&
      instLoginStatus === 'ACCEPTED';

    const shouldShowAcceptedAviation =
      (lastLogin === 'AVIATION' || switchingDomain === 'AVIATION' || lastLogin == null) && avLoginStatus === 'ACCEPTED';

    return shouldShowAcceptedInstallation || shouldShowAcceptedAviation;
  }

  private shouldShowDisabled(userState: UserState): boolean {
    const [hasInstallationLogin, hasAviationLogin] = this.hasLogins(userState);
    const [installationDisabled, aviationDisabled] = this.loginsDisabled(userState);
    const lastLogin = userState.lastLoginDomain;

    return (
      (hasInstallationLogin && installationDisabled && (!hasAviationLogin || lastLogin === 'INSTALLATION')) ||
      (hasAviationLogin && aviationDisabled && (!hasInstallationLogin || lastLogin === 'AVIATION')) ||
      (hasInstallationLogin && hasAviationLogin && installationDisabled && aviationDisabled)
    );
  }

  private isSwitchingToNoAuthorityDomain(userState: UserState, switchingDomain: UserState['lastLoginDomain']): boolean {
    return (
      userState.roleType === 'OPERATOR' &&
      !!switchingDomain &&
      userState.domainsLoginStatuses?.[switchingDomain] === 'NO_AUTHORITY'
    );
  }

  private allNoAuthority(userState: UserState): boolean {
    const [hasInstallationLogin, hasAviationLogin] = this.hasLogins(userState);
    return !hasInstallationLogin && !hasAviationLogin;
  }

  private hasLogins(userState: UserState): [boolean, boolean] {
    return [hasDomainLogin(userState, 'INSTALLATION'), hasDomainLogin(userState, 'AVIATION')];
  }

  private loginsDisabled(userState: UserState): [boolean, boolean] {
    return [
      ['DISABLED', 'TEMP_DISABLED'].includes(userState.domainsLoginStatuses?.INSTALLATION),
      ['DISABLED', 'TEMP_DISABLED'].includes(userState.domainsLoginStatuses?.AVIATION),
    ];
  }
}
