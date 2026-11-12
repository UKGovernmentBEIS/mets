import { Injectable } from '@angular/core';
import { Router, UrlTree } from '@angular/router';

import { first, map, Observable, of, switchMap, tap, withLatestFrom } from 'rxjs';

import { AuthStore, selectCurrentDomain, selectLoginStatus, selectUserRoleType } from '@core/store/auth';

import { UsersService } from 'pmrv-api';

@Injectable({
  providedIn: 'root',
})
export class InstallationAuthGuard {
  constructor(
    private store: AuthStore,
    private router: Router,
    private usersService: UsersService,
  ) {}

  canActivate(): Observable<boolean | UrlTree> {
    return this.store.pipe(selectLoginStatus('INSTALLATION')).pipe(
      first(),
      withLatestFrom(this.store.pipe(selectCurrentDomain), this.store.pipe(selectUserRoleType)),
      switchMap(([loginStatus, currentDomain, role]) => {
        if (loginStatus === 'ENABLED' || (loginStatus === 'NO_AUTHORITY' && ['REGULATOR', 'VERIFIER'].includes(role))) {
          // @TODO :: Discard this after installation is in its own module/route
          return currentDomain !== 'INSTALLATION'
            ? this.usersService.registerUserLastLoginDomain({ loginDomain: 'INSTALLATION' }).pipe(
                tap(() => {
                  this.store.setCurrentDomain('INSTALLATION');
                  this.store.setLastLoginDomain('INSTALLATION');
                  this.store.setSwitchingDomain(null);
                }),
                map(() => true),
              )
            : of(true);
        }

        // Remember the requested domain so the landing page can show the no-access message instead of redirecting
        if (loginStatus === 'NO_AUTHORITY') {
          this.store.setSwitchingDomain('INSTALLATION');
        }

        return of(this.router.parseUrl('landing'));
      }),
    );
  }
}
