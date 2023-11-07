import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';

import { combineLatest, first, map, Observable } from 'rxjs';

import { AerService } from '@tasks/aer/core/aer.service';
import { getSectionStatus } from '@tasks/aer/core/aer-task-statuses';

@Injectable({ providedIn: 'root' })
export class SummaryGuard implements CanActivate {
  constructor(private readonly aerService: AerService, private readonly router: Router) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean | UrlTree> {
    const baseUrl = state.url.slice(0, state.url.lastIndexOf(route.url[route.url.length - 1].path) - 1);

    return combineLatest([this.aerService.getPayload(), this.aerService.isEditable$]).pipe(
      first(),
      map(
        ([payload, isEditable]) =>
          !isEditable ||
          getSectionStatus(route.data.aerTask, payload) !== 'not started' ||
          this.router.parseUrl(baseUrl),
      ),
    );
  }
}
