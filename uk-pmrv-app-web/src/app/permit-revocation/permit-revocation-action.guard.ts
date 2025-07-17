import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot } from '@angular/router';

import { map, tap } from 'rxjs';

import { PermitRevocationState } from '@permit-revocation/store/permit-revocation.state';
import { PermitRevocationStore } from '@permit-revocation/store/permit-revocation-store';
import { IncorporateHeaderStore } from '@shared/incorporate-header/store/incorporate-header.store';

import { RequestActionsService } from 'pmrv-api';

@Injectable({ providedIn: 'root' })
export class PermitRevocationActionGuard {
  constructor(
    private readonly store: PermitRevocationStore,
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
          requestId: requestAction.requestId,
          requestType: requestAction.requestType,
          competentAuthority: requestAction.competentAuthority,
          requestActionId: requestAction.id,
          requestActionType: requestAction.type,
          isEditable: false,
          requestActionCreationDate: requestAction.creationDate,
          requestActionSubmitter: requestAction.submitter,
        } as PermitRevocationState);

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
