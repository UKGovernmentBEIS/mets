import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlTree } from '@angular/router';

import { combineLatest, first, map, Observable } from 'rxjs';

import { AlrService } from '@tasks/alr/core';
import { activityComplete } from '@tasks/alr/utils';

@Injectable({ providedIn: 'root' })
export class ActivitySummaryGuard {
  constructor(
    private readonly alrService: AlrService,
    private readonly router: Router,
  ) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean | UrlTree> {
    const baseUrl = state.url.slice(0, state.url.lastIndexOf(route.url[route.url.length - 1].path) - 1);

    return combineLatest([this.alrService.getPayload(), this.alrService.isEditable$]).pipe(
      first(),
      map(([payload, isEditable]) => !isEditable || activityComplete(payload) || this.router.parseUrl(baseUrl)),
    );
  }
}
