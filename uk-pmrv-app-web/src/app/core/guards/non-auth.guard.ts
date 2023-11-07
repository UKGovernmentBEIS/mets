import { Injectable } from '@angular/core';
import { CanActivate, Router, UrlTree } from '@angular/router';

import { first, map, Observable, switchMap } from 'rxjs';

import { AuthService } from '@core/services/auth.service';
import { AuthStore, selectIsLoggedIn } from '@core/store/auth';

@Injectable({ providedIn: 'root' })
export class NonAuthGuard implements CanActivate {
  constructor(
    private readonly router: Router,
    private readonly authService: AuthService,
    private readonly authStore: AuthStore,
  ) {}

  canActivate(): Observable<boolean | UrlTree> {
    return this.authService.checkUser().pipe(
      switchMap(() => this.authStore.pipe(selectIsLoggedIn)),
      map((isLoggedIn) => !isLoggedIn || this.router.parseUrl('landing')),
      first(),
    );
  }
}
