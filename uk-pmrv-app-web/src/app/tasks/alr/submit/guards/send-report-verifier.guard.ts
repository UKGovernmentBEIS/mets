import { Injectable } from '@angular/core';

import { Observable, of, switchMap } from 'rxjs';

import { BusinessErrorService } from '@error/business-error/business-error.service';
import { notFoundVerificationBodyError } from '@tasks/aer/error/business-errors';
import { AlrService } from '@tasks/alr/core';

import { AccountVerificationBodyService } from 'pmrv-api';

@Injectable()
export class AlrSendReportVerifierGuard {
  constructor(
    private readonly alrService: AlrService,
    private readonly accountVerificationBodyService: AccountVerificationBodyService,
    private readonly businessErrorService: BusinessErrorService,
  ) {}

  canActivate(): Observable<boolean> {
    return this.alrService.requestAccountId$.pipe(
      switchMap((accountId) => this.accountVerificationBodyService.getVerificationBodyOfAccount(accountId)),
      switchMap((vb) => (!vb ? this.businessErrorService.showError(notFoundVerificationBodyError()) : of(true))),
    );
  }
}
