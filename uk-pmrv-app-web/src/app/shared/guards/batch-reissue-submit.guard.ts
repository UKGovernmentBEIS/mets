import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { first, map, Observable } from 'rxjs';

import { AccountType } from '@core/store/auth';

import { RequestsService } from 'pmrv-api';

@Injectable({ providedIn: 'root' })
export class BatchReissueSubmitGuard implements CanActivate {
  constructor(private router: Router, private readonly requestsService: RequestsService) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    const accountType = route.data.accountType as AccountType;
    return this.requestsService.getBatchReissueRequests(accountType, 0, 30).pipe(
      first(),
      map((response) => (response.canInitiateBatchReissue ? true : this.router.parseUrl('/landing'))),
    );
  }
}
