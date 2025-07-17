import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, Router, UrlTree } from '@angular/router';

import { map, Observable } from 'rxjs';

import { InspectionService } from '@tasks/inspection/core/inspection.service';
import { InspectionSubmitRequestTaskPayload } from '@tasks/inspection/shared/inspection';
import { isInstallationInspectionFollowUpSubmitCompleted } from '@tasks/inspection/submit/submit.wizard';

@Injectable()
export class FollowUpSubmitSummaryGuard {
  constructor(
    private readonly router: Router,
    private readonly inspectionService: InspectionService,
  ) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.inspectionService.payload$.pipe(
      map((payload: InspectionSubmitRequestTaskPayload) => {
        return isInstallationInspectionFollowUpSubmitCompleted(payload?.installationInspection)
          ? true
          : this.router.parseUrl(
              `tasks/${route.paramMap.get('taskId')}/inspection/${route.paramMap.get('type')}/submit/follow-up-actions`,
            );
      }),
    );
  }
}
