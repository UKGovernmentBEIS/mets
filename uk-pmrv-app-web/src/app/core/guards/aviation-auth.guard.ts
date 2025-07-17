import { Injectable } from '@angular/core';
import { Router, UrlTree } from '@angular/router';

import { first, map, Observable, of, switchMap, withLatestFrom } from 'rxjs';

import { AuthStore, selectLoginStatus, selectUserRoleType } from '@core/store/auth';

import { UsersService } from 'pmrv-api';

@Injectable({
  providedIn: 'root',
})
export class AviationAuthGuard {
  constructor(
    private store: AuthStore,
    private router: Router,
    private usersService: UsersService,
  ) {}

  canActivate(): Observable<boolean | UrlTree> {
    return this.store.pipe(selectLoginStatus('AVIATION')).pipe(
      first(),
      withLatestFrom(this.store.pipe(selectUserRoleType)),
      switchMap(([loginStatus, role]) => {
        if (loginStatus === 'ENABLED' || (loginStatus === 'NO_AUTHORITY' && ['REGULATOR', 'VERIFIER'].includes(role))) {
          return this.usersService.registerUserLastLoginDomain({ loginDomain: 'AVIATION' }).pipe(
            map(() => {
              this.store.setCurrentDomain('AVIATION');
              this.store.setLastLoginDomain('AVIATION');
              this.store.setSwitchingDomain(null);
              return true;
            }),
          );
        }

        return of(this.router.parseUrl('landing'));
      }),
    );
  }
}
