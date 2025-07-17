import { Injectable } from '@angular/core';
import { Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { AviationAccountsStore, selectIsInitiallySubmitted } from '../store';

@Injectable()
export class CreateAviationAccountSummaryGuard {
  constructor(
    private readonly store: AviationAccountsStore,
    private readonly router: Router,
  ) {}

  canActivate(): Observable<boolean | UrlTree> {
    return this.store.pipe(
      selectIsInitiallySubmitted,
      map((isInitiallySubmitted) => {
        return isInitiallySubmitted || this.router.parseUrl('/aviation/accounts/create');
      }),
    );
  }
}
