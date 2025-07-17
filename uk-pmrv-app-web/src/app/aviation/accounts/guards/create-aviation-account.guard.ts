import { Injectable } from '@angular/core';
import { UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { AuthStore, selectUserRoleType } from '@core/store';

import { AviationAccountFormProvider } from '../services';
import { AviationAccountsStore } from '../store';

@Injectable()
export class CreateAviationAccountGuard {
  constructor(
    private readonly authStore: AuthStore,
    private readonly store: AviationAccountsStore,
    private readonly accountFormProvider: AviationAccountFormProvider,
  ) {}

  canActivate(): Observable<boolean | UrlTree> {
    return this.authStore.pipe(
      selectUserRoleType,
      map((role) => role === 'REGULATOR'),
    );
  }

  canDeactivate(): boolean {
    this.accountFormProvider.destroyForm();
    this.store.resetCreateAccount();
    return true;
  }
}
