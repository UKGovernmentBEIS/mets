import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';

import { first, map, Observable } from 'rxjs';

import { PermitTransferAService } from '../permit-transfer-a.service';
import { isWizardCompleted } from '../permit-transfer-a-task-statuses';

@Injectable({
  providedIn: 'root',
})
export class WizardStepGuard implements CanActivate {
  constructor(private readonly permitTransferAService: PermitTransferAService, private readonly router: Router) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean | UrlTree> {
    return (
      this.router.getCurrentNavigation().extras?.state?.changing ||
      this.permitTransferAService.getPayload().pipe(
        first(),
        map((payload) => {
          const wizardIsCompleted = isWizardCompleted(payload);
          const baseUrl = `tasks/${route.paramMap.get('taskId')}/permit-transfer-a/submit`;
          const summaryUrl = `/${baseUrl}/summary`;
          const wizardFirstStep = `/${baseUrl}/reason`;
          const isCurrentSummaryPage = state.url.includes(summaryUrl);

          return (
            (!wizardIsCompleted && !isCurrentSummaryPage) ||
            (!wizardIsCompleted && isCurrentSummaryPage && this.router.parseUrl(wizardFirstStep)) ||
            (wizardIsCompleted && !isCurrentSummaryPage && this.router.parseUrl(summaryUrl)) ||
            true
          );
        }),
      )
    );
  }
}
