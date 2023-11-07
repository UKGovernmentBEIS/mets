import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, CanDeactivate, UrlTree } from '@angular/router';

import { filter, mapTo, Observable, tap } from 'rxjs';

import { IncorporateHeaderStore } from '../shared/incorporate-header/store/incorporate-header.store';
import { CommonTasksStore } from './store/common-tasks.store';

@Injectable({ providedIn: 'root' })
export class TaskGuard implements CanActivate, CanDeactivate<any> {
  constructor(
    private readonly store: CommonTasksStore,
    private readonly incorporateHeaderStore: IncorporateHeaderStore,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    const taskId = route.params['taskId'];
    if (taskId) {
      this.store.requestedTask(taskId);
    }
    return this.store.storeInitialized$.pipe(
      filter((init) => !!init),
      tap(() => {
        this.incorporateHeaderStore.reset();
        this.incorporateHeaderStore.setState({
          ...this.incorporateHeaderStore.getState(),
          accountId: this.store.getState().requestTaskItem.requestInfo.accountId,
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
