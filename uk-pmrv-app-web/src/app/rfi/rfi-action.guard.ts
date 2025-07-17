import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot } from '@angular/router';

import { map, tap } from 'rxjs';

import { IncorporateHeaderStore } from '@shared/incorporate-header/store/incorporate-header.store';

import { RequestActionsService } from 'pmrv-api';

import { RfiStore } from './store/rfi.store';

@Injectable({ providedIn: 'root' })
export class RfiActionGuard {
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
      map(() => true),
    );
  }

  canDeactivate(): boolean {
    this.incorporateHeaderStore.reset();
    return true;
  }
}
