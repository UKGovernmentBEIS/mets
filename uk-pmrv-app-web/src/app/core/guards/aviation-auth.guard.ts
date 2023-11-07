import { Injectable } from '@angular/core';
import { CanActivate, Router, UrlTree } from '@angular/router';

import { first, map, Observable, of, switchMap } from 'rxjs';

import { AuthStore, selectLoginStatus } from '@core/store/auth';

import { UsersService } from 'pmrv-api';

@Injectable({
  providedIn: 'root',
})
export class AviationAuthGuard implements CanActivate {
  constructor(private store: AuthStore, private router: Router, private usersService: UsersService) {}

  canActivate(): Observable<boolean | UrlTree> {
    return this.store.pipe(selectLoginStatus('AVIATION')).pipe(
      first(),
      switchMap((loginStatus) => {
        if (loginStatus === 'ENABLED') {
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
