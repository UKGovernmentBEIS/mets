import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot } from '@angular/router';

import { first, map, Observable, tap } from 'rxjs';

import { InstallationAccountPermitDTO, InstallationAccountViewService } from 'pmrv-api';

@Injectable({
  providedIn: 'root',
})
export class AccountGuard {
  accountPermit: InstallationAccountPermitDTO;

  constructor(private readonly accountViewService: InstallationAccountViewService) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean> {
    return this.accountViewService.getInstallationAccountById(Number(route.paramMap.get('accountId'))).pipe(
      first(),
      tap((accountPermit) => (this.accountPermit = accountPermit)),
      map((accountPermit) => !!accountPermit),
    );
  }

  canDeactivate(): boolean {
    return true;
  }

  resolve(): InstallationAccountPermitDTO {
    return this.accountPermit;
  }
}
