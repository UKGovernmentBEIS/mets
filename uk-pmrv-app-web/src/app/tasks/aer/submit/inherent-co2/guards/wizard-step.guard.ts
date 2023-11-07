import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { AerService } from '@tasks/aer/core/aer.service';

import { isWizardComplete } from '../inherent-co2-wizard';

@Injectable({
  providedIn: 'root',
})
export class WizardStepGuard implements CanActivate {
  constructor(private readonly router: Router, private readonly aerService: AerService) {}

  canActivate(route: ActivatedRouteSnapshot): true | Observable<true | UrlTree> {
    return (
      this.router.getCurrentNavigation().extras?.state?.changing ||
      this.aerService.aerInherentInstallations$.pipe(
        map((aerInherentInstallations) => {
          const index = Number(route.paramMap.get('index'));
          const installation = aerInherentInstallations?.map(
            (aerInherent) => aerInherent?.inherentReceivingTransferringInstallation,
          )?.[index];
          const url = `/tasks/${route.paramMap.get('taskId')}/aer/submit/inherent-co2-emissions`;

          return !isWizardComplete(installation) || this.router.parseUrl(url);
        }),
      )
    );
  }
}
