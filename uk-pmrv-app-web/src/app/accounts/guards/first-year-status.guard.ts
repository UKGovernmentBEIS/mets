import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { AuthStore } from '@core/store/auth';

import { InstallationAccountDTO, InstallationAccountViewService } from 'pmrv-api';

import { accountFirstYearStatuses } from '../core/accountFinalStatuses';

@Injectable({
  providedIn: 'root',
})
export class FirstYearStatusGuard {
  constructor(
    private readonly installationAccountViewService: InstallationAccountViewService,
    private readonly router: Router,
    readonly authStore: AuthStore,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    const accountId = Number(route.paramMap.get('accountId'));
    return this.installationAccountViewService.getInstallationAccountById(accountId).pipe(
      map((account) => {
        return (
          accountFirstYearStatuses((account?.account as InstallationAccountDTO)?.status) ||
          this.router.parseUrl(`/accounts/${accountId}`)
        );
      }),
    );
  }
}
