import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { PermitApplicationState } from '../store/permit-application.state';
import { PermitApplicationStore } from '../store/permit-application.store';

@Injectable()
export class SummaryGuard {
  constructor(
    private readonly store: PermitApplicationStore<PermitApplicationState>,
    private readonly router: Router,
  ) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean | UrlTree> {
    return this.store.pipe(
      map((storeState) => {
        if (!storeState.isRequestTask || !storeState.isEditable) {
          return true;
        } else if (route.data.statusKey) {
          return (
            storeState.permitSectionsCompleted?.[route.data.statusKey]?.[route.paramMap.get('index') || 0] ||
            this.router.parseUrl(state.url.slice(0, state.url.lastIndexOf('/summary')))
          );
        } else {
          return (
            storeState.permitSectionsCompleted?.[route.data.permitTask]?.[route.paramMap.get('index') ?? 0] ||
            this.router.parseUrl(state.url.slice(0, state.url.lastIndexOf('/summary')))
          );
        }
      }),
    );
  }
}
