import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { RdeStore } from '../../store/rde.store';

@Injectable({
  providedIn: 'root',
})
export class NotifyUsersGuard {
  constructor(
    private readonly router: Router,
    private readonly store: RdeStore,
  ) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): true | Observable<true | UrlTree> {
    return (
      this.router.getCurrentNavigation().extras?.state?.changing ||
      this.store.pipe(
        map((storeState) => {
          const baseUrl = state.url.slice(0, state.url.lastIndexOf(route.url[route.url.length - 1].path) - 1);

          return (
            (!(storeState.rdePayload.extensionDate && storeState.rdePayload.deadline) &&
              this.router.parseUrl(baseUrl.concat('/extend-determination'))) ||
            (storeState.rdePayload.extensionDate &&
              storeState.rdePayload.deadline &&
              !(storeState.rdePayload.operators && storeState.rdePayload.signatory)) ||
            (storeState.rdePayload.extensionDate &&
              storeState.rdePayload.deadline &&
              storeState.rdePayload.operators &&
              storeState.rdePayload.signatory &&
              this.router.parseUrl(baseUrl.concat('/answers')))
          );
        }),
      )
    );
  }
}
