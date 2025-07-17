import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { InspectionService } from '@tasks/inspection/core/inspection.service';

import { InstallationInspectionOperatorRespondRequestTaskPayload } from 'pmrv-api';

import { isInstallationInspectionRespondCompleted } from '../submit.wizard';

@Injectable()
export class FollowUpRespondSummaryGuard {
  constructor(
    private readonly router: Router,
    private readonly inspectionService: InspectionService,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.inspectionService.payload$.pipe(
      map((payload: InstallationInspectionOperatorRespondRequestTaskPayload) => {
        return isInstallationInspectionRespondCompleted(
          payload?.followupActionsResponses,
          route.paramMap.get('actionId'),
        )
          ? true
          : this.router.parseUrl(
              `tasks/${route.paramMap.get('taskId')}/inspection/${route.paramMap.get('type')}/respond/${route.paramMap.get('actionId')}/follow-up-action`,
            );
      }),
    );
  }
}
