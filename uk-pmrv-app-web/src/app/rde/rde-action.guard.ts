import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot } from '@angular/router';

import { map, Observable, tap } from 'rxjs';

import { RequestActionsService } from 'pmrv-api';

import { IncorporateHeaderStore } from '../shared/incorporate-header/store/incorporate-header.store';
import { RdeStore } from './store/rde.store';

@Injectable({
  providedIn: 'root',
})
export class RdeActionGuard {
  constructor(
    private readonly store: RdeStore,
    private readonly requestActionsService: RequestActionsService,
    private readonly incorporateHeaderStore: IncorporateHeaderStore,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean> {
    return this.requestActionsService.getRequestActionById(Number(route.paramMap.get('actionId'))).pipe(
      tap((requestAction) => {
        this.store.reset();
        const state = this.store.getState();
        this.store.setState({
          ...state,
          ...requestAction.payload,
          actionId: requestAction.id,
          requestId: requestAction.requestId,
          requestType: requestAction.requestType,
          actionType: requestAction.type,
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
