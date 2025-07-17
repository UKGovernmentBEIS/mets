import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { first, map, Observable } from 'rxjs';

import { AirService } from '@tasks/air/shared/services/air.service';
import { AirImprovementResponseService } from '@tasks/air/shared/services/air-improvement-response.service';

import { AirApplicationSubmitRequestTaskPayload } from 'pmrv-api';

@Injectable()
export class AirImprovementResponseGuard {
  constructor(
    private readonly router: Router,
    private readonly airService: AirService,
    private readonly airImprovementResponseService: AirImprovementResponseService,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    const reference = route.paramMap.get('id');

    const operatorImprovementResponse$ = (
      this.airService.payload$ as Observable<AirApplicationSubmitRequestTaskPayload>
    ).pipe(map((payload) => payload?.operatorImprovementResponses[reference]));

    return operatorImprovementResponse$.pipe(
      first(),
      map((operatorImprovementResponse) => {
        const lastUrlSegment = route.url[route.url.length - 1].path;
        const matches = this.airImprovementResponseService.typeMatches(
          operatorImprovementResponse?.type,
          lastUrlSegment,
        );
        return matches || this.router.parseUrl(`tasks/${route.paramMap.get('taskId')}/air/submit`);
      }),
    );
  }
}
