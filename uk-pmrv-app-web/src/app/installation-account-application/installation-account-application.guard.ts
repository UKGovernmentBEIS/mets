import { Injectable } from '@angular/core';
import { CanActivate, Router, UrlTree } from '@angular/router';

import { first, map, Observable, switchMap } from 'rxjs';

import { AuthService } from '@core/services/auth.service';
import { AuthStore } from '@core/store/auth';

@Injectable()
export class InstallationAccountApplicationGuard implements CanActivate {
  constructor(
    private readonly authService: AuthService,
    private readonly router: Router,
    private readonly authStore: AuthStore,
  ) {}

  canActivate(): Observable<true | UrlTree> {
    return this.authService.checkUser().pipe(
      switchMap(() => this.authStore),
      map(({ isLoggedIn, userState, terms, user }) =>
        isLoggedIn &&
        terms.version === user.termsVersion &&
        (userState.roleType === 'OPERATOR' ||
          (['REGULATOR', 'VERIFIER'].includes(userState.roleType) &&
            userState.domainsLoginStatuses.INSTALLATION === 'ENABLED'))
          ? (true as const)
          : this.router.parseUrl(isLoggedIn && terms.version !== user.termsVersion ? '/terms' : ''),
      ),
      first(),
    );
  }
}
