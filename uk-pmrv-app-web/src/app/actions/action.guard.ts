import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, UrlTree } from '@angular/router';

import { filter, map, Observable, tap } from 'rxjs';

import { IncorporateHeaderStore } from '../shared/incorporate-header/store/incorporate-header.store';
import { CommonActionsStore } from './store/common-actions.store';

@Injectable({ providedIn: 'root' })
export class ActionGuard {
  constructor(
    private readonly store: CommonActionsStore,
    private readonly incorporateHeaderStore: IncorporateHeaderStore,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    const actionId = route.params['actionId'];
    if (actionId) {
      this.store.requestedAction(actionId);
    }
    return this.store.storeInitialized$.pipe(
      filter((init) => !!init),
      tap(() => {
        this.incorporateHeaderStore.reset();
        this.incorporateHeaderStore.setState({
          ...this.incorporateHeaderStore.getState(),
          accountId: this.store.getState().action.requestAccountId,
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
