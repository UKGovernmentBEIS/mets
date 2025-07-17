import { Injectable } from '@angular/core';
import { ActivatedRoute, ActivatedRouteSnapshot } from '@angular/router';

import { first, map, Observable } from 'rxjs';

import { RequestsService } from 'pmrv-api';

@Injectable({
  providedIn: 'root',
})
export class MarkAsNotRequiredGuard {
  requestId$ = this.route.paramMap.pipe(map((paramMap) => paramMap.get('request-id')));

  constructor(
    private readonly route: ActivatedRoute,
    private requestsService: RequestsService,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean> {
    return this.requestsService.hasAccessMarkAsNotRequired(route.paramMap.get('request-id')).pipe(
      first(),
      map((accountPermit) => !!accountPermit),
    );
  }
}
