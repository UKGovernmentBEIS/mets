import { Injectable } from '@angular/core';
import { Router, RouterStateSnapshot, UrlTree } from '@angular/router';

import { combineLatest, first, map, Observable, switchMap } from 'rxjs';

import { selectIsFeatureEnabled } from '@core/config/config.selectors';
import { ConfigStore } from '@core/config/config.store';
import { AuthService } from '@core/services/auth.service';
import { AuthStore } from '@core/store/auth';
import { LatestTermsStore } from '@core/store/latest-terms/latest-terms.store';

@Injectable({
  providedIn: 'root',
})
export class TermsAndConditionsGuard {
  constructor(
    protected router: Router,
    protected authService: AuthService,
    private authStore: AuthStore,
    private latestTermsStore: LatestTermsStore,
    private configStore: ConfigStore,
  ) {}

  canActivate(_, state: RouterStateSnapshot): Observable<true | UrlTree> {
    return this.authService.checkUser().pipe(
      switchMap(() =>
        combineLatest([this.configStore.pipe(selectIsFeatureEnabled('terms')), this.latestTermsStore, this.authStore]),
      ),
      map(([termsEnabled, latestTerms, { userTerms }]) => {
        if (!termsEnabled) {
          return true;
        }

        if (state.url === '/terms') {
          return latestTerms.version !== userTerms.termsVersion || this.router.parseUrl('landing');
        }

        return latestTerms.version === userTerms.termsVersion || this.router.parseUrl('landing');
      }),
      first(),
    );
  }
}
