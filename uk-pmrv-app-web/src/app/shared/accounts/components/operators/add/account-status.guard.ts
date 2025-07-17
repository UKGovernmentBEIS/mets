import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { map, Observable, switchMap } from 'rxjs';

import { AuthStore, selectCurrentDomain } from '@core/store/auth';

import { AviationAccountViewService, InstallationAccountDTO, InstallationAccountViewService } from 'pmrv-api';

import { accountFinalStatuses } from '../../../../../accounts/core/accountFinalStatuses';

@Injectable({
  providedIn: 'root',
})
export class AccountStatusGuard {
  constructor(
    private readonly installationAccountViewService: InstallationAccountViewService,
    private readonly aviationAccountViewService: AviationAccountViewService,
    private readonly router: Router,
    readonly authStore: AuthStore,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    const accountId = Number(route.paramMap.get('accountId'));
    return this.authStore.pipe(selectCurrentDomain).pipe(
      switchMap((domain) => {
        if (domain === 'INSTALLATION') {
          return this.installationAccountViewService.getInstallationAccountById(accountId).pipe(
            map((account) => {
              return (
                accountFinalStatuses((account?.account as InstallationAccountDTO)?.status) ||
                this.router.parseUrl(`/accounts/${accountId}`)
              );
            }),
          );
        } else {
          return this.aviationAccountViewService.getAviationAccountById(accountId).pipe(
            map((account) => {
              return (
                accountFinalStatuses((account?.aviationAccount as InstallationAccountDTO)?.status) ||
                this.router.parseUrl(`aviation/accounts/${accountId}`)
              );
            }),
          );
        }
      }),
    );
  }
}
