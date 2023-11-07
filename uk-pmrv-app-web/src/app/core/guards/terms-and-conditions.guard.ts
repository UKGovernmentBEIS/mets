import { Injectable } from '@angular/core';
import { CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';

import { first, map, Observable, switchMap } from 'rxjs';

import { AuthService } from '@core/services/auth.service';
import { AuthStore } from '@core/store/auth';

@Injectable({
  providedIn: 'root',
})
export class TermsAndConditionsGuard implements CanActivate {
  constructor(protected router: Router, protected authService: AuthService, private authStore: AuthStore) {}

  canActivate(_, state: RouterStateSnapshot): Observable<true | UrlTree> {
    return this.authService.checkUser().pipe(
      switchMap(() => this.authStore),
      map(({ terms, user }) => {
        if (state.url === '/terms') {
          return terms.version !== user.termsVersion || this.router.parseUrl('landing');
        }

        return terms.version === user.termsVersion || this.router.parseUrl('landing');
      }),
      first(),
    );
  }
}
