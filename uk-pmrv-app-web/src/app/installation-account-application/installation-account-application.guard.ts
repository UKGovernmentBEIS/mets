import { Injectable } from '@angular/core';
import { Router, UrlTree } from '@angular/router';

import { combineLatest, first, map, Observable, switchMap } from 'rxjs';

import { selectIsFeatureEnabled } from '@core/config/config.selectors';
import { ConfigStore } from '@core/config/config.store';
import { AuthService } from '@core/services/auth.service';
import { AuthStore } from '@core/store/auth';
import { LatestTermsStore } from '@core/store/latest-terms/latest-terms.store';

@Injectable()
export class InstallationAccountApplicationGuard {
  constructor(
    private readonly authService: AuthService,
    private readonly router: Router,
    private readonly authStore: AuthStore,
    private latestTermsStore: LatestTermsStore,
    private readonly configStore: ConfigStore,
  ) {}

  canActivate(): Observable<true | UrlTree> {
    return this.authService.checkUser().pipe(
      switchMap(() =>
        combineLatest([this.authStore, this.latestTermsStore, this.configStore.pipe(selectIsFeatureEnabled('terms'))]),
      ),
      map(([{ isLoggedIn, userState, userTerms }, latestTerms, termsEnabled]) =>
        isLoggedIn &&
        (!termsEnabled || latestTerms.version === userTerms.termsVersion) &&
        (userState.roleType === 'OPERATOR' ||
          (['REGULATOR', 'VERIFIER'].includes(userState.roleType) &&
            userState.domainsLoginStatuses.INSTALLATION === 'ENABLED'))
          ? (true as const)
          : this.router.parseUrl(
              isLoggedIn && termsEnabled && latestTerms.version !== userTerms.termsVersion ? '/terms' : '',
            ),
      ),
      first(),
    );
  }
}
