import { Injectable } from '@angular/core';
import { CanActivate, Router, UrlTree } from '@angular/router';

import { first, map, Observable } from 'rxjs';

import { AuthStore, selectIsLoggedIn } from '@core/store/auth';

@Injectable({
  providedIn: 'root',
})
export class LoggedInGuard implements CanActivate {
  constructor(private store: AuthStore, private router: Router) {}

  canActivate(): Observable<boolean | UrlTree> {
    return this.store.pipe(
      selectIsLoggedIn,
      map((isLoggedIn) => {
        if (!isLoggedIn) {
          return this.router.parseUrl('landing');
        }

        return true;
      }),
      first(),
    );
  }
}
