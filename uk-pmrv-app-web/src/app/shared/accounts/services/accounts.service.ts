import { Injectable } from '@angular/core';

import { first, Observable, switchMap } from 'rxjs';

import { AuthStore, selectCurrentDomain } from '@core/store/auth';

import {
  AccountSearchResults,
  AviationAccountSearchResults,
  AviationAccountsService,
  InstallationAccountsService,
} from 'pmrv-api';

export type AccountSearchResultsResponse = AccountSearchResults | AviationAccountSearchResults;

@Injectable({
  providedIn: 'root',
})
export class AccountsService {
  constructor(
    private readonly authStore: AuthStore,
    private readonly installationAccountsService: InstallationAccountsService,
    private readonly aviationAccountsService: AviationAccountsService,
  ) {}

  getCurrentUserAccounts(
    page: number,
    pageSize: number,
    searchTerm?: string,
  ): Observable<AccountSearchResultsResponse> {
    return this.authStore.pipe(
      selectCurrentDomain,
      switchMap((currentDomain) => {
        switch (currentDomain) {
          case 'INSTALLATION':
            return this.installationAccountsService.getCurrentUserInstallationAccounts(page, pageSize, searchTerm);
          case 'AVIATION':
            return this.aviationAccountsService.getCurrentUserAviationAccounts(page, pageSize, searchTerm);
        }
      }),
      first(),
    );
  }
}
