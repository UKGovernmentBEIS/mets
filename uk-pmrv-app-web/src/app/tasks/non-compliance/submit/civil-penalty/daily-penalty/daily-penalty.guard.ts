import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { NonComplianceApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import {
  isCivilPenaltyStepCompleted,
  isDetailsOfBreachStepCompleted,
  isNoticeOfIntentStepCompleted,
  isWizardCompleted,
} from '../../non-compliance.wizard';

@Injectable({ providedIn: 'root' })
export class DailyPenaltyGuard implements CanActivate {
  constructor(private readonly store: CommonTasksStore, private readonly router: Router) {}

  canActivate(_route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean | UrlTree> {
    return (
      this.router.getCurrentNavigation().extras?.state?.changing ||
      this.store.pipe(
        map((storeState) => {
          const payload = storeState.requestTaskItem.requestTask
            .payload as NonComplianceApplicationSubmitRequestTaskPayload;
          const summary = `${state.url.slice(0, state.url.lastIndexOf('/'))}/summary`;
          const firstWizardStep = `${state.url.slice(0, state.url.lastIndexOf('/'))}/details-of-breach`;

          return (
            (!payload?.sectionCompleted && payload?.civilPenalty && payload?.dailyPenalty === null) ||
            ((payload?.sectionCompleted || isWizardCompleted(payload)) && this.router.parseUrl(summary)) ||
            (!this.arePreviousStepsCompleted(payload) && this.router.parseUrl(firstWizardStep)) ||
            true
          );
        }),
      )
    );
  }

  private arePreviousStepsCompleted(payload: NonComplianceApplicationSubmitRequestTaskPayload) {
    return (
      isDetailsOfBreachStepCompleted(payload) &&
      isCivilPenaltyStepCompleted(payload) &&
      isNoticeOfIntentStepCompleted(payload)
    );
  }
}
