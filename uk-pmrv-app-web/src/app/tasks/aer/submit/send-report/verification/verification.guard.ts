import { Injectable } from '@angular/core';

import { Observable, of, switchMap } from 'rxjs';

import { AerService } from '@tasks/aer/core/aer.service';

import { AccountVerificationBodyService } from 'pmrv-api';

import { BusinessErrorService } from '../../../../../error/business-error/business-error.service';
import { notFoundVerificationBodyError } from '../../../error/business-errors';

@Injectable({
  providedIn: 'root',
})
export class VerificationGuard {
  constructor(
    private readonly aerService: AerService,
    private readonly accountVerificationBodyService: AccountVerificationBodyService,
    private readonly businessErrorService: BusinessErrorService,
  ) {}

  canActivate(): Observable<boolean> {
    let accountId: number;

    return this.aerService.requestAccountId$.pipe(
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
