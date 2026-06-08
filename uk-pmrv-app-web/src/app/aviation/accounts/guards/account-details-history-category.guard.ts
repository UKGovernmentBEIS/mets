import { Injectable } from '@angular/core';
import { UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { AuthStore, selectUserRoleType } from '@core/store';

import { AviationAccountsStore } from '../store';

@Injectable()
export class AviationAccountDetailsHistoryGuard {
  constructor(
    private readonly authStore: AuthStore,
    private readonly store: AviationAccountsStore,
  ) {}

  canActivate(): Observable<boolean | UrlTree> {
    return this.authStore.pipe(
      selectUserRoleType,
      map((role) => role === 'REGULATOR'),
    );
  }

  canDeactivate(): boolean {
    this.store.resetAccountDetailsHistory();
    return true;
  }
}
