import { Injectable } from '@angular/core';
import { Router, UrlTree } from '@angular/router';

import { combineLatest, first, map, Observable, switchMap } from 'rxjs';

import { selectIsFeatureEnabled } from '@core/config/config.selectors';
import { ConfigStore } from '@core/config/config.store';
import { AuthService } from '@core/services/auth.service';
import { AuthStore } from '@core/store/auth';
import { LatestTermsStore } from '@core/store/latest-terms/latest-terms.store';

@Injectable({ providedIn: 'root' })
export class AuthGuard {
  constructor(
    protected router: Router,
    protected authService: AuthService,
    private store: AuthStore,
    private latestTermsStore: LatestTermsStore,
    private configStore: ConfigStore,
  ) {}

  canActivate(): Observable<boolean | UrlTree> {
    return this.authService.checkUser().pipe(
      switchMap(() =>
        combineLatest([this.store, this.configStore.pipe(selectIsFeatureEnabled('terms')), this.latestTermsStore]),
      ),
      map(([{ isLoggedIn, userState, userTerms, currentDomain }, termsEnabled, latestTerms]) =>
        isLoggedIn &&
        (!termsEnabled || latestTerms.version === userTerms.termsVersion) &&
        !['DISABLED', 'TEMP_DISABLED'].includes(userState.domainsLoginStatuses[currentDomain])
          ? (true as const)
          : isLoggedIn && termsEnabled && latestTerms.version !== userTerms.termsVersion
            ? this.router.parseUrl('/terms')
            : this.router.parseUrl('landing'),
      ),
      first(),
    );
  }
}
