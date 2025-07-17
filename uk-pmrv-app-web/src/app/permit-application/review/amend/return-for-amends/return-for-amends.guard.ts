import { Injectable } from '@angular/core';
import { Router, UrlTree } from '@angular/router';

import { combineLatest, map, Observable } from 'rxjs';

import { returnForAmendRequestTaskActionTypes } from '../../../shared/utils/permit';
import { PermitApplicationState } from '../../../store/permit-application.state';
import { PermitApplicationStore } from '../../../store/permit-application.store';

@Injectable()
export class ReturnForAmendsGuard {
  constructor(
    private readonly store: PermitApplicationStore<PermitApplicationState>,
    private readonly router: Router,
  ) {}

  canActivate(): Observable<boolean | UrlTree> {
    return combineLatest([this.store, this.store.isAnySectionForAmend$()]).pipe(
      map(
        ([state, isAnyForAmend]) =>
          !state.isRequestTask ||
          (returnForAmendRequestTaskActionTypes.some((actionType) =>
            state?.allowedRequestTaskActions?.includes(actionType),
          ) &&
            isAnyForAmend) ||
          this.router.parseUrl(`/`),
      ),
    );
  }
}
