import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate } from '@angular/router';

import { Observable, of, switchMap } from 'rxjs';

import { AccountVerificationBodyService } from 'pmrv-api';

import { BusinessErrorService } from '../../../../../../error/business-error/business-error.service';
import { appointedVerificationBodyError } from '../../errors/business-error';

@Injectable({ providedIn: 'root' })
export class AppointGuard implements CanActivate {
  constructor(
    private readonly accountVerificationBodyService: AccountVerificationBodyService,
    private readonly businessErrorService: BusinessErrorService,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean> {
    const accountId = Number(route.paramMap.get('accountId'));

    return this.accountVerificationBodyService
      .getVerificationBodyOfAccount(accountId)
      .pipe(
        switchMap((vb) =>
          vb ? this.businessErrorService.showError(appointedVerificationBodyError(accountId)) : of(true),
        ),
      );
  }
}
