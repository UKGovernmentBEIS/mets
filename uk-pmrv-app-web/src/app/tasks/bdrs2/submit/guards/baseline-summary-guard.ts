import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlTree } from '@angular/router';

import { combineLatest, first, map, Observable } from 'rxjs';

import { BdrS2Service } from '@tasks/bdrs2/core';
import { baselineComplete } from '@tasks/bdrs2/utils';

@Injectable({ providedIn: 'root' })
export class BDRS2BaselineSummaryGuard {
  constructor(
    private readonly bdrs2Service: BdrS2Service,
    private readonly router: Router,
  ) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean | UrlTree> {
    const baseUrl = state.url.slice(0, state.url.lastIndexOf(route.url[route.url.length - 1].path) - 1);
    return combineLatest([this.bdrs2Service.getPayload(), this.bdrs2Service.isEditable$]).pipe(
      first(),
      map(([payload, isEditable]) => !isEditable || baselineComplete(payload) || this.router.parseUrl(baseUrl)),
    );
  }
}
