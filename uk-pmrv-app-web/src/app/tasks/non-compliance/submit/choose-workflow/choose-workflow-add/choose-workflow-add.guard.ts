import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { CommonTasksStore } from '@tasks/store/common-tasks.store';

import { NonComplianceApplicationSubmitRequestTaskPayload } from 'pmrv-api';

import { isDetailsOfBreachStepCompleted, isWizardCompleted } from '../../non-compliance.wizard';

@Injectable({ providedIn: 'root' })
export class ChooseWorkflowAddGuard implements CanActivate {
  constructor(private readonly store: CommonTasksStore, private readonly router: Router) {}

  canActivate(_route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean | UrlTree> {
    return (
      this.router.getCurrentNavigation().extras?.state?.changing ||
      this.store.pipe(
        map((storeState) => {
          const payload = storeState.requestTaskItem.requestTask
            .payload as NonComplianceApplicationSubmitRequestTaskPayload;
          const wizardBaseUrl = `/tasks/${_route.paramMap.get('taskId')}/non-compliance/submit`;
          const summaryStep = `${wizardBaseUrl}/summary`;
          const firstWizardStep = `${wizardBaseUrl}/details-of-breach`;
          const chooseWorkflowStep = `${state.url.slice(0, state.url.lastIndexOf('/'))}`;

          const index = _route.paramMap.get('index');

          return (
            ((payload?.sectionCompleted || isWizardCompleted(payload)) && this.router.parseUrl(summaryStep)) ||
            (!this.arePreviousStepsCompleted(payload) && this.router.parseUrl(firstWizardStep)) ||
            (index !== null &&
              !payload?.selectedRequests?.[Number(index)] &&
              this.router.parseUrl(chooseWorkflowStep)) ||
            true
          );
        }),
      )
    );
  }

  private arePreviousStepsCompleted(payload: NonComplianceApplicationSubmitRequestTaskPayload) {
    return isDetailsOfBreachStepCompleted(payload);
  }
}
