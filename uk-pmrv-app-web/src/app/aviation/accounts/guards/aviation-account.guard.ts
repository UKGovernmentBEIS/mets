import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot } from '@angular/router';

import { first, map, Observable, tap } from 'rxjs';

import { AviationAccountEmpDTO, AviationAccountViewService } from 'pmrv-api';

import { AviationAccountsStore } from '../store';

@Injectable({
  providedIn: 'root',
})
export class AviationAccountGuard {
  account: AviationAccountEmpDTO;

  constructor(
    private readonly accountViewService: AviationAccountViewService,
    private readonly store: AviationAccountsStore,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean> {
    return this.accountViewService.getAviationAccountById(Number(route.paramMap.get('accountId'))).pipe(
      first(),
      tap((account) => {
        this.account = account;
        this.store.setCurrentAccount(account);
      }),
      map((account) => !!account),
    );
  }
}
