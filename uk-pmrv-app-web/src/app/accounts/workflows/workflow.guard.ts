import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot } from '@angular/router';

import { IncorporateHeaderStore } from '../../shared/incorporate-header/store/incorporate-header.store';

@Injectable({
  providedIn: 'root',
})
export class WorkflowGuard {
  constructor(private readonly store: IncorporateHeaderStore) {}

  canActivate(route: ActivatedRouteSnapshot): boolean {
    this.store.reset();
    this.store.setState({
      ...this.store.getState(),
      accountId: Number(route.paramMap.get('accountId')),
    });
    return true;
  }

  canDeactivate(): boolean {
    this.store.reset();
    return true;
  }
}
