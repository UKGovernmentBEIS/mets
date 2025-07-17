import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { first, map, Observable } from 'rxjs';

import { PermitApplicationState } from '../../store/permit-application.state';
import { PermitApplicationStore } from '../../store/permit-application.store';

@Injectable()
export class EmissionPointDetailsGuard {
  constructor(
    private readonly store: PermitApplicationStore<PermitApplicationState>,
    private readonly router: Router,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.store.getTask('emissionPoints').pipe(
      first(),
      map(
        (emissionPoints) =>
          emissionPoints.some((emissionPoint) => emissionPoint.id === route.paramMap.get('emissionPointId')) ||
          this.router.parseUrl(`/${this.store.urlRequestType}/${route.paramMap.get('taskId')}/emission-points/summary`),
      ),
    );
  }
}
