import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot } from '@angular/router';

import { combineLatest, first, map } from 'rxjs';

import { selectFeatures } from '@core/config/config.selectors';
import { ConfigStore } from '@core/config/config.store';
import { AuthStore, selectUserState } from '@core/store/auth';
import { PermitApplicationActionGuard } from '@permit-application/permit-application-action.guard';

import { RequestActionsService } from 'pmrv-api';

import { IncorporateHeaderStore } from '../shared/incorporate-header/store/incorporate-header.store';
import { PermitIssuanceStore } from './store/permit-issuance.store';

@Injectable({ providedIn: 'root' })
export class PermitIssuanceActionGuard extends PermitApplicationActionGuard {
  constructor(
    private readonly store: PermitIssuanceStore,
    private readonly incorporateHeaderStore: IncorporateHeaderStore,
    private readonly requestActionsService: RequestActionsService,
    private readonly authStore: AuthStore,
    private readonly configStore: ConfigStore,
  ) {
    super();
  }

  canActivate(route: ActivatedRouteSnapshot): any {
    const actionId = Number(route.paramMap.get('actionId'));

    return combineLatest([
      this.requestActionsService.getRequestActionById(actionId),
      this.authStore.pipe(selectUserState),
      this.configStore.pipe(selectFeatures),
    ]).pipe(
      first(),
      map(([requestAction, userState, features]) => {
        this.store.reset();
        const state = this.store.getState();
        this.store.setState({
          ...state,
          ...this.buildPermitApplicationState(requestAction, userState),
          actionId: actionId,

          features: features,
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
