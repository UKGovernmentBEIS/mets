import { Injectable } from '@angular/core';

import { Observable, of } from 'rxjs';

import { InstallationAccountsService } from 'pmrv-api';

@Injectable()
export class AccountsServiceStub implements Partial<InstallationAccountsService> {
  isExistingAccountName(name: string): Observable<any> {
    if (name === 'Existing') {
      return of(true);
    } else {
      return of(false);
    }
  }
}
