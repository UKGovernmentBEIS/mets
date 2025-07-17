import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, RouterStateSnapshot, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { DreApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import {
  isDeterminationReasonStepCompleted,
  isMonitoringApproachesStepCompleted,
  isOfficialNoticeReasonStepCompleted,
  isWizardCompleted,
} from '../dre.wizard';

@Injectable({ providedIn: 'root' })
export class ReportableEmissionsGuard {
  constructor(
    private readonly store: CommonTasksStore,
    private readonly router: Router,
  ) {}

  canActivate(_route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean | UrlTree> {
    return (
      this.router.getCurrentNavigation().extras?.state?.changing ||
      this.store.pipe(
        map((storeState) => {
          const payload = storeState.requestTaskItem.requestTask.payload as DreApplicationSubmitRequestTaskPayload;
          const summary = `${state.url.slice(0, state.url.lastIndexOf('/'))}/summary`;
          const firstWizardStep = `${state.url.slice(0, state.url.lastIndexOf('/'))}/determination-reason`;

          return (
            ((payload?.sectionCompleted || isWizardCompleted(payload?.dre)) && this.router.parseUrl(summary)) ||
            (!this.arePreviousStepsCompleted(payload) && this.router.parseUrl(firstWizardStep)) ||
            true
          );
        }),
      )
    );
  }

  private arePreviousStepsCompleted(payload: DreApplicationSubmitRequestTaskPayload) {
    return (
      isDeterminationReasonStepCompleted(payload?.dre) &&
      isOfficialNoticeReasonStepCompleted(payload?.dre) &&
      isMonitoringApproachesStepCompleted(payload?.dre)
    );
  }
}
