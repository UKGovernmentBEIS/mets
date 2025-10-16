import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlTree } from '@angular/router';

import { combineLatest, first, map, Observable } from 'rxjs';

import { PermitTransferAService } from '../permit-transfer-a.service';
import { isWizardCompleted } from '../permit-transfer-a-task-statuses';

@Injectable({
  providedIn: 'root',
})
export class WizardStepGuard {
  constructor(
    private readonly permitTransferAService: PermitTransferAService,
    private readonly router: Router,
  ) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean | UrlTree> {
    return (
      this.router.getCurrentNavigation().extras?.state?.changing ||
      combineLatest([this.permitTransferAService.getPayload(), this.permitTransferAService.isAlrVisible$]).pipe(
        first(),
        map(([payload, isAlrVisible]) => {
          const wizardIsCompleted = isWizardCompleted(payload, isAlrVisible);
          const baseUrl = `tasks/${route.paramMap.get('taskId')}/permit-transfer-a/submit`;
          const summaryUrl = `/${baseUrl}/summary`;
          const wizardFirstStep = `/${baseUrl}/reason`;
          const alrpage = `/${baseUrl}/activity-level-report`;
          const isCurrentSummaryPage = state.url.includes(summaryUrl);
          const isCurrentAlrPage = state.url.includes(alrpage);

          return (
            (isCurrentAlrPage && !isAlrVisible && this.router.parseUrl(baseUrl.concat('/code'))) ||
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
