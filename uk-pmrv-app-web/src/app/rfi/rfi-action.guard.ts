import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate } from '@angular/router';

import { mapTo, tap } from 'rxjs';

import { RequestActionsService } from 'pmrv-api';

import { IncorporateHeaderStore } from '../shared/incorporate-header/store/incorporate-header.store';
import { RfiStore } from './store/rfi.store';

@Injectable({ providedIn: 'root' })
export class RfiActionGuard implements CanActivate {
  constructor(
    private readonly store: RfiStore,
    private readonly requestActionsService: RequestActionsService,
    private readonly incorporateHeaderStore: IncorporateHeaderStore,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): any {
    return this.requestActionsService.getRequestActionById(Number(route.paramMap.get('actionId'))).pipe(
      tap((requestAction) => {
        this.store.reset();
        const state = this.store.getState();
        this.store.setState({
          ...state,
          ...requestAction.payload,
          actionType: requestAction.type,
          actionId: requestAction.id,
          requestId: requestAction.requestId,
          requestType: requestAction.requestType,
          competentAuthority: requestAction.competentAuthority,
          isEditable: false,
          requestActionCreationDate: requestAction.creationDate,
        });

        this.incorporateHeaderStore.reset();
        this.incorporateHeaderStore.setState({
          ...this.incorporateHeaderStore.getState(),
          accountId: requestAction.requestAccountId,
        });
      }),
      mapTo(true),
    );
  }

  canDeactivate(): boolean {
    this.incorporateHeaderStore.reset();
    return true;
  }
}
