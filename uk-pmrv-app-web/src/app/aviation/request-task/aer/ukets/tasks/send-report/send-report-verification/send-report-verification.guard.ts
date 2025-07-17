import { Injectable } from '@angular/core';

import { map, Observable, of, switchMap, take } from 'rxjs';

import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { BusinessErrorService } from '@error/business-error/business-error.service';
import { notFoundVerificationBodyError } from '@tasks/aer/error/business-errors';

import { AccountVerificationBodyService } from 'pmrv-api';

@Injectable({
  providedIn: 'root',
})
export class VerificationGuard {
  constructor(
    private readonly store: RequestTaskStore,
    private readonly accountVerificationBodyService: AccountVerificationBodyService,
    private readonly businessErrorService: BusinessErrorService,
  ) {}

  canActivate(): Observable<boolean> {
    return this.store
      .pipe(
        take(1),
        requestTaskQuery.selectRequestInfo,
        map((info) => info.accountId),
      )
      .pipe(
        switchMap((accountId) => this.accountVerificationBodyService.getVerificationBodyOfAccount(accountId)),
        switchMap((vb) => (!vb ? this.businessErrorService.showError(notFoundVerificationBodyError()) : of(true))),
      );
  }
}
