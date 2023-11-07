import { Injectable } from '@angular/core';
import { CanActivate } from '@angular/router';

import { map, switchMap } from 'rxjs';

import { AerService } from '@tasks/aer/core/aer.service';

import { AccountVerificationBodyService } from 'pmrv-api';

import { BusinessErrorService } from '../../../../../error/business-error/business-error.service';
import { notFoundVerificationBodyError } from '../../../error/business-errors';

@Injectable({
  providedIn: 'root',
})
export class VerificationGuard implements CanActivate {
  constructor(
    private readonly aerService: AerService,
    private readonly accountVerificationBodyService: AccountVerificationBodyService,
    private readonly businessErrorService: BusinessErrorService,
  ) {}

  canActivate(): any {
    return this.aerService.requestAccountId$.pipe(
      switchMap((accountId) => this.accountVerificationBodyService.getVerificationBodyOfAccount(accountId)),
      map((vb) => (!vb ? this.businessErrorService.showError(notFoundVerificationBodyError()) : true)),
    );
  }
}
