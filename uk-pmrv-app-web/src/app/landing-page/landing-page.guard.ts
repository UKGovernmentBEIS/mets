import { Injectable } from '@angular/core';
import { Router, UrlTree } from '@angular/router';

import { combineLatest, first, map, Observable, switchMap } from 'rxjs';

import { selectIsFeatureEnabled } from '@core/config/config.selectors';
import { ConfigStore } from '@core/config/config.store';
import { AuthService } from '@core/services/auth.service';
import {
  AuthStore,
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

  canActivate(): Observable<boolean | UrlTree> {
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
          switchingDomain,
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

          if (['REGULATOR', 'VERIFIER'].includes(userState.roleType) && this.allNoAuthority(userState)) {
            return this.router.parseUrl('dashboard');
          }

          if (
            this.shouldShowDisabled(userState) ||
            this.allNoAuthority(userState) ||
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
      return lastLoginDomain === 'INSTALLATION' ? this.installationDashboard : this.aviationDashboard;
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

  private allNoAuthority(userState: UserState): boolean {
    const [hasInstallationLogin, hasAviationLogin] = this.hasLogins(userState);
    return !hasInstallationLogin && !hasAviationLogin;
  }

  private hasLogins({ domainsLoginStatuses }: UserState): [boolean, boolean] {
    return [
      !!domainsLoginStatuses.INSTALLATION && domainsLoginStatuses.INSTALLATION !== 'NO_AUTHORITY',
      !!domainsLoginStatuses.AVIATION && domainsLoginStatuses.AVIATION !== 'NO_AUTHORITY',
    ];
  }

  private loginsDisabled(userState: UserState): [boolean, boolean] {
    return [
      ['DISABLED', 'TEMP_DISABLED'].includes(userState.domainsLoginStatuses?.INSTALLATION),
      ['DISABLED', 'TEMP_DISABLED'].includes(userState.domainsLoginStatuses?.AVIATION),
    ];
  }
}
