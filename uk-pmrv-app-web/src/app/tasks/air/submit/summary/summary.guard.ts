import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { AirService } from '@tasks/air/shared/services/air.service';
import { operatorImprovementResponseComplete } from '@tasks/air/submit/submit.wizard';

@Injectable()
export class SummaryGuard implements CanActivate {
  constructor(private readonly router: Router, private readonly airService: AirService) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.airService.payload$.pipe(
      map((payload) => {
        const reference = route.paramMap.get('id');
        return (
          operatorImprovementResponseComplete(payload?.operatorImprovementResponses?.[reference]) ||
          this.router.parseUrl(`tasks/${route.paramMap.get('taskId')}/air/submit/${reference}/improvement-question`)
        );
      }),
    );
  }
}
