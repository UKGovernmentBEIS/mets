import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot } from '@angular/router';

import { map, tap } from 'rxjs';

import { RequestActionsService } from 'pmrv-api';

import { IncorporateHeaderStore } from '../shared/incorporate-header/store/incorporate-header.store';
import { PaymentState } from './store/payment.state';
import { PaymentStore } from './store/payment.store';

@Injectable({ providedIn: 'root' })
export class PaymentActionGuard {
  constructor(
    private readonly store: PaymentStore,
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
          requestId: requestAction.requestId,
          requestType: requestAction.requestType,
          competentAuthority: requestAction.competentAuthority,
          actionPayload: requestAction.payload,
          requestActionId: requestAction.id,
          isEditable: false,
          requestActionCreationDate: requestAction.creationDate,
        } as PaymentState);

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
