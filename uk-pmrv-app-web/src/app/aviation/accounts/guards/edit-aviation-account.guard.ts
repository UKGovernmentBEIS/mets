import { Injectable } from '@angular/core';
import { UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { AuthStore, selectUserRoleType } from '@core/store';

import { AviationAccountFormProvider } from '../services';

@Injectable()
export class EditAviationAccountGuard {
  constructor(
    private readonly authStore: AuthStore,
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
    return true;
  }
}
