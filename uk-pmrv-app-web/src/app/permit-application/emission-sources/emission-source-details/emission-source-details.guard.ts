import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { first, map, Observable } from 'rxjs';

import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';

@Injectable()
export class EmissionSourceDetailsGuard implements CanActivate {
  constructor(
    private readonly store: PermitApplicationStore<PermitApplicationState>,
    private readonly router: Router,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.store.getTask('emissionSources').pipe(
      first(),
      map(
        (emissionSources) =>
          emissionSources.some((emissionSource) => emissionSource.id === route.paramMap.get('sourceId')) ||
          this.router.parseUrl(
            `/${this.store.urlRequestType}/${route.paramMap.get('taskId')}/emission-sources/summary`,
          ),
      ),
    );
  }
}
