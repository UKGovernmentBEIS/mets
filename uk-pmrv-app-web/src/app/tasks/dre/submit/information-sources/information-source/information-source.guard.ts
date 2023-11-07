import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { DreApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import {
  isDeterminationReasonStepCompleted,
  isMonitoringApproachesStepCompleted,
  isOfficialNoticeReasonStepCompleted,
  isReportableEmissionsStepCompleted,
  isWizardCompleted,
} from '../../dre.wizard';

@Injectable({ providedIn: 'root' })
export class InformationSourceGuard implements CanActivate {
  constructor(private readonly store: CommonTasksStore, private readonly router: Router) {}

  canActivate(_route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean | UrlTree> {
    return (
      this.router.getCurrentNavigation().extras?.state?.changing ||
      this.store.pipe(
        map((storeState) => {
          const payload = storeState.requestTaskItem.requestTask.payload as DreApplicationSubmitRequestTaskPayload;
          const wizardBaseUrl = `/tasks/${_route.paramMap.get('taskId')}/dre/submit`;
          const summaryStep = `${wizardBaseUrl}/summary`;
          const firstWizardStep = `${wizardBaseUrl}/determination-reason`;
          const informationSourcesStep = `${state.url.slice(0, state.url.lastIndexOf('/'))}`;

          const index = _route.paramMap.get('index');

          return (
            ((payload?.sectionCompleted || isWizardCompleted(payload?.dre)) && this.router.parseUrl(summaryStep)) ||
            (!this.arePreviousStepsCompleted(payload) && this.router.parseUrl(firstWizardStep)) ||
            (index !== null &&
              !payload?.dre?.informationSources?.[Number(index)] &&
              this.router.parseUrl(informationSourcesStep)) ||
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
      isMonitoringApproachesStepCompleted(payload?.dre) &&
      isReportableEmissionsStepCompleted(payload?.dre)
    );
  }
}
