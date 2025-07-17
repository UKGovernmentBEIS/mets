import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { first, map, Observable } from 'rxjs';

import { AuthStore, selectUserRoleType, UserState } from '../../core/store';

@Injectable({
  providedIn: 'root',
})
export class RoleTypeGuard {
  constructor(
    private router: Router,
    private authStore: AuthStore,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    const roleType = route.data.roleTypeGuards as UserState['roleType'];
    return this.authStore.pipe(
      selectUserRoleType,
      first(),
      map((authUserRoleType) => (authUserRoleType === roleType ? true : this.router.parseUrl('landing'))),
    );
  }
}
