import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { AerService } from '@tasks/aer/core/aer.service';
import { isWizardComplete } from '@tasks/aer/submit/fallback/fallback-wizard';

import { AerApplicationSubmitRequestTaskPayload } from 'pmrv-api';

@Injectable({
  providedIn: 'root',
})
export class WizardStepGuard implements CanActivate {
  constructor(private readonly router: Router, private readonly aerService: AerService) {}

  canActivate(route: ActivatedRouteSnapshot): true | Observable<true | UrlTree> {
    return (
      this.router.getCurrentNavigation().extras?.state?.changing ||
      this.aerService.getPayload().pipe(
        map((payload: AerApplicationSubmitRequestTaskPayload) => {
          const url = `/tasks/${route.paramMap.get('taskId')}/aer/submit/fallback/summary`;

          return !isWizardComplete(payload) || this.router.parseUrl(url);
        }),
      )
    );
  }
}
