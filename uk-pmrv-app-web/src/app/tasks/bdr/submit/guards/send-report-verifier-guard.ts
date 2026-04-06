import { Injectable } from '@angular/core';

import { Observable, of, switchMap } from 'rxjs';

import { BusinessErrorService } from '@error/business-error/business-error.service';
import { notFoundVerificationBodyError } from '@tasks/aer/error/business-errors';
import { BdrService } from '@tasks/bdr/shared/services/bdr.service';

import { AccountVerificationBodyService } from 'pmrv-api';

@Injectable({
  providedIn: 'root',
})
export class SendReportVerifierGuard {
  constructor(
    private readonly bdrService: BdrService,
    private readonly accountVerificationBodyService: AccountVerificationBodyService,
    private readonly businessErrorService: BusinessErrorService,
  ) {}

  canActivate(): Observable<boolean> {
    let accountId: number;

    return this.bdrService.requestAccountId$.pipe(
      switchMap((id) => {
        accountId = id;
        return this.accountVerificationBodyService.getVerificationBodyOfAccount(accountId);
      }),
      switchMap((vb) =>
        !vb ? this.businessErrorService.showError(notFoundVerificationBodyError(accountId)) : of(true),
      ),
    );
  }
}
