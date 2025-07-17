import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, UrlTree } from '@angular/router';

import { map, Observable, tap } from 'rxjs';

import { RequestActionPayload, RequestActionsService } from 'pmrv-api';

import { updateState } from '../functions/update-state';
import { InstallationAccountApplicationStore } from '../store/installation-account-application.store';

@Injectable({ providedIn: 'root' })
export class SummaryActionGuard {
  private type: RequestActionPayload['payloadType'];

  constructor(
    private readonly requestActionsService: RequestActionsService,
    private readonly store: InstallationAccountApplicationStore,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.requestActionsService.getRequestActionById(Number(route.paramMap.get('actionId'))).pipe(
      tap((res) => updateState(this.store, res)),
      tap((res) => (this.type = res.payload.payloadType)),
      map(() => true),
    );
  }

  resolve(): RequestActionPayload['payloadType'] {
    return this.type;
  }
}
