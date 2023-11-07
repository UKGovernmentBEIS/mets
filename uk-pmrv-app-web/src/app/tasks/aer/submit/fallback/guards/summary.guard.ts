import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { first, map, Observable } from 'rxjs';

import { AerService } from '@tasks/aer/core/aer.service';
import { fallbackStatus } from '@tasks/aer/submit/fallback/fallback-status';
import { isWizardComplete } from '@tasks/aer/submit/fallback/fallback-wizard';

@Injectable({ providedIn: 'root' })
export class SummaryGuard implements CanActivate {
  constructor(private readonly aerService: AerService, private readonly router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    const taskId = route.paramMap.get('taskId');
    const baseUrl = `tasks/${taskId}/aer/submit/fallback`;

    return this.aerService.getPayload().pipe(
      first(),
      map((payload) => {
        const needsReview = fallbackStatus(payload) === 'needs review';
        return (isWizardComplete(payload) && !needsReview) || this.router.parseUrl(baseUrl);
      }),
    );
  }
}
