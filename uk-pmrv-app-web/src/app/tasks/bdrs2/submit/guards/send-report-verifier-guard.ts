import { Injectable } from '@angular/core';

import { Observable, of, switchMap } from 'rxjs';

import { BusinessErrorService } from '@error/business-error/business-error.service';
import { notFoundVerificationBodyError } from '@tasks/aer/error/business-errors';
import { BdrS2Service } from '@tasks/bdrs2/core';

import { AccountVerificationBodyService } from 'pmrv-api';

@Injectable({
  providedIn: 'root',
})
export class Bdrs2SendReportVerifierGuard {
  constructor(
    private readonly bdrs2Service: BdrS2Service,
    private readonly accountVerificationBodyService: AccountVerificationBodyService,
    private readonly businessErrorService: BusinessErrorService,
  ) {}

  canActivate(): Observable<boolean> {
    let accountId: number;

    return this.bdrs2Service.requestAccountId$.pipe(
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
