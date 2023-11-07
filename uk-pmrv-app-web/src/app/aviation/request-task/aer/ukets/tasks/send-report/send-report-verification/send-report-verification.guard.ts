import { Injectable } from '@angular/core';
import { CanActivate } from '@angular/router';

import { map, switchMap, take } from 'rxjs';

import { requestTaskQuery, RequestTaskStore } from '@aviation/request-task/store';
import { BusinessErrorService } from '@error/business-error/business-error.service';
import { notFoundVerificationBodyError } from '@tasks/aer/error/business-errors';

import { AccountVerificationBodyService } from 'pmrv-api';

@Injectable({
  providedIn: 'root',
})
export class VerificationGuard implements CanActivate {
  constructor(
    private readonly store: RequestTaskStore,
    private readonly accountVerificationBodyService: AccountVerificationBodyService,
    private readonly businessErrorService: BusinessErrorService,
  ) {}

  canActivate(): any {
    return this.store
      .pipe(
        take(1),
        requestTaskQuery.selectRequestInfo,
        map((info) => info.accountId),
      )
      .pipe(
        switchMap((accountId) => this.accountVerificationBodyService.getVerificationBodyOfAccount(accountId)),
        map((vb) => (!vb ? this.businessErrorService.showError(notFoundVerificationBodyError()) : true)),
      );
  }
}
