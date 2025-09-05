import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlTree } from '@angular/router';

import { combineLatest, first, map, Observable } from 'rxjs';

import { HseTiService } from '@tasks/hseti/core';
import { detailsComplete } from '@tasks/hseti/utils';

@Injectable({ providedIn: 'root' })
export class DetailsSummaryGuard {
  constructor(
    private readonly hsetiService: HseTiService,
    private readonly router: Router,
  ) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean | UrlTree> {
    const baseUrl = state.url.slice(0, state.url.lastIndexOf(route.url[route.url.length - 1].path) - 1);

    return combineLatest([this.hsetiService.getPayload(), this.hsetiService.isEditable$]).pipe(
      first(),
      map(([payload, isEditable]) => !isEditable || detailsComplete(payload) || this.router.parseUrl(baseUrl)),
    );
  }
}
