import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, CanDeactivate } from '@angular/router';

import { mapTo, tap } from 'rxjs';

import { RequestActionsService } from 'pmrv-api';

import { IncorporateHeaderStore } from '../shared/incorporate-header/store/incorporate-header.store';
import { PermitSurrenderState } from './store/permit-surrender.state';
import { PermitSurrenderStore } from './store/permit-surrender.store';

@Injectable({ providedIn: 'root' })
export class PermitSurrenderActionGuard implements CanActivate, CanDeactivate<any> {
  constructor(
    private readonly store: PermitSurrenderStore,
    private readonly incorporateHeaderStore: IncorporateHeaderStore,
    private readonly requestActionsService: RequestActionsService,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): any {
    const actionId = Number(route.paramMap.get('actionId'));

    return this.requestActionsService.getRequestActionById(actionId).pipe(
      tap((requestAction) => {
        this.store.reset();
        this.store.setState({
          ...this.store.getState(),
          ...requestAction.payload,
          requestActionId: requestAction.id,
          requestActionType: requestAction.type,
          requestId: requestAction.requestId,
          requestType: requestAction.requestType,
          competentAuthority: requestAction.competentAuthority,
          isEditable: false,
          requestActionCreationDate: requestAction.creationDate,
          requestActionSubmitter: requestAction.submitter,
        } as PermitSurrenderState);

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
