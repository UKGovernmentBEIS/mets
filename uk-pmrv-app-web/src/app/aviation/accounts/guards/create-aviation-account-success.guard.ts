import { Injectable } from '@angular/core';
import { CanActivate, Router, UrlTree } from '@angular/router';

import { combineLatest, map, Observable, take } from 'rxjs';

import { AviationAccountsStore, selectIsInitiallySubmitted, selectIsSubmitted, selectNewAccount } from '../store';

@Injectable()
export class CreateAviationAccountSuccessGuard implements CanActivate {
  constructor(private readonly store: AviationAccountsStore, private readonly router: Router) {}

  canActivate(): Observable<boolean | UrlTree> {
    return combineLatest([
      this.store.pipe(selectIsInitiallySubmitted),
      this.store.pipe(selectIsSubmitted),
      this.store.pipe(selectNewAccount),
    ]).pipe(
      take(1),
      map(([isInitiallySubmitted, isSubmitted, newAccount]) => {
        if (isSubmitted) {
          return true;
        } else if (isInitiallySubmitted && newAccount) {
          return this.router.parseUrl('/aviation/accounts/create');
        } else {
          return this.router.parseUrl('/aviation/dashboard');
        }
      }),
    );
  }
}
