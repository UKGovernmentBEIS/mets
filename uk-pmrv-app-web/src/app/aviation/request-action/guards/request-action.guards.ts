import { inject } from '@angular/core';
import { CanActivateFn, CanDeactivateFn, Router } from '@angular/router';

import { catchError, concatMap, map, of, tap } from 'rxjs';

import { CommonActionsStore } from '@actions/store/common-actions.store';
import { AuthStore, selectUserRoleType } from '@core/store';
import { AVIATION_REQUEST_TYPES } from '@shared/utils/request.utils';

import { RequestActionsService } from 'pmrv-api';

import { IncorporateHeaderStore } from '../../../shared/incorporate-header/store/incorporate-header.store';
import { RequestActionStore } from '../store';

export const canActivateRequestAction: CanActivateFn = (route) => {
  const requestActionsService = inject(RequestActionsService);
  const authStore = inject(AuthStore);
  const store = inject(RequestActionStore);
  const router = inject(Router);
  const incorporateHeaderStore = inject(IncorporateHeaderStore);
  const commonStore = inject(CommonActionsStore);

  const id = +route.paramMap.get('actionId');
  if (!route.paramMap.has('actionId') || Number.isNaN(id)) {
    return router.createUrlTree(['aviation', 'dashboard']);
  }

  return requestActionsService.getRequestActionById(id).pipe(
    concatMap((requestActionItem) => {
      if (!AVIATION_REQUEST_TYPES.includes(requestActionItem?.requestType)) {
        throw new Error(`Action with id: ${id} is not an aviation related action`);
      }

      return authStore.pipe(
        selectUserRoleType,
        map((roleType) => ({ requestActionItem, roleType })),
      );
    }),
    tap(({ requestActionItem, roleType }) => {
      store.setRegulatorViewer(roleType === 'REGULATOR');
      store.setRequestActionItem(requestActionItem);
      incorporateHeaderStore.reset();
      incorporateHeaderStore.setState({
        ...incorporateHeaderStore.getState(),
        accountId: requestActionItem.requestAccountId,
      });

      commonStore.setState({
        action: store.getState().requestActionItem,
        storeInitialized: true,
      });
    }),
    map(() => true),
    catchError(() => {
      return of(router.createUrlTree(['aviation', 'dashboard']));
    }),
  );
};

export const canDeactivateRequestAction: CanDeactivateFn<any> = () => {
  const incorporateHeaderStore = inject(IncorporateHeaderStore);

  incorporateHeaderStore.reset();
  return true;
};
