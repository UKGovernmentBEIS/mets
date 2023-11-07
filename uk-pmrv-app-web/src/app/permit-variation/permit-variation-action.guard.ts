import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate } from '@angular/router';

import { combineLatest, first, map } from 'rxjs';

import { AuthStore, selectUserState } from '@core/store/auth';
import { PermitApplicationActionGuard } from '@permit-application/permit-application-action.guard';

import { RequestActionsService } from 'pmrv-api';

import { IncorporateHeaderStore } from '../shared/incorporate-header/store/incorporate-header.store';
import { PermitVariationStore } from './store/permit-variation.store';

@Injectable({ providedIn: 'root' })
export class PermitVariationActionGuard extends PermitApplicationActionGuard implements CanActivate {
  constructor(
    private readonly store: PermitVariationStore,
    private readonly incorporateHeaderStore: IncorporateHeaderStore,
    private readonly requestActionsService: RequestActionsService,
    private readonly authStore: AuthStore,
  ) {
    super();
  }

  canActivate(route: ActivatedRouteSnapshot): any {
    const actionId = Number(route.paramMap.get('actionId'));

    return combineLatest([
      this.requestActionsService.getRequestActionById(actionId),
      this.authStore.pipe(selectUserState),
    ]).pipe(
      first(),
      map(([requestAction, userState]) => {
        this.store.reset();
        const state = this.store.getState();
        this.store.setState({
          ...state,
          ...this.buildPermitApplicationState(requestAction, userState),
          actionId: actionId,
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
